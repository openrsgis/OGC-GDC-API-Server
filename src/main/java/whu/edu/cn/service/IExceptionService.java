package whu.edu.cn.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.exception.GDCException;

@Service
public interface IExceptionService {
    GDCException checkQueryParams(String jobKey);
    GDCException submitCoverageTask(String jobKey);
}
