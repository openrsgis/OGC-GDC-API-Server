package whu.edu.cn.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcRasterTileFact;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 栅格事实表
 * @Author: jeecg-boot
 * @Date:   2020-08-29
 * @Version: V1.0
 */
public interface IGcRasterTileFactService extends IService<GcRasterTileFact> {
    boolean insertBatch(List<GcRasterTileFact> entityList);
    boolean insertBatch(List<GcRasterTileFact> entityList, int batchSize);
    Integer getMaxRasterFactId();
    List<GcRasterTileFact> getMaxFactKeyRasterFact();
}
