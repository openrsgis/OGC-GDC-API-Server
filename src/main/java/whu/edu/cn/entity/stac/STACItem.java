package whu.edu.cn.entity.stac;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import whu.edu.cn.entity.feature.Feature;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class STACItem extends Feature {

    @JsonProperty("stac_version")
    private String stacVersion = "1.0.0";

    @JsonProperty("stac_extensions")
    private List<String> stacExtensions;

    @JsonProperty("assets")
    private Map<String, Asset> assets;

    @JsonProperty("bbox")
    private List<BigDecimal> bbox;

    public String getStacVersion() {
        return stacVersion;
    }

    public void setStacVersion(String stacVersion) {
        this.stacVersion = stacVersion;
    }

    public List<String> getStacExtensions() {
        return stacExtensions;
    }

    public void setStacExtensions(List<String> stacExtensions) {
        this.stacExtensions = stacExtensions;
    }

    public Map<String, Asset> getAssets() {
        return assets;
    }

    public void setAssets(Map<String, Asset> assets) {
        this.assets = assets;
    }


    public List<BigDecimal> getBbox() {
        return bbox;
    }

    public void setBbox(List<BigDecimal> bbox) {
        this.bbox = bbox;
    }
}
