package whu.edu.cn.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcDimension;
import whu.edu.cn.entity.Ingest.GcProduct;
import whu.edu.cn.entity.Ingest.conf.GcCubeConf;

import java.util.List;

@Service
public interface IGcCubeStructureService {
    boolean createCube(GcCubeConf gcCubeConf);

    boolean createGCDimensionTable(String gcDimensionTableName);

    boolean createGcProductTable(String cubeId, String additionalColumns);

    boolean createGcProductMeasurementTable(String cubeId);

    boolean createGcExtentTable(String cubeId);

    boolean createGcRasterTileFactTable(String cubeId);

    boolean createGcLevelAndExtentView(String cubeId);

    boolean createGcMeasurementsAndProductView(String cubeId);

    boolean createGcSensorLevelAndProductView(String cubeId);

    boolean insertGcProductBatchWithDimension(String cubeId, List<GcProduct> gcProductList);

    boolean insertGCDimensionTable(String insertGCDimensionTable, GcDimension dimensionName);
}
