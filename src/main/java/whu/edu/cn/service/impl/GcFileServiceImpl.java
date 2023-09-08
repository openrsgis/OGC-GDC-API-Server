package whu.edu.cn.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import whu.edu.cn.config.mybatis.MybatisPlusConfig;
import whu.edu.cn.config.spark.SparkAppParas;
import whu.edu.cn.entity.Ingest.*;

import whu.edu.cn.definition.ProductMeta;
import whu.edu.cn.entity.Ingest.conf.*;
import whu.edu.cn.entity.Ingest.GcCube;
import whu.edu.cn.entity.Ingest.GcDimension;
import whu.edu.cn.mapper.GcCubeMapper;
import whu.edu.cn.service.*;
import whu.edu.cn.util.*;
import com.alibaba.fastjson.JSON;
import geotrellis.vector.Extent;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.immutable.Map;
import whu.edu.cn.core.raster.ingest.Ingestor;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author wkx
 * @version 2.0
 * @date 2023/8/28 11:11
 */
@Service
@Slf4j
public class GcFileServiceImpl implements IGcFileService{
    @Resource
    private GcCubeMapper gcCubeMapper;
    @Resource
    private GeoUtil geoUtil;
    @Resource
    private IGcCubeStructureService gcCubeStructureService;
    @Resource
    private IGcExtentService gcExtentService;
    @Resource
    private IGcRasterTileFactService gcRasterTileFactService;
    @Resource
    private IGcProductService gcProductService;
    @Resource
    private IGcRasterTileFactService iGcRasterTileFactService;
    @Resource
    private IGcProductMeasurementService iGcProductMeasurementService;
    @Resource
    private ITilingFuncService tilingFuncService;
    @Resource
    private IGcCubeService cubeService;
    @Resource
    private GcQuery gcQuery;
    @Resource
    private GcShellUtil gcShellUtil;
    @Value("${table-prefix.raster_hbase}")
    private String rasterHbase;
    @Value("${sparkappparas.jarPath.maps.param-txt}")
    private String paramPath;
    @Value("${sh.createHbase}")
    private String createHbaseCommand;
    @Value("${sh.dropHbase}")
    private String dropHbaseCommand;
    @Value("${hdfs.master}")
    private String hdfsMaster;
    @Autowired
    SparkAppParas sparkAppParas;

    /**
     * Initializes the database for a specific cube, creating tables, views, and inserting data as needed.
     *
     * @param cubeId        the id of the cube
     * @param resolutionKey the resolution of the key
     * @param gridDimX      the number of divisions in the X-direction
     * @param gridDimY      the number of divisions in the Y-direction
     * @param minX          the minX of the cube
     * @param minY          the minY of the cube
     * @param maxX          the maxX of the cube
     * @param maxY          the maxY of the cube
     * @param pixNumX       the number of pixels in the x-direction of the grid
     * @param pixNumY       the number of pixels in the y-direction of the grid
     * @return Whether the initialization was successful
     */
    @Override
    public boolean initDatabase(String cubeId, Integer resolutionKey, Integer gridDimX, Integer gridDimY, Integer minX,
                                Integer minY, Integer maxX, Integer maxY, Integer pixNumX, Integer pixNumY, String productAdditionalSQL) {
        MybatisPlusConfig.cubeId.set(cubeId);

        // Create PostgreSQL data tables
        log.info("-------Create Product Table-----");
        try {
            gcCubeStructureService.createGcProductTable(cubeId, productAdditionalSQL);
            log.info("Create Product Table Success!");
        } catch (Exception e) {
            log.error("Failed to Create Product Table!");
            e.printStackTrace();
            return false;
        }

        log.info("-------Create ProductMeasurement Table-----");
        try {
            gcCubeStructureService.createGcProductMeasurementTable(cubeId);
            log.info("Create ProductMeasurement Success!");
        } catch (Exception e) {
            log.info("Failed to Create ProductMeasurement Table!");
            e.printStackTrace();
            return false;
        }
        log.info("-------Creating Extent Table-----");
        try {
            gcCubeStructureService.createGcExtentTable(cubeId);
            log.info("Extent Table created successfully!");
        } catch (Exception e) {
            log.info("Failed to create Extent Table!");
            e.printStackTrace();
            return false;
        }
        log.info("-------Creating Raster Table-----");
        try {
            gcCubeStructureService.createGcRasterTileFactTable(cubeId);
            log.info("Raster Table created successfully!");
        } catch (Exception e) {
            log.info("Failed to create Raster Table!");
            e.printStackTrace();
            return false;
        }
        // Insert data into the extent table
        log.info("-------Inserting data into the Extent table-----");
        try {
            insertExtent("custom", resolutionKey, gridDimX, gridDimY, minX, minY, maxX, maxY, pixNumX, pixNumY, cubeId);
            log.info("Data inserted into the extent table successfully!");
        } catch (Exception e) {
            log.info("Failed to insert data into the extent table!");
            e.printStackTrace();
            return false;
        }
        // Create LevelAndExtent view
        log.info("-------Creating LevelAndExtent view-----");
        try {
            gcCubeStructureService.createGcLevelAndExtentView(cubeId);
            log.info("LevelAndExtent view created successfully!");
        } catch (Exception e) {
            log.info("Failed to create LevelAndExtent view!");
            e.printStackTrace();
            return false;
        }
        // Create MeasurementsAndProduct view
        log.info("-------Creating MeasurementsAndProduct view-----");
        try {
            gcCubeStructureService.createGcMeasurementsAndProductView(cubeId);
            log.info("MeasurementsAndProduct view created successfully!");
        } catch (Exception e) {
            log.info("Failed to create MeasurementsAndProduct view!");
            e.printStackTrace();
            return false;
        }
        // Create SensorLevelAndProduct view
        log.info("-------Creating SensorLevelAndProduct view-----");
        try {
            gcCubeStructureService.createGcSensorLevelAndProductView(cubeId);
            log.info("SensorLevelAndProduct view created successfully!");
        } catch (Exception e) {
            log.info("Failed to create SensorLevelAndProduct view!");
            e.printStackTrace();
            return false;
        }
        // Create HBase data tables
        boolean createHbaseTable = createHbaseTable(cubeId);
        if (createHbaseTable) {
            log.info("HBase tables created successfully!");
            return true;
        } else {
            log.info("Failed to create HBase tables!");
            return false;
        }
    }

