package whu.edu.cn.entity.Ingest;

import whu.edu.cn.service.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author czp
 * @version 1.0
 * @date 2020/5/6 17:57
 */
@Component
public class GcQuery {

    @Resource
    private IGcExtentService extentService;

    @Resource
    private IGcMeasurementService gcMeasurementService;

    @Resource
    private IGcSensorService gcSensorService;

    @Resource
    private IGcProductService gcProductService;

    @Resource
    private IGcProductMeasurementService gcProductMeasurementService;

    @Resource
    private IGcTileQualityService gcTileQualityService;


    /***
     * 获得范围数据
     * @param gcParam
     * @return
     */
    public List<GcExtent> getExtents(GcParam gcParam, String type) {
//        System.out.println("调用getExtents");
        List<GcExtent> extents = new LinkedList<>();
        String combinationParam = "";
        Integer extentKey = gcParam.getExtentKey();
        if (extentKey != null) {
            String extentKeyParam = "extent_key=" + extentKey;
            combinationParam = combinationParam + " AND " + extentKeyParam;
        }
        Integer resolutionKey = gcParam.getResolutionKey();
        if (resolutionKey != null) {
            String resolutionKeyParam = "resolution_key=" + resolutionKey;
            combinationParam = combinationParam + " AND " + resolutionKeyParam;
        }
        String gridCode = gcParam.getGridCode();
        //System.out.println("gridCode"+gridCode);
        if (!gridCode.isEmpty()) {
            String gridCodeParam = "grid_code='" + gridCode + "'";
            combinationParam = combinationParam + " AND " + gridCodeParam;
            //System.out.println("combinationParam："+combinationParam);
        }
        String cityCode = gcParam.getCityCode();
//        System.out.println("cityCode"+cityCode);
        if (cityCode != null) {
            String cityCodeParam = "city_code='" + cityCode + "'";
            combinationParam = combinationParam + " AND " + cityCodeParam;
            //System.out.println("combinationParam："+combinationParam);
        }
        String cityName = gcParam.getCityName();
        if (cityName != null) {
            String cityNameParam = "city_name LIKE '%" + cityName + "%'";
            combinationParam = combinationParam + " AND " + cityNameParam;
        }
        String provinceName = gcParam.getProvinceName();
        if (provinceName != null) {
            String provinceNameParam = "province_name LIKE '%" + provinceName + "%'";
            combinationParam = combinationParam + " AND " + provinceNameParam;
        }
        String districtName = gcParam.getDistrictName();
        if (districtName != null) {
            String districtNameParam = "district_name LIKE '%" + districtName + "%'";
            combinationParam = combinationParam + " AND " + districtNameParam;
        }

//        System.out.println(combinationParam);
        if (("TMS").equals(type)) {
//            System.out.println("it is tms");
            extents = extentService.getTMSExtentFromCombinedParam(combinationParam);
        } else {
//            System.out.println("it is custom");
            extents = extentService.getExtentFromCombinedParam(combinationParam, gcParam.getCubeId());
        }

        return extents;
    }

    public List<GcTileQuality> getTileQualitys(GcParam gcParam) {
        List<GcTileQuality> tileQualities = new LinkedList<>();
        String combinationParam = "";
        Integer tileQualityKey = gcParam.getTileQualityKey();
        if (tileQualityKey != null) {
            String tileQualityKeyParam = "tile_quality_key=" + tileQualityKey;
            combinationParam = combinationParam + " AND " + tileQualityKeyParam;
        }
        Double cloud = gcParam.getCloud();
        if (cloud != null) {
            String cloudParam = "cloud <" + cloud;
            combinationParam = combinationParam + " AND " + cloudParam;
        }
        Double cloudShadow = gcParam.getCloudShadow();
        if (cloudShadow != null) {
            String cloudShadowParam = "cloudshadow <" + cloudShadow;
            combinationParam = combinationParam + " AND " + cloudShadowParam;
        }
        tileQualities = gcTileQualityService.getTileQualityFromCombinedParam(combinationParam);
        return tileQualities;
    }

