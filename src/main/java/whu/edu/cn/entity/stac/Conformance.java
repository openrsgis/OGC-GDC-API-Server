package whu.edu.cn.entity.stac;

import lombok.Data;

import java.util.List;

@Data
public class Conformance {
    private List<String> conformsTo;

    public List<String> getConformsTo() {
        return conformsTo;
    }

    public void setConformsTo(List<String> conformsTo) {
        this.conformsTo = conformsTo;
    }
}
