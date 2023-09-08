package whu.edu.cn.entity.coverage;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AxisInfo {
    private String type;
    private String axisLabel;
    private Object lowerBound;
    private Object upperBound;
    private Object resolution;
    private String uomLabel;
    private List<Object> coordinate;

    public AxisInfo(String type, String axisLabel, Object lowerBound, Object upperBound) {
        this.type = type;
        this.axisLabel = axisLabel;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public AxisInfo(String type, String axisLabel, Object lowerBound, Object upperBound, Object resolution) {
        this.type = type;
        this.axisLabel = axisLabel;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.resolution = resolution;
    }

    public AxisInfo(String type, String axisLabel, Object lowerBound, Object upperBound, Object resolution, String uomLabel) {
        this.type = type;
        this.axisLabel = axisLabel;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.resolution = resolution;
        this.uomLabel = uomLabel;
    }


    public AxisInfo(String type, String axisLabel, Object lowerBound, Object upperBound, Object resolution, String uomLabel, List<Object> coordinate) {
        this.type = type;
        this.axisLabel = axisLabel;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.resolution = resolution;
        this.uomLabel = uomLabel;
        this.coordinate = coordinate;
    }

    public Object getResolution() {
        return resolution;
    }

    public void setResolution(Object resolution) {
        this.resolution = resolution;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAxisLabel() {
        return axisLabel;
    }

    public void setAxisLabel(String axisLabel) {
        this.axisLabel = axisLabel;
    }

    public Object getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(Object lowerBound) {
        this.lowerBound = lowerBound;
    }

    public Object getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(Object upperBound) {
        this.upperBound = upperBound;
    }

    public String getUomLabel() {
        return uomLabel;
    }

    public void setUomLabel(String uomLabel) {
        this.uomLabel = uomLabel;
    }

    public List<Object> getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(List<Object> coordinate) {
        this.coordinate = coordinate;
    }
}
