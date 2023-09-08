package whu.edu.cn.entity.stac;

import lombok.Data;

import java.util.List;

@Data
public class Endpoint {
    private String path;
    private List<String> methods;

    public Endpoint() {
    }

    public Endpoint(String path, List<String> methods) {
        this.path = path;
        this.methods = methods;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }
}
