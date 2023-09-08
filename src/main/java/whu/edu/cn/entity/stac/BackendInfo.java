package whu.edu.cn.entity.stac;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import whu.edu.cn.entity.process.Link;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BackendInfo {

    private String id = "stac-api";

    private String type = "Catalog";

    @JsonProperty("gdc_version")
    private String gdcVersion = "1.0.0-beta";

    @JsonProperty("backend_version")
    private String backendVersion = "1.0.0";

    @JsonProperty("stac_version")
    private String stacVersion = "1.0.0";

    @JsonProperty("api_version")
    private String apiVersion;

    private String title = "The information of the backend for stac api";

    private String description = "The service follows the STAC API to provide a catalog service for geo data cube, which allows you to access Cube metadata information and query for asset data in the Cube";

    private List<String> conformsTo;

    private List<Endpoint> endpoints;

    private List<Link> links;

    public String getGdcVersion() {
        return gdcVersion;
    }

    public void setGdcVersion(String gdcVersion) {
        this.gdcVersion = gdcVersion;
    }

    public String getBackendVersion() {
        return backendVersion;
    }

    public void setBackendVersion(String backendVersion) {
        this.backendVersion = backendVersion;
    }

    public String getStacVersion() {
        return stacVersion;
    }

    public void setStacVersion(String stacVersion) {
        this.stacVersion = stacVersion;
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

    public List<String> getConformsTo() {
        return conformsTo;
    }

    public void setConformsTo(List<String> conformsTo) {
        this.conformsTo = conformsTo;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
