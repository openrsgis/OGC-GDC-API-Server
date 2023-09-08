package whu.edu.cn.entity.process;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessesDesc implements Serializable {
    private List<ProcessDesc> processes;
    private List<Link> links;

    public ProcessesDesc(List<ProcessDesc> processes, List<Link> links) {
        this.processes = processes;
        this.links = links;
    }
    public ProcessesDesc(){

    }
    public List<ProcessDesc> getProcesses() {
        return processes;
    }

    public void setProcesses(List<ProcessDesc> processes) {
        this.processes = processes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
