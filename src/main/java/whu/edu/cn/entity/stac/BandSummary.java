package whu.edu.cn.entity.stac;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;


/**
 * A link to another resource on the web. Bases on [RFC 5899](https://tools.ietf.org/html/rfc5988).
 */
@Data
@ApiModel(description = "A link to another resource on the web. Bases on [RFC 5899](https://tools.ietf.org/html/rfc5988).")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BandSummary {
  @JsonProperty("name")
  private String name;

  @JsonProperty("common_name")
  private String commonName;

  @JsonProperty("description")
  private String description;

  @JsonProperty("center_wavelength")
  private Double centerWaveLength;

  @JsonProperty("gsd")
  private Double gsd;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCommonName() {
    return commonName;
  }

  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Double getCenterWaveLength() {
    return centerWaveLength;
  }

  public void setCenterWaveLength(Double centerWaveLength) {
    this.centerWaveLength = centerWaveLength;
  }

  public Double getGsd() {
    return gsd;
  }

  public void setGsd(Double gsd) {
    this.gsd = gsd;
  }
}

