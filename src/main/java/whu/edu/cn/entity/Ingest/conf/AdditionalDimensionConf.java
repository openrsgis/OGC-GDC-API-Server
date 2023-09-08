package whu.edu.cn.entity.Ingest.conf;

public class AdditionalDimensionConf {
    private String name;
    private String type;
    private String dataType;
    private String description;
    private Double step;
    private String unit;

    public String generateProductTableColumn(){
        return null;
    }
    public Double getStep() {
        return step;
    }

    public void setStep(Double step) {
        this.step = step;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
