package whu.edu.cn.entity.coverage;

import java.util.List;

public class Extent {
    private List<Double> spatial;
    private List<String> temporal;

    public List<Double> getSpatial() {
        return spatial;
    }

    public void setSpatial(List<Double> spatial) {
        this.spatial = spatial;
    }

    public List<String> getTemporal() {
        return temporal;
    }

    public void setTemporal(List<String> temporal) {
        this.temporal = temporal;
    }
}
