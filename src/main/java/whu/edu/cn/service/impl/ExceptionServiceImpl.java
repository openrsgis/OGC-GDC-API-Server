package whu.edu.cn.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import whu.edu.cn.exception.GDCException;
import whu.edu.cn.service.IExceptionService;
import whu.edu.cn.util.RedisUtil;

import javax.annotation.Resource;

@Slf4j
@Service
public class ExceptionServiceImpl implements IExceptionService {
    @Resource
    private RedisUtil redisUtil;

    @Override
    public GDCException checkQueryParams(String jobKey) {
        GDCException coverageException = new GDCException();
        coverageException.setFlag(false);
        String statusValue = redisUtil.getValueByKey(jobKey);
        String status = statusValue.split(",")[0];
        if (status.equals("SpatialOut")) {
            coverageException.setFlag(true);
            coverageException.setCode(204);
            coverageException.setMessage("The temporal constraints do not intersect with the actual extent of the cube.");
        }
        if (status.equals("TimeOut")) {
            coverageException.setFlag(true);
            coverageException.setCode(204);
            coverageException.setMessage("The spatial constraints do not intersect with the actual spatial extent of the image collection.");
        }
        return coverageException;
    }

    @Override
    public GDCException submitCoverageTask(String jobKey) {
        GDCException coverageException = new GDCException();
        coverageException.setFlag(false);
        if (redisUtil.keyExists(jobKey + "_spark")) {
            String status = redisUtil.getValueByKey(jobKey + "_spark").split(",")[0];
            log.info("status: " + status);
            if (status.equals("LIMIT_EXCEED")) {
                coverageException.setFlag(true);
                coverageException.setCode(403);
                coverageException.setMessage("The volume of data involved in your request is too large. Please use filtering criteria to reduce the data volume.");
            }
        }else{
            String statusValue = redisUtil.getValueByKey(jobKey);
            String status = statusValue.split(",")[0];
            log.info("CoverageTask status: " + status + " in job key " + jobKey);
            if (status.equals("TIME_CANCEL")) {
                coverageException.setFlag(true);
                coverageException.setCode(503);
                coverageException.setMessage("The volume of data involved is too large for the scope of service provision.");
            }
            if (status.equals("FAILED")) {
                coverageException.setFlag(true);
                coverageException.setCode(500);
                coverageException.setMessage("An error occurred retrieving data");
            }
        }
        return coverageException;
    }
}
