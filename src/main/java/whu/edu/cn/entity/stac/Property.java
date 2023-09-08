package whu.edu.cn.entity.stac;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Property {
    private String title;
    private String description;
    private String type;
    private Integer multipleOf;
    private Object minimum;
    private Integer exclusiveMinimum;
    private Object maximum;
    private Integer exclusiveMaximum;
    @JsonProperty("enum")
    private List<Object> enums;

    private String pattern;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMultipleOf() {
        return multipleOf;
    }

    public void setMultipleOf(Integer multipleOf) {
        this.multipleOf = multipleOf;
    }

    public Object getMinimum() {
        return minimum;
    }

    public void setMinimum(Object minimum) {
        this.minimum = minimum;
    }

    public Integer getExclusiveMinimum() {
        return exclusiveMinimum;
    }

    public void setExclusiveMinimum(Integer exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    public Object getMaximum() {
        return maximum;
    }

    public void setMaximum(Object maximum) {
        this.maximum = maximum;
    }

    public Integer getExclusiveMaximum() {
        return exclusiveMaximum;
    }

    public void setExclusiveMaximum(Integer exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    public List<Object> getEnums() {
        return enums;
    }

    public void setEnums(List<Object> enums) {
        this.enums = enums;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
