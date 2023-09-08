package whu.edu.cn.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcDimension;
import whu.edu.cn.entity.Ingest.GcLevel;
import whu.edu.cn.entity.Ingest.GcProduct;
import whu.edu.cn.entity.Ingest.conf.AdditionalDimensionValueConf;
import whu.edu.cn.entity.Ingest.conf.GcCubeConf;
import whu.edu.cn.entity.Ingest.GcCube;
import whu.edu.cn.mapper.GcCubeStructureMapper;
import whu.edu.cn.mapper.GcDimensionMapper;
import whu.edu.cn.mapper.GcProductMapper;
import whu.edu.cn.service.IGcCubeService;
import whu.edu.cn.service.IGcCubeStructureService;
import whu.edu.cn.service.IGcLevelService;
import whu.edu.cn.service.IGcMeasurementService;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
public class GcCubeStructureImpl implements IGcCubeStructureService {

    @Resource
    private IGcCubeService cubeService;

    @Resource
    private IGcLevelService gcLevelService;

    @Resource
    private IGcMeasurementService gcMeasurementService;

    @Resource
    private GcCubeStructureMapper gcCubeStructureMapper;

    @Resource
    private GcProductMapper gcProductMapper;

    @Resource
    private GcDimensionMapper gcDimensionMapper;

    @Value("${table-prefix.dimension}")
    private String dimensionPrefix;

