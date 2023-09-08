package whu.edu.cn.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcProductMeasurement;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 产品波段对照表
 * @Author: jeecg-boot
 * @Date:   2020-06-28
 * @Version: V1.0
 */
public interface IGcProductMeasurementService extends IService<GcProductMeasurement> {
    boolean insertBatch(List<GcProductMeasurement> entityList);
    boolean insertBatch(List<GcProductMeasurement> entityList, int batchSize);
    Integer getMaxProductMeasurementId();
    List<GcProductMeasurement> getProductMeasurementFromCombinedParam(String combinedParam);
}
