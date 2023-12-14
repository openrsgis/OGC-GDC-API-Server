package whu.edu.cn.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import whu.edu.cn.util.RedisUtil;

import javax.annotation.Resource;

/**
 *  from geocube core
 */
@Slf4j
@ApiIgnore
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/geocube/callback")
public class CallbackController {

    @Resource
    private RedisUtil redisUtil;

    /**
     * 实现 oge_computation_ogc 将 url 传递给 springboot 项目
     *
     */
    @PostMapping("/exceedLimit")
    public void exceedLimit(@RequestBody String paramJson) {
        String processName = JSONObject.parseObject(paramJson).getString("processName");
        String jobId = JSONObject.parseObject(paramJson).getString("jobId");
        log.info("Exceed limit callback with " + processName + "_" + jobId);
        redisUtil.saveKeyValue(processName + "_" + jobId + "_state", "LIMIT_EXCEED", 60 * 60 * 24);
        redisUtil.saveKeyValue(processName + "_" + jobId + "_state_spark", "LIMIT_EXCEED", 60 * 60 * 24);
    }

}
