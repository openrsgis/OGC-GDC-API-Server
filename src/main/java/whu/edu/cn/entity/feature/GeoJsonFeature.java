package whu.edu.cn.entity.feature;

import java.util.Map;

public class GeoJsonFeature {
    private String id;
    public static final String TYPE = "Feature";
    private GeoJsonGeometry geometry;
    private Map<String, Object> properties;

    public static String getTYPE() {
        return TYPE;
    }

    public GeoJsonGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoJsonGeometry geometry) {
        this.geometry = geometry;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
