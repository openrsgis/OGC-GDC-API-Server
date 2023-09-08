package whu.edu.cn.entity.stac;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import whu.edu.cn.entity.extent.Extent;
import whu.edu.cn.entity.process.Link;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Collection {

    public static final String TYPE = "Collection"; // STAC >=v1.0.0-rc.1

    @JsonProperty("stac_version")
    private String stacVersion = "1.0.0";

    @JsonProperty("stac_extensions")
    @Valid
    private List<String> stacExtensions;

    @JsonProperty("type")
    @Valid
    private String type = TYPE; // fallback for STAC<v1.0.0-rc.1

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("keywords")
    @Valid
    private List<String> keywords;

    @JsonProperty("version")
    private String version;

    @JsonProperty("deprecated")
    private Boolean deprecated;

    @JsonProperty("license")
    private String license;

    @JsonProperty("sci:citation")
    private String citation;

    @JsonProperty("providers")
    @Valid
    private List<Providers> providers;

    @JsonProperty("extent")
    private Extent extent;

    @JsonProperty("links")
    @Valid
    private List<Link> links;

    @JsonProperty("cube:dimensions")
    @Valid
    private Map<String, Dimension> cubeDimensions;

    @JsonProperty("summaries")
    @Valid
    private CollectionSummaries summaries;

    @JsonProperty("assets")
    @Valid
    private Map<String, Asset> assets;

    public Collection(){

    }

    public Collection(String id, String title, String description, String license, Extent extent,
                      List<String> keywords, List<Providers> providers, List<Link> links){
        this.id = id;
        this.title = title;
        this.description = description;
        this.license = license;
        this.extent = extent;
        this.keywords = keywords;
        this.providers = providers;
        this.links = links;
    }

    public static String getTYPE() {
        return TYPE;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public List<Providers> getProviders() {
        return providers;
    }

    public void setProviders(List<Providers> providers) {
        this.providers = providers;
    }

    public Extent getExtent() {
        return extent;
    }

    public void setExtent(Extent extent) {
        this.extent = extent;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Map<String, Dimension> getCubeDimensions() {
        return cubeDimensions;
    }

    public void setCubeDimensions(Map<String, Dimension> cubeDimensions) {
        this.cubeDimensions = cubeDimensions;
    }

    public CollectionSummaries getSummaries() {
        return summaries;
    }

    public void setSummaries(CollectionSummaries summaries) {
        this.summaries = summaries;
    }

    public Map<String, Asset> getAssets() {
        return assets;
    }

    public void setAssets(Map<String, Asset> assets) {
        this.assets = assets;
    }
}
