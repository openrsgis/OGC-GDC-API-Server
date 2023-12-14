package whu.edu.cn.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcProduct;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 产品表
 * @Author: jeecg-boot
 * @Date:   2020-08-29
 * @Version: V1.0
 */
public interface IGcProductService extends IService<GcProduct> {
    boolean insertBatch(List<GcProduct> entityList);
    boolean insertBatch(List<GcProduct> entityList, int batchSize);
    Integer getMaxProductKey(String cubeId);
    Integer getMaxProductId(String cubeId);
    List<GcProduct> getProductFromCombinedParam(String combinedParam, String cubeId);
    String getLatestTime(String cubeId, String  time);
}
