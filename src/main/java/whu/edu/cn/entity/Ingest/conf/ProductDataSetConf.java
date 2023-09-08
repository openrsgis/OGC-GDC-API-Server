package whu.edu.cn.entity.Ingest.conf;

import java.util.List;

public class ProductDataSetConf {
    private String location;
    private List<MeasurementsFileConf> measurements;
    private String phenomenonTime;
    private String format;
    private List<AdditionalDimensionValueConf> additionalDimensions;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getPhenomenonTime() {
        return phenomenonTime;
    }

    public void setPhenomenonTime(String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<MeasurementsFileConf> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<MeasurementsFileConf> measurements) {
        this.measurements = measurements;
    }

    public List<AdditionalDimensionValueConf> getAdditionalDimensions() {
        return additionalDimensions;
    }

    public void setAdditionalDimensions(List<AdditionalDimensionValueConf> additionalDimensions) {
        this.additionalDimensions = additionalDimensions;
    }
}
