package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import whu.edu.cn.entity.Ingest.GcSensor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: 传感器表
 * @Author: jeecg-boot
 * @Date:   2020-04-26
 * @Version: V1.0
 */
@Mapper
public interface GcSensorMapper extends BaseMapper<GcSensor> {
    @Select("SELECT * FROM gc_sensor WHERE 1=1 ${CombinedParam};")
    List<GcSensor> getSensorFromCombinedParam(@Param("CombinedParam") String CombinedParam);
}
