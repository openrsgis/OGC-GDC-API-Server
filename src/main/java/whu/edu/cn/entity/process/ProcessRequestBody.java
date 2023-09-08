package whu.edu.cn.entity.process;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessRequestBody {
    /**
     * process name
     */
    private String process;

    private Map<String, Object> inputs;
    /**
     * 包含transmissionMode:value/reference; format:{mediaType: ""} 在description中是可以选择的；
     * 注意，如果在request中包含了outputs，那么ogc process API只支持返回outputs中所列出的输出名字的输出
     */
    private Map<String, Object> outputs;
    /**
     * enum: document/raw 要选择document，返回的是json格式
     */
    private String response;
    /**
     * 订阅者 暂时不考虑
     */
    private JSONObject subscriber;

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, Object> outputs) {
        this.outputs = outputs;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public JSONObject getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(JSONObject subscriber) {
        this.subscriber = subscriber;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    @Override
    public String toString() {
        return "ProcessRequestBody{" +
                "inputs=" + inputs +
                ", outputs=" + outputs +
                ", response='" + response + '\'' +
                ", subscriber=" + subscriber +
                '}';
    }
}
