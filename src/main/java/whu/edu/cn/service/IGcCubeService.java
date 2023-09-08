package whu.edu.cn.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.conf.GcCubeConf;
import whu.edu.cn.entity.coverage.CollectionInfo;
import whu.edu.cn.entity.coverage.CollectionsInfo;
import whu.edu.cn.entity.modify.ModifyParam;
import whu.edu.cn.entity.coverage.CoverageSubset;
import whu.edu.cn.entity.Ingest.GcCube;

import java.sql.Timestamp;
import java.text.ParseException;

/**
 * Cube service class
 */
@Service
public interface IGcCubeService {

    GcCube insertCube(GcCube cube, String type, GcCubeConf gcCubeConf);

    /**
     * Get cube by id
     *
     * @return Cube
     */
    GcCube getCubeById(Integer cubeId);

    /**
     * Insert cube data source with info
     * return true or false
     */
    Boolean insertOneData(String productName, String filePath, String fileName, String geom, String metaPath, Timestamp phenomenonTime, Timestamp resultTime, Double minx, Double miny, Double maxx, Double maxy);

    /**
     * @param startTime start time
     * @param endTime   end time
     * @param WKT       spatial extent
     * @param limit     limit number
     * @return collectionInfo CollectionInfo
     */
    CollectionsInfo getCollectionsFromCubesByParam(Timestamp startTime, Timestamp endTime, String WKT, int limit);

    CollectionInfo getCollectionByCubeName(String cubeName);

    String getCoverage(String cubeName, String bbox, String datetime, CoverageSubset coverageSubset, String f) throws ParseException;

    CollectionInfo modifyParam2Collection(String collectionId, ModifyParam modifyParam, CollectionInfo collectionInfo);

    Boolean executeWorkflowByCoverage(String workflowJSON, String jobId, String cubeName, String bbox, String datetime, CoverageSubset coverageSubset, String outputDir, String f);

    CollectionInfo getWorkflowCollectionInfo(String collectionId);

    Boolean getCoverageBySubmitSpark(String cubeName, String jobId, String bbox, String datetime, CoverageSubset coverageSubset, String outputDir, String f) throws ParseException;
}
