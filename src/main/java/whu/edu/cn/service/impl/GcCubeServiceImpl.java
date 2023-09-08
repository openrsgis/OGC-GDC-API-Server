package whu.edu.cn.service.impl;

import com.alibaba.fastjson.JSONObject;;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import scala.Option;
import scala.runtime.BoxesRunTime;
import whu.edu.cn.application.gdc.GDCTrigger;
import whu.edu.cn.config.spark.SparkAppParas;
import whu.edu.cn.core.entity.QueryParams;
import whu.edu.cn.core.entity.SubsetQuery;
import whu.edu.cn.core.entity.WorkflowCollectionParam;
import whu.edu.cn.entity.Ingest.GcCube;
import whu.edu.cn.entity.Ingest.GcDimension;
import whu.edu.cn.entity.Ingest.conf.GcCubeConf;
import whu.edu.cn.entity.Ingest.view.GcMeasurementProduct;
import whu.edu.cn.entity.coverage.*;
import whu.edu.cn.entity.extent.Extent;
import whu.edu.cn.entity.extent.SpatialExtent;
import whu.edu.cn.entity.extent.TemporalExtent;
import whu.edu.cn.entity.modify.ModifyBands;
import whu.edu.cn.entity.modify.ModifyParam;
import whu.edu.cn.entity.process.Link;
import whu.edu.cn.mapper.*;
import whu.edu.cn.service.IGcCubeService;
import whu.edu.cn.service.IGcFileService;
import whu.edu.cn.service.ISparkApplicationService;
import whu.edu.cn.util.GeoUtil;
import whu.edu.cn.util.RedisUtil;
import whu.edu.cn.util.TimeUtil;
import whu.edu.cn.util.TypeUtil;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class GcCubeServiceImpl implements IGcCubeService {

    @Resource
    private GcCubeMapper gcCubeMapper;

    @Resource
    private GeoUtil geoUtil;

    @Resource
    private IGcFileService gcFileService;

    @Resource
    private GcMeasurementProductView gcMeasurementProductView;

    @Resource
    private GcProductMapper gcProductMapper;

    @Resource
    private GcDimensionMapper dimensionMapper;


    @Resource
    private ISparkApplicationService sparkApplicationService;

    @Resource
    TimeUtil timeUtil;

    @Resource
    TypeUtil typeUtil;

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    SparkAppParas sparkAppParas;

    @Value("${address.localDataRoot}")
    private String localDataRoot;

    @Value("${address.gdcApiUrl}")
    private String gdcApiUrl;

    @Value("${table-prefix.dimension}")
    private String dimensionPrefix;

    @Value("${table-prefix.product}")
    private String productPrefix;

    @Value("${table-prefix.extent}")
    private String extentPrefix;

    @Value("${table-prefix.measurement}")
    private String measurementPrefix;

    @Value("${table-prefix.raster_fact}")
    private String rasterFact;

    @Value("${table-prefix.vector_fact}")
    private String vectorFact;

    @Value("${table-prefix.raster_hbase}")
    private String rasterHbase;

    @Value("${table-prefix.vector_hbase}")
    private String vectorHbase;

    @Value("${table-prefix.sensor_level_product_view}")
    private String sensorLevelProductView;


    @Value("${table-prefix.measurement_product_view}")
    private String measurementProductView;


    @Value("${table-prefix.level_extent_view}")
    private String levelExtentView;

    @Override
    public GcCube insertCube(GcCube cube, String type, GcCubeConf gcCubeConf) {
        Integer maxId = gcCubeMapper.getCubeMaxId();
        Integer nextId = maxId + 1;
        cube.setId(nextId);
        cube.setProductTableName(productPrefix + nextId);
        cube.setDimensionTableName(dimensionPrefix + nextId);
        String additionalDimensionColumnsSQL = gcCubeConf.generateProductColumnSQL();
        cube.setProductMeasurementTableName(measurementPrefix + nextId);
        cube.setExtentTableName(extentPrefix + nextId);
        if (type.equals("EO")) {
            cube.setFactTableName(rasterFact + nextId);
        } else if (type.equals("Vector")) {
            cube.setFactTableName(vectorFact + nextId);
        }
        if (type.equals("EO")) {
            cube.setHbaseTableName(rasterHbase + nextId);
        } else if (type.equals("Vector")) {
            cube.setHbaseTableName(vectorHbase + nextId);
        }
        cube.setSensorLevelProductViewName(sensorLevelProductView + nextId);
        cube.setMeasurementsProductViewName(measurementProductView + nextId);
        cube.setLevelExtentViewName(levelExtentView + nextId);
        cube.setDimensionTableName(dimensionPrefix + nextId);
        String wkt = geoUtil.DoubleToWKT(cube.getSliceMinX(), cube.getSliceMinY(), cube.getSliceMinY(), cube.getSliceMaxY());
        String geom = "ST_GeomFromText('" + wkt + "', 4326)";
        cube.setGeom(geom);
        Boolean insert = gcCubeMapper.insertCubeFromCube(cube);
        cube.setInsert(insert);
        int gridDimX = (int) ((cube.getSliceMaxX() - cube.getSliceMinX()) / cube.getCellSize());
        int gridDimY = (int) ((cube.getSliceMaxY() - cube.getSliceMinY()) / cube.getCellSize());
        if (!gcFileService.initDimensionTable(cube.getId(), cube.getDimensionTableName(), gcCubeConf)) return null;
        gcFileService.initDatabase(cube.getId().toString(), cube.getLevelKey(), gridDimX, gridDimY,
                cube.getSliceMinX(), cube.getSliceMinY(), cube.getSliceMaxX(), cube.getSliceMaxY(), cube.getCellRes(), cube.getCellRes(), additionalDimensionColumnsSQL);
        return cube;
    }

    /**
     * Insert cube data source with info
     * return true or false
     */
    @Override
    public Boolean insertOneData(String productName, String filePath, String fileName, String geom, String metaPath, Timestamp phenomenonTime, Timestamp resultTime, Double minx, Double miny, Double maxx, Double maxy) {
        return gcCubeMapper.insetCubeData(productName, filePath, phenomenonTime, resultTime, geom, fileName, metaPath, minx, miny, maxx, maxy);
    }

    public GcCube getCubeById(Integer cubeId) {
        return gcCubeMapper.getCubeById(cubeId);
    }

    /**
     * @param startTime start time
     * @param endTime   end time
     * @param WKT       spatial extent
     * @param limit     limit number
     * @return collectionInfo CollectionInfo
     */
    public CollectionsInfo getCollectionsFromCubesByParam(Timestamp startTime, Timestamp endTime, String WKT, int limit) {
        List<GcCube> cubeList = gcCubeMapper.getCubesByParams(startTime, endTime, WKT, limit);
        CollectionsInfo collectionsInfo = new CollectionsInfo();
        List<CollectionInfo> collectionInfos = new ArrayList<>();
        for (GcCube cube : cubeList) {
            collectionInfos.add(Cube2CollectionInfo(cube));
        }
        collectionsInfo.setCollections(collectionInfos);
        return collectionsInfo;
    }

    /**
     * Cube is transformed to CollectionInfo
     *
     * @param cube the cube instance
     * @return CollectionInfo
     */
    private CollectionInfo Cube2CollectionInfo(GcCube cube) {
        CollectionInfo collectionInfo = new CollectionInfo();
        String cubeName = cube.getCubeName();
        collectionInfo.setId(cubeName);
        collectionInfo.setTitle(cubeName);
        List<List<Double>> bbox = new ArrayList<>();
        bbox.add(Arrays.asList(cube.getLeftBottomLongitude(), cube.getLeftBottomLatitude(), cube.getRightTopLongitude(), cube.getRightTopLatitude()));
        String crsCode = cube.getCrs();
        List<String> crs = Arrays.asList(geoUtil.getCRSHref(crsCode));
        collectionInfo.setCrs(crs);
        SpatialExtent spatialExtent = new SpatialExtent(bbox, geoUtil.getCRSHref(crsCode));
        List<List<String>> interval = new ArrayList<>();
        interval.add(Arrays.asList(cube.getStartTime(), cube.getEndTime()));
        TemporalExtent temporalExtent = new TemporalExtent(interval);
        Extent extent = new Extent(spatialExtent, temporalExtent);
        collectionInfo.setExtent(extent);
        List<Link> links = new ArrayList<>();

        links.add(new Link("http://oge.whu.edu.cn/ogcapi/coverages_api", "root", "application/json",
                "The landing page of this server as JSON"));
        links.add(new Link("http://oge.whu.edu.cn/ogcapi/coverages_api/collections/" + cubeName,
                "self", "application/json", "Detailed Coverage metadata in JSON"));
        links.add(new Link("http://oge.whu.edu.cn/ogcapi/coverages_api/collections/" + cubeName + "/coverage",
                "http://www.opengis.net/def/rel/ogc/1.0/coverage", "image/tiff; application=geotiff",
                "Coverage data"));
        links.add(new Link("http://oge.whu.edu.cn/ogcapi/coverages_api/collections/" + cubeName + "/coverage/domainset",
                "http://www.opengis.net/def/rel/ogc/1.0/coverage-domainset", "application/json",
                "Coverage domain set of collection in JSON"));
        links.add(new Link("http://oge.whu.edu.cn/ogcapi/coverages_api/collections/" + cubeName + "/coverage/rangetype",
                "http://www.opengis.net/def/rel/ogc/1.0/coverage-rangetype", "application/json",
                "Coverage range type of collection in JSON"));
        links.add(new Link("http://oge.whu.edu.cn/ogcapi/coverages_api/collections/" + cubeName + "/coverage/rangeset",
                "http://www.opengis.net/def/rel/ogc/1.0/coverage-rangeset", "image/tiff; application=geotiff",
                "Coverage range set of collection in tif"));
        collectionInfo.setLinks(links);
        return collectionInfo;
    }

    /**
     * modify the collection by the process workflow
     *
     * @param collectionId   the collection id
     * @param modifyParam    the modify param
     * @param collectionInfo the native collectionInfo
     * @return the modified collectionInfo
     */
    public CollectionInfo modifyParam2Collection(String collectionId, ModifyParam modifyParam, CollectionInfo collectionInfo) {
        collectionInfo.setItemType("coverage");
        collectionInfo.setId(collectionId);
        collectionInfo.setTitle(modifyParam.getCollection());
        Extent extent = new Extent();
        if (!(modifyParam.getStartTime() == null && modifyParam.getEndTime() == null)) {
            if (modifyParam.getStartTime().equals("None") || modifyParam.getEndTime().equals("None")) {
                extent.setTemporal(null);
            } else {
                List<List<String>> interval = new ArrayList<>();
                interval.add(Arrays.asList(modifyParam.getStartTime(), modifyParam.getEndTime()));
                TemporalExtent temporalExtent = new TemporalExtent(interval);
                extent.setTemporal(temporalExtent);
            }
        } else {
            // if startTime and endTime are all equal null, it means no change in time extent compared with native cube
            extent.setTemporal(collectionInfo.getExtent().getTemporal());
        }
        if (modifyParam.getExtent() != null) {
            List<List<Double>> bbox = new ArrayList<>();
            String[] extents = modifyParam.getExtent().split(",");
            bbox.add(Arrays.asList(Double.valueOf(extents[0]), Double.valueOf(extents[1]), Double.valueOf(extents[2]), Double.valueOf(extents[3])));
            String crs = collectionInfo.getExtent().getSpatial().getCrs();
            SpatialExtent spatialExtent = new SpatialExtent(bbox, crs);
            extent.setSpatial(spatialExtent);
        } else {
            extent.setSpatial(collectionInfo.getExtent().getSpatial());
        }
        collectionInfo.setRangeType(modifyRangeType(modifyParam, collectionInfo.getRangeType()));
        collectionInfo.setDomainSet(modifyDomainSet(modifyParam, collectionInfo.getDomainSet()));
        collectionInfo.setExtent(extent);
        List<Link> links = new ArrayList<>();
        links.add(new Link(gdcApiUrl, "root", "application/json",
                "The landing page of this server as JSON"));
        links.add(new Link(gdcApiUrl + "/collections/" + collectionId,
                "self", "application/json", "Detailed Coverage metadata in JSON"));
        links.add(new Link(gdcApiUrl + "/collections/" + collectionId + "/domainset",
                "http://www.opengis.net/def/rel/ogc/1.0/coverage-domainset", "application/json",
                "Coverage domain set of collection in JSON"));
        links.add(new Link(gdcApiUrl + "/collections/" + collectionId + "/rangetype",
                "http://www.opengis.net/def/rel/ogc/1.0/coverage-rangetype", "application/json",
                "Coverage range type of collection in JSON"));
        links.add(new Link(gdcApiUrl + "/collections/" + collectionId + "/rangeset",
                "http://www.opengis.net/def/rel/ogc/1.0/coverage-rangeset", "image/tiff; application=geotiff",
                "Coverage range set of collection in tif"));
        links.add(new Link(gdcApiUrl + "/collections/" + collectionId + "/coverage",
                "http://www.opengis.net/def/rel/ogc/1.0/coverage", "image/tiff; application=geotiff",
                "Coverage data"));
        links.add(new Link(gdcApiUrl + "/collections/" + collectionId + "/workflow",
                "http://www.opengis.net/def/rel/ogc/1.0/processes-request", "application/json",
                modifyParam.getCollection() + " workflow document (as JSON)"));
        collectionInfo.setLinks(links);
        return collectionInfo;
    }

    /**
     * modify the domainSet by the process workflow
     *
     * @param modifyParam the modify params
     * @param domainSet   the native domainSet
     * @return the modified params
     */
    public DomainSet modifyDomainSet(ModifyParam modifyParam, DomainSet domainSet) {
        GeneralGrid generalGrid = domainSet.getGeneralGrid();
        List<AxisInfo> axisInfos = generalGrid.getAxis();
        GridLimits gridLimits = generalGrid.getGridLimits();
        if (modifyParam.getExtent() != null) {
            String[] extents = modifyParam.getExtent().split(",");
            for (int i = 0; i < axisInfos.size(); i++) {
                AxisInfo axisInfo = axisInfos.get(i);
                if (axisInfo.getAxisLabel().equals("Lat")) {
                    axisInfo.setLowerBound(Double.parseDouble(extents[1]));
                    axisInfo.setUpperBound(Double.parseDouble(extents[3]));
                    gridLimits.getAxis().get(i).setUpperBound((double) Math.round((Double.parseDouble(extents[3]) - Double.parseDouble(extents[1])) / (Double) axisInfo.getResolution()));
                }
                if (axisInfo.getAxisLabel().equals("Lon")) {
                    axisInfo.setLowerBound(Double.parseDouble(extents[0]));
                    axisInfo.setUpperBound(Double.parseDouble(extents[2]));
                    gridLimits.getAxis().get(i).setUpperBound((double) Math.round((Double.parseDouble(extents[0]) - Double.parseDouble(extents[3])) / (Double) axisInfo.getResolution()));
                }
            }
        }
        // hardcode,delete the cloud dimension
        for (int j = 0; j < axisInfos.size(); j++) {
            AxisInfo axisInfo = axisInfos.get(j);
            if (axisInfo.getAxisLabel().equals("time")) {
                generalGrid.getAxisLabels().remove(j);
                axisInfos.remove(axisInfo);
                gridLimits.getAxisLabels().remove(j);
                gridLimits.getAxis().remove(j);
            }
        }
        for (int j = 0; j < axisInfos.size(); j++) {
            AxisInfo axisInfo = axisInfos.get(j);
            if (axisInfo.getAxisLabel().equals("cloud")) {
                generalGrid.getAxisLabels().remove(j);
                axisInfos.remove(axisInfo);
                gridLimits.getAxisLabels().remove(j);
                gridLimits.getAxis().remove(j);
            }
        }
        generalGrid.setAxis(axisInfos);
        generalGrid.setGridLimits(gridLimits);
        domainSet.setGeneralGrid(generalGrid);
        return domainSet;
    }

    /**
     * use the modify param to modify the rangeType
     *
     * @param modifyParam the modify param
     * @param rangeType   the native rangetype
     * @return rangeType
     */
    public RangeType modifyRangeType(ModifyParam modifyParam, RangeType rangeType) {
        List<ModifyBands> modifyBands = modifyParam.getBands();
        rangeType.setType("DataRecord");
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        for (int i = 0; i < modifyBands.size(); i++) {
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setId(i + 1);
            fieldInfo.setName(modifyBands.get(i).getName());
            fieldInfo.setDescription(modifyBands.get(i).getDescription());
            fieldInfo.setType("Quantity");
            fieldInfoList.add(fieldInfo);
        }
        rangeType.setFieldInfoList(fieldInfoList);
        return rangeType;
    }

    /**
     * RangeType 波段测量信息
     *
     * @param measurementProductList 列表
     * @return RangeType
     */
    private RangeType getRangeTypeByMeasurementProduct(List<GcMeasurementProduct> measurementProductList) {
        RangeType rangeType = new RangeType();
        rangeType.setType("DataRecord");
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        for (int i = 0; i < measurementProductList.size(); i++) {
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setId(i + 1);
            GcMeasurementProduct measurementProduct = measurementProductList.get(i);
            fieldInfo.setName(measurementProduct.getMeasurementName());
            if (measurementProduct.getUnit() != null) {
                fieldInfo.setUom(new Uom("UnitReference", measurementProduct.getUnit()));
            }
            fieldInfo.setType("Quantity");
            fieldInfoList.add(fieldInfo);
        }
        rangeType.setFieldInfoList(fieldInfoList);
        return rangeType;
    }

    /**
     * DomainSet信息
     *
     * @param cube Cube cube实体
     * @return 返回DomainSet
     */
    private DomainSet getDomainSetByCube(GcCube cube, List<GcDimension> dimensionList) {
        DomainSet domainSet = new DomainSet();
        domainSet.setType("DomainSet");
        GeneralGrid generalGrid = new GeneralGrid();
        generalGrid.setType("GeneralGridCoverage");
        String crsCode = cube.getCrs();
        generalGrid.setSrsName(geoUtil.getCRSHref(crsCode));
        List<String> usedAxisLabels = new ArrayList<>(Arrays.asList("Lon", "Lat"));
        Double resolution = cube.getCellSize() / cube.getCellRes();
        AxisInfo axisInfoLong = new AxisInfo("RegularAxis", "Lon", cube.getLeftBottomLongitude(),
                cube.getRightTopLongitude(), resolution, "deg");
        AxisInfo axisInfoLat = new AxisInfo("RegularAxis", "Lat", cube.getLeftBottomLatitude(),
                cube.getRightTopLatitude(), resolution, "deg");
        List<AxisInfo> generalGridAxis = new ArrayList<>(Arrays.asList(axisInfoLong, axisInfoLat));
        if (!(cube.getStartTime() == null || cube.getStartTime().contains("1978-01-01") || cube.getEndTime() == null || cube.getCubeName().contains("ECMWF"))) {
            usedAxisLabels.add("time");
            AxisInfo axisInfoTime = new AxisInfo("RegularAxis", "time", timeUtil.convertTime2Standard(cube.getStartTime()),
                    timeUtil.convertTime2Standard(cube.getEndTime()), 1, "s");
            generalGridAxis.add(axisInfoTime);
        }
        for (GcDimension dimension : dimensionList) {
            AxisInfo axisInfo = additionalDimension2Axis(dimension, cube.getProductTableName(), null);
            if (axisInfo != null) {
                usedAxisLabels.add(dimension.getDimensionName());
                generalGridAxis.add(axisInfo);
            }
        }
        generalGrid.setAxisLabels(usedAxisLabels);
        generalGrid.setAxis(generalGridAxis);
        GridLimits gridLimits = new GridLimits();
        gridLimits.setType("GridLimits");
        gridLimits.setSrsName(geoUtil.getCRSHref(crsCode));
        usedAxisLabels = new ArrayList<>(Arrays.asList("i", "j"));
        AxisInfo axisInfoI = new AxisInfo("IndexAxis", "i", 0,
                (double) Math.round((cube.getRightTopLongitude() - cube.getLeftBottomLongitude()) / cube.getCellSize() * cube.getCellRes()));
        AxisInfo axisInfoJ = new AxisInfo("IndexAxis", "j", 0,
                (double) Math.round((cube.getRightTopLongitude() - cube.getLeftBottomLongitude()) / cube.getCellSize() * cube.getCellRes()));
        List<AxisInfo> gridLimitGridAxis = new ArrayList<>(Arrays.asList(axisInfoI, axisInfoJ));
        if (!(cube.getStartTime() == null || cube.getStartTime().contains("1978-01-01") || cube.getEndTime() == null || cube.getCubeName().contains("ECMWF"))) {
            Instant startInstant = Instant.parse(timeUtil.convertTime2Standard(cube.getStartTime()));
            Instant endInstant = Instant.parse(timeUtil.convertTime2Standard(cube.getEndTime()));
            long secondsBetween = ChronoUnit.SECONDS.between(startInstant, endInstant);
            usedAxisLabels.add("k");
            AxisInfo axisInfoK = new AxisInfo("IndexAxis", "k", 0,
                    secondsBetween - 1);
            gridLimitGridAxis.add(axisInfoK);
        }
        String[] axisLabels = new String[]{"l", "m", "n", "o", "p", "q", "s", "t", "u", "v", "w", "x", "y", "z"};
        int labelIndex = 0;
        for (GcDimension dimension : dimensionList) {
            AxisInfo axisInfo = additionalDimension2Axis(dimension, cube.getProductTableName(), axisLabels[labelIndex]);
            if (axisInfo != null) {
                labelIndex = labelIndex + 1;
                gridLimitGridAxis.add(axisInfo);
                usedAxisLabels.add(axisLabels[labelIndex]);
            }
        }
        gridLimits.setAxisLabels(usedAxisLabels);
        gridLimits.setAxis(gridLimitGridAxis);
        generalGrid.setGridLimits(gridLimits);
        domainSet.setGeneralGrid(generalGrid);
        return domainSet;
    }

    /**
     * additional dimension will be transformed to the axis
     *
     * @param dimension        the dimension
     * @param productTableName the product table name
     * @param axisLabel        the axisLabel, if it's value is null, the axis will be generalGrid, otherwise it will be IndexAxis
     * @return the axisInfo instance
     */
    private AxisInfo additionalDimension2Axis(GcDimension dimension, String productTableName, String axisLabel) {
        String dimensionName = dimension.getDimensionName();
        if (!(dimensionName.equals("extent") || dimensionName.equals("product") ||
                dimensionName.equals("phenomenonTime") || dimensionName.equals("measurement"))) {
            String axisType = "RegularAxis";
            Double resolution = null;
            List<Object> coordinates = null;
            Object lowerBound = null;
            Object upperBound = null;
            if (dimension.getStep() == null) {
                axisType = "IrregularAxis";
                coordinates = gcProductMapper.getDimensionCoordinates(productTableName, dimensionName);
            } else {
                resolution = dimension.getStep();
            }
            if (!dimension.getMemberType().equals("string")) {
                lowerBound = gcProductMapper.getDimensionCoordinatesMin(productTableName, dimensionName);
                upperBound = gcProductMapper.getDimensionCoordinatesMax(productTableName, dimensionName);
            }
            if (axisLabel == null) {
                return new AxisInfo(axisType, dimension.getDimensionName(), lowerBound, upperBound, resolution, dimension.getUnit(), coordinates);
            } else {
                Integer coordinatesCount = gcProductMapper.getDimensionCoordinatesCount(productTableName, dimensionName);
                return new AxisInfo("IndexAxis", axisLabel, 0, coordinatesCount - 1);
            }
        }
        return null;
    }


    /**
     * Cube is transformed to CollectionInfo
     *
     * @param cubeName the id of cube
     * @return CollectionInfo
     */
    public CollectionInfo getCollectionByCubeName(String cubeName) {
        GcCube cube = gcCubeMapper.getCubeByName(cubeName);
        if (cube != null) {
            List<GcMeasurementProduct> measurementProducts = gcMeasurementProductView.getMeasurementProducts(cube.getMeasurementsProductViewName());
            String dimensionTableName = cube.getDimensionTableName();
            List<GcDimension> dimensionList = new ArrayList<>();
            if (dimensionTableName != null) {
                dimensionList = dimensionMapper.selectAllDimensions(dimensionTableName);
            }
            RangeType rangeType = getRangeTypeByMeasurementProduct(measurementProducts);
            DomainSet domainSet = getDomainSetByCube(cube, dimensionList);
            CollectionInfo collectionInfo = Cube2CollectionInfo(cube);
            collectionInfo.setRangeType(rangeType);
            collectionInfo.setDomainSet(domainSet);
            return collectionInfo;
        } else {
            return null;
        }
    }

    /**
     * Get the collection generated by the adhoc workflow
     *
     * @param collectionId the temp collection id
     * @return CollectionInfo
     */
    public CollectionInfo getWorkflowCollectionInfo(String collectionId) {
        String dagStr = redisUtil.getValueByKey(collectionId);
        Random random = new Random();
        JSONObject dagObj = GDCTrigger.runMetaAnalysis(dagStr, Integer.toString(random.nextInt()));
        System.out.println(dagObj.toJSONString());
        ModifyParam modifyParam = dagObj.toJavaObject(ModifyParam.class);
        System.out.println(modifyParam.toString());
        CollectionInfo collectionInfo = getCollectionByCubeName(modifyParam.getCollection());
        return modifyParam2Collection(collectionId, modifyParam, collectionInfo);
    }

    /**
     * check the subset, if the wrong subset that the dimension of cube doesn't have, then will return null
     *
     * @param cube           the cube instance
     * @param coverageSubset the coverageSubset instance
     * @return List<Subset> subsets
     */
    public List<Subset> checkSubset(GcCube cube, CoverageSubset coverageSubset) {
        List<Subset> subsetList = coverageSubset.getAdditionalSubset();
        if (subsetList == null) return null;
        String dimensionTableName = cube.getDimensionTableName();
        if (subsetList.size() != 0) {
            List<GcDimension> dimensionList = dimensionMapper.selectAdditionalDimensions(dimensionTableName);
            for (int i = 0; i < subsetList.size(); i++) {
                Subset subset = subsetList.get(i);
                boolean flag = false;
                for (GcDimension dimension : dimensionList) {
                    if (dimension.getDimensionName().equals(subset.getAxisName())) {
                        flag = true;
                        subset.setPoint(typeUtil.convertDataByType(subset.getPoint(), dimension.getMemberType()));
                        subset.setLowPoint(typeUtil.convertDataByType(subset.getLowPoint(), dimension.getMemberType()));
                        subset.setHighPoint(typeUtil.convertDataByType(subset.getHighPoint(), dimension.getMemberType()));
                        subsetList.set(i, subset);
                        break;
                    }
                }
                if (!flag) return null;
            }
        }
        return subsetList;
    }

    /**
     * set the spatial extent from the subset param
     *
     * @param queryParams    query params
     * @param bbox           bounding box
     * @param coverageSubset the subset param
     * @param collectionInfo the cube information
     * @return QueryParams the modified query params
     */
    public QueryParams setSpatialExtent(QueryParams queryParams, String bbox, CoverageSubset coverageSubset,
                                        CollectionInfo collectionInfo) {
        Double[] coordinates = new Double[4];
        if (bbox != null) {
            String[] coordinateList = bbox.split(",");
            for (int i = 0; i < coordinateList.length; i++) {
                coordinates[i] = Double.parseDouble(coordinateList[i]);
            }
        } else if (coverageSubset != null && coverageSubset.getSpatialSubset() != null) {
            coordinates = coverageSubset.getSpatialSubset();
        } else {
            coordinates = collectionInfo.getExtent().getSpatial().getBbox().get(0).toArray(new Double[0]);
        }
        queryParams.setExtent(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
        return queryParams;
    }

    /**
     * set the temporal extent from the subset param
     *
     * @param queryParams    query params
     * @param datetime       the datetime param
     * @param coverageSubset the subset param
     * @param collectionInfo the cube information
     * @return QueryParams the modified query params
     * @throws ParseException the exception
     */
    public QueryParams setTemporalExtent(QueryParams queryParams, String datetime, CoverageSubset coverageSubset,
                                         CollectionInfo collectionInfo) throws ParseException {
        String startTime = "";
        String endTime = "";
        if (datetime != null) {
            TimeUtil.DateTimeResult dateTimeResult = timeUtil.parseDateTime(datetime);
            if (dateTimeResult.type.equals(TimeUtil.DateTimeType.DATE_TIME)) {
                startTime = timeUtil.convertTime(dateTimeResult.value);
                endTime = timeUtil.convertTime(dateTimeResult.value);
            } else if (dateTimeResult.type.equals(TimeUtil.DateTimeType.INTERVAL)) {
                startTime = timeUtil.convertTime(dateTimeResult.startTime);
                endTime = timeUtil.convertTime(dateTimeResult.endTime);
            }
        } else if (coverageSubset != null && coverageSubset.getTemporalSubsetDouble() != null) {
            startTime = coverageSubset.getTemporalSubsetDouble().get(0);
            endTime = coverageSubset.getTemporalSubsetDouble().get(1);
        } else {
            // 获取整个Cube的时间
            startTime = collectionInfo.getExtent().getTemporal().getInterval().get(0).get(0);
            endTime = collectionInfo.getExtent().getTemporal().getInterval().get(0).get(1);
        }
        if (startTime == null) {
            startTime = "1978-01-01 07:00:00";
        }
        if (endTime == null) {
            endTime = "2200-01-01 07:00:00";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = sdf.parse(startTime);
        Date endDate = sdf.parse(endTime);
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        startCalendar.add(Calendar.SECOND, -1);
        startTime = sdf.format(startCalendar.getTime());
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        endCalendar.add(Calendar.SECOND, 1);
        endTime = sdf.format(endCalendar.getTime());
        queryParams.setTime(startTime, endTime);
        return queryParams;
    }

    /**
     * set the measurements from the properties param
     *
     * @param queryParams    query params
     * @param coverageSubset the coverage subset param, including the properties
     * @param collectionInfo the cube information
     * @return QueryParams the modified query params
     */
    public QueryParams setMeasurements(QueryParams queryParams, CoverageSubset coverageSubset,
                                       CollectionInfo collectionInfo) {
        // 波段参数 measurementNames
        if (coverageSubset != null && coverageSubset.getProperties() != null) {
            String[] measurementNames = coverageSubset.getPropertyList();
            queryParams.setMeasurements(measurementNames);
        } else {
            List<FieldInfo> fieldInfoList = collectionInfo.getRangeType().getFieldInfoList();
            String[] measurementNames = new String[fieldInfoList.size()];
            for (int i = 0; i < fieldInfoList.size(); i++) {
                measurementNames[i] = fieldInfoList.get(i).getName();
            }
            queryParams.setMeasurements(measurementNames);
        }
        return queryParams;
    }

    /**
     * Return the coverage with the certain format
     *
     * @param cubeName the name of the cube
     * @return image
     * @throws ParseException parse exception
     */
    @Override
    public String getCoverage(String cubeName, String bbox, String datetime, CoverageSubset coverageSubset, String f) throws ParseException {
        QueryParams queryParams = getQueryParams(cubeName, bbox, datetime, coverageSubset, f);
        return null;
    }

    @Override
    public Boolean getCoverageBySubmitSpark(String cubeName, String jobId, String bbox, String datetime, CoverageSubset coverageSubset, String outputDir, String f) throws ParseException {
        try {
            QueryParams queryParams = getQueryParams(cubeName, bbox, datetime, coverageSubset, f);
            queryParams.setPolygon(null);
            WorkflowCollectionParam workflowCollectionParam = new WorkflowCollectionParam();
            workflowCollectionParam.setQueryParams(queryParams);
            Integer[] scaleSize = coverageSubset.getScaleSizeList();
            if (scaleSize == null) {
                workflowCollectionParam.setScaleSize(null);
            } else {
                workflowCollectionParam.setScaleSize(Arrays.stream(scaleSize)
                        .mapToInt(Integer::intValue)
                        .toArray());
            }
            Double[] scaleAxes = coverageSubset.getScaleAxesList();
            if (scaleAxes == null) {
                workflowCollectionParam.setScaleAxes(null);
            } else {
                workflowCollectionParam.setScaleAxes(Arrays.stream(scaleAxes)
                        .mapToDouble(Double::doubleValue)
                        .toArray());
            }
            Double scaleFactor = coverageSubset.getScaleFactor();
            if (scaleFactor != null) {
                double scalaDouble = scaleFactor;
                scala.Option<Object> scalaOption = scala.Option.apply(BoxesRunTime.boxToDouble(scalaDouble));
                workflowCollectionParam.setScaleFactor(scalaOption);
            } else {
                workflowCollectionParam.setScaleFactor(Option.empty());
            }
            workflowCollectionParam.setImageFormat(f);
            String processName = "Coverage";
            redisUtil.saveKeyValue(processName + "_" + jobId + "_state", "STARTED,0%", 60 * 10);
//            sparkApplicationService.submitGetCoverage(sparkAppParas, jobId, outputDir, workflowCollectionParam.toJSONString());
            sparkApplicationService.submitGetCoverageByLivy(jobId, processName, outputDir, workflowCollectionParam.toJSONString());
            return pollJob(processName + "_" + jobId + "_state");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public QueryParams getQueryParams(String cubeName, String bbox, String datetime, CoverageSubset coverageSubset, String f) throws ParseException {
        CollectionInfo collectionInfo = getCollectionByCubeName(cubeName);
        Integer cubeId = gcCubeMapper.getCubeIdByName(cubeName);
        GcCube cube = gcCubeMapper.getCubeByName(cubeName);
        QueryParams queryParams = new QueryParams();
        queryParams.setCubeId(String.valueOf(cubeId));
        // check subset
        List<Subset> subsets = checkSubset(cube, coverageSubset);
        List<SubsetQuery> subsetQueryList = typeUtil.subsetList2SubsetQueryList(subsets);
        if (subsetQueryList != null) {
            queryParams.setSubsetQuery(typeUtil.subsetList2SubsetQueryList(subsets));
        }
        // set measurements
        queryParams = setMeasurements(queryParams, coverageSubset, collectionInfo);
        // set temporal extent
        queryParams = setTemporalExtent(queryParams, datetime, coverageSubset, collectionInfo);
        // set spatial extent
        queryParams = setSpatialExtent(queryParams, bbox, coverageSubset, collectionInfo);
        // 产品名称
        List<String> rasterProductNames = gcMeasurementProductView.getProductNames(cube.getMeasurementsProductViewName());
        if (rasterProductNames.size() == 1) {
            queryParams.setRasterProductName(rasterProductNames.get(0));
        } else {
            log.warn("productName不存在或者大于1");
            return null;
        }
        return queryParams;
    }

    @Override
    public Boolean executeWorkflowByCoverage(String workflowJSON, String jobId, String cubeName, String bbox, String datetime, CoverageSubset coverageSubset, String outputDir, String f) {
        try {
            QueryParams queryParams = getQueryParams(cubeName, bbox, datetime, coverageSubset, f);
            queryParams.setPolygon(null);
            WorkflowCollectionParam workflowCollectionParam = new WorkflowCollectionParam();
            workflowCollectionParam.setQueryParams(queryParams);
            Integer[] scaleSize = coverageSubset.getScaleSizeList();
            if (scaleSize == null) {
                workflowCollectionParam.setScaleSize(null);
            } else {
                workflowCollectionParam.setScaleSize(Arrays.stream(scaleSize)
                        .mapToInt(Integer::intValue)
                        .toArray());
            }
            Double[] scaleAxes = coverageSubset.getScaleAxesList();
            if (scaleAxes == null) {
                workflowCollectionParam.setScaleAxes(null);
            } else {
                workflowCollectionParam.setScaleAxes(Arrays.stream(scaleAxes)
                        .mapToDouble(Double::doubleValue)
                        .toArray());
            }
            Double scaleFactor = coverageSubset.getScaleFactor();
            if (scaleFactor != null) {
                double scalaDouble = scaleFactor;
                scala.Option<Object> scalaOption = scala.Option.apply(BoxesRunTime.boxToDouble(scalaDouble));
                workflowCollectionParam.setScaleFactor(scalaOption);
            } else {
                workflowCollectionParam.setScaleFactor(Option.empty());
            }
            workflowCollectionParam.setImageFormat(f);
            String processName = "WorkflowCollection";
            redisUtil.saveKeyValue(processName + "_" + jobId + "_state", "STARTED,0%", 60 * 10);
//            sparkApplicationService.submitGDCWorkflow(sparkAppParas, "WorkflowCollection", workflowJSON, jobId, outputDir, "true", workflowCollectionParam.toJSONString());
            sparkApplicationService.submitGDCWorkflowByLivy(jobId, "WorkflowCollection", workflowJSON, outputDir, "true", workflowCollectionParam.toJSONString());
            return pollJob(processName + "_" + jobId + "_state");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Poll the status of the job in redis, true: success, false: failed
     *
     * @param key the redis key for storaging the job status
     * @return Boolean
     */
    public Boolean pollJob(String key) throws InterruptedException {
        Timer timer = new Timer();
        final Boolean[] condition = {false, false};
        timer.scheduleAtFixedRate(new TimerTask() {
            int secondsPassed = 0;

            @Override
            public void run() {

                secondsPassed++;
                String jobStatus = redisUtil.getValueByKey(key);
                String statusRet = jobStatus.split(",")[0];
                log.info("status:" + statusRet);
                switch (statusRet) {
                    case "FINISHED":
                        condition[0] = true;
                        condition[1] = true;
                        timer.cancel();
                        break;
                    case "FAILED":
                        timer.cancel();
                        condition[1] = true;
                        break;
                    default:
                        break;
                }
                if (secondsPassed >= 120) { // 如果满足条件或超过2分钟，退出
                    log.info("Time out");
                    condition[1] = true;
                    timer.cancel();
                }
            }
        }, 0, 1000); // 以毫秒为单位，表示每秒执行一次
        while (!condition[1]) {
            Thread.sleep(1000); // Sleep for 100 milliseconds before checking again
        }
        Thread.sleep(1000);
        return condition[0];
    }


}
