package whu.edu.cn.entity.stac;

import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Dimension
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = DimensionSpatial.class, name = "spatial"),
  @JsonSubTypes.Type(value = DimensionTemporal.class, name = "temporal"),
  @JsonSubTypes.Type(value = DimensionBands.class, name = "bands"),
  @JsonSubTypes.Type(value = DimensionOther.class, name = "other"),
})

@Data
public class Dimension {
  /**
   * Type of the dimension.
   */
  public enum TypeEnum {
    SPATIAL("spatial"),
    
    TEMPORAL("temporal"),
    
    BANDS("bands"),
    
    OTHER("other");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("type")
  private TypeEnum type;

  @JsonProperty("description")
  private String description;

  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}

