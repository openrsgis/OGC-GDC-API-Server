package whu.edu.cn.entity.extent;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemporalExtent {
    /**
     * example [["2011-11-11T11:11:11+00:00", null]]
     */
    List<List<String>> interval;

    public TemporalExtent(List<List<String>> interval) {
        this.interval = interval;
    }

    public List<List<String>> getInterval() {
        return interval;
    }

    public void setInterval(List<List<String>> interval) {
        this.interval = interval;
    }
}
