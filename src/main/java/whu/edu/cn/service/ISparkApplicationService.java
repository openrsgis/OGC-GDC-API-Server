package whu.edu.cn.service;

import whu.edu.cn.config.spark.SparkAppParas;

import java.io.IOException;

public interface ISparkApplicationService {

    void submitGDCWorkflow(SparkAppParas sparkAppParas, String funcName, String paramJson, String jobID, String outputDir,
                           String isCollection, String collectionParam) throws IOException, InterruptedException;

    void submitGetCoverage(SparkAppParas sparkAppParas, String jobID, String outputDir, String collectionParam)
            throws IOException, InterruptedException;

    void submitGDCWorkflowByLivy(String jobID, String functionName, String paramJson,
                                 String outputDir, String isCollection, String collectionParam);

    void submitGetCoverageByLivy(String jobID, String functionName, String outputDir, String collectionParam);

}
