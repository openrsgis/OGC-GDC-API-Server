package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import whu.edu.cn.entity.Ingest.GcMeasurement;

import java.util.List;

@Mapper
public interface GcMeasurementMapper {
    @Select("SELECT * FROM gc_measurement WHERE 1=1 ${CombinedParam};")
    List<GcMeasurement> getMeasurementFromCombinedParam(@Param("CombinedParam") String CombinedParm);

    @Insert("INSERT INTO gc_measurement(id, measurement_key, measurement_name, unit) VALUES (#{id}, #{measurementKey}, " +
            "#{measurementName}, #{unit})")
    boolean insertMeasurement(GcMeasurement measurement);

    @Select("SELECT max(id) from gc_measurement;")
    Integer getMaxMeasurementId();

    @Select("SELECT max(measurement_key) FROM gc_measurement;")
    int getMaxMeasurementKey();
}
