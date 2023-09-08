package whu.edu.cn.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import whu.edu.cn.service.ITilingFuncService;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author czp
 * @version 1.0
 * @date 2020/8/29 20:20
 */
@Service
@Slf4j
public class TilingFuncServiceImpl implements ITilingFuncService {

    @Value("${sparkappparas.jarPath.maps.param-txt}")
    private String paramPath;

    @Value("${hadoop.confDir}")
    private String hadoopConfDir;


    @Value("${sparkappparas.jarPath.javaHome}")
    private String javaHome;

    @Override
    @Async
    public void submitTiling(String appResource, String type, String threadSize, String hbaseTableName,
                          Integer gridDimX, Integer gridDimY, Integer minX, Integer minY,
                          Integer maxX, Integer maxY, String fileName)
            throws IOException, InterruptedException {
        Map<String, String> env = new HashMap<>();
        env.put("HADOOP_CONF_DIR", hadoopConfDir);
        env.put("JAVA_HOME", javaHome);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String outputPath = "/home/geocube/tb19/server/status.txt";
        System.out.println("fileName:" + fileName);
        writeToFile(outputPath, fileName);
        SparkAppHandle handle = new SparkLauncher(env)
                .setSparkHome("/home/geocube/spark")
                .setAppResource(appResource)
                .setMainClass("whu.edu.cn.core.raster.ingest.Ingestor")
                .addAppArgs(paramPath, type, threadSize, hbaseTableName, gridDimX.toString(),
                        gridDimY.toString(), minX.toString(), minY.toString(), maxX.toString(), maxY.toString())
                .setMaster("local[*]")
                .setConf("spark.driver.memory", "8g")
                .setConf("spark.executor.memory", "2g")
                .setConf("spark.cores.max", "16")
                .setConf("spark.executor.cores", "1")
                .setConf("spark.kryoserializer.buffer.max", "256m")
                .setConf("spark.rpc.message.maxSize", "512")
                .setVerbose(true).startApplication(new SparkAppHandle.Listener() {
                    @Override
                    public void stateChanged(SparkAppHandle sparkAppHandle) {
                        if (sparkAppHandle.getState().isFinal()) {
                            countDownLatch.countDown();
                        }
                        log.info("state:" + sparkAppHandle.getState().toString());
                        writeToFile(outputPath, sparkAppHandle.getState().toString() + " ");
                    }

                    @Override
                    public void infoChanged(SparkAppHandle sparkAppHandle) {
                        log.info("Info:" + sparkAppHandle.getState().toString());
                    }
                });
        writeToFile(outputPath, "\n");
        log.info("The task is executing, please wait ....");
        countDownLatch.await();
        log.info("The task is finished!");
    }

    public void writeToFile(String filename, String content){
        try {
            FileWriter writer = new FileWriter(filename, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
