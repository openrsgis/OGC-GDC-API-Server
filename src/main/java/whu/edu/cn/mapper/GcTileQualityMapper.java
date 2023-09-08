package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import whu.edu.cn.entity.Ingest.GcTileQuality;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: 瓦片质量表
 * @Author: jeecg-boot
 * @Date:   2020-04-26
 * @Version: V1.0
 */
@Mapper
public interface GcTileQualityMapper extends BaseMapper<GcTileQuality> {
    @Select("SELECT * FROM gc_tile_quality WHERE 1=1 ${CombinedParm};")
    List<GcTileQuality> getTileQualityFromCombinedParm(@Param("CombinedParm") String CombinedParm);
}
