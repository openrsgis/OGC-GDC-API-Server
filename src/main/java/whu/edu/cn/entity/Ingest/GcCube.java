package whu.edu.cn.entity.Ingest;

import java.util.ArrayList;
import java.util.List;

public class GcCube {
    private Integer id;
    private String cubeName;
    private String productTableName;
    private String productMeasurementTableName;
    private String extentTableName;
    private String factTableName;
    private String sensorLevelProductViewName;
    private String measurementsProductViewName;
    private String levelExtentViewName;
    private String hbaseTableName;
    private Integer levelKey;
    private Double cellSize;
    private Integer cellRes;
    private String startTime;
    private String endTime;
    private String geom;
    private Boolean pyramid;
    private String district;
    private Boolean insert;
    private String description;
    private Double leftBottomLongitude;
    private Double leftBottomLatitude;
    private Double rightTopLongitude;
    private Double rightTopLatitude;
    private Integer sliceMinX;
    private Integer sliceMinY;
    private Integer sliceMaxX;
    private Integer sliceMaxY;
    private String crs;
    private String dimensionTableName;


    public GcCube() {
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getCubeName() {
        return cubeName;
    }

    public void setCubeName(String cubeName) {
        this.cubeName = cubeName;
    }

    public String getProductTableName() {
        return productTableName;
    }

    public void setProductTableName(String productTableName) {
        this.productTableName = productTableName;
    }

    public String getProductMeasurementTableName() {
        return productMeasurementTableName;
    }

    public void setProductMeasurementTableName(String productMeasurementTableName) {
        this.productMeasurementTableName = productMeasurementTableName;
    }

    public String getExtentTableName() {
        return extentTableName;
    }

    public void setExtentTableName(String extentTableName) {
        this.extentTableName = extentTableName;
    }

    public String getFactTableName() {
        return factTableName;
    }

    public void setFactTableName(String factTableName) {
        this.factTableName = factTableName;
    }

    public String getSensorLevelProductViewName() {
        return sensorLevelProductViewName;
    }

    public void setSensorLevelProductViewName(String sensorLevelProductViewName) {
        this.sensorLevelProductViewName = sensorLevelProductViewName;
    }

    public String getMeasurementsProductViewName() {
        return measurementsProductViewName;
    }

    public void setMeasurementsProductViewName(String measuremtsProductViewName) {
        this.measurementsProductViewName = measuremtsProductViewName;
    }

    public String getLevelExtentViewName() {
        return levelExtentViewName;
    }

    public void setLevelExtentViewName(String levelExtentViewName) {
        this.levelExtentViewName = levelExtentViewName;
    }

    public String getHbaseTableName() {
        return hbaseTableName;
    }

    public void setHbaseTableName(String hbaseTableName) {
        this.hbaseTableName = hbaseTableName;
    }

    public Integer getLevelKey() {
        return levelKey;
    }

    public void setLevelKey(Integer levelKey) {
        this.levelKey = levelKey;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }


    public Boolean getPyramid() {
        return pyramid;
    }

    public void setPyramid(Boolean pyramid) {
        this.pyramid = pyramid;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Boolean getInsert() {
        return insert;
    }

    public void setInsert(Boolean insert) {
        this.insert = insert;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLeftBottomLongitude() {
        return leftBottomLongitude;
    }

    public void setLeftBottomLongitude(Double leftBottomLongitude) {
        this.leftBottomLongitude = leftBottomLongitude;
    }

    public Double getLeftBottomLatitude() {
        return leftBottomLatitude;
    }

    public void setLeftBottomLatitude(Double leftBottomLatitude) {
        this.leftBottomLatitude = leftBottomLatitude;
    }

    public Double getRightTopLongitude() {
        return rightTopLongitude;
    }

    public void setRightTopLongitude(Double rightTopLongitude) {
        this.rightTopLongitude = rightTopLongitude;
    }

    public Double getRightTopLatitude() {
        return rightTopLatitude;
    }

    public void setRightTopLatitude(Double rightTopLatitude) {
        this.rightTopLatitude = rightTopLatitude;
    }

    public Double getCellSize() {
        return cellSize;
    }

    public void setCellSize(Double cellSize) {
        this.cellSize = cellSize;
    }

    public Integer getCellRes() {
        return cellRes;
    }

    public void setCellRes(Integer cellRes) {
        this.cellRes = cellRes;
    }

    public Integer getSliceMinX() {
        return sliceMinX;
    }

    public void setSliceMinX(Integer sliceMinX) {
        this.sliceMinX = sliceMinX;
    }

    public Integer getSliceMinY() {
        return sliceMinY;
    }

    public void setSliceMinY(Integer sliceMinY) {
        this.sliceMinY = sliceMinY;
    }

    public Integer getSliceMaxX() {
        return sliceMaxX;
    }

    public void setSliceMaxX(Integer sliceMaxX) {
        this.sliceMaxX = sliceMaxX;
    }

    public Integer getSliceMaxY() {
        return sliceMaxY;
    }

    public void setSliceMaxY(Integer sliceMaxY) {
        this.sliceMaxY = sliceMaxY;
    }

    public String getDimensionTableName() {
        return dimensionTableName;
    }

    public void setDimensionTableName(String dimensionTableName) {
        this.dimensionTableName = dimensionTableName;
    }

    public List<Double> getActualImageExtent(){
        List<Double> extent = new ArrayList<>();
        extent.add(0, this.leftBottomLongitude);
        extent.add(1, this.leftBottomLatitude);
        extent.add(2, this.rightTopLongitude);
        extent.add(3, this.rightTopLatitude);
        return extent;
    }
}