    /**
     * Initializes a dimension table for a specified cube by creating and populating necessary dimension records.
     *
     * @param cubeId             The id of the cube.
     * @param dimensionTableName The name of the dimension table to be initialized.
     * @param cubeConf           The configuration of the cube containing additional dimension information.
     * @return True if the initialization of the dimension table is successful, false otherwise.
     */
    @Override
    public boolean initDimensionTable(int cubeId, String dimensionTableName, GcCubeConf cubeConf) {
        try {
            gcCubeStructureService.createGCDimensionTable(dimensionTableName);
            GcDimension extentDimension = new GcDimension(cubeId, "extent");
            gcCubeStructureService.insertGCDimensionTable(dimensionTableName, extentDimension);
            GcDimension productDimension = new GcDimension(cubeId, "product");
            gcCubeStructureService.insertGCDimensionTable(dimensionTableName, productDimension);
            GcDimension timeDimension = new GcDimension(cubeId, "phenomenonTime");
            gcCubeStructureService.insertGCDimensionTable(dimensionTableName, timeDimension);
            GcDimension measurementDimension = new GcDimension(cubeId, "measurement");
            gcCubeStructureService.insertGCDimensionTable(dimensionTableName, measurementDimension);
            List<AdditionalDimensionConf> additionalDimensionConfList = cubeConf.getAdditionalDimensions();
            for (AdditionalDimensionConf additionalDimensionConf : additionalDimensionConfList) {
                GcDimension additionalDimension = new GcDimension(cubeId, additionalDimensionConf);
                gcCubeStructureService.insertGCDimensionTable(dimensionTableName, additionalDimension);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Create dimension or insert dimension record error");
            return false;
        }
    }

    /**
     * Creates an HBase table associated with a specific cube.
     *
     * @param cubeId The id of the cube for which the HBase table is to be dropped.
     * @return True if the HBase table drop operation was successful, false otherwise.
     */
    @Override
    public boolean createHbaseTable(String cubeId) {
        boolean create = false;
        String tableName = rasterHbase + cubeId;
        String command = createHbaseCommand + " " + tableName;
        Integer res = gcShellUtil.ExecCommand(command);
        if (res == 0) {
            create = true;
        }
        return create;
    }

    /**
     * Drops an HBase table associated with a specific cube.
     *
     * @param cubeId The id of the cube for which the HBase table is to be dropped.
     * @return True if the HBase table drop operation was successful, false otherwise.
     */
    @Override
    public boolean dropHbaseTable(String cubeId) {
        boolean create = false;
        String tableName = rasterHbase + cubeId;
        String command = dropHbaseCommand + " " + tableName;
        Integer res = gcShellUtil.ExecCommand(command);
        if (res == 0) {
            create = true;
        }
        return create;
    }

    /**
     * Reads raster files, partitions and processes the data, and inserts the data into the database and HBase.
     *
     * @param cubeId      The unique identifier of the cube.
     * @param splitsCount The number of splits for data partitioning.
     * @param threadSize  The size of the thread pool for processing.
     * @param productConf The configuration of the product containing datasets and measurements.
     * @return True if the reading, processing, and insertion are successful, false otherwise.
     */
    @Override
    public boolean readRasterFileAndPartition(String cubeId, Integer splitsCount, String threadSize, ProductConf productConf) {
        try {
//            GcToScalaUtil gcToScalaUtil = new GcToScalaUtil();
            TypeUtil typeUtil = new TypeUtil();
            long startTime = System.currentTimeMillis();
            MybatisPlusConfig.cubeId.set(cubeId);
            GcCube cube = cubeService.getCubeById(Integer.parseInt(cubeId));
            String hbaseTableName = rasterHbase + cubeId;
            List<ProductDataSetConf> productDataSetConfList = productConf.getDataSets();
            for (ProductDataSetConf productDataSetConf : productDataSetConfList) {
                List<MeasurementsFileConf> measurementsFileConfList = productDataSetConf.getMeasurements();
                String parentDir = productDataSetConf.getLocation();
                for (MeasurementsFileConf measurementsFileConf : measurementsFileConfList) {
                    String filePath = parentDir + measurementsFileConf.getPath();
                    File file = new File(filePath);
                    String hdfsFilePath = hdfsMaster + filePath;
                    String type = "custom";
                    int gridDimX = (int) ((cube.getSliceMaxX() - cube.getSliceMinX()) / cube.getCellSize());
                    int gridDimY = (int) ((cube.getSliceMaxY() - cube.getSliceMinY()) / cube.getCellSize());
                    if (cube.getPyramid()) type = "TMS";
                    ProductMeta productMeta = new ProductMeta();
                    productMeta.setProductName(productConf.getProductName());
                    productMeta.setProductType(productConf.getType());
                    productMeta.setSensor(productConf.getSensor());
                    productMeta.setCrs(productConf.getCrs());
                    productMeta.setCellType(measurementsFileConf.getDataType());
                    Instant timeInstant = Instant.parse(productDataSetConf.getPhenomenonTime());
                    productMeta.setPhenomenonTime(Timestamp.from(timeInstant));
                    productMeta.setMeasurement(measurementsFileConf.getName());
                    GcImageMetaData gcImageMetaData = readCustomRasterFile(hdfsFilePath, type, cube.getLevelKey(), cube.getCellRes(),
                            gridDimX, gridDimY, cube.getSliceMinX(), cube.getSliceMinY(), cube.getSliceMaxX(), cube.getSliceMaxY(), productMeta);
                    //获得最大productkey
                    Integer product_key = gcProductService.getMaxProductKey(cubeId);
                    //获得最大productid
                    Integer product_id = gcProductService.getMaxProductId(cubeId);
                    //log.info("product_key:"+product_key);
                    if (product_key == null) {
                        product_key = 0;
                    }
                    if (product_id == null) {
                        product_id = 0;
                    }
                    Integer factId = gcRasterTileFactService.getMaxRasterFactId();
                    if (factId == null) {
                        factId = 0;
                    }
                    Integer factKey;
                    List<GcRasterTileFact> gcRasterTileFacts = gcRasterTileFactService.getMaxFactKeyRasterFact();
                    if (gcRasterTileFacts.isEmpty()) {
                        factKey = 0;
                    } else {
                        factKey = gcRasterTileFacts.get(0).getFactKey();
                    }
                    Integer tileDataId = factKey;
                    //获得最大product_measurement_id
                    Integer product_measurement_id = iGcProductMeasurementService.getMaxProductMeasurementId();
                    if (product_measurement_id == null) {
                        product_measurement_id = 0;
                    }
                    //1.生成product导入列表
                    //2.生成product_measurement导入列表
                    //3.生成fact导入列表
                    List<GcProduct> gcProductList = new ArrayList<>();
                    List<GcRasterTileFact> gcRasterTileFactList = new ArrayList<>();
                    List<GcProductMeasurement> gcProductMeasurementList = new ArrayList<>();
                    HashMap<Integer, String[]> gridsHashMap = gcImageMetaData.getGridsHashMap();
                    //获得各个对应的key
                    //1.extent_key
                    HashMap<String, GcExtent> extentHash = getExtents(gridsHashMap, type, cubeId);//String表示resolutionKey_grid
                    HashMap<String, Integer> extentKeyHash = new HashMap<>();//String表示resolutionKey_grid
                    HashMap<String, GcTileExtent> tileExtentHash = new HashMap<>();//String表示resolutionKey_grid
                    for (String resolutionKey_grid : extentHash.keySet()) {
                        GcExtent gcExtent = extentHash.get(resolutionKey_grid);
                        extentKeyHash.put(resolutionKey_grid, gcExtent.getExtentKey());
                        String extentStr = String.valueOf(gcExtent.getExtent());
                        GcTileExtent gcTileExtent = JSON.parseObject(extentStr, GcTileExtent.class);
                        tileExtentHash.put(resolutionKey_grid, gcTileExtent);
                    }
                    String productName = gcImageMetaData.getProductName();
                    String fileName = file.getName();
                    String productIdentification = fileName.substring(0, fileName.lastIndexOf("."));
                    //2.tile_quality_key
                    //目前将瓦片对应的质量统一设置
                    //后期需要先获得云量数据
                    HashMap<String, Integer> tileQualityKeyHash = getTileQualityKeys(gridsHashMap);//String表示resolutionKey_grids
                    String measurement = gcImageMetaData.getMeasurement();
                    //3.measurementKey
                    //获得波段名称，进而获得measurementkey
                    GcParam gcParam = new GcParam();
                    gcParam.setMeasurementName(measurement);
                    List<GcMeasurement> measurementList = gcQuery.getMeasurement(gcParam);
                    Integer measurementKey = null;
                    if (!measurementList.isEmpty()) {
                        measurementKey = measurementList.get(0).getMeasurementKey();
                    }

                    String TLLong = gcImageMetaData.getTopLeftLongitude().toString();
                    String TLLat = gcImageMetaData.getTopLeftLatitude().toString();
                    String TRLong = gcImageMetaData.getTopRightLongitude().toString();
                    String TRLat = gcImageMetaData.getTopRightLatitude().toString();

                    String BLLong = gcImageMetaData.getBottomLeftLongitude().toString();
                    String BLLat = gcImageMetaData.getBottomLeftLatitude().toString();
                    String BRLong = gcImageMetaData.getBottomRightLongitude().toString();
                    String BRLat = gcImageMetaData.getBottomRightLatitude().toString();
                    //获得瓦片对应的真实数据范围
                    HashMap<String, String> gridTrueData = getTrueData(tileExtentHash,
                            TLLong, TLLat, TRLong, TRLat, BLLong, BLLat, BRLong, BRLat);//String对应resolutionKey_grid

                    String sensorName = gcImageMetaData.getSensor();
                    gcParam.setSensorName(sensorName);
                    List<GcSensor> sensors = gcQuery.getSensor(gcParam);
                    Integer sensorKey;
                    if (!sensors.isEmpty()) {
                        GcSensor gcSensor = sensors.get(0);
                        sensorKey = gcSensor.getSensorKey();
                    } else {
                        // FIXME 如果没有传感器key，则设置为null
                        sensorKey = null;
                    }

                    //需要level与productKey对应关系
                    HashMap<Integer, Integer> resolKeyProdKeyHashMap = new HashMap<>();
                    HashMap<String, String> fileReGrid_TileDataId = new HashMap<>();
                    HashMap<String, String> gcTileMetaDataHashMap = new HashMap<>();
                    for (Integer resolutionKey : gridsHashMap.keySet()) {
                        //需要判断product是否重复，重复则不插入，获得对应的product_key
                        GcParam gcParam1 = new GcParam();
                        gcParam1.setCubeId(cubeId);
                        gcParam1.setProductName(productName);
                        gcParam1.setProductIdentification(productIdentification);
                        gcParam1.setResolutionKey(resolutionKey);
                        List<GcProduct> gcProducts = gcQuery.getProduct(gcParam1);
                        if (gcProducts.size() == 0) {
                            product_key++;
                            product_id++;
                            GcProduct gcProduct = new GcProduct();
                            gcProduct.setId(product_id);
                            gcProduct.setProductName(productName);
                            gcProduct.setResolutionKey(resolutionKey);
                            resolKeyProdKeyHashMap.put(resolutionKey, product_key);
                            gcProduct.setPhenomenonTime(gcImageMetaData.getPhenomenonTime());
                            gcProduct.setResultTime(gcImageMetaData.getResultTime());
                            gcProduct.setProductKey(product_key);
                            gcProduct.setSensorKey(sensorKey);
                            gcProduct.setProductIdentification(productIdentification);
                            //增加product_type
                            gcProduct.setProductType("EO");
                            gcProduct.setUpperLeftLong(BigDecimal.valueOf(gcImageMetaData.getTopLeftLongitude()));
                            gcProduct.setUpperLeftLat(BigDecimal.valueOf(gcImageMetaData.getTopLeftLatitude()));
                            gcProduct.setUpperRightLong(BigDecimal.valueOf(gcImageMetaData.getTopRightLongitude()));
                            gcProduct.setUpperRightLat(BigDecimal.valueOf(gcImageMetaData.getTopRightLatitude()));
                            gcProduct.setUpperRightLat(BigDecimal.valueOf(gcImageMetaData.getTopRightLatitude()));
                            gcProduct.setLowerRightLong(BigDecimal.valueOf(gcImageMetaData.getBottomRightLongitude()));
                            gcProduct.setLowerRightLat(BigDecimal.valueOf(gcImageMetaData.getBottomRightLatitude()));
                            gcProduct.setLowerLeftLong(BigDecimal.valueOf(gcImageMetaData.getBottomLeftLongitude()));
                            gcProduct.setLowerLeftLat(BigDecimal.valueOf(gcImageMetaData.getBottomLeftLatitude()));
                            gcProduct.setAdditionalDimensionValueConfList(productDataSetConf.getAdditionalDimensions());
                            String wkt = geoUtil.imageToWKT(TLLong, TLLat, TRLong, TRLat, BRLong, BRLat, BLLong, BLLat);
                            String geom = "ST_GeomFromText('" + wkt + "', 4326)";
                            gcProduct.setGeom(geom);
                            gcProduct.setCrs("EPSG:4326");
                            gcProduct.setCreateBy("admin");
                            gcProduct.setCreateTime(new Timestamp(System.currentTimeMillis()));
                            gcProduct.setUpdateBy(null);
                            gcProduct.setUpdateTime(null);
                            gcProductList.add(gcProduct);
                        } else {
                            Integer temProductKey = gcProducts.get(0).getProductKey();
                            resolKeyProdKeyHashMap.put(resolutionKey, temProductKey);
                        }
                        //product_measurement表生成

                        Integer tempProductKey = resolKeyProdKeyHashMap.get(resolutionKey);
                        //判断product_measurement是否重复，重复则不插入
                        GcParam gcParam2 = new GcParam();
                        gcParam2.setProductKey(tempProductKey);
                        gcParam2.setMeasurementKey(measurementKey);
                        gcParam2.setdType(gcImageMetaData.getCellType());
                        List<GcProductMeasurement> gcProductMeasurements = gcQuery.getProductMeasurement(gcParam2);
                        if (gcProductMeasurements.size() == 0) {
                            //如果为0说明数据未导入过
                            product_measurement_id++;
                            GcProductMeasurement gcProductMeasurement = new GcProductMeasurement();
                            gcProductMeasurement.setId(product_measurement_id);
                            gcProductMeasurement.setMeasurementKey(measurementKey);
                            gcProductMeasurement.setProductKey(tempProductKey);
                            gcProductMeasurement.setDtype(gcImageMetaData.getCellType());
                            gcProductMeasurement.setCreateBy("admin");
                            gcProductMeasurement.setCreateTime(new Timestamp(System.currentTimeMillis()));
                            gcProductMeasurement.setUpdateBy(null);
                            gcProductMeasurement.setUpdateTime(null);
                            gcProductMeasurementList.add(gcProductMeasurement);
                            String[] grids = gridsHashMap.get(resolutionKey);
                            for (int i = 0; i < grids.length; i++) {
                                factId++;
                                factKey++;
                                tileDataId++;
                                String grid = grids[i];
                                String resolutionKey_grid = resolutionKey.toString() + "_" + grid;
                                Integer extentKey = extentKeyHash.get(resolutionKey_grid);
                                Integer tileQualityKey = tileQualityKeyHash.get(resolutionKey_grid);

                                GcRasterTileFact gcRasterTileFact = new GcRasterTileFact();
                                gcRasterTileFact.setId(factId);
                                gcRasterTileFact.setFactKey(factKey);
                                gcRasterTileFact.setProductKey(tempProductKey);
                                gcRasterTileFact.setMeasurementKey(measurementKey);
                                gcRasterTileFact.setExtentKey(extentKey);
                                gcRasterTileFact.setTileQualityKey(tileQualityKey);
                                String tileDataIdSalt = salt(tileDataId, splitsCount);
                                gcRasterTileFact.setTileDataId(tileDataIdSalt);
                                gcRasterTileFact.setCreateBy("admin");
                                gcRasterTileFact.setCreateTime(new Timestamp(System.currentTimeMillis()));
                                gcRasterTileFact.setUpdateBy(null);
                                gcRasterTileFact.setUpdateTime(null);
                                gcRasterTileFactList.add(gcRasterTileFact);
                                //获得file与grid对应的每个tileDataId

                                String fileReGrid = hdfsFilePath + "_" + resolutionKey_grid;
                                fileReGrid_TileDataId.put(fileReGrid, tileDataIdSalt);
                                //获得每个tileDataId对应的metaData
                                GcTileExtent gcTileExtent = tileExtentHash.get(resolutionKey_grid);
                                String trueDataWKT = gridTrueData.get(resolutionKey_grid);
                                String crs = "WGS84";
                                String cellType = gcImageMetaData.getCellType();
                                GcTileMetaData gcTileMetaData = new GcTileMetaData(tileDataIdSalt, grid, gcTileExtent, crs, measurement, gcTileExtent.getColumn(),
                                        gcTileExtent.getRow(), trueDataWKT, cellType);
                                String gcTileMetaDataStr = JSON.toJSONString(gcTileMetaData);
                                gcTileMetaDataHashMap.put(tileDataIdSalt, gcTileMetaDataStr);
                            }
                        } else {
                            log.info("数据重复");
                        }
                    }

                    //将数据导入postgresql
                    log.info("gcProductList.size()" + gcProductList.size());
                    log.info("gcRasterTileFactList.size()" + gcRasterTileFactList.size());
                    log.info("fileReGrid_TileDataId.size()" + fileReGrid_TileDataId.size());
                    for (String fileReGrid : fileReGrid_TileDataId.keySet()) {
                        log.info(fileReGrid + "\t:\t" + fileReGrid_TileDataId.get(fileReGrid));
                    }
                    log.info("gcTileMetaDataHashMap.size()" + gcTileMetaDataHashMap.size());
                    //将数据进行切分并导入hbase
                    //获得rowkey对应的tileMateData
                    //获得文件与grid对应的rowkeys
                    //参数gcTileMetaDataHashMap，bandGrid_TileDataId，filePath
                    String[] filePathsArray = {hdfsFilePath};
                    Map<String, String> tdiMetaData = typeUtil.JavaMapToScala(gcTileMetaDataHashMap);
                    Map<String, String> frgTileDataId = typeUtil.JavaMapToScala(fileReGrid_TileDataId);
                    Ingestor.writeGlobalDcTilingHbaseParams(paramPath, filePathsArray,
                            frgTileDataId, tdiMetaData, cube.getLevelKey(), cube.getCellRes());
                    boolean insertProduct = true;
                    if (gcProductList.size() != 0) {
//                    insertProduct = gcPostgresqlService.insertGcProductBatch(gcProductList);
                        insertProduct = gcCubeStructureService.insertGcProductBatchWithDimension(cubeId, gcProductList);
                    }
                    if (insertProduct) {
                        boolean insertFact = iGcRasterTileFactService.insertBatch(gcRasterTileFactList);
                        if (insertFact) {
                            boolean insertProductMeasurement = iGcProductMeasurementService.insertBatch(gcProductMeasurementList);
                            if (insertProductMeasurement) {
                                String appResource = sparkAppParas.getJarPath().get("maps.geocube-core");
                                tilingFuncService.submitTiling(appResource, type, threadSize, hbaseTableName, gridDimX, gridDimY,
                                        cube.getSliceMinX(), cube.getSliceMinY(), cube.getSliceMaxX(), cube.getSliceMaxY(), fileName);
                                // update the real extent of the image in cube table
                                if (cube.getLeftBottomLongitude() == null || cube.getLeftBottomLatitude() == null
                                        || cube.getRightTopLatitude() == null || cube.getRightTopLongitude() == null) {
                                    gcCubeMapper.updateCubeRealExtent(Integer.parseInt(cubeId), gcImageMetaData.getBottomLeftLongitude(),
                                            gcImageMetaData.getBottomLeftLatitude(), gcImageMetaData.getBottomRightLongitude(),
                                            gcImageMetaData.getBottomRightLatitude());
                                } else {
                                    List<Double> newExtent = geoUtil.compareExtentAndGetBigger(cube.getActualImageExtent(), gcImageMetaData.getExtent());
                                    if (!geoUtil.isSameWithinExtents(newExtent, cube.getActualImageExtent())) {
                                        gcCubeMapper.updateCubeRealExtent(Integer.parseInt(cubeId), newExtent.get(0),
                                                newExtent.get(1), newExtent.get(2), newExtent.get(3));
                                    }
                                }

                            } else {
                                log.error("error product_measurement插入失败！！！");
                                return false;
                            }
                        } else {
                            log.error("error raster_fact插入失败！！！");
                            return false;
                        }
                    } else {
                        log.error("error product插入失败！！！");
                        return false;
                    }
                    long endTime = System.currentTimeMillis();
                    log.info("插入耗时：" + (endTime - startTime) / 1000 + "秒");
                }
            }
            return true;
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return false;
        }
    }

    /**
     * Reads a custom raster file and creates a GcImageMetaData object with relevant metadata.
     *
     * @param rasterPath  Path to the raster file.
     * @param type        Type of raster data (custom or TMS).
     * @param resKey      Resolution key.
     * @param tileSize    Tile size.
     * @param gridDX      The number of divisions in the X-direction.
     * @param gridDY      The number of divisions in the Y-direction.
     * @param minX        Minimum X coordinate of the cube.
     * @param minY        Minimum Y coordinate of the cube.
     * @param maxX        Maximum X coordinate of the cube.
     * @param maxY        Maximum Y coordinate of the cube.
     * @param productMeta Product metadata.
     * @return GcImageMetaData object containing extracted metadata.
     */
    public GcImageMetaData readCustomRasterFile(String rasterPath, String type, Integer resKey, Integer tileSize,
                                                Integer gridDX, Integer gridDY, Integer minX, Integer minY, Integer maxX, Integer maxY, ProductMeta productMeta) {
        try {
            Extent extent = Ingestor.reprojectExtent(rasterPath);
            Double xmin = extent.xmin();
            Double ymin = extent.ymin();
            Double xmax = extent.xmax();
            Double ymax = extent.ymax();
            String wkt = geoUtil.DoubleToWKT(xmin, ymin, xmax, ymax);
            HashMap<Integer, String[]> gridsHashMap = readWKTToGridsHashMap(wkt, type, resKey, tileSize, gridDX, gridDY, minX, minY,
                    maxX, maxY);
            Timestamp phenomenonTime = productMeta.getPhenomenonTime();
            Timestamp resultTime = productMeta.getResultTime();
            String measurement = productMeta.getMeasurement();
            String cellType = productMeta.getCellType();
            String sensor = productMeta.getSensor();
            String productName = productMeta.getProductName();
            String dataType = productMeta.getDataType();
            return new GcImageMetaData(gridsHashMap, phenomenonTime, resultTime, measurement, ymax, xmin,
                    ymax, xmax, ymin, xmax, ymin, xmin, cellType, sensor, productName, dataType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Adds a "salting" prefix to the given tileDataId to distribute data evenly across multiple partitions.
     * The salting prefix is calculated by taking the hash code of the tileDataId and modding it by the
     * given splitsCount. The resulting salting code is then converted to a 3-digit string with leading
     * zeros if necessary, and concatenated with a tilde character and the tileDataId to form the final string.
     *
     * @param tileDataId  the ID of the tile data to salt
     * @param splitsCount the number of partitions to distribute the data across
     * @return the salted row key, which consists of a 3-digit salting code, a tilde character, and the
     * 10-digit tileDataId string with leading zeros if necessary
     */
    public String salt(Integer tileDataId, Integer splitsCount) {
        String rowKey = String.format("%010d", tileDataId);
        int saltingCode = rowKey.hashCode() % splitsCount;
        String saltingKey = "" + saltingCode;
        if (saltingCode < 10) {
            saltingKey = "00" + saltingKey;
        } else if (saltingCode < 100) {
            saltingKey = "0" + saltingKey;
        }
        rowKey = saltingKey + "~" + rowKey;
        return rowKey;
    }

    /**
     * Reads a WKT string and converts it to a hash map of grids.
     * Uses different methods to generate grid codes based on the given type and parameters.
     *
     * @param wkt      the WKT string to convert
     * @param type     the type of grid to generate ("custom" or "TMS")
     * @param resKey   the resolution key to use in the hash map
     * @param tileSize the size of the grid tiles
     * @param gridDX   the number of grids in the X direction
     * @param gridDY   the number of grids in the Y direction
     * @param minX     the minimum longitude of the cube
     * @param minY     the minimum latitude of the cube
     * @param maxX     the maximum longitude of the cube
     * @param maxY     the maximum latitude of the cube
     * @return a hash map of grid codes, where the keys are integers and the values are arrays of strings
     * <resolutionKey, grid codes>
     */
    public HashMap<Integer, String[]> readWKTToGridsHashMap(String wkt, String type, Integer resKey, Integer tileSize,
                                                            Integer gridDX, Integer gridDY, Integer minX, Integer minY, Integer maxX, Integer maxY) {
        HashMap<Integer, String[]> gridsHashMap = new HashMap<>();
        String[] grids;
        switch (type) {
            case "custom":
                grids = Ingestor.getGeoTrellisTilesCode(wkt, gridDX, gridDY, minX, minY,
                        maxX, maxY, tileSize, tileSize);
                gridsHashMap.put(resKey, grids);
                break;
            case "TMS":
                for (int zoo = 0; zoo < 10; zoo++) {
                    int gridDimX = (int) Math.pow(2, zoo + 1);
                    int gridDimY = (int) Math.pow(2, zoo);
                    grids = Ingestor.getTilesCode(wkt, gridDimX, gridDimY, -180, -90,
                            180, 90, 256, 256);
                    gridsHashMap.put(zoo, grids);
                }
                break;
            default:
                break;
        }
        return gridsHashMap;
    }


    /**
     * Reads a WKT string and converts it to a hash map of grids.
     * Uses different methods to generate grid codes based on the given type and parameters.
     *
     * @param tileExtentHash a hash map of tile extents, where the keys are strings and the values are GcTileExtent objects
     * @param TLLong         the longitude of the top-left corner of the image
     * @param TLLat          the latitude of the top-left corner of the image
     * @param TRLong         the longitude of the top-right corner of the image
     * @param TRLat          the latitude of the top-right corner of the image
     * @param BLLong         the longitude of the bottom-left corner of the image
     * @param BLLat          the latitude of the bottom-left corner of the image
     * @param BRLong         the longitude of the bottom-right corner of the image
     * @param BRLat          the latitude of the bottom-right corner of the image
     * @return a hash map of true data, where the keys are strings and the values are strings
     */
    public HashMap<String, String> getTrueData(HashMap<String, GcTileExtent> tileExtentHash,
                                               String TLLong, String TLLat, String TRLong, String TRLat,
                                               String BLLong, String BLLat, String BRLong, String BRLat) {
        WKTReader reader = new WKTReader();
        HashMap<String, String> trueData = new HashMap<>();
        String wkt = geoUtil.imageToWKT(TLLong, TLLat, TRLong, TRLat, BRLong, BRLat, BLLong, BLLat);
        log.info("dataWKT:" + wkt);
        try {
            Polygon dataPolygon = (Polygon) reader.read(wkt);
            //遍历瓦片
            for (String resolutionKey_grid : tileExtentHash.keySet()) {
                GcTileExtent gcTileExtent = tileExtentHash.get(resolutionKey_grid);
                Double BLLongD = gcTileExtent.getLeftBottomLong();
                Double BLLatD = gcTileExtent.getLeftBottomLat();
                Double URLongD = gcTileExtent.getRightUpperLong();
                Double URLatD = gcTileExtent.getRightUpperLat();
                String gridWKT = geoUtil.DoubleToWKT(BLLongD, BLLatD, URLongD, URLatD);
                Polygon gridPolygon = (Polygon) reader.read(gridWKT);
                Polygon intersectPolygon = (Polygon) gridPolygon.intersection(dataPolygon);
                String trueDataWKT = intersectPolygon.toText();
                trueData.put(resolutionKey_grid, trueDataWKT);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return trueData;
    }

    /**
     * Gets the extents of a set of grids based on a given hash map of grid codes.
     * Uses a GcQuery object to retrieve the extents from a database.
     *
     * @param gridsHashMap a hash map of grid codes, where the keys (resolutionKey) are integers and the values are arrays of strings
     * @param type         the type of grid to retrieve extents for
     * @param cubeId       the ID of the cube to retrieve extents for
     * @return a hash map of extents, where the keys are strings and the values are GcExtent objects
     */
    public HashMap<String, GcExtent> getExtents(HashMap<Integer, String[]> gridsHashMap, String type, String cubeId) {
        HashMap<String, GcExtent> extentHash = new HashMap<>();
        for (Integer resolutionKey : gridsHashMap.keySet()) {
            String[] grids = gridsHashMap.get(resolutionKey);
            for (String grid : grids) {
                GcParam gcParam = new GcParam();
                gcParam.setCubeId(cubeId);
                gcParam.setGridCode(grid);
                List<GcExtent> extents = gcQuery.getExtents(gcParam, type);
                if (extents.isEmpty()) {
                    log.info(grid + "网格编码不存在");
                } else {
                    GcExtent gcExtent = extents.get(0);
                    String resolutionKey_grid = resolutionKey.toString() + "_" + grid;
                    extentHash.put(resolutionKey_grid, gcExtent);
                }
            }
        }
        return extentHash;
    }

    /**
     * Gets the tile quality keys for a set of grids based on a given hash map of grid codes.
     * Creates a hash map of resolutionKeyGrid strings and integer values, where the keys represent
     * a combination of the resolution key and the grid code, and the values are all initialized to 0.
     *
     * @param gridsHashMap a hash map of grid codes, where the keys are integers and the values are arrays of strings
     * @return a hash map of tile quality keys, where the keys are strings and the values are integers
     */
    public HashMap<String, Integer> getTileQualityKeys(HashMap<Integer, String[]> gridsHashMap) {
        HashMap<String, Integer> tileQualityKeyHash = new HashMap<>();
        for (Integer resolutionKey : gridsHashMap.keySet()) {
            String[] grids = gridsHashMap.get(resolutionKey);
            for (String grid : grids) {
                String resolutionKeyGrid = resolutionKey.toString() + "_" + grid;
                tileQualityKeyHash.put(resolutionKeyGrid, 0);
            }
        }
        return tileQualityKeyHash;
    }


    /**
     * Inserts an extent into a database based on a given set of parameters.
     * Uses different methods to generate grid codes and extents based on the given type and parameters.
     *
     * @param type          the type of grid to generate extents for ("custom" or "TMS")
     * @param resolutionKey the resolution key to use for the extents
     * @param gridDimX      the X dimension of the grid
     * @param gridDimY      the Y dimension of the grid
     * @param minX          the minimum longitude
     * @param minY          the minimum latitude
     * @param maxX          the maximum longitude
     * @param maxY          the maximum latitude
     * @param pixNumX       the number of pixels in the X dimension
     * @param pixNumY       the number of pixels in the Y dimension
     * @param cubeId        the ID of the cube to insert the extent into
     * @return true if the extent was inserted successfully, false otherwise
     */
    public boolean insertExtent(String type, Integer resolutionKey,
                                Integer gridDimX, Integer gridDimY,
                                Integer minX, Integer minY,
                                Integer maxX, Integer maxY,
                                Integer pixNumX, Integer pixNumY,
                                String cubeId) {
        if (!type.equals("TMS")) {
            MybatisPlusConfig.cubeId.set(cubeId);
        }
        Integer maxExtentkey = gcExtentService.getMaxExtentKey();
        if (maxExtentkey == null) {
            maxExtentkey = 0;
        }
        Integer maxExtentId = gcExtentService.getMaxExtentId();
        if (maxExtentId == null) {
            maxExtentId = 0;
        }
        List<GcExtent> gcExtentList = new ArrayList<>();
        Tuple2<String, Tuple3<Object, Object, Extent>>[] grids;
        switch (type) {
            case "custom":
                grids = Ingestor.getAllGeoTrellisTilesCode(gridDimX, gridDimY, minX, minY,
                        maxX, maxY, pixNumX, pixNumY);
                for (Tuple2<String, Tuple3<Object, Object, Extent>> grid : grids) {
                    maxExtentkey++;
                    maxExtentId++;
                    String code = grid._1;
                    Integer column = Integer.parseInt(grid._2._1().toString());
                    Integer row = Integer.parseInt(grid._2._2().toString());
                    Extent extend = grid._2._3();
                    GcTileExtent gcTileExtent = new GcTileExtent(extend.xmin(), extend.ymin(), extend.xmax(), extend.ymax(), row, column);
                    String jsonExtent = JSON.toJSONString(gcTileExtent);
                    Date day = new Date();
                    GcExtent gcExtent = new GcExtent(maxExtentId, maxExtentkey, code, null, null, null, null, null, null,
                            jsonExtent, resolutionKey, "admin", day, null, null);
                    gcExtentList.add(gcExtent);
                }
                break;
            case "TMS":
                for (int zoom = 0; zoom < 10; zoom++) {
                    gridDimX = (int) Math.pow(2, zoom + 1);
                    gridDimY = (int) Math.pow(2, zoom);
                    grids = Ingestor.getAllTilesCode(gridDimX, gridDimY, -180, -90,
                            180, 90, 256, 256);
                    for (Tuple2<String, Tuple3<Object, Object, Extent>> grid : grids) {
                        maxExtentkey++;
                        maxExtentId++;
                        String code = grid._1;
                        Integer column = Integer.parseInt(grid._2._1().toString());
                        Integer row = Integer.parseInt(grid._2._2().toString());
                        Extent extend = grid._2._3();
                        GcTileExtent gcTileExtent = new GcTileExtent(extend.xmin(), extend.ymin(), extend.xmax(), extend.ymax(), row, column);
                        String jsonExtent = JSON.toJSONString(gcTileExtent);
                        Date day = new Date();
                        GcExtent gcExtent = new GcExtent(maxExtentId, maxExtentkey, code, null, null, null, null, null, null,
                                jsonExtent, zoom, "admin", day, null, null);
                        gcExtentList.add(gcExtent);
                    }
                }
                break;
            default:
                break;
        }
        return gcExtentService.insertBatch(gcExtentList);
    }

}
