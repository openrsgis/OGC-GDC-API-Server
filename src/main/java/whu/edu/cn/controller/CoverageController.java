package whu.edu.cn.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import whu.edu.cn.config.Address;
import whu.edu.cn.entity.coverage.CollectionInfo;
import whu.edu.cn.entity.coverage.CollectionsInfo;
import whu.edu.cn.entity.coverage.DomainSet;
import whu.edu.cn.entity.coverage.RangeType;
import whu.edu.cn.entity.process.Link;
import whu.edu.cn.entity.coverage.CoverageSubset;
import whu.edu.cn.service.IGcCubeService;
import whu.edu.cn.util.FileUtil;
import whu.edu.cn.util.GeoUtil;
import whu.edu.cn.util.TimeUtil;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * OGC API - Coverages.
 * Allows discovery, visualization and query of GeoCube.
 *
 * */
@Slf4j
@Api(tags = "GeoCube-OGC Coverages API")
@ApiIgnore
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/geocube/coverages_api")
public class CoverageController {

    @Resource
    private IGcCubeService cubeService;

    @Resource
    private Address address;

    @Resource
    private TimeUtil timeUtil;

    @Resource
    private GeoUtil geoUtil;

    @Resource
    private FileUtil fileUtil;

    /**
     * Landing page of geocube coverage API.
     *
     * @return
     */
    @ApiOperation(value = "Landing page", notes = "Landing page")
    @GetMapping(value = "/")
    public Map<String, Object> getLandingPage() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "Geocube processing server");
        map.put("description", "Geocube server implementing the OGC API - Processes 1.0");

        List<Link> linkList = new ArrayList<>();
        Link self = new Link();
        self.setHref(address.getCoverageApiUrl() + "/");
        self.setRel("self");
        self.setType("application/json");
        self.setTitle("landing page");
        linkList.add(self);

        Link serviceDesc = new Link();
        serviceDesc.setHref(address.getCoverageApiUrl() + "/api");
        serviceDesc.setRel("service-desc");
        serviceDesc.setType("application/openapi+json;version=3.0");
        serviceDesc.setTitle("the API definition");
        linkList.add(serviceDesc);

        Link conformance = new Link();
        conformance.setHref(address.getCoverageApiUrl() + "/conformance");
        conformance.setRel("conformance");
        conformance.setType("application/json");
        conformance.setTitle("OGC API - Coverage conformance classes implemented by this server");
        linkList.add(conformance);

        Link processes = new Link();
        processes.setHref(address.getCoverageApiUrl() + "/collections");
        processes.setRel("coverage");
        processes.setType("application/json");
        processes.setTitle("The list of available collections");
        linkList.add(processes);

        map.put("links", linkList);
        return map;
    }

    /**
     * Information about standards that this API conforms to.
     *
     * @return Map<String, Object>
     */
    @ApiOperation(value = "Conformance class", notes = "Conformance class")
    @GetMapping(value = "/conformance")
    public Map<String, Object> getConformanceClasses() {
        Map<String, Object> map = new HashMap<>();
        List<String> comformList = new ArrayList<>();
        comformList.add("http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/core");
        comformList.add("http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/json");
        comformList.add("http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/oas3");
        comformList.add("http://www.opengis.net/spec/ogcapi-common-2/1.0/conf/geodata");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/geodata-coverage");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/geodata-subset");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/geodata-bbox");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/geodata-datetime");
        map.put("conformsTo", comformList);
        return map;
    }

    /**
     * Describe the collections in the dataset.
     *
     * @param limit max number of return collections
     * @param bbox spatial extent
     * @param time temporal range
     * @return dataset collections in the GeoCube
     */
    @ApiOperation(value = "Collections provided", notes = "Collections list")
    @GetMapping(value = "/collections")
    public CollectionsInfo describeCollections(
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
            @RequestParam(value = "bbox", required = false) String bbox,
            @RequestParam(value = "time", required = false) String time) {

        double minx = -179;
        double miny = -89;
        double maxx = 179;
        double maxy = 89;
        if(bbox !=null){
            String [] bboxList = bbox.split(",");
            minx = Double.parseDouble(bboxList[0]);
            miny = Double.parseDouble(bboxList[1]);
            maxx = Double.parseDouble(bboxList[2]);
            maxy = Double.parseDouble(bboxList[3]);
        }
        String startTime = null;
        String endTime = null;
        if(time != null){
            String [] timeList = time.split("/");
            startTime = timeUtil.convertTime(timeList[0]);
            endTime = timeUtil.convertTime(timeList[1]);
        }
        //先判断参数是否正确
        if (minx > 180 || minx < -180) {
            log.info(minx + " is not in global extent!");
            return null;
        }
        if (maxx > 180 || maxx < -180) {
            log.info(maxx + " is not in global extent!");
            return null;
        }
        if (miny > 90 || miny < -90) {
            log.info(miny + " is not in global extent!");
            return null;
        }
        if (maxy > 90 || maxy < -90) {
            log.info(maxy + " is not in global extent!！");
            return null;
        }

        String WKT_rec = "";
        if (minx == -180.0 && miny == -90.0 && maxx == 180.0 && maxy == 90.0) {
            log.info("Use default global spatial extent!");
        } else {
            WKT_rec = geoUtil.DoubleToWKT(minx, miny, maxx, maxy);
        }

        Timestamp startTimeT;
        if (startTime == null || startTime.equals("")) {
            startTimeT = Timestamp.valueOf("1977-01-01 00:00:00");
        } else {
            startTimeT = Timestamp.valueOf(startTime);
        }

        Timestamp endTimeT;
        if (endTime == null || endTime.equals("")) {
            endTimeT = new Timestamp(System.currentTimeMillis());
        } else {
            endTimeT = Timestamp.valueOf(endTime);
        }
        return cubeService.getCollectionsFromCubesByParam(startTimeT, endTimeT, WKT_rec, limit);
    }

    /**
     * Collection Information is the set of metadata which
     * describes a single collection, or in the the case of
     * API-Coverages, a single Coverage.
     *
     * @param collectionId collection name
     * @return description of a collection
     */
    @ApiOperation(value = "Collection description", notes = "Collection description")
    @GetMapping(value = "/collections/{collectionId}")
    public CollectionInfo getCoverageInfo(@PathVariable("collectionId") String collectionId) {
        return cubeService.getCollectionByCubeName(collectionId);
    }

    /**
     * Get the coverage
     * @param collectionId cube id
     * @param bbox bbox
     * @param datetime datetime
     * @param subset subset (spatial temporal)
     * @param properties different bands or measurements
     * @param scaleFactor scale factor
     * @return ResponseEntity<org.springframework.core.io.Resource>
     */
    @ApiOperation(value = "Description of a coverage", notes = "Return the coverage")
    @GetMapping(value = "/collections/{collectionId}/coverage")
    public ResponseEntity<?> getCoverage(@PathVariable("collectionId") String collectionId,
                                                        @RequestParam(value = "bbox", required = false) String bbox,
                                                        @RequestParam(value = "datetime", required = false) String datetime,
                                                        @RequestParam(value = "subset", required = false) String subset,
                                                        @RequestParam(value = "properties", required = false) String properties,
                                                        @RequestParam(value = "scale-factor", required = false) Double scaleFactor,
                                                        @RequestParam(value = "scale-size", required = false) String scaleSize,
                                                        @RequestParam(value = "scale-axes", required = false) String scaleAxes,
                                                        @RequestParam(value = "f", required = false, defaultValue = "tif") String f){
        try{
            CoverageSubset coverageSubset = new CoverageSubset();
            coverageSubset.setSubset(subset);
            coverageSubset.setProperties(properties);
            coverageSubset.setScaleFactor(scaleFactor);
            coverageSubset.setScaleSize(scaleSize);
            coverageSubset.setScaleAxes(scaleAxes);
            String coverageFilePath = cubeService.getCoverage(collectionId, bbox, datetime, coverageSubset, f);
            return fileUtil.downloadFile(coverageFilePath);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Retrieve a coverage domainset, use content
     * negotiation to request HTML or GeoJSON.
     *
     * @param collectionId the cube id
     * @return a coverage domainset
     */
    @ApiOperation(value = "Domainset of a coverage", notes = "Domainset of a coverage")
    @GetMapping(value = "/collections/{collectionId}/coverage/domainset")
    public DomainSet getCoverageDomainSet(@PathVariable("collectionId") String collectionId){
        CollectionInfo collectionInfo = cubeService.getCollectionByCubeName(collectionId);
        return collectionInfo.getDomainSet();
    }

    /**
     * Retrieve a coverage rangetype, use content
     * negotiation to request HTML or GeoJSON.
     *
     * @param collectionId the cube id
     * @return a coverage rangetype
     */
    @ApiOperation(value = "Rangetype of a coverage", notes = "Rangetype of a coverage")
    @GetMapping(value = "/collections/{collectionId}/coverage/rangetype")
    public RangeType getCoverageRangetype(@PathVariable("collectionId") String collectionId){
        CollectionInfo collectionInfo = cubeService.getCollectionByCubeName(collectionId);
        return collectionInfo.getRangeType();
    }

    /**
     * Get the rangeSet of the coverage
     * @param collectionId cube id
     * @param bbox bbox
     * @param datetime datetime
     * @param subset subset (spatial temporal)
     * @param properties different bands or measurements
     * @param scaleFactor scale factor
     * @param scaleSize scale size
     * @param scaleAxes scale axes
     * @param f the format of coverage
     * @return  ResponseEntity<org.springframework.core.io.Resource>
     */
    @ApiOperation(value = "Description of a coverage", notes = "Return the coverage")
    @GetMapping(value = "/collections/{collectionId}/coverage/rangeset")
    public ResponseEntity<?> getCoverageRangeSet(@PathVariable("collectionId") String collectionId,
                                                                                    @RequestParam(value = "bbox", required = false) String bbox,
                                                                                    @RequestParam(value = "datetime", required = false) String datetime,
                                                                                    @RequestParam(value = "subset", required = false) String subset,
                                                                                    @RequestParam(value = "properties", required = false) String properties,
                                                                                    @RequestParam(value = "scale-factor", required = false) Double scaleFactor,
                                                                                    @RequestParam(value = "scale-size", required = false) String scaleSize,
                                                                                    @RequestParam(value = "scale-axes", required = false) String scaleAxes,
                                                                                    @RequestParam(value = "f", required = false, defaultValue = "tif") String f){
       return getCoverage(collectionId, bbox, datetime, subset, properties, scaleFactor, scaleSize, scaleAxes, f);
    }


}