    public List<GcSensor> getSensor(GcParam gcParam) {
        List<GcSensor> sensors = new LinkedList<>();
        String combinationParam = "";
        Integer sensorKey = gcParam.getSensorKey();
        if (sensorKey != null) {
            String sensorKeyParam = "sensor_key =" + sensorKey;
            combinationParam = combinationParam + " AND " + sensorKeyParam;
        }
        String sensorName = gcParam.getSensorName();
        if (sensorName != null) {
            String sensorNameParam = "sensor_name ='" + sensorName + "'";
            combinationParam = combinationParam + " AND " + sensorNameParam;
        }
        String platformName = gcParam.getPlatformName();
        if (platformName != null) {
            String platformNameParam = "platform_name ='" + platformName + "'";
            combinationParam = combinationParam + " AND " + platformNameParam;
        }
        String sensorModel = gcParam.getSensorModel();
        System.out.println("sensorModel:" + sensorModel);
        if (sensorModel != null) {
            String sensorModelParam = "sensor_model ='" + sensorModel + "'";
            combinationParam = combinationParam + " AND " + sensorModelParam;
        }
        sensors = gcSensorService.getSensorFromCombinedParam(combinationParam);
        return sensors;
    }

    public List<GcMeasurement> getMeasurement(GcParam gcParam) {
        List<GcMeasurement> gcMeasurements = new ArrayList<>();
        String combinationParam = "";
        String measurementName = gcParam.getMeasurementName();
        if (measurementName != null) {
            String measurementNameParam = "measurement_name ='" + measurementName + "'";
            combinationParam = combinationParam + " AND " + measurementNameParam;
        }
        gcMeasurements = gcMeasurementService.getMeasurementFromCombinedParam(combinationParam);
        return gcMeasurements;
    }

    public List<GcProduct> getProduct(GcParam gcParam) {
        List<GcProduct> products = new LinkedList<>();
        String combinationParam = "";
        String productName = gcParam.getProductName();
        if (productName != null) {
            String productNameParam = "product_name ='" + productName + "'";
            combinationParam = combinationParam + " AND " + productNameParam;
        }
        String productIdentification = gcParam.getProductIdentification();
        if (productIdentification != null) {
            String productIdentificationParam = "product_identification ='" + productIdentification + "'";
            combinationParam = combinationParam + " AND " + productIdentificationParam;
        }
        Integer resolutionKey = gcParam.getResolutionKey();
        if (resolutionKey != null) {
            String resolutionKeyParam = "resolution_key =" + resolutionKey;
            combinationParam = combinationParam + " AND " + resolutionKeyParam;
        }
        products = gcProductService.getProductFromCombinedParam(combinationParam, gcParam.getCubeId());
        return products;
    }

    public List<GcProductMeasurement> getProductMeasurement(GcParam gcParam) {
        List<GcProductMeasurement> productMeasurements = new LinkedList<>();
        String combinationParam = "";
        Integer productKey = gcParam.getProductKey();
        if (productKey != null) {
            String productKeyParam = "product_key =" + productKey;
            combinationParam = combinationParam + " AND " + productKeyParam;
        }
        Integer measurementKey = gcParam.getMeasurementKey();
        if (measurementKey != null) {
            String measurementKeyParam = "measurement_key =" + measurementKey;
            combinationParam = combinationParam + " AND " + measurementKeyParam;
        }
        String dType = gcParam.getdType();
        if (dType != null) {
            String dTypeParam = "dtype ='" + dType + "'";
            combinationParam = combinationParam + " AND " + dTypeParam;
        }
        productMeasurements = gcProductMeasurementService.getProductMeasurementFromCombinedParam(combinationParam);
        return productMeasurements;
    }
}
