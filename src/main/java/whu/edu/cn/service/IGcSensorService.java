package whu.edu.cn.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcSensor;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 传感器表
 * @Author: jeecg-boot
 * @Date:   2020-04-26
 * @Version: V1.0
 */
public interface IGcSensorService extends IService<GcSensor> {
    List<GcSensor> getSensorFromCombinedParam(String combinedParam);
}
