package whu.edu.cn.entity.Ingest.view;

public class Measurement {
    private String measurementKey;
    private String measurementName;
    private String dType;
    private String unit;

    public Measurement(){}

    public String getMeasurementKey() {
        return measurementKey;
    }

    public void setMeasurementKey(String measurementKey) {
        this.measurementKey = measurementKey;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public String getdType() {
        return dType;
    }

    public void setdType(String dType) {
        this.dType = dType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
