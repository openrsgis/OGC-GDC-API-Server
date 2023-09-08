package whu.edu.cn.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import whu.edu.cn.config.spark.SparkAppParas;
import whu.edu.cn.service.ISparkApplicationService;
import whu.edu.cn.util.LivyUtil;
import whu.edu.cn.util.RedisUtil;


/**
 * This class is used to submit spark application and monitor its execution progress.
 */
@Slf4j
@Service
public class SparkApplicationServiceImpl implements ISparkApplicationService {

    @Resource
    private RedisUtil redisUtil;

    @Value("${hadoop.confDir}")
    private String hadoopConfDir;

    @Resource
    private LivyUtil livyUtil;

    /**
     * Submits a Spark application with the specified configurations and waits for it to finish.
     * Writes the output to a JSON file.
     *
     * @param sparkAppParas   the SparkAppParas object containing the configurations
     * @param funcName        the name of the function
     * @param paramJson       the JSON string containing the input parameters
     * @param jobID           the ID of the job
     * @param outputDir       the directory where the output file will be saved
     * @param isCollection    a flag indicating whether the input is a collection
     * @param collectionParam the collection parameter
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    @Override
    @Async
    public void submitGDCWorkflow(SparkAppParas sparkAppParas,
                                  String funcName,
                                  String paramJson,
                                  String jobID,
                                  String outputDir,
                                  String isCollection,
                                  String collectionParam)
            throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        initializeSparkLauncher(sparkAppParas)
                .setMainClass(sparkAppParas.getMainClass().get("maps.gdcTrigger"))
                .addAppArgs(paramJson, outputDir, isCollection, collectionParam)
                .startApplication(initializeListener(funcName, jobID));
        log.info("The task is executing, please wait ....");
        countDownLatch.await();
        log.info("The task is finished!");
        writeOutputJson(jobID, paramJson, outputDir, funcName);
    }

    /**
     * Submits a Spark application to retrieve coverage information and waits for it to finish.
     *
     * @param sparkAppParas   sparkAppParas the SparkAppParas object containing the configurations
     * @param jobID           jobID the ID of the job
     * @param outputDir       outputDir the directory where the output will be saved
     * @param collectionParam collectionParam the collection parameter
     * @throws IOException          IOException if an I/O error occurs
     * @throws InterruptedException InterruptedException if the current thread is interrupted while waiting
     */
    @Override
    @Async
    public void submitGetCoverage(SparkAppParas sparkAppParas,
                                  String jobID,
                                  String outputDir,
                                  String collectionParam)
            throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        initializeSparkLauncher(sparkAppParas)
                .addAppArgs(outputDir, collectionParam)
                .setMainClass(sparkAppParas.getMainClass().get("maps.gdcCoverage"))
                .startApplication(initializeListener("Coverage", jobID));
        log.info("The task is executing, please wait ....");
        countDownLatch.await();
        log.info("The task is finished!");
    }

    /**
     * Initializes a SparkLauncher object with the specified configurations.
     *
     * @param sparkAppParas the SparkAppParas object containing the configurations
     * @return a SparkLauncher object with the specified configurations
     */
    public SparkLauncher initializeSparkLauncher(SparkAppParas sparkAppParas) {
        Map<String, String> env = new HashMap<>();
        env.put("HADOOP_CONF_DIR", hadoopConfDir);
        env.put("JAVA_HOME", sparkAppParas.getJarPath().get("javaHome"));
        return new SparkLauncher(env)
                .setSparkHome(sparkAppParas.getSparkHome())
                .setAppResource(sparkAppParas.getJarPath().get("maps.geocube-core"))
                .setMaster("local[*]")
                .setConf("spark.jars", "/home/geocube/mylib/geocube/json4s-native_2.11-3.6.12.jar,/home/geocube/mylib/geocube/netcdfAll-5.5.3.jar,/home/geocube/mylib/geocube/fastjson-1.2.59.jar")
                .setConf("spark.driver.memory", "8g")
                .setConf("spark.executor.memory", "2g")
                .setConf("spark.cores.max", "16")
                .setConf("spark.executor.cores", "1")
                .setConf("spark.kryoserializer.buffer.max", "256m")
                .setConf("spark.rpc.message.maxSize", "512");
    }

    /**
     * Initializes a SparkAppHandle.Listener object with the specified configurations.
     *
     * @param funcName the name of the function
     * @param jobID    the ID of the job
     * @return a SparkAppHandle.Listener object with the specified configurations
     */
    public SparkAppHandle.Listener initializeListener(String funcName, String jobID) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        return new SparkAppHandle.Listener() {
            int progressbarPercent = 0;

            @Override
            public void stateChanged(SparkAppHandle sparkAppHandle) {
                if (sparkAppHandle.getState().isFinal()) {
                    countDownLatch.countDown();
                }
                if ((!sparkAppHandle.getState().toString().equals("FINISHED")) && progressbarPercent < 90)
                    progressbarPercent += 10;
                else if (sparkAppHandle.getState().toString().equals("FINISHED")) progressbarPercent = 100;
                redisUtil.saveKeyValue(funcName + "_" + jobID + "_state", sparkAppHandle.getState().toString() + "," + progressbarPercent + "%");
                System.out.println(funcName + "_" + jobID + "_state" + sparkAppHandle.getState().toString() + "," + progressbarPercent + "%");

            }

            @Override
            public void infoChanged(SparkAppHandle sparkAppHandle) {
                if ((!sparkAppHandle.getState().toString().equals("FINISHED")) && progressbarPercent < 90)
                    progressbarPercent += 10;
                else if (sparkAppHandle.getState().toString().equals("FINISHED")) progressbarPercent = 100;
                redisUtil.saveKeyValue(funcName + "_" + jobID + "_state", sparkAppHandle.getState().toString() + "," + progressbarPercent + "%");
                System.out.println("Info:" + sparkAppHandle.getState().toString());
            }
        };
    }

    /**
     * Writes output to a JSON file with the specified configurations.
     *
     * @param jobID     the ID of the job
     * @param paramJson the JSON string containing the input parameters
     * @param outputDir the directory where the output file will be saved
     * @param funcName  the name of the function
     * @return true if the output file was written successfully, false otherwise
     */
    public Boolean writeOutputJson(String jobID, String paramJson, String outputDir, String funcName) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.createObjectNode();
            node.put("job-id", jobID);
            ObjectNode inputNode = objectMapper.createObjectNode();
            node.put("input", inputNode);
            inputNode.put("paramJson", paramJson);
            ObjectNode outputNode = objectMapper.createObjectNode();
            node.put("output", outputNode);
            File resultsDir = new File(outputDir);
            File[] subFiles = resultsDir.listFiles();
            int record = 0;
            for (File subFile : subFiles) {
                if (subFile.getName().endsWith(".json")) {
                    ObjectMapper objMap = new ObjectMapper();
                    JsonNode root = objMap.readTree(subFile);
                    outputNode.put(String.valueOf(record), root);
                    record++;
                }
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new FileOutputStream(outputDir + funcName + "_job_meta.json"), node);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred in the writing the output file!");
            return false;
        }
    }

    @Override
    @Async
    public void submitGetCoverageByLivy(String jobID,
                                        String functionName,
                                        String outputDir,
                                        String collectionParam){
        String code = "whu.edu.cn.application.gdc.gdcCoverage.runGetCoverage(sc," +
                "\"" + outputDir + "\"," +
                "\"" + escapeString(collectionParam) + "\"" +
                ")";
        livyUtil.livyTrigger(code, jobID, functionName);
    }

    @Async
    public void submitGDCWorkflowByLivy(String jobID, String functionName, String paramJson,
                                        String outputDir, String isCollection, String collectionParam){
        String code = "whu.edu.cn.application.gdc.GDCTrigger.runWorkflow(sc," +
                "\"" + escapeString(paramJson) + "\"," +
                "\"" + escapeString(outputDir) + "\"," +
                "\"" + escapeString(isCollection) + "\"," +
                "\"" + escapeString(collectionParam)+ "\"" +
                ")";
        livyUtil.livyTrigger(code, jobID, functionName);
    }

    private String escapeString(String inputString){
        return inputString.replace("\"", "\\\"");
    }

}

