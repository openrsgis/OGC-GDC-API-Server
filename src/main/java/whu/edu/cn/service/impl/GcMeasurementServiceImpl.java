package whu.edu.cn.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcMeasurement;
import whu.edu.cn.entity.Ingest.conf.GcCubeConf;
import whu.edu.cn.entity.Ingest.conf.MeasurementConf;
import whu.edu.cn.mapper.GcMeasurementMapper;
import whu.edu.cn.service.IGcMeasurementService;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class GcMeasurementServiceImpl implements IGcMeasurementService {

    @Resource
    private GcMeasurementMapper measurementMapper;

    /**
     * check the measurement have existed
     *
     * @param gcCubeConf the GcCubeConf object
     * @return boolean, if one measurement have not insert successfully, return false, else return true
     */
    @Override
    public boolean checkMeasurementTables(GcCubeConf gcCubeConf) {
        boolean flag = true;
        List<MeasurementConf> measurementConfList = gcCubeConf.getMeasurements();
        for (MeasurementConf measurementConf : measurementConfList) {
            String measurementName = measurementConf.getName();
            String unit = measurementConf.getUnit();
            String querySql = "And " + "measurement_name = " + "'" + measurementName + "'";
            if (measurementName == null || measurementName.equals("")) return false;
            if (unit == null || unit.equals("")) {
                querySql = querySql + " And " + "unit is NULL";
            } else {
                querySql = querySql + " And " + "unit = '" + "'" + unit + "'";
            }
            List<GcMeasurement> gcMeasurements = measurementMapper.getMeasurementFromCombinedParam(querySql);
            if (gcMeasurements.size() == 0) {
                GcMeasurement gcMeasurement = new GcMeasurement();
                int maxMeasurementKey = measurementMapper.getMaxMeasurementKey();
                gcMeasurement.setId(measurementMapper.getMaxMeasurementId() + 1);
                gcMeasurement.setMeasurementKey(maxMeasurementKey + 1);
                gcMeasurement.setMeasurementName(measurementConf.getName());
                gcMeasurement.setUnit(measurementConf.getUnit());
                if (measurementMapper.insertMeasurement(gcMeasurement)) {
                    log.info("insert measurement " + gcMeasurement.getMeasurementName() + "successful");
                } else {
                    flag = false;
                    log.error("insert measurement " + gcMeasurement.getMeasurementName() + "error");
                }
            }
        }
        return flag;
    }


    @Override
    public List<GcMeasurement> getMeasurementFromCombinedParam(String combinedParam) {
        return measurementMapper.getMeasurementFromCombinedParam(combinedParam);
    }
}
