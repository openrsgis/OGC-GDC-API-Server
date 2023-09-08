package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.*;
import whu.edu.cn.entity.Ingest.*;
import whu.edu.cn.entity.Ingest.GcDimension;

import java.util.List;

/**
 * @author czp
 * @version 1.0
 * @date 2020/5/1 18:26
 */
@Mapper
public interface GcCubeStructureMapper {

    @Update("CREATE TABLE ${gcDimensionTableName} (\n" +
            "  id serial PRIMARY KEY NOT NULL,\n" +
            "  dimension_name varchar(30),\n" +
            "  dimension_type varchar(30),\n" +
            "  dimension_table_name varchar(30),\n" +
            "  member_type varchar(30),\n" +
            "  step float8,\n" +
            "  description varchar(255),\n" +
            "  unit varchar(30),\n" +
            "  dimension_table_column_name varchar(30)\n" +
            ");")
    boolean createGcDimensionTable(@Param("gcDimensionTableName") String gcDimensionTableName);

    @Insert("INSERT INTO ${gcDimensionTableName} (dimension_name, dimension_type, dimension_table_name, " +
            "dimension_table_column_name, member_type, step, description, unit) " +
            "VALUES (#{dimensionName}, #{dimensionType}, #{dimensionTableName}," +
            "  #{dimensionTableColumnName}, #{memberType}, #{step}, #{description}, #{unit});")
    boolean insertGcDimensionTable(@Param("gcDimensionTableName") String gcDimensionTableName,
                                   @Param("dimensionName") String dimensionName,
                                   @Param("dimensionType") String dimensionType,
                                   @Param("dimensionTableName") String dimensionTableName,
                                   @Param("dimensionTableColumnName") String dimensionTableColumnName,
                                   @Param("memberType") String memberType,
                                   @Param("step") Double step,
                                   @Param("description") String description,
                                   @Param("unit") String unit);

    @Insert("${sql}")
    boolean insertGcProductBatchWithDimensions(@Param("sql") String sql);

    @Update("CREATE TABLE gc_product_${cubeId}(\n" +
            "  id serial PRIMARY KEY NOT NULL,\n" +
            "  product_key\t\t\tint UNIQUE ,\n" +
            "  product_name\ttext,\n" +
            "  product_identification\ttext,\n" +
            "  product_type varchar(30),\n" +
            " " + "${additionalColumns}" + "\n" +
            "  sensor_key\tint REFERENCES gc_sensor(sensor_key),\n" +
            "  resolution_key\tInt REFERENCES gc_level(resolution_key),\n" +
            "  crs\tvarchar(30),\n" +
            "  phenomenon_time\tTIMESTAMP WITHOUT TIME ZONE,\n" +
            "  result_time\tTIMESTAMP WITHOUT TIME ZONE,\n" +
            "  geom geometry,\n" +
            "  upper_left_lat\tnumeric(20,10),\n" +
            "  upper_left_long\tnumeric(20,10),\n" +
            "  upper_right_lat\tnumeric(20,10),\n" +
            "  upper_right_long\tnumeric(20,10),\n" +
            "  lower_left_lat\tnumeric(20,10),\n" +
            "  lower_left_long\tnumeric(20,10),\n" +
            "  lower_right_lat\tnumeric(20,10),\n" +
            "  lower_right_long\tnumeric(20,10),\n" +
            "  phenomenon_time_month\tint,\n" +
            "  phenomenon_time_year\tint,\n" +
            "  create_by VARCHAR(32) DEFAULT NULL ,\n" +
            "  create_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL ,\n" +
            "  update_by VARCHAR(32) DEFAULT NULL ,\n" +
            "  update_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL\n" +
            ");")
    boolean createGcProductTable(@Param("cubeId") String cubId, @Param("additionalColumns") String additionalColumns);

    @Update("CREATE TABLE gc_product_measurement_${cubeId}(\n" +
            "  id serial PRIMARY KEY NOT NULL,\n" +
            "  product_key\t\tint REFERENCES gc_product_${cubeId}(product_key),\n" +
            "  measurement_key\tint REFERENCES gc_measurement(measurement_key),\n" +
            "  dtype\tvarchar(10),\n" +
            "  create_by VARCHAR(32) DEFAULT NULL ,\n" +
            "  create_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL ,\n" +
            "  update_by VARCHAR(32) DEFAULT NULL ,\n" +
            "  update_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL\n" +
            ");")
    boolean createGcProductMeasurementTable(@Param("cubeId") String cubId);

    @Update("CREATE TABLE gc_extent_${cubeId}(\n" +
            "  id serial PRIMARY KEY NOT NULL,\n" +
            "  extent_key\tint UNIQUE ,\n" +
            "  grid_code\tvarchar(30),\n" +
            "  city_code\tvarchar(30),\n" +
            "  city_name\tvarchar(30),\n" +
            "  province_name\tvarchar(30),\n" +
            "  province_code varchar(30),\n" +
            "  district_name\tvarchar(30),\n" +
            "  district_code\tvarchar(30),\n" +
            "  extent\ttext,\n" +
            "  resolution_key\tInt REFERENCES gc_level(resolution_key),\n" +
            "  create_by VARCHAR(32) DEFAULT NULL ,\n" +
            "  create_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL ,\n" +
            "  update_by VARCHAR(32) DEFAULT NULL ,\n" +
            "  update_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL\n" +
            ");")
    boolean createGcExtentTable(@Param("cubeId") String cubId);

