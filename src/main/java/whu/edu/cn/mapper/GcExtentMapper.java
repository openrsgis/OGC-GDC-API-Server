package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import whu.edu.cn.entity.Ingest.GcExtent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: 空间范围表
 * @Author: jeecg-boot
 * @Date:   2020-11-25
 * @Version: V1.0
 */
@Mapper
public interface GcExtentMapper extends BaseMapper<GcExtent> {
    @Select("SELECT max(id) FROM gc_extent;")
    Integer getMaxExtentId();

    @Select("SELECT max(extent_key) FROM gc_extent;")
    Integer getMaxExtentKey();

    @Select("SELECT * FROM gc_tms_extent WHERE 1=1 ${CombinedParam};")
    List<GcExtent> getTMSExtentFromCombinedParam(@Param("CombinedParam") String CombinedParam);

    @Select("SELECT * FROM gc_extent_${cubeId} WHERE 1=1 ${CombinedParam};")
    List<GcExtent> getExtentFromCombinedParam(@Param("CombinedParam") String CombinedParam, @Param("cubeId") String cubeId);
}
