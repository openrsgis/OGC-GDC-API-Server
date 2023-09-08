package whu.edu.cn.entity.Ingest.conf;

import java.util.List;

public class ProductConf {
    private String productName;
    private String type;
    private String sensor;
    private String crs;
    private List<ProductDataSetConf> dataSets;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ProductDataSetConf> getDataSets() {
        return dataSets;
    }

    public void setDataSets(List<ProductDataSetConf> dataSets) {
        this.dataSets = dataSets;
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
}
