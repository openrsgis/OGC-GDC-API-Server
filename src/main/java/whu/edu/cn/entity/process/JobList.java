package whu.edu.cn.entity.process;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

public class JobList implements Serializable {
    private List<Job> jobs;
    private List<Link> links;

    public JobList(List<Job> jobes, List<Link> links) {
        jobs = jobes;
        this.links = links;
    }

    public JobList(){

    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobes) {
        jobs = jobes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
