package whu.edu.cn.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcLevel;
import com.baomidou.mybatisplus.extension.service.IService;
import whu.edu.cn.entity.Ingest.conf.GcCubeConf;

import java.util.List;

/**
 * @Description: 层级表
 * @Author: jeecg-boot
 * @Date:   2020-04-26
 * @Version: V1.0
 */
@Service
public interface IGcLevelService {

    Integer getMaxResolutionKey();

    GcLevel getGcLevelByParam(GcCubeConf gcCubeConf);

    boolean insertGcLevel(GcLevel gcLevel);

    List<GcLevel> getExistGcLevel(Integer tileSize, Double tileExtent, String crs);
}
