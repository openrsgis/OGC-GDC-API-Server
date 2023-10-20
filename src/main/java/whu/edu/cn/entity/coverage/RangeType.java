package whu.edu.cn.entity.coverage;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RangeType {
    private String type;

    @JsonProperty("field")
    @JSONField(name = "field")
    private List<FieldInfo> fieldInfoList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FieldInfo> getFieldInfoList() {
        return fieldInfoList;
    }

    public void setFieldInfoList(List<FieldInfo> fieldInfoList) {
        this.fieldInfoList = fieldInfoList;
    }
}
