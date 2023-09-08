package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.*;
import whu.edu.cn.entity.Ingest.GcCube;

import java.sql.Timestamp;
import java.util.List;

/**
 * A cube mapper.
 */
@Mapper
public interface GcCubeMapper {

    @Insert("Insert into gc_cube values (#{id},#{cubeName},#{productTableName},#{productMeasurementTableName},#{extentTableName},#{levelKey},#{factTableName}," +
            "#{startTime},#{district},#{sensorLevelProductViewName },#{measurementsProductViewName},#{levelExtentViewName},#{hbaseTableName}," +
            "#{pyramid},#{endTime},#{description},#{leftBottomLongitude},#{leftBottomLatitude},#{rightTopLongitude},#{rightTopLatitude},#{cellSize},#{cellRes}," +
            "#{sliceMinX},#{sliceMinY},#{sliceMaxX},#{sliceMaxY},${geom},#{crs},#{dimensionTableName})")
    Boolean insertCubeFromCube(GcCube cube);

    @Update("UPDATE gc_cube SET left_bottom_longitude = #{minX}, left_bottom_latitude = #{minY}, " +
            "right_top_longitude = #{maxX}, right_top_latitude = #{maxY} WHERE id = #{cubeId}")
    Boolean updateCubeRealExtent(@Param("cubeId") Integer cubeId, @Param("minX") Double minX, @Param("minY") Double minY,
                                 @Param("maxX") Double maxX, @Param("maxY") Double maxY);

    @Select("Select max(id) from gc_cube")
    Integer getCubeMaxId();

    @Select("Select id from gc_cube where cube_name=#{cubeName}")
    Integer getCubeIdByName(String cubeName);

    @Select("select * from gc_cube where id in (29, 33, 34, 36, 37)")
    List<GcCube> getCubeListLimit();

    /**
     * 查询符合条件的collection元信息
     */
    List<GcCube> getCubesByParams(@Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime,
                                  @Param("WKT") String WKT, @Param("limit") int limit);

    @Select("select * from gc_cube where id = #{cubeId}")
    GcCube getCubeById(@Param("cubeId") Integer cubeId);

    @Select("select * from gc_cube where cube_name = #{cubeName}")
    GcCube getCubeByName(@Param("cubeName") String cubeName);

    @Insert("Insert into gc_data_source(product_name,file_path,phenomenon_time,result_time,geom,file_name,meta_path,left_bottom_longitude,left_bottom_latitude,right_top_longitude,right_top_latitude) " +
            "values (#{product_name},#{file_path},#{phenomenon_time},#{result_time},${geom},#{file_name},#{meta_path},#{left_bottom_longitude},#{left_bottom_latitude},#{right_top_longitude},#{right_top_latitude})")
    Boolean insetCubeData(@Param("product_name") String productName, @Param("file_path") String filePath, @Param("phenomenon_time") Timestamp phenomenonTime, @Param("result_time") Timestamp resultTime,
                          @Param("geom") String geom, @Param("file_name") String fileName, @Param("meta_path") String metaPath,
                          @Param("left_bottom_longitude") Double minx, @Param("left_bottom_latitude") Double miny, @Param("right_top_longitude") Double maxx, @Param("right_top_latitude") Double maxy);

}
