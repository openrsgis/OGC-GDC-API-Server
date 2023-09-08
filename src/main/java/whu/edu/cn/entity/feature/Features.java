package whu.edu.cn.entity.feature;

import com.fasterxml.jackson.annotation.JsonInclude;
import whu.edu.cn.entity.process.Link;
import whu.edu.cn.entity.stac.STACItem;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Features {
    public static final String TYPE = "FeatureCollection";
    private String type = TYPE;
    private List<Feature> features;
    private Integer numberMatched;
    private Integer numberReturned;
    private List<Link> links;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public void setFeaturesBySTACItems(List<STACItem> items) {
        this.features = new ArrayList<>(items);
    }

    public Integer getNumberMatched() {
        return numberMatched;
    }

    public void setNumberMatched(Integer numberMatched) {
        this.numberMatched = numberMatched;
    }

    public Integer getNumberReturned() {
        return numberReturned;
    }

    public void setNumberReturned(Integer numberReturned) {
        this.numberReturned = numberReturned;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