    @Update("CREATE TABLE gc_raster_tile_fact_${cubeId}(\n" +
            "  id serial PRIMARY KEY NOT NULL,\n" +
            "  fact_key\tint,\n" +
            "  product_key\tint REFERENCES gc_product_${cubeId}(product_key),\n" +
            "  extent_key\tint,\n" +
            "  measurement_key\tint REFERENCES gc_measurement(measurement_key),\n" +
            "  tile_quality_key\tint REFERENCES gc_tile_quality(tile_quality_key),\n" +
            "  tile_data_id\ttext,\n" +
            "  create_by VARCHAR(32) DEFAULT NULL ,\n" +
            "  create_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL ,\n" +
            "  update_by VARCHAR(32) DEFAULT NULL ,\n" +
            "  update_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL\n" +
            ");")
    boolean createGcRasterTileFactTable(@Param("cubeId") String cubId);

    @Update("CREATE VIEW \"LevelAndExtent_${cubeId}\" AS SELECT\n" +
            "  gc_extent_${cubeId}.extent_key,\n" +
            "  gc_extent_${cubeId}.grid_code,\n" +
            "  gc_extent_${cubeId}.city_code,\n" +
            "  gc_extent_${cubeId}.province_name,\n" +
            "  gc_extent_${cubeId}.district_name,\n" +
            "  gc_extent_${cubeId}.extent,\n" +
            "  gc_level.tile_size,\n" +
            "  gc_level.cell_res,\n" +
            "  gc_level.level\n" +
            "  FROM (gc_extent_${cubeId} \n" +
            "  JOIN gc_level ON ((gc_extent_${cubeId}.resolution_key = gc_level.resolution_key)))")
    boolean createGcLevelAndExtentView(@Param("cubeId") String cubId);

    @Update("create view \"MeasurementsAndProduct_${cubeId}\" as " +
            "SELECT gc_measurement.measurement_key,\n" +
            "    gc_measurement.measurement_name,\n" +
            "    gc_measurement.unit,\n" +
            "    gc_product_measurement_${cubeId}.dtype,\n" +
            "    gc_product_${cubeId}.product_key,\n" +
            "    gc_product_${cubeId}.product_name\n" +
            "   FROM ((gc_measurement\n" +
            "     JOIN gc_product_measurement_${cubeId} ON ((gc_product_measurement_${cubeId}.measurement_key = gc_measurement.measurement_key)))\n" +
            "     JOIN gc_product_${cubeId} ON ((gc_product_measurement_${cubeId}.product_key = gc_product_${cubeId}.product_key)))")
    boolean createGcMeasurementsAndProductView(@Param("cubeId") String cubId);

    @Update("create view \"SensorLevelAndProduct_${cubeId}\" as" +
            " SELECT gc_level.tile_size,\n" +
            "    gc_level.cell_res,\n" +
            "    gc_level.level,\n" +
            "    gc_product_${cubeId}.product_key,\n" +
            "    gc_product_${cubeId}.product_name,\n" +
            "    gc_product_${cubeId}.product_type,\n" +
            "    gc_product_${cubeId}.crs,\n" +
            "    gc_product_${cubeId}.phenomenon_time,\n" +
            "    gc_product_${cubeId}.result_time,\n" +
            "    gc_product_${cubeId}.geom,\n" +
            "    gc_product_${cubeId}.upper_left_lat,\n" +
            "    gc_product_${cubeId}.upper_left_long,\n" +
            "    gc_product_${cubeId}.upper_right_lat,\n" +
            "    gc_product_${cubeId}.upper_right_long,\n" +
            "    gc_product_${cubeId}.lower_left_lat,\n" +
            "    gc_product_${cubeId}.lower_left_long,\n" +
            "    gc_product_${cubeId}.lower_right_lat,\n" +
            "    gc_product_${cubeId}.lower_right_long,\n" +
            "    gc_sensor.sensor_name,\n" +
            "    gc_sensor.platform_name,\n" +
            "    gc_sensor.imaging_length,\n" +
            "    gc_sensor.imaging_width,\n" +
            "    gc_product_${cubeId}.phenomenon_time_month,\n" +
            "    gc_product_${cubeId}.phenomenon_time_year\n" +
            "   FROM ((gc_level\n" +
            "     JOIN gc_product_${cubeId} ON ((gc_product_${cubeId}.resolution_key = gc_level.resolution_key)))\n" +
            "     JOIN gc_sensor ON ((gc_product_${cubeId}.sensor_key = gc_sensor.sensor_key)))")
    boolean createGcSensorLevelAndProductView(@Param("cubeId") String cubId);
}
