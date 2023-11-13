package whu.edu.cn.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
public class LivyUtil {
    @Resource
    private RedisUtil redisUtil;

    @Value("${livy.host}")
    private String livyHost;

    @Value("${livy.port}")
    private String livyPort;

    @Value("${livy.url}")
    private String livyUrl;

    @Value("${livy.user}")
    private String livyUser;

    @Value("${livy.password}")
    private String livyPassword;

    @Value("${livy.sessionNumber}")
    private Integer livySessionNumber;

    @Value("${sparkappparas.jarPath.maps.geocube-core}")
    private String geocubeCore;

    public void initLivy() {
        for (int i = 0; i < livySessionNumber; i++) {
            JSONObject livyConf = new JSONObject();
            livyConf.put("kind", "spark");
            String[] str = {"hdfs:/tb19/core/geocube-core.jar", "hdfs:/tb19/core/json4s-native_2.11-3.6.12.jar", "hdfs:/tb19/core/netcdfAll-5.5.3.jar", "hdfs:/tb19/core/fastjson-1.2.59.jar"};
            livyConf.put("jars", str);
            JSONObject bodyChildren = new JSONObject();
            bodyChildren.put("spark.driver.extraClassPath", "local:/home/geocube/spark/jars/*");
            bodyChildren.put("spark.executor.extraClassPath", "local:/home/geocube/spark/jars/*");
            bodyChildren.put("spark.driver.memory", "2G");
            bodyChildren.put("spark.executor.cores", 4);
            bodyChildren.put("spark.cores.max", 16);
            bodyChildren.put("spark.executor.memory", "8G");
            livyConf.put("conf", bodyChildren);
            String param = livyConf.toJSONString();
            String postSt = HttpRequestUtil.sendPost(livyUrl + "/sessions", param);
            System.out.println("postSt = " + postSt);
            System.out.println(geocubeCore);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void livyTrigger(String code, String jobID, String functionName) {
        String baseUrl = livyUrl;
        int sessionNumExpected = livySessionNumber;

        // 获取所有的session
        String allSessionInfoString = HttpRequestUtil.sendGet(baseUrl + "/sessions/");
        JSONObject allSessionInfoObject = JSON.parseObject(allSessionInfoString);
        int sessionNum = Integer.parseInt(allSessionInfoObject.getString("total"));
        System.out.println("当前的session数目:" + sessionNum);
        JSONArray sessionArray = allSessionInfoObject.getJSONArray("sessions");
        ArrayList<Integer> sessionIdList = new ArrayList<>();
        for (int i = 0; i < sessionArray.size(); i++) {
            JSONObject sessionInfoObject = sessionArray.getJSONObject(i);
            int sessionId = Integer.parseInt(sessionInfoObject.getString("id"));
            sessionIdList.add(sessionId);
        }

        // 已经挂掉的session重新启动
        if (sessionNum < sessionNumExpected) {
            for (int i = 0; i < sessionNumExpected - sessionNum; i++) {
                JSONObject body = new JSONObject();
                body.put("kind", "spark");
                String[] str = {"hdfs:/tb19/core/geocube-core.jar", "hdfs:/tb19/core/json4s-native_2.11-3.6.12.jar", "hdfs:/tb19/core/netcdfAll-5.5.3.jar", "hdfs:/tb19/core/fastjson-1.2.59.jar"};
                body.put("jars", str);
                JSONObject bodyChildren = new JSONObject();
                bodyChildren.put("spark.driver.extraClassPath", "local:/home/geocube/spark/jars/*");
                bodyChildren.put("spark.executor.extraClassPath", "local:/home/geocube/spark/jars/*");
                bodyChildren.put("spark.driver.memory", "2G");
                bodyChildren.put("spark.executor.cores", 4);
                bodyChildren.put("spark.cores.max", 12);
                bodyChildren.put("spark.executor.memory", "8G");
                body.put("conf", bodyChildren);
                String param = body.toJSONString();
                String postSt = HttpRequestUtil.sendPost(livyUrl + "/sessions", param);
                System.out.println("postSt = " + postSt);
            }
        }

        // 检查session能否使用
        int sessionIdAvailable = -1;
        for (Integer integer : sessionIdList) {
            String sessionInfoString = HttpRequestUtil.sendGet(baseUrl + "/sessions/" + integer);
            JSONObject sessionInfoJson = JSON.parseObject(sessionInfoString);
            if (Objects.equals(sessionInfoJson.getString("state"), "idle")) {
                sessionIdAvailable = integer;
                break;
            } else if (Objects.equals(sessionInfoJson.getString("state"), "dead")) {
                HttpRequestUtil.sendDelete(baseUrl + "/sessions/" + integer);
            }
        }

        // 如果没有一个session是闲置的，则提示在排队中
        if (sessionIdAvailable == -1) {
            redisUtil.saveKeyValue(functionName + "_" + jobID + "_state", "SUBMITTED" + "," + 0 + "%");
        } else {
            // 提交任务给session
            JSONObject body = new JSONObject();
            System.out.println("code: " + code);
            body.put("code", code);
            body.put("kind", "spark");
            String parameter = body.toJSONString();
            String outputString = HttpRequestUtil.sendPost(baseUrl + "/sessions/" + sessionIdAvailable + "/statements", parameter);
            System.out.println("outputString = " + outputString);
            JSONObject jsonObject = JSON.parseObject(outputString);
            int statementId = jsonObject.getInteger("id");
            int progressbarPercent = 0;
            int secondsPassed = 0;
            while (true) {
                secondsPassed++;
                if (secondsPassed >= 60) {
                    // 大于1分钟视为超时
                    String canceledMsg = HttpRequestUtil.sendPost(baseUrl + "/sessions/" + sessionIdAvailable + "/statements/" + statementId + "/cancel", null);
                    redisUtil.saveKeyValue(functionName + "_" + jobID + "_state", "TIME_CANCEL" + "," + progressbarPercent + "%");
                }
                if (progressbarPercent < 100) {
                    progressbarPercent = progressbarPercent + 10;
                }
                redisUtil.saveKeyValue(functionName + "_" + jobID + "_state", "RUNNING" + "," + progressbarPercent + "%");
                String statementInfoString = HttpRequestUtil.sendGet(baseUrl + "/sessions/" + sessionIdAvailable + "/statements/" + statementId);
                JSONObject statementInfoJson = JSON.parseObject(statementInfoString);
                String state = statementInfoJson.getString("state");
                if (Objects.equals(state, "available")) {
                    progressbarPercent = 100;
                    redisUtil.saveKeyValue(functionName + "_" + jobID + "_state", "FINISHED" + "," + progressbarPercent + "%");
                    break;
                }
                if (Objects.equals(state, "error")) {
                    redisUtil.saveKeyValue(functionName + "_" + jobID + "_state", "FAILED" + "," + progressbarPercent + "%");
                }
                try {
                    Thread.sleep(1000); // 1000 milliseconds = 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
