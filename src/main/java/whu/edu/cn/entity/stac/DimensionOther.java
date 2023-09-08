package whu.edu.cn.entity.stac;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DimensionOther
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DimensionOther extends Dimension implements HasUnit {
    @JsonProperty("extent")
    @Valid
    private List<BigDecimal> extent = null;

    @JsonProperty("values")
    @Valid
    private List<String> values = null;

    @JsonProperty("step")
    private String step = null;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("reference_system")
    private String referenceSystem;

    public List<BigDecimal> getExtent() {
        return extent;
    }

    public List<String> getValues() {
        return values;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getReferenceSystem() {
        return referenceSystem;
    }

    public void setReferenceSystem(String referenceSystem) {
        this.referenceSystem = referenceSystem;
    }

    public void setValues(List<Object> values) {
        this.values = values.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public void setExtent(List<Object> extent) {
        this.extent = extent.stream()
                .map(obj -> {
                    if (obj instanceof Double) {
                        return BigDecimal.valueOf((Double) obj);
                    } else if (obj instanceof String) {
                        return new BigDecimal((String) obj);
                    } else if (obj instanceof Integer) {
                        return BigDecimal.valueOf((Integer) obj);
                    } else if (obj instanceof Float) {
                        return BigDecimal.valueOf((Float) obj);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

}