    /**
     * 创建Cube表以及事实星座模型的表格
     * @param gcCubeConf GcCubeConf实例
     * @return boolean, if successful return true, if error return false
     */
    @Override
    public boolean createCube(GcCubeConf gcCubeConf) {
        GcCube cube = gcCubeConf.getCubeFromGcCubeConf();
        List<GcLevel> gcLevels = gcLevelService.getExistGcLevel(gcCubeConf.getCellRes(), gcCubeConf.getCellSize(), gcCubeConf.getCrs());
        if (gcLevels.size() == 0) {
            GcLevel gcLevel = gcLevelService.getGcLevelByParam(gcCubeConf);
            if (gcLevelService.insertGcLevel(gcLevel)) {
                log.info("Insert gc_level table successful");
                cube.setLevelKey(gcLevel.getResolutionKey());
            }else{
                log.info("Insert gc_level table error");
                return false;
            }
        } else {
            GcLevel gcLevel = gcLevels.get(0);
            cube.setLevelKey(gcLevel.getResolutionKey());
        }
        if(!gcMeasurementService.checkMeasurementTables(gcCubeConf)){
            return false;
        }
        try{
            cubeService.insertCube(cube, "EO", gcCubeConf);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createGCDimensionTable(String gcDimensionTableName) {
        return gcCubeStructureMapper.createGcDimensionTable(gcDimensionTableName);
    }

    @Override
    public boolean createGcProductTable(String cubeId, String additionalColumns) {
        return gcCubeStructureMapper.createGcProductTable(cubeId, additionalColumns);
    }

    @Override
    public boolean createGcProductMeasurementTable(String cubeId) {
        return gcCubeStructureMapper.createGcProductMeasurementTable(cubeId);
    }

    @Override
    public boolean createGcExtentTable(String cubeId) {
        return gcCubeStructureMapper.createGcExtentTable(cubeId);
    }

    @Override
    public boolean createGcRasterTileFactTable(String cubeId) {
        return gcCubeStructureMapper.createGcRasterTileFactTable(cubeId);
    }

    @Override
    public boolean createGcLevelAndExtentView(String cubeId) {
        return gcCubeStructureMapper.createGcLevelAndExtentView(cubeId);
    }

    @Override
    public boolean createGcMeasurementsAndProductView(String cubeId) {
        return gcCubeStructureMapper.createGcMeasurementsAndProductView(cubeId);
    }

    @Override
    public boolean createGcSensorLevelAndProductView(String cubeId) {
        return gcCubeStructureMapper.createGcSensorLevelAndProductView(cubeId);
    }

    @Override
    public boolean insertGcProductBatchWithDimension(String cubeId, List<GcProduct> gcProductList) {
        String sql;
        long startTime = System.currentTimeMillis();
        String sqlHead = "INSERT INTO gc_product_" + cubeId + "(id,product_key, product_name, product_identification,  product_type, " +
                "sensor_key, resolution_key, crs, phenomenon_time, result_time, geom, upper_left_lat, " +
                "upper_left_long, upper_right_lat, upper_right_long, lower_left_lat, lower_left_long, " +
                "lower_right_lat, lower_right_long, create_by, create_time, update_by, update_time";
        GcProduct firstProduct = gcProductList.get(0);
        if (firstProduct.getAdditionalDimensionValueConfList() != null) {
            for (AdditionalDimensionValueConf additionalDimensionValueConf : firstProduct.getAdditionalDimensionValueConfList()) {
                String name = additionalDimensionValueConf.getName();
                sqlHead = sqlHead + ", " + name;
            }
        }
        sqlHead = sqlHead + ") values ";
        sql = sqlHead;
        System.out.println("=====product 开始插入共" + gcProductList.size() + "个数据=====");
        for (int i = 0; i < gcProductList.size(); i++) {
            GcProduct gcProduct = gcProductList.get(i);
            Integer maxProductId = gcProductMapper.getMaxProductId(cubeId);
            String id = maxProductId == null ? "1" : String.valueOf(maxProductId + 1);
            String product_name = gcProduct.getProductName() == null ? "null" : "'" + gcProduct.getProductName() + "'";
            String product_identification = gcProduct.getProductIdentification() == null ? "null" : "'" + gcProduct.getProductIdentification() + "'";
            String product_type = gcProduct.getProductType() == null ? "null" : "'" + gcProduct.getProductType() + "'";
            String sensor_key = gcProduct.getSensorKey() == null ? "null" : String.valueOf(gcProduct.getSensorKey());
            String resolution_key = gcProduct.getResolutionKey() == null ? "null" : String.valueOf(gcProduct.getResolutionKey());
            String crs = gcProduct.getCrs() == null ? "null" : "'" + gcProduct.getCrs() + "'";
            String phenomenon_time = "null";
            if (gcProduct.getPhenomenonTime() != null) {
                phenomenon_time = "'" + new Timestamp(gcProduct.getPhenomenonTime().getTime()) + "'";
            }
            String result_time = "null";
            String geom = gcProduct.getGeom() == null ? "null" : gcProduct.getGeom().toString();
            String upper_left_lat = gcProduct.getUpperLeftLat() == null ? "null" : String.valueOf(gcProduct.getUpperLeftLat().doubleValue());
            String upper_left_long = gcProduct.getUpperLeftLong() == null ? "null" : String.valueOf(gcProduct.getUpperLeftLong().doubleValue());
            String upper_right_lat = gcProduct.getUpperRightLat() == null ? "null" : String.valueOf(gcProduct.getUpperRightLat().doubleValue());
            String upper_right_long = gcProduct.getUpperRightLong() == null ? "null" : String.valueOf(gcProduct.getUpperRightLong().doubleValue());
            String lower_left_lat = gcProduct.getLowerLeftLat() == null ? "null" : String.valueOf(gcProduct.getLowerLeftLat().doubleValue());
            String lower_left_long = gcProduct.getLowerLeftLong() == null ? "null" : String.valueOf(gcProduct.getLowerLeftLong().doubleValue());
            String lower_right_lat = gcProduct.getLowerRightLat() == null ? "null" : String.valueOf(gcProduct.getLowerRightLat().doubleValue());
            String lower_right_long = gcProduct.getLowerRightLong() == null ? "null" : String.valueOf(gcProduct.getLowerRightLong().doubleValue());
            String create_by = gcProduct.getCreateBy() == null ? "null" : "'" + gcProduct.getCreateBy() + "'";
            String create_time = "null";
            if (gcProduct.getCreateTime() != null) {
                create_time = "'" + new Timestamp(gcProduct.getCreateTime().getTime()) + "'";
            }
            String update_by = gcProduct.getUpdateBy() == null ? "null" : "'" + gcProduct.getUpdateBy() + "'";
            String update_time_str = null;
            if (gcProduct.getUpdateTime() != null) {
                Timestamp update_time = new Timestamp(gcProduct.getUpdateTime().getTime());
                update_time_str = "'" + update_time + "'";
            }
            String sqlValue = "(" +
                    id + "," +
                    id + "," +
                    product_name + "," +
                    product_identification + "," +
                    product_type + "," +
                    sensor_key + "," +
                    resolution_key + "," +
                    crs + "," +
                    phenomenon_time + "," +
                    result_time + "," +
                    geom + "," +
                    upper_left_lat + "," +
                    upper_left_long + "," +
                    upper_right_lat + "," +
                    upper_right_long + "," +
                    lower_left_lat + "," +
                    lower_left_long + "," +
                    lower_right_lat + "," +
                    lower_right_long + "," +
                    create_by + "," +
                    create_time + "," +
                    update_by + "," +
                    update_time_str;
            if (gcProduct.getAdditionalDimensionValueConfList() != null) {
                for (AdditionalDimensionValueConf additionalDimensionValueConf : gcProduct.getAdditionalDimensionValueConfList()) {
                    String name = additionalDimensionValueConf.getName();
                    Object value = additionalDimensionValueConf.getValue();
                    sqlHead = sqlHead + ", " + name;
                    String dimensionTableName = dimensionPrefix + cubeId;
                    GcDimension dimension = gcDimensionMapper.selectDimension(dimensionTableName, name);
                    if (dimension.isNumberDataType()) {
                        sqlValue = sqlValue + ", " + value;
                    } else {
                        sqlValue = sqlValue + ", '" + value + "'";
                    }

                }
            }
            sql = sql + sqlValue + ")";
            if (i < gcProductList.size() - 1) {
                sql = sql + ",";
            }
            log.info("SQL语句是：" + sql);
        }
        boolean insert = gcCubeStructureMapper.insertGcProductBatchWithDimensions(sql);
        long endTime = System.currentTimeMillis();
        System.out.println("总共插入" + gcProductList.size() + "个耗时" + (endTime - startTime));
        return insert;
    }

    @Override
    public boolean insertGCDimensionTable(String gcDimensionTableName, GcDimension dimension) {
        return gcCubeStructureMapper.insertGcDimensionTable(gcDimensionTableName, dimension.getDimensionName(),
                dimension.getDimensionType(), dimension.getDimensionTableName(), dimension.getDimensionTableColumnName(),
                dimension.getMemberType(), dimension.getStep(), dimension.getDescription(), dimension.getUnit());
    }

}
