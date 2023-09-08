package whu.edu.cn.entity.process;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessDesc {
    private String id; //name
    private String title;
    private String version;
    private String description;
    private List<String> keywords;
    private List<String> jobControlOptions;
    private List<String> outputTransmission;
    private Map<String, Input> inputs;
    private Map<String, Output> outputs;
    private List<Link> links;


    public ProcessDesc(String id, String title, String version,
                       List<String> jobControlOptions, List<String> outputTransmission, List<Link> links){
        this.id = id;
        this.title = title;
        this.version = version;
        this.jobControlOptions = jobControlOptions;
        this.outputTransmission = outputTransmission;
        this.links = links;
    }

    public ProcessDesc(){}

    public ProcessDesc(String id, String title, String version, String description, List<String> keywords,
                       List<String> jobControlOptions, List<String> outputTransmission, List<Link> links,
                       Map<String, Input> inputs, Map<String, Output> outputs) {
        this.id = id;
        this.title = title;
        this.version = version;
        this.description = description;
        this.keywords = keywords;
        this.jobControlOptions = jobControlOptions;
        this.outputTransmission = outputTransmission;
        this.links = links;
//        this.inputs = inputs;
//        this.outputs = outputs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getJobControlOptions() {
        return jobControlOptions;
    }

    public void setJobControlOptions(List<String> jobControlOptions) {
        this.jobControlOptions = jobControlOptions;
    }

    public List<String> getOutputTransmission() {
        return outputTransmission;
    }

    public void setOutputTransmission(List<String> outputTransmission) {
        this.outputTransmission = outputTransmission;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Map<String, Input> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Input> inputs) {
        this.inputs = inputs;
    }

    public Map<String, Output> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, Output> outputs) {
        this.outputs = outputs;
    }

    public void convertJSON2Process(JSONObject processObj){
        if(processObj.containsKey("id")){
            this.id = processObj.getString("id");
        }
        if(processObj.containsKey("title")){
            this.title = processObj.getString("title");
        }
        if(processObj.containsKey("version")){
            this.version = processObj.getString("version");
        }
        if(processObj.containsKey("description")){
            this.description = processObj.getString("description");
        }
        if(processObj.containsKey("keywords")){
            this.keywords = processObj.getJSONArray("keywords").toJavaList(String.class);
        }
        if(processObj.containsKey("jobControlOptions")){
            this.jobControlOptions = processObj.getJSONArray("jobControlOptions").toJavaList(String.class);
        }
        if(processObj.containsKey("outputTransmission")){
            this.outputTransmission = processObj.getJSONArray("outputTransmission").toJavaList(String.class);
        }
        if(processObj.containsKey("inputs")){
            Map<String, Input> inputMap = new HashMap<String, Input>();
            for(String inputName: processObj.getJSONObject("inputs").keySet()){
                inputMap.put(inputName, processObj.getJSONObject("inputs").getJSONObject(inputName).toJavaObject(Input.class));
            }
            this.inputs = inputMap;
        }
        if(processObj.containsKey("outputs")){
            Map<String, Output> outputMap = new HashMap<String, Output>();
            for(String outputName: processObj.getJSONObject("outputs").keySet()){
                outputMap.put(outputName, processObj.getJSONObject("outputs").getJSONObject(outputName).toJavaObject(Output.class));
            }
            this.outputs = outputMap;
        }
        if(processObj.containsKey("links")){
            this.links = processObj.getJSONArray("links").toJavaList(Link.class);
        }
    }
}
