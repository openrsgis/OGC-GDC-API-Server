package whu.edu.cn.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcMeasurement;
import com.baomidou.mybatisplus.extension.service.IService;
import whu.edu.cn.entity.Ingest.conf.GcCubeConf;

import java.util.List;

/**
 * @Description: 波段表
 * @Author: jeecg-boot
 * @Date:   2020-04-26
 * @Version: V1.0
 */
@Service
public interface IGcMeasurementService {
    boolean checkMeasurementTables(GcCubeConf gcCubeConf);
    List<GcMeasurement> getMeasurementFromCombinedParam(String combinedParam);
}
