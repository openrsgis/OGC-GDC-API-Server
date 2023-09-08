package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import whu.edu.cn.entity.Ingest.GcRasterTileFact;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: 栅格事实表
 * @Author: jeecg-boot
 * @Date:   2020-08-29
 * @Version: V1.0
 */
@Mapper
public interface GcRasterTileFactMapper extends BaseMapper<GcRasterTileFact> {
    @Select("SELECT max(id) from gc_raster_tile_fact;")
    Integer getMaxRasterFactId();

    @Select("select * from gc_raster_tile_fact where fact_key=(SELECT max(fact_key) from gc_raster_tile_fact);")
    List<GcRasterTileFact> getMaxFactKeyRasterFact();
}
