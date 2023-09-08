package whu.edu.cn.entity.stac;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import whu.edu.cn.entity.process.Link;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Collections
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Collections {
    @JsonProperty("collections")
    @Valid
    private List<Collection> collections = new ArrayList<>();

    @JsonProperty("links")
    @Valid
    private List<Link> links = new ArrayList<>();

    public Collections(){}

    public Collections(List<Collection> collections, List<Link> links) {
        this.collections = collections;
        this.links = links;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
