package whu.edu.cn.entity.stac;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DimensionTemporal
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DimensionTemporal extends Dimension {
    @JsonProperty("values")
    @Valid
    private List<String> values = null;

    @JsonProperty("extent")
    @Valid
    private List<String> extent = new ArrayList<>();

    @JsonProperty("step")
    private String step = null;

    public DimensionTemporal(List<String> values, List<String> extent, String step) {
        this.values = values;
        this.extent = extent;
        this.step = step;
    }

    public DimensionTemporal() {
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public List<String> getExtent() {
        return extent;
    }

    public void setExtent(List<String> extent) {
        this.extent = extent;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

}

