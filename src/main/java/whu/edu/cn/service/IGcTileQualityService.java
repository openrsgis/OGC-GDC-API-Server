package whu.edu.cn.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcTileQuality;

import java.util.List;

@Service
public interface IGcTileQualityService {
    List<GcTileQuality> getTileQualityFromCombinedParam(String combinedParam);
}
