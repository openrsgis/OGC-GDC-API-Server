package whu.edu.cn.service.impl;

import java.sql.Timestamp;

import org.apache.ibatis.session.RowBounds;
//import org.geotools.filter.text.cql2.CQL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcProduct;
import whu.edu.cn.entity.extent.Extent;
import whu.edu.cn.entity.extent.SpatialExtent;
import whu.edu.cn.entity.extent.TemporalExtent;
import whu.edu.cn.entity.feature.*;
import whu.edu.cn.entity.process.Link;
import whu.edu.cn.entity.Ingest.GcCube;
import whu.edu.cn.entity.Ingest.GcDimension;
import whu.edu.cn.entity.Ingest.view.Measurement;
import whu.edu.cn.entity.Ingest.view.GcMeasurementProduct;
import whu.edu.cn.entity.stac.*;
import whu.edu.cn.entity.stac.Collection;
import whu.edu.cn.entity.stac.Collections;
import whu.edu.cn.mapper.*;
import whu.edu.cn.service.ISTACService;
import whu.edu.cn.util.GeoUtil;
import whu.edu.cn.util.TimeUtil;


import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class STACServiceImpl implements ISTACService {

    @Resource
    private GcCubeMapper gcCubeMapper;

    @Resource
    private GcMeasurementProductView gcMeasurementProductView;

    @Resource
    private GcProductMapper gcProductMapper;

    @Resource
    private GeoUtil geoUtil;

    @Resource
    private TimeUtil timeUtil;

    @Resource
    private GcCubeStructureMapper postgresqlMapper;

    @Resource
    private GcDimensionMapper gcDimensionMapper;

    @Value("${address.stacApiUrl}")
    private String stacApiUrl;

    /**
     * Cube is transformed to CollectionInfo
     *
     * @return STAC collections
     */
    @Override
    public Collections getSTACollections() {
        List<GcCube> cubeList = gcCubeMapper.getCubeListLimit();
        List<Collection> collectionList = new ArrayList<>();
        for (GcCube cube : cubeList) {
            collectionList.add(cube2STACSimpleCollection(cube));
        }
        List<Link> linkList = new ArrayList<>();
        linkList.add(new Link(stacApiUrl + "/collections",
                "self", "application/json", "Get collections encoded in JSON"));
        return new Collections(collectionList, linkList);
    }

    @Override
    public Collection getSTACCollection(String collectionName) {
        GcCube cube = gcCubeMapper.getCubeByName(collectionName);
        return cube2STACCollection(cube);
    }

    @Override
    public Queryables getSTACQueryables(String collectionName) {
        GcCube cube = gcCubeMapper.getCubeByName(collectionName);
        return cube2Queryables(cube);
    }

    @Override
    public STACItems getSTACItems(String collectionName, String bbox, String filter, String datetime, int pageNum, int pageSize) {
        GcCube cube = gcCubeMapper.getCubeByName(collectionName);
        String WKT = null;
        Timestamp startTime = null;
        Timestamp endTime = null;
        if (bbox != null) {
            String[] bboxList = bbox.split(",");
            double minx = Double.parseDouble(bboxList[0]);
            double miny = Double.parseDouble(bboxList[1]);
            double maxx = Double.parseDouble(bboxList[2]);
            double maxy = Double.parseDouble(bboxList[3]);
            WKT = geoUtil.DoubleToWKT(minx, miny, maxx, maxy);
        }
        if (datetime != null) {
            TimeUtil.DateTimeResult dateTimeResult = timeUtil.parseDateTime(datetime);
            if (dateTimeResult.startTime != null) {
                startTime = timeUtil.convertStringToTimestamp(dateTimeResult.startTime);
            }
            if (dateTimeResult.endTime != null) {
                endTime = timeUtil.convertStringToTimestamp(dateTimeResult.endTime);
            }
        }

//        STACItems initialItems = cube2STACItems(cube, startTime, endTime, WKT, pageNum, pageSize);
        return cube2STACItems(cube, startTime, endTime, WKT, filter, pageNum, pageSize);
    }

    @Override
    public STACItem getSTACItem(String collectionName, String itemName) {
        GcCube cube = gcCubeMapper.getCubeByName(collectionName);
        return cube2STACItem(cube, itemName);
    }

    /**
     * select stac items with cql2 language
     *
     * @param features stac items
     * @param cql2     cql2 string
     * @return the selected stac items
     */
//    public Set<String> filterItems(FeaturesGeojson features, String cql2) {
//        try {
//            FeatureJSON featureJSON = new FeatureJSON();
//            String stacItemsJson = JSON.toJSONString(features);
//            SimpleFeatureCollection simpleFeatureCollection = (SimpleFeatureCollection) featureJSON.readFeatureCollection(stacItemsJson);
//            Filter cql2filter = CQL.toFilter(cql2);
//            SimpleFeatureCollection filteredCollection = simpleFeatureCollection.subCollection(cql2filter);
//            JSONArray filterArray = JSON.parseObject(featureJSON.toString(filteredCollection)).getJSONArray("features");
//            Set<String> idSet = new HashSet<>();
//            for (int i = 0; i < filterArray.size(); i++) {
//                JSONObject jsonObject = filterArray.getJSONObject(i);
//                String id = jsonObject.getString("id");
//                idSet.add(id);
//            }
//            return idSet;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    /**
     * get the simple collection, only contains the necessary keys
     *
     * @param cube Cube the instance of Cube
     * @return Collection
     */
    public Collection cube2STACSimpleCollection(GcCube cube) {
        String id = cube.getCubeName();
        String title = cube.getCubeName();
        String description = cube.getDescription();
        String license = "Apache-1.0";
        List<List<Double>> bbox = new ArrayList<>();
        bbox.add(Arrays.asList(cube.getLeftBottomLongitude(), cube.getLeftBottomLatitude(), cube.getRightTopLongitude(), cube.getRightTopLatitude()));
        String crsCode = cube.getCrs();
        SpatialExtent spatialExtent = new SpatialExtent(bbox, geoUtil.getCRSHref(crsCode));
        List<List<String>> interval = new ArrayList<>();
        TemporalExtent temporalExtent = null;
        if(!(cube.getStartTime() == null || cube.getStartTime().contains("1978-01-01"))){
            interval.add(Arrays.asList(cube.getStartTime(), cube.getEndTime()));
            temporalExtent = new TemporalExtent(interval);
        }
        Extent extent = new Extent(spatialExtent, temporalExtent);
        List<Link> links = new ArrayList<>();
        links.add(new Link(stacApiUrl + "/", "root", "application/json",
                "The landing page of this server as JSON"));
        links.add(new Link(stacApiUrl + "/collections/" + cube.getCubeName(),
                "self", "application/json", "Get a certain cube encoded in JSON"));
        links.add(new Link(stacApiUrl + "/collections/" + cube.getCubeName() + "/items",
                "items", "application/json", "Get stac items encoded in JSON"));
        links.add(new Link(stacApiUrl + "/collections/" + cube.getCubeName() + "/coverage",
                "http://www.opengis.net/def/rel/ogc/1.0/coverage", "image/tiff; application=geotiff", "Get coverage data in GeoTIFF"));
        links.add(new Link(stacApiUrl + "/collections/" + cube.getCubeName() + "/coverage?f=netcdf",
                "http://www.opengis.net/def/rel/ogc/1.0/coverage", "application/x-netcdf", "Get coverage data in NetCDF"));
        return new Collection(id, title, description, license, extent, null, null, links);
    }

    /**
     * get the STAC collection from the cube
     *
     * @param cube cube instance
     * @return Collection instance
     */
    public Collection cube2STACCollection(GcCube cube) {
        Collection simpleCollection = cube2STACSimpleCollection(cube);
        simpleCollection.setCubeDimensions(cube2Dimension(cube));
        simpleCollection.setStacExtensions(java.util.Collections.singletonList("https://stac-extensions.github.io/datacube/v2.2.0/schema.json"));
        // TODO 添加卫星的描述信息
        CollectionSummaries collectionSummaries = new CollectionSummaries();
        if (cube.getCubeName().equals("SENTINEL-2 Level-2A MSI")) {
            collectionSummaries.setConstellation(java.util.Collections.singletonList("Sentinel-2"));
            collectionSummaries.setInstruments(java.util.Collections.singletonList("MSI"));
            List<BandSummary> bandSummaries = new ArrayList<>();
            List<GcMeasurementProduct> measurementProducts = gcMeasurementProductView.getMeasurementProducts(cube.getMeasurementsProductViewName());
            for (GcMeasurementProduct measurementProduct : measurementProducts) {
                BandSummary bandSummary = new BandSummary();
                bandSummary.setName(measurementProduct.getMeasurementName());
                bandSummary.setGsd(10.0);
                bandSummaries.add(bandSummary);
            }
            collectionSummaries.setBands(bandSummaries);
        }
        collectionSummaries.setEpsg(java.util.Collections.singletonList(Integer.valueOf(cube.getCrs().replace("EPSG:", ""))));
        if(!(cube.getStartTime() == null || cube.getStartTime().contains("1978-01-01"))){
            CollectionSummaryStats datetime = new CollectionSummaryStats();
            datetime.setMinimum(timeUtil.convertTime2Standard(cube.getStartTime()));
            datetime.setMaximum(timeUtil.convertTime2Standard(cube.getEndTime()));
            collectionSummaries.setDatetime(datetime);
        }
        simpleCollection.setSummaries(collectionSummaries);
        return simpleCollection;
    }


    public Map<String, Dimension> cube2Dimension(GcCube cube) {
        Map<String, Dimension> dimensionMap = new HashMap<>();

        List<BigDecimal> xExtent = new ArrayList<>();
        xExtent.add(BigDecimal.valueOf(cube.getLeftBottomLongitude()));
        xExtent.add(BigDecimal.valueOf(cube.getRightTopLongitude()));
        DimensionSpatial xDimensionSpatial = new DimensionSpatial(DimensionSpatial.AxisEnum.X, xExtent, null, null, null, Integer.parseInt(cube.getCrs().replace("EPSG:", "")));
        dimensionMap.put("x", xDimensionSpatial);

        List<BigDecimal> yExtent = new ArrayList<>();
        yExtent.add(BigDecimal.valueOf(cube.getLeftBottomLatitude()));
        yExtent.add(BigDecimal.valueOf(cube.getRightTopLatitude()));
        DimensionSpatial yDimensionSpatial = new DimensionSpatial(DimensionSpatial.AxisEnum.Y, yExtent, null, null, null, Integer.parseInt(cube.getCrs().replace("EPSG:", "")));
        dimensionMap.put("y", yDimensionSpatial);

        if(!(cube.getStartTime() == null || cube.getStartTime().contains("1978-01-01"))){
            List<String> temporalExtent = new ArrayList<>();
            temporalExtent.add(cube.getStartTime());
            temporalExtent.add(cube.getEndTime());
            DimensionTemporal dimensionTemporal = new DimensionTemporal(null, temporalExtent, null);
            dimensionMap.put("t", dimensionTemporal);
        }
        List<GcMeasurementProduct> measurementProducts = gcMeasurementProductView.getMeasurementProducts(cube.getMeasurementsProductViewName());
        List<String> bands = new ArrayList<>();
        for (GcMeasurementProduct measurementProduct : measurementProducts) {
            bands.add(measurementProduct.getMeasurementName());
        }
        DimensionBands dimensionBands = new DimensionBands(bands);
        dimensionMap.put("bands", dimensionBands);

        String dimensionTableName = cube.getDimensionTableName();
        List<GcDimension> dimensionList;
        if (dimensionTableName != null) {
            dimensionList = gcDimensionMapper.selectAdditionalDimensions(dimensionTableName);
            for (GcDimension dimension : dimensionList) {
                dimensionMap.put(dimension.getDimensionName(), cube2DimensionOther(cube, dimension));
            }
        }
        return dimensionMap;
    }

    /**
     * Cube to queryables
     *
     * @param cube the cube instance
     * @return queryables
     */
    public Queryables cube2Queryables(GcCube cube) {
        Queryables queryables = new Queryables();
        queryables.setTitle(cube.getCubeName());
        queryables.set$id(stacApiUrl + "/collections/" + cube.getCubeName() + "/queryables");
        queryables.setType("object");
        Map<String, Property> propertyMap = getQueryablesProperties(cube);
        queryables.setProperties(propertyMap);
        queryables.setAdditionalProperties(false);
        return queryables;
    }

    /**
     * get the query properties from the cube
     *
     * @param cube the Cube
     * @return the Map<String, Property>
     */
    public Map<String, Property> getQueryablesProperties(GcCube cube) {
        Map<String, Property> propertyMap = new HashMap<>();
        if(!(cube.getStartTime() == null || cube.getStartTime().contains("1978-01-01"))){
            Property timeProperty = new Property();
            timeProperty.setType("string");
            timeProperty.setTitle("Phenomenon time");
            timeProperty.setMinimum(cube.getStartTime());
            timeProperty.setMaximum(cube.getEndTime());
            timeProperty.setPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$");
            propertyMap.put("phenomenon_time", timeProperty);
        }
        List<GcMeasurementProduct> measurementProducts = gcMeasurementProductView.getMeasurementProducts(cube.getMeasurementsProductViewName());
        Property bandProperty = new Property();
        bandProperty.setTitle("Measurement");
        bandProperty.setType("string");
        List<Object> bandList = new ArrayList<>();
        for (GcMeasurementProduct measurementProduct : measurementProducts) {
            bandList.add(measurementProduct.getMeasurementName());
        }
        bandProperty.setEnums(bandList);
        propertyMap.put("measurement_name", bandProperty);

        String dimensionTableName = cube.getDimensionTableName();
        List<GcDimension> dimensionList;
        if (dimensionTableName != null) {
            dimensionList = gcDimensionMapper.selectAdditionalDimensions(dimensionTableName);
            for (GcDimension dimension : dimensionList) {
                propertyMap.put(dimension.getDimensionName(), otherDimension2Property(cube, dimension));
            }
        }
        return propertyMap;
    }

    public Property otherDimension2Property(GcCube cube, GcDimension dimension) {
        Property property = new Property();
        String dimensionName = dimension.getDimensionName();
        List<Object> coordinates;
        Object lowerBound;
        Object upperBound;
        String memberType = dimension.getMemberType().toLowerCase();
        property.setTitle(dimensionName);
        if (memberType.equals("string")) {
            coordinates = gcProductMapper.getDimensionCoordinates(cube.getProductTableName(), dimensionName);
            property.setEnums(coordinates);
            property.setType("string");
        } else {
            lowerBound = gcProductMapper.getDimensionCoordinatesMin(cube.getProductTableName(), dimensionName);
            upperBound = gcProductMapper.getDimensionCoordinatesMax(cube.getProductTableName(), dimensionName);
            switch (memberType) {
                case "time":
                    property.setType("string");
                    property.setPattern("^\\d{4}-\\d{2}-\\d{2}$");
                    break;
                case "date":
                    property.setType("string");
                    property.setPattern("^\\d{2}:\\d{2}:\\d{2}$");
                    break;
                case "float":
                case "double":
                    property.setType("number");
                    break;
                case "integer":
                case "int":
                    property.setType("integer");
                    break;
                default:
                    return null;
            }
            property.setMaximum(upperBound);
            property.setMinimum(lowerBound);
        }
        return property;
    }

    /**
     * @param cube      Cube instance
     * @param dimension Dimension instance
     * @return DimensionOther instance
     */
    public DimensionOther cube2DimensionOther(GcCube cube, GcDimension dimension) {
        DimensionOther dimensionOther = new DimensionOther();
        String dimensionName = dimension.getDimensionName();
        List<Object> coordinates;
        Object lowerBound;
        Object upperBound;
        if (dimension.getStep() == null) {
            coordinates = gcProductMapper.getDimensionCoordinates(cube.getProductTableName(), dimensionName);
            dimensionOther.setValues(coordinates);
        } else {
            dimensionOther.setStep(dimension.getStep().toString());
            List<Object> extent = new ArrayList<>();
            lowerBound = gcProductMapper.getDimensionCoordinatesMin(cube.getProductTableName(), dimensionName);
            upperBound = gcProductMapper.getDimensionCoordinatesMax(cube.getProductTableName(), dimensionName);
            extent.add(lowerBound);
            extent.add(upperBound);
            dimensionOther.setExtent(extent);
        }
        dimensionOther.setUnit(dimension.getUnit());
        if (dimension.getStep() != null) {
            dimensionOther.setStep(dimension.getStep().toString());
        }
        return dimensionOther;
    }

    public STACItems cube2STACItems(GcCube cube, Timestamp startTime, Timestamp endTime, String WKT, String filter, Integer pageNum, Integer pageSize) {
        try {
            STACItems stacItems = new STACItems();
            String cql = null;
            if (filter != null) {
                cql = " AND " + filter;
            }
            List<Link> links = new ArrayList<>();
            Integer numberMatched = gcProductMapper.getProductsOfCubeCountWithCQL(cube.getId().toString(), cql, startTime, endTime, WKT);
            List<GcProduct> gcProducts;
            if (pageNum == null || pageSize == null) {
                RowBounds rowBounds = new RowBounds(0, numberMatched);
                gcProducts = gcProductMapper.getProductsOfCubeWithCQL(cube.getId().toString(), cql, startTime, endTime, WKT, rowBounds);
                stacItems.setNumberReturned(numberMatched);
                links.add(new Link(stacApiUrl + "/collections/" + cube.getCubeName() + "/items", "self", "application/json", "self"));
            } else {
                int offset = (pageNum - 1) * pageSize;
                RowBounds rowBounds = new RowBounds(offset, pageSize);
                gcProducts = gcProductMapper.getProductsOfCubeWithCQL(cube.getId().toString(), cql, startTime, endTime, WKT, rowBounds);
                int maxPages = (int) Math.ceil((double) numberMatched / pageSize);
                if ((pageNum + 1) <= maxPages) {
                    links.add(new Link(stacApiUrl + "/collections/" + cube.getCubeName() + "/items?limit=" + pageSize + "&pageNum=" + (pageNum + 1),
                            "next", "application/json", "the next page"));
                }
                if (pageNum != 1) {
                    links.add(new Link(stacApiUrl + "/collections/" + cube.getCubeName() + "/items?limit=" + pageSize + "&pageNum=" + (pageNum - 1),
                            "prev", "application/json", "the previous page"));
                }
                links.add(new Link(stacApiUrl + "/collections/" + cube.getCubeName() + "/items?limit=" + pageSize + "&pageNum=1",
                        "first", "application/json", "the first page"));
                links.add(new Link(stacApiUrl + "/collections/" + cube.getCubeName() + "/items?limit=" + pageSize + "&pageNum=" + maxPages,
                        "last", "application/json", "the last page"));
            }
            stacItems.setType("FeatureCollection");
            List<STACItem> stacItemList = new ArrayList<>();
            for (GcProduct gcProduct : gcProducts) {
                STACItem stacItem = cube2STACItem(cube, gcProduct.getProductIdentification().toString());
                stacItemList.add(stacItem);
            }
            stacItems.setFeaturesBySTACItems(stacItemList);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String timeStamp = sdf.format(new Date());
            stacItems.setTimeStamp(timeStamp);
            stacItems.setNumberMatched(numberMatched);
            stacItems.setLinks(links);
            return stacItems;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public STACItems cube2STACItems(Cube cube, Timestamp startTime, Timestamp endTime, String WKT, String filter, Integer pageNum, Integer pageSize) {
//        STACItems stacItems = new STACItems();
//        Integer numberMatched = productMapper.getProductsOfCubeCount(cube.getId().toString(), startTime, endTime, WKT);
//        List<GcProduct> gcProducts = productMapper.getProductsOfCube(cube.getId().toString(), startTime, endTime, WKT);
//        stacItems.setType("FeatureCollection");
//        List<FeatureGeojson> featureList = new ArrayList<>();
//        Map<String, STACItem> stacItemMap = new HashMap<>();
//        List<STACItem> stacItemList =  new ArrayList<>();
//        for (GcProduct gcProduct : gcProducts) {
//            STACItem stacItem = cube2STACItem(cube, gcProduct.getProductIdentification().toString());
//            FeatureGeojson feature = new FeatureGeojson();
//            feature.setId(gcProduct.getProductIdentification().toString());
//            feature.setGeometry(stacItem.getGeometry());
//            feature.setProperties(stacItem.getProperties());
//            stacItemMap.put(gcProduct.getProductIdentification().toString(), stacItem);
//            if(filter == null){
//                stacItemList.add(stacItem);
//            }
//            featureList.add(feature);
//        }
//        if (filter != null) {
//            FeaturesGeojson featuresGeojson = new FeaturesGeojson();
//            featuresGeojson.setFeatures(featureList);
//            Set<String> idSet = filterItems(featuresGeojson, filter);
//            idSet.forEach(element -> {
//                stacItemList.add(stacItemMap.get(element));
//            });
//            numberMatched = stacItemList.size();
//        }
//        stacItems.setFeaturesBySTACItems(stacItemList);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String timeStamp = sdf.format(new Date());
//        stacItems.setTimeStamp(timeStamp);
//        stacItems.setNumberMatched(numberMatched);
//        List<Link> links = new ArrayList<>();
//        if(pageNum == null || pageSize == null){
//            stacItems.setNumberReturned(numberMatched);
//            links.add(new Link("http://oge.whu.edu.cn/stac/collections/" + cube.getCubeName() + "/items", "self","application/json", "self"));
//        }else{
//            int maxPages = (int) Math.ceil((double) numberMatched / pageSize);
//            if ((pageNum + 1) <= maxPages) {
//                links.add(new Link("http://oge.whu.edu.cn/stac/collections/" + cube.getCubeName() + "/items?limit=" + pageSize + "&pageNum=" + (pageNum + 1),
//                        "next", "application/json", "the next page"));
//            }
//            if (pageNum != 1) {
//                links.add(new Link("http://oge.whu.edu.cn/stac/collections/" + cube.getCubeName() + "/items?limit=" + pageSize + "&pageNum=" + (pageNum - 1),
//                        "prev", "application/json", "the previous page"));
//            }
//            links.add(new Link("http://oge.whu.edu.cn/stac/collections/" + cube.getCubeName() + "/items?limit=" + pageSize + "&pageNum=1",
//                    "first", "application/json", "the first page"));
//            links.add(new Link("http://oge.whu.edu.cn/stac/collections/" + cube.getCubeName() + "/items?limit=" + pageSize + "&pageNum=" + maxPages,
//                    "last", "application/json", "the last page"));
//        }
//        stacItems.setLinks(links);
//        return stacItems;
//    }

    /**
     * @param cube
     * @param featureId
     * @return
     */
    public STACItem cube2STACItem(GcCube cube, String featureId) {
        GcProduct gcProduct = gcProductMapper.getProductByIdentification(cube.getId().toString(), featureId);
        GeoJsonGeometry geometry = gcProduct2Geometry(cube, gcProduct);
        STACItem stacItem = new STACItem();
        stacItem.setGeometry(geometry);
        stacItem.setType("Feature");
        stacItem.setProperties(gcProduct2Properties(cube, gcProduct));
        stacItem.setId(gcProduct.getProductIdentification().toString());
        List<BigDecimal> bbox = new ArrayList<>();
        bbox.add(gcProduct.getLowerLeftLat());
        bbox.add(gcProduct.getLowerLeftLong());
        bbox.add(gcProduct.getUpperRightLat());
        bbox.add(gcProduct.getUpperRightLong());
        stacItem.setBbox(bbox);
        Map<String, Asset> assets = getAssets(cube, gcProduct);
        stacItem.setAssets(assets);
        List<Link> links = new ArrayList<>();
        links.add(new Link(stacApiUrl + "/", "root", "application/json", "root"));
        links.add(new Link(stacApiUrl + "/collections/" + cube.getCubeName() + "/items/" + featureId, "self", "application/json", "self"));
        stacItem.setLinks(links);
        return stacItem;
    }

    /**
     * @param gcProduct
     * @return
     */
    public GeoJsonPolygon gcProduct2Geometry(GcCube cube, GcProduct gcProduct) {
        GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon();
        geoJsonPolygon.setType(GeoJsonGeometry.TypeEnum.POLYGON);
        List<List<List<BigDecimal>>> coordinates = new ArrayList<>();
        List<BigDecimal> point1 = new ArrayList<>();
        List<BigDecimal> point2 = new ArrayList<>();
        List<BigDecimal> point3 = new ArrayList<>();
        List<BigDecimal> point4 = new ArrayList<>();
        point1.add(gcProduct.getLowerLeftLong());
        point1.add(gcProduct.getLowerLeftLat());
        point2.add(gcProduct.getLowerLeftLong());
        point2.add(gcProduct.getUpperLeftLat());
        point3.add(gcProduct.getUpperRightLong());
        point3.add(gcProduct.getUpperRightLat());
        point4.add(gcProduct.getLowerRightLong());
        point4.add(gcProduct.getLowerRightLat());
        coordinates = Arrays.asList(Arrays.asList(point1, point2, point3, point4, point1));
        geoJsonPolygon.setCoordinates(coordinates);
        return geoJsonPolygon;
    }


    /**
     * @param gcProduct
     * @return
     */
    public Map<String, Object> gcProduct2Properties(GcCube cube, GcProduct gcProduct) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("start_datetime", gcProduct.getPhenomenonTime());
        properties.put("end_datetime", gcProduct.getPhenomenonTime());
        properties.put("tile", gcProduct.getProductName());
        properties.put("license", "Apache-1.0");
        properties.put("created", gcProduct.getCreateTime());
        properties.put("updated", gcProduct.getUpdateTime());
        properties.put("measurement_name", gcProduct.getMeasurementName());
        String dimensionTableName = cube.getDimensionTableName();
        Integer productKey = gcProduct.getProductKey();

        List<GcDimension> dimensionList;
        if (dimensionTableName != null) {
            dimensionList = gcDimensionMapper.selectAdditionalDimensions(dimensionTableName);
            for (GcDimension dimension : dimensionList) {
                properties.put(dimension.getDimensionName(), gcProductMapper.getDimensionValue(cube.getId().toString(), productKey, dimension.getDimensionName()));
            }
        }
        return properties;
    }

    /**
     * get asset
     *
     * @param cube      the cube instance
     * @param gcProduct the gcProduct instance
     * @return Map<String, Asset>
     */
    public Map<String, Asset> getAssets(GcCube cube, GcProduct gcProduct) {
        List<Measurement> measurements = gcMeasurementProductView.getMeasurements(cube.getId().toString(), gcProduct.getProductKey());
        Map<String, Asset> assets = new HashMap<>();
        for (Measurement measurement : measurements) {
            Asset asset = new Asset();
            asset.setType("image/tiff; application=geotiff");
            asset.setRoles(java.util.Collections.singletonList("data"));
            asset.setTitle(measurement.getMeasurementName());
            asset.setHref(stacApiUrl + "/collections/" + cube.getCubeName() + "/image/" + gcProduct.getProductIdentification());
            assets.put(measurement.getMeasurementName(), asset);
        }
        return assets;
    }


}
