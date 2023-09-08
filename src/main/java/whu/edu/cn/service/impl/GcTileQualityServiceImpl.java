package whu.edu.cn.service.impl;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcTileQuality;
import whu.edu.cn.mapper.GcTileQualityMapper;
import whu.edu.cn.service.IGcTileQualityService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GcTileQualityServiceImpl implements IGcTileQualityService {

    @Resource
    private GcTileQualityMapper gcTileQualityMapper;

    @Override
    public List<GcTileQuality> getTileQualityFromCombinedParam(String combinedParam) {
        return gcTileQualityMapper.getTileQualityFromCombinedParm(combinedParam);
    }
}
