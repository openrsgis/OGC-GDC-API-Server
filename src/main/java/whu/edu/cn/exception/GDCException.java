package whu.edu.cn.exception;

import org.springframework.stereotype.Component;
import whu.edu.cn.util.RedisUtil;

import javax.annotation.Resource;

@Component
public class GDCException {
    private Boolean flag;
    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }
}
