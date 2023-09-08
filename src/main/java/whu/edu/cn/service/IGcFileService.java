package whu.edu.cn.service;

import whu.edu.cn.entity.Ingest.conf.GcCubeConf;
import whu.edu.cn.entity.Ingest.conf.ProductConf;

public interface IGcFileService {
    boolean initDatabase(String cubeId, Integer resolutionKey, Integer gridDimX, Integer gridDimY, Integer minX,
                                Integer minY, Integer maxX, Integer maxY, Integer pixNumX, Integer pixNumY, String productAdditionalSQL);
    boolean initDimensionTable(int cubeId, String dimensionTableName, GcCubeConf cubeConf);
    boolean createHbaseTable(String cubeId);
    boolean dropHbaseTable(String cubeId);
    boolean readRasterFileAndPartition(String cubeId, Integer splitsCount, String threadSize, ProductConf productConf);
}
