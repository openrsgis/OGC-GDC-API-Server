package whu.edu.cn.entity.extent;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Extent {
    private SpatialExtent spatial;
    private TemporalExtent temporal;

    public Extent() {
    }

    public Extent(SpatialExtent spatial, TemporalExtent temporal) {
        this.spatial = spatial;
        this.temporal = temporal;
    }

    public SpatialExtent getSpatial() {
        return spatial;
    }

    public void setSpatial(SpatialExtent spatial) {
        this.spatial = spatial;
    }

    public TemporalExtent getTemporal() {
        return temporal;
    }

    public void setTemporal(TemporalExtent temporal) {
        this.temporal = temporal;
    }
}
