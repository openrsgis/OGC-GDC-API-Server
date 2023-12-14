package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;
import whu.edu.cn.entity.Ingest.GcProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Description: 产品表
 * @Author: jeecg-boot
 * @Date: 2020-08-29
 * @Version: V1.0
 */
@Mapper
public interface GcProductMapper extends BaseMapper<GcProduct> {

    @Select("SELECT max(product_key) from gc_product_${cubeId};")
    Integer getMaxProductKey(@Param("cubeId") String cubeId);

    @Select("SELECT max(id) from gc_product_${cubeId};")
    Integer getMaxProductId(@Param("cubeId") String cubeId);

    @Select("SELECT * FROM gc_product_${cubeId} WHERE 1=1 ${CombinedParam};")
    List<GcProduct> getProductFromCombinedParam(@Param("CombinedParam") String CombinedParam, @Param("cubeId") String cubeId);

    @Select("select ${col} from gc_product_${cubeID} where product_key = #{productId}")
    Object getDimensionValue(@Param("cubeID") String cubeID, @Param("productId") Integer productId, @Param("col") String col);

    @Select("Select distinct ${dimensionName} from ${productTableName} order by ${dimensionName}")
    List<Object> getDimensionCoordinates(@Param("productTableName") String productTableName, @Param("dimensionName") String dimensionName);

    @Select("Select max(${dimensionName}) as max from ${productTableName}")
    Object getDimensionCoordinatesMax(@Param("productTableName") String productTableName, @Param("dimensionName") String dimensionName);

    @Select("Select min(${dimensionName}) as min from ${productTableName}")
    Object getDimensionCoordinatesMin(@Param("productTableName") String productTableName, @Param("dimensionName") String dimensionName);

    @Select("Select COUNT(DISTINCT ${dimensionName}) AS count from ${productTableName}")
    Integer getDimensionCoordinatesCount(@Param("productTableName") String productTableName, @Param("dimensionName") String dimensionName);

    Integer getProductsOfCubeCountWithCQL(@Param("cubeID") String cubeID, @Param("cql") String cql, @Param("startTime") Timestamp startTime,
                                          @Param("endTime") Timestamp endTime, @Param("WKT") String WKT);

    List<GcProduct> getProductsOfCubeWithCQL(@Param("cubeID") String cubeID, @Param("cql") String cql, @Param("startTime") Timestamp startTime,
                                             @Param("endTime") Timestamp endTime, @Param("WKT") String WKT, RowBounds rowBounds);

    GcProduct getProductByIdentification(String cubeID, String identification);

    @Select("SELECT phenomenon_time FROM gc_product_${cubeId} ORDER BY ABS(EXTRACT(EPOCH FROM phenomenon_time - #{time}::timestamp)) LIMIT 1")
    String getLatestTime(@Param("cubeId") String cubeId, @Param("time") String time);

}
