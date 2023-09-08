package whu.edu.cn.entity.Ingest;

/**
 * @author czp
 * @version 1.0
 * @date 2020/5/13 18:07
 */
public class GcParam {
    private String cubeId;
    private Integer extentKey;
    private Integer resolutionKey;
    private String gridCode;
    private String cityCode;
    private String cityName;
    private String provinceName;
    private String districtName;
    private Integer tileQualityKey;
    private Double cloud;
    private Double cloudShadow;
    private Integer sensorKey;
    private String sensorName;
    private String platformName;
    private String measurementName;
    private String productName;
    private String productIdentification;
    private Integer productKey;
    private Integer measurementKey;
    private String dType;
    private String sensorModel;


    public String getCubeId() {
        return cubeId;
    }

    public void setCubeId(String cubeId) {
        this.cubeId = cubeId;
    }

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Integer getExtentKey() {
        return extentKey;
    }

    public void setExtentKey(Integer extentKey) {
        this.extentKey = extentKey;
    }

    public Integer getTileQualityKey() {
        return tileQualityKey;
    }

    public void setTileQualityKey(Integer tileQualityKey) {
        this.tileQualityKey = tileQualityKey;
    }

    public Double getCloud() {
        return cloud;
    }

    public void setCloud(Double cloud) {
        this.cloud = cloud;
    }

    public Double getCloudShadow() {
        return cloudShadow;
    }

    public void setCloudShadow(Double cloudShadow) {
        this.cloudShadow = cloudShadow;
    }

    public Integer getSensorKey() {
        return sensorKey;
    }

    public void setSensorKey(Integer sensorKey) {
        this.sensorKey = sensorKey;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public Integer getResolutionKey() {
        return resolutionKey;
    }

    public void setResolutionKey(Integer resolutionKey) {
        this.resolutionKey = resolutionKey;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductIdentification() {
        return productIdentification;
    }

    public void setProductIdentification(String productIdentification) {
        this.productIdentification = productIdentification;
    }

    public Integer getProductKey() {
        return productKey;
    }

    public void setProductKey(Integer productKey) {
        this.productKey = productKey;
    }

    public Integer getMeasurementKey() {
        return measurementKey;
    }

    public void setMeasurementKey(Integer measurementKey) {
        this.measurementKey = measurementKey;
    }

    public String getdType() {
        return dType;
    }

    public void setdType(String dType) {
        this.dType = dType;
    }

    public String getSensorModel() {
        return sensorModel;
    }

    public void setSensorModel(String sensorModel) {
        this.sensorModel = sensorModel;
    }
}
