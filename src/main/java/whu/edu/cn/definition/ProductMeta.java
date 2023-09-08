package whu.edu.cn.definition;

import java.sql.Timestamp;

/**
 * @author czp
 * @version 1.0
 * @date 2020/8/12 16:40
 */
public class ProductMeta {
    private String productName;
    private String productType;
    private String sensor;
    private String crs;
    private String dataType;
    private Timestamp phenomenonTime;
    private Timestamp resultTime;
    private String measurement;
    private String cellType;


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Timestamp getPhenomenonTime() {
        return phenomenonTime;
    }

    public void setPhenomenonTime(Timestamp phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    public Timestamp getResultTime() {
        return resultTime;
    }

    public void setResultTime(Timestamp resultTime) {
        this.resultTime = resultTime;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }
}
