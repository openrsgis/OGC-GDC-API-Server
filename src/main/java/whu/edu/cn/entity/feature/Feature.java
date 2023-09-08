package whu.edu.cn.entity.feature;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import whu.edu.cn.entity.process.Link;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Feature extends GeoJsonFeature {

    private String type = TYPE;
    private List<Link> links;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static String getTYPE() {
        return TYPE;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
