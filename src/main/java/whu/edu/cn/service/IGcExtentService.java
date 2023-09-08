package whu.edu.cn.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcExtent;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 空间范围表
 * @Author: jeecg-boot
 * @Date:   2020-11-25
 * @Version: V1.0
 */
public interface IGcExtentService extends IService<GcExtent> {
    boolean insertBatch(List<GcExtent> entityList);
    boolean insertBatch(List<GcExtent> entityList, int batchSize);
    Integer getMaxExtentId();
    Integer getMaxExtentKey();
    List<GcExtent> getTMSExtentFromCombinedParam(String combinedParam);
    List<GcExtent> getExtentFromCombinedParam(String combinedParam, String cubeId);
}
