package whu.edu.cn.entity.stac;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * By default, only ranges with a minimum and a maximum value can be specified. Ranges can be specified for ordinal values only, which means they need to have a rank order. Therefore, ranges can only be specified for numbers and some special types of strings. Examples: grades (A to F), dates or times. Implementors are free to add other derived statistical values to the object, for example &#x60;mean&#x60; or &#x60;stddev&#x60;.
 */
@Data
@ApiModel(description = "By default, only ranges with a minimum and a maximum value can be specified. Ranges can be specified for ordinal values only, which means they need to have a rank order. Therefore, ranges can only be specified for numbers and some special types of strings. Examples: grades (A to F), dates or times. Implementors are free to add other derived statistical values to the object, for example `mean` or `stddev`.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionSummaryStats {
  @JsonProperty("min")
  private Double min;

  @JsonProperty("max")
  private Double max;

  @JsonProperty("minimum")
  private String minimum;

  @JsonProperty("maximum")
  private String maximum;

  public Double getMin() {
    return min;
  }

  public void setMin(Double min) {
    this.min = min;
  }

  public Double getMax() {
    return max;
  }

  public void setMax(Double max) {
    this.max = max;
  }

  public String getMinimum() {
    return minimum;
  }

  public void setMinimum(String minimum) {
    this.minimum = minimum;
  }

  public String getMaximum() {
    return maximum;
  }

  public void setMaximum(String maximum) {
    this.maximum = maximum;
  }
}

