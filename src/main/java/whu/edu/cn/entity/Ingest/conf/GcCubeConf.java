package whu.edu.cn.entity.Ingest.conf;

import lombok.extern.slf4j.Slf4j;
import whu.edu.cn.entity.Ingest.GcCube;

import java.util.List;

@Slf4j
public class GcCubeConf {
    private String cubeName;
    private String startTime;
    private String endTime;
    private String district;
    private String description;
    private String pyramid;
    private List<Integer> cubeExtent;
    private Double cellSize;
    private Integer cellRes;
    private String crs;
    private List<AdditionalDimensionConf> additionalDimensions;
    private List<MeasurementConf> measurements;
    private List<ProductConf> products;

    public String generateProductColumnSQL() {
        String sql = " ";
        for (AdditionalDimensionConf additionalDimensionConf : this.additionalDimensions) {
            String type = getDatabaseValueType(additionalDimensionConf.getDataType());
            String name = additionalDimensionConf.getName();
            if (type != null) {
                sql = sql + name + " " + type + ",";
            } else {
                log.error("additionalDimension " + name + " type error");
            }
        }
        return sql;
    }

    public String getDatabaseValueType(String type) {
        type = type.toLowerCase();
        if (type.contains("float")) {
            return "float8";
        }
        if (type.contains("int")) {
            return "int8";
        }
        if (type.contains("string")) {
            return "varchar";
        }
        if(type.equals("date") || type.equals("time") || type.equals("timestamp")){
            return type;
        }
        if(type.equals("datetime")){
            return "timestamp";
        }
        return null;
    }

    public GcCube getCubeFromGcCubeConf() {
        GcCube cube = new GcCube();
        cube.setCubeName(this.cubeName);
        cube.setSliceMinX(this.cubeExtent.get(0));
        cube.setSliceMinY(this.cubeExtent.get(1));
        cube.setSliceMaxX(this.cubeExtent.get(2));
        cube.setSliceMaxY(this.cubeExtent.get(3));
        cube.setDescription(this.description);
        cube.setCellSize(this.cellSize);
        cube.setCellRes(this.cellRes);
        cube.setStartTime(this.startTime);
        cube.setEndTime(this.endTime);
        cube.setPyramid(!this.pyramid.equals("0"));
        cube.setDistrict(this.district);
        cube.setCrs(this.crs);
        return cube;
    }

    public String getCubeName() {
        return cubeName;
    }

    public void setCubeName(String cubeName) {
        this.cubeName = cubeName;
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

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPyramid() {
        return pyramid;
    }

    public void setPyramid(String pyramid) {
        this.pyramid = pyramid;
    }

    public List<Integer> getCubeExtent() {
        return cubeExtent;
    }

    public void setCubeExtent(List<Integer> cubeExtent) {
        this.cubeExtent = cubeExtent;
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

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public List<AdditionalDimensionConf> getAdditionalDimensions() {
        return additionalDimensions;
    }

    public void setAdditionalDimensions(List<AdditionalDimensionConf> additionalDimensions) {
        this.additionalDimensions = additionalDimensions;
    }

    public List<MeasurementConf> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<MeasurementConf> measurements) {
        this.measurements = measurements;
    }

    public List<ProductConf> getProducts() {
        return products;
    }

    public void setProducts(List<ProductConf> products) {
        this.products = products;
    }
}
