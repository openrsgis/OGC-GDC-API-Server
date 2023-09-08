package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import whu.edu.cn.entity.Ingest.GcLevel;

import java.util.List;

@Mapper
public interface GcLevelMapper {
    @Select("Select max(resolution_key) from gc_level")
    Integer getMaxResolutionKey();

    @Select("Select max(id) from gc_level")
    Integer getMaxId();


    @Select("Select * from gc_level where tile_size = #{tileSize} and tile_extent = #{tileExtent} and crs = #{crs}")
    List<GcLevel> getGcLevel(@Param("tileSize") Integer tileSize, @Param("tileExtent") Double tileExtent, @Param("crs") String crs);

    @Insert("INSERT INTO gc_level (id, resolution_key, tile_size, cell_res, level, crs, tile_extent, unit)" +
            "VALUES (#{id}, #{resolutionKey}, #{tileSize}, #{cellRes}, #{level}, #{crs}, #{tileExtent}, #{unit});")
    boolean insertGcLevel(GcLevel gcLevel);
}
