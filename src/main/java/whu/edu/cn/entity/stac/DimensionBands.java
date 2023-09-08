package whu.edu.cn.entity.stac;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DimensionBands
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DimensionBands extends Dimension {
    @JsonProperty("values")
    @Valid
    private List<String> values = new ArrayList<>();

    public DimensionBands() {
    }

    public DimensionBands(List<String> values) {
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public boolean containsValue(String value) {
        return values.contains(value);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DimensionBands dimensionBands = (DimensionBands) o;
        return Objects.equals(this.values, dimensionBands.values) &&
                super.equals(o);
    }
}

