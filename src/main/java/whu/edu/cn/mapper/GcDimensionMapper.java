package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import whu.edu.cn.entity.Ingest.GcDimension;

import java.util.List;

@Mapper
public interface GcDimensionMapper {
    @Select("SELECT * from ${gcDimensionTableName} where dimension_name = #{dimensionName};")
    GcDimension selectDimension(@Param("gcDimensionTableName") String gcDimensionTableName, @Param("dimensionName") String dimensionName);

    @Select("SELECT * from ${gcDimensionTableName};")
    List<GcDimension> selectAllDimensions(@Param("gcDimensionTableName") String gcDimensionTableName);

    @Select("SELECT * from ${gcDimensionTableName} where dimension_name NOT IN ('extent', 'product', 'phenomenonTime', 'measurement');")
    List<GcDimension> selectAdditionalDimensions(@Param("gcDimensionTableName") String gcDimensionTableName);
}
