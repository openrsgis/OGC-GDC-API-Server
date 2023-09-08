package whu.edu.cn.entity.Ingest;

import whu.edu.cn.entity.Ingest.conf.AdditionalDimensionConf;

public class GcDimension {
    private int id;
    private String dimensionName;
    private String dimensionType;
    private String dimensionTableName;
    private String memberType;
    private Double step;
    private String description;
    private String unit;
    private String dimensionTableColumnName;

    public GcDimension() {

    }

    public GcDimension(int id, String dimensionName, String dimensionType, String dimensionTableName, String memberType,
                       Double step, String description, String unit, String dimensionTableColumnName) {
        this.id = id;
        this.dimensionName = dimensionName;
        this.dimensionType = dimensionType;
        this.dimensionTableName = dimensionTableName;
        this.memberType = memberType;
        this.step = step;
        this.description = description;
        this.unit = unit;
        this.dimensionTableColumnName = dimensionTableColumnName;
    }

    public GcDimension(String dimensionName, String dimensionType, String dimensionTableName, String memberType,
                       Double step, String description, String unit, String dimensionTableColumnName) {
        this.dimensionName = dimensionName;
        this.dimensionType = dimensionType;
        this.dimensionTableName = dimensionTableName;
        this.memberType = memberType;
        this.step = step;
        this.description = description;
        this.unit = unit;
        this.dimensionTableColumnName = dimensionTableColumnName;
    }

    public GcDimension(int cubeId, String defaultType) {
        switch (defaultType) {
            case "extent":
                this.dimensionName = "extent";
                this.dimensionType = "measurable";
                this.dimensionTableName = "gc_extent_" + cubeId;
                this.memberType = "float";
                this.step = null;
                this.description = "extent dimension";
                this.unit = null;
                this.dimensionTableColumnName = "ALL";
                break;
            case "product":
                this.dimensionName = "product";
                this.dimensionType = "uncountable";
                this.dimensionTableName = "gc_product_" + cubeId;
                this.memberType = "string";
                this.step = null;
                this.description = "product dimension";
                this.unit = null;
                this.dimensionTableColumnName = "product_name";
                break;
            case "phenomenonTime":
                this.dimensionName = "phenomenonTime";
                this.dimensionType = "measurable";
                this.dimensionTableName = "gc_product_" + cubeId;
                this.memberType = "timestamp";
                this.step = null;
                this.description = "phenomenon time dimension";
                this.unit = null;
                this.dimensionTableColumnName = "phenomenon_time";
                break;
            case "measurement":
                this.dimensionName = "measurement";
                this.dimensionType = "uncountable";
                this.dimensionTableName = "gc_product_measurement_" + cubeId;
                this.memberType = "string";
                this.step = null;
                this.description = "product measurement dimension";
                this.unit = null;
                this.dimensionTableColumnName = "ALL";
                break;
        }
    }

    public GcDimension(int cubeId, AdditionalDimensionConf additionalDimensionConf) {
        this.dimensionName = additionalDimensionConf.getName();
        this.dimensionType = additionalDimensionConf.getType();
        this.dimensionTableName = "gc_product_" + cubeId;
        this.memberType = additionalDimensionConf.getDataType();
        this.step = additionalDimensionConf.getStep();
        this.description = additionalDimensionConf.getDescription();
        this.unit = additionalDimensionConf.getUnit();
        this.dimensionTableColumnName = additionalDimensionConf.getName();
    }


    public boolean isNumberDataType(){
        String memberType = this.memberType;
        return !memberType.contains("string") && !memberType.contains("date") &&
                !memberType.contains("time") && !memberType.contains("timestamp");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public String getDimensionType() {
        return dimensionType;
    }

    public void setDimensionType(String dimensionType) {
        this.dimensionType = dimensionType;
    }

    public String getDimensionTableName() {
        return dimensionTableName;
    }

    public void setDimensionTableName(String dimensionTableName) {
        this.dimensionTableName = dimensionTableName;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public Double getStep() {
        return step;
    }

    public void setStep(Double step) {
        this.step = step;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDimensionTableColumnName() {
        return dimensionTableColumnName;
    }

    public void setDimensionTableColumnName(String dimensionTableColumnName) {
        this.dimensionTableColumnName = dimensionTableColumnName;
    }
}
