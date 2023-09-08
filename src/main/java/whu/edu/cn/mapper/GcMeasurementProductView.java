package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import whu.edu.cn.entity.Ingest.view.GcMeasurementProduct;
import whu.edu.cn.entity.Ingest.view.Measurement;

import java.util.List;

@Mapper
public interface GcMeasurementProductView {
    @Select("Select measurement_key,measurement_name,dtype from \"MeasurementsAndProduct_${cubeID}\" where product_key = #{ProductKey}")
    List<Measurement> getMeasurements(String cubeID, Integer ProductKey);

    @Select("SELECT DISTINCT measurement_key, measurement_name, dtype, unit FROM \"${measurementsProductViewName}\" ORDER BY measurement_key")
    List<GcMeasurementProduct> getMeasurementProducts(String measurementsProductViewName);

    @Select("Select DISTINCT product_name from \"${measurementsProductViewName}\"")
    List<String> getProductNames(String measurementsProductViewName);
}
