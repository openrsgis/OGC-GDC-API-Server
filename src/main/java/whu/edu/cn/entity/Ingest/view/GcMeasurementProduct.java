package whu.edu.cn.entity.Ingest.view;

public class GcMeasurementProduct {
    private Integer measurementKey;
    private String measurementName;
    private String dtype;
    private String productName;
    private String productKey;
    private String unit;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getMeasurementKey() {
        return measurementKey;
    }

    public void setMeasurementKey(Integer measurementKey) {
        this.measurementKey = measurementKey;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }
}
