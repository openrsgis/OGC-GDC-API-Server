package whu.edu.cn.entity.process;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public enum JobStatus {
    ACCEPTED("accepted"),
    RUNNING("running"),
    SUCCESSFUL("successful"),
    FAILED("failed"),
    DISMISSED("dismissed");

    private final String status;

    JobStatus(String status) {
        this.status = status;
    }
    public String getStatus(){
        return status;
    }

//    public static void main(String [] args){
//        String failed = JobStatus.FAILED.getStatus();
//        System.out.println(failed);
//    }
}
