package whu.edu.cn.service.impl;

import whu.edu.cn.entity.Ingest.GcSensor;
import whu.edu.cn.mapper.GcSensorMapper;
import whu.edu.cn.service.IGcSensorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 传感器表
 * @Author: jeecg-boot
 * @Date:   2020-04-26
 * @Version: V1.0
 */
@Service
public class GcSensorServiceImpl extends ServiceImpl<GcSensorMapper, GcSensor> implements IGcSensorService {
    @Resource
    private GcSensorMapper gcSensorMapper;
    @Override
    public List<GcSensor> getSensorFromCombinedParam(String combinedParam) {
        return gcSensorMapper.getSensorFromCombinedParam(combinedParam);
    }
}
