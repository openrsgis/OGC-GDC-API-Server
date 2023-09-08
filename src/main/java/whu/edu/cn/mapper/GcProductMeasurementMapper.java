package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import whu.edu.cn.entity.Ingest.GcProductMeasurement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: 产品波段对照表
 * @Author: jeecg-boot
 * @Date:   2020-06-28
 * @Version: V1.0
 */
@Mapper
public interface GcProductMeasurementMapper extends BaseMapper<GcProductMeasurement> {
    @Select("SELECT max(id)FROM gc_product_measurement;")
    Integer getMaxProductMeasurementId();

    @Select("SELECT * FROM gc_product_measurement WHERE 1=1 ${CombinedParam};")
    List<GcProductMeasurement> getProductMeasurementFromCombinedParam(@Param("CombinedParam") String CombinedParam);
}
