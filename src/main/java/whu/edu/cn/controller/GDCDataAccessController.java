package whu.edu.cn.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import whu.edu.cn.application.gdc.GDCTrigger;
import whu.edu.cn.config.Address;
import whu.edu.cn.entity.coverage.CollectionInfo;
import whu.edu.cn.entity.coverage.DomainSet;
import whu.edu.cn.entity.coverage.RangeType;
import whu.edu.cn.entity.modify.ModifyParam;
import whu.edu.cn.entity.process.Link;
import whu.edu.cn.entity.coverage.CoverageSubset;
import whu.edu.cn.entity.stac.*;
import whu.edu.cn.service.IGcCubeService;
import whu.edu.cn.service.ISTACService;
import whu.edu.cn.util.FileUtil;
import whu.edu.cn.util.RedisUtil;

import javax.annotation.Resource;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Api(tags = "GDC API - Data Access: STAC API/OGC API - Coverages")
@Slf4j
@RestController
@RequestMapping("/geocube/gdc_api_t19")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GDCDataAccessController {

    @Resource
    private IGcCubeService cubeService;

    @Resource
    private FileUtil fileUtil;

    @Resource
    private ISTACService stacService;

    @Resource
    private ResourceLoader resourceLoader;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private Address address;

    @ApiOperation(value = "Backend information", notes = "Backend information", position = 1)
    @GetMapping(value = "/")
    public ResponseEntity<BackendInfo> getBackendInfo() {
        BackendInfo backendInfo = new BackendInfo();
        List<String> comformList = new ArrayList<>();
        comformList.add("https://api.geodatacube.example/1.0.0-beta");
        comformList.add("https://api.stacspec.org/v1.0.0/core");
        comformList.add("https://api.stacspec.org/v1.0.0/collections");
        comformList.add("https://api.stacspec.org/v1.0.0/ogcapi-features");
        comformList.add("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/core");
        comformList.add("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/oas30");
        comformList.add("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/geojson");
        comformList.add("http://www.opengis.net/spec/ogcapi-features-3/0.0/conf/features-filter");
        comformList.add("http://www.opengis.net/spec/ogcapi-features-3/0.0/conf/filter");
        comformList.add("http://www.opengis.net/spec/cql2/0.0/conf/cql2-text");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/geodata-coverage");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/0.0/conf/geotiff");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/coverage-subset");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/0.0/conf/coverage-scaling");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/0.0/conf/coverage-rangesubset");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/0.0/conf/coverage-bbox");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/0.0/conf/coverage-datetime");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/oas30");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/core");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/ogc-process-description");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/json");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/oas30");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-3/0.0/conf/moaw-definition");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-3/0.0/conf/collection-output");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-3/0.0/conf/nested-process");
        backendInfo.setConformsTo(comformList);
        List<Endpoint> endpoints = new ArrayList<>();
        endpoints.add(new Endpoint("/collections", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/{collectionId}", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/{collectionId}/queryables", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/{collectionId}/items", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/{collectionId}/items/{featureId}", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/{collectionId}/coverage", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/processes", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/processes/{processId}", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/processes/{processId}/execution", java.util.Collections.singletonList("POST")));
        endpoints.add(new Endpoint("/collections/processes/{processId}/jobs/{jobId}", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/processes/{processId}/jobs/{jobId}/results", java.util.Collections.singletonList("GET")));
        backendInfo.setEndpoints(endpoints);
        List<Link> linkList = new ArrayList<>();
        linkList.add(new Link(address.getGdcApiUrl() + "/",
                "self", "application/json", "The JSON representation of the landing page for this OGC API Service providing links to the API definition, the conformance declaration and information about the data collections offered at this endpoint."));
        linkList.add(new Link(address.getGdcApiUrl() + "/api",
                "service-desc", "application/vnd.oai.openapi+json;version=3.0", "The JSON OpenAPI 3.0 document that describes the API offered at this endpoint"));
        linkList.add(new Link(address.getGdcApiUrl() + "/conformance",
                "http://www.opengis.net/def/rel/ogc/1.0/conformance", "application/json", "The JSON representation of the conformance declaration for this server listing the requirement classes implemented by this server"));
        linkList.add(new Link(address.getGdcApiUrl() + "/conformance",
                "conformance", "application/json", "The JSON representation of the conformance declaration for this server listing the requirement classes implemented by this server"));
        linkList.add(new Link(address.getGdcApiUrl() + "/collections",
                "data", "application/json", "The JSON representation of the list of all data collections served from this endpoint"));
        linkList.add(new Link(address.getGdcApiUrl() + "/processes",
                "http://www.opengis.net/def/rel/ogc/1.0/processes", "application/json", "The JSON representation of the list of all processes available from this endpoint"));
        backendInfo.setLinks(linkList);
        return new ResponseEntity<>(backendInfo, HttpStatus.OK);
    }

    @ApiOperation(value = "Open API 3.0", notes = "Open API 3.0")
    @GetMapping(value = "/api")
    public ResponseEntity<?> getOpenAPI() {
        try {
            org.springframework.core.io.Resource configResource = resourceLoader.getResource("classpath:static/openapi-3.0.json");
            InputStream inputStream = configResource.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String jsonContent = stringBuilder.toString();
            JSONObject configObject = JSONObject.parseObject(jsonContent, Feature.OrderedField, com.alibaba.fastjson.parser.Feature.DisableSpecialKeyDetect);
            return new ResponseEntity<>(configObject, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @ApiOperation(value = "Conformance", notes = "Conformance", position = 2)
    @GetMapping(value = "/conformance")
    public ResponseEntity<Conformance> getConformance() {
        Conformance conformance = new Conformance();
        List<String> comformList = new ArrayList<>();
        comformList.add("https://api.geodatacube.example/1.0.0-beta");
        comformList.add("https://api.stacspec.org/v1.0.0/core");
        comformList.add("https://api.stacspec.org/v1.0.0/collections");
        comformList.add("https://api.stacspec.org/v1.0.0/ogcapi-features");
        comformList.add("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/core");
        comformList.add("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/oas30");
        comformList.add("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/geojson");
        comformList.add("http://www.opengis.net/spec/ogcapi-features-3/0.0/conf/features-filter");
        comformList.add("http://www.opengis.net/spec/ogcapi-features-3/0.0/conf/filter");
        comformList.add("http://www.opengis.net/spec/cql2/0.0/conf/cql2-text");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/geodata-coverage");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/0.0/conf/geotiff");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/coverage-subset");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/0.0/conf/coverage-scaling");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/0.0/conf/coverage-rangesubset");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/0.0/conf/coverage-bbox");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/0.0/conf/coverage-datetime");
        comformList.add("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/oas30");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/core");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/ogc-process-description");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/json");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/oas30");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-3/0.0/conf/moaw-definition");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-3/0.0/conf/collection-output");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-3/0.0/conf/nested-process");
        conformance.setConformsTo(comformList);
        return new ResponseEntity<>(conformance, HttpStatus.OK);
    }

    @ApiOperation(value = "Collections provided", notes = "Collections list")
    @GetMapping(value = "/collections")
    public ResponseEntity<Collections> getCollections() {
        Collections collectionsList = stacService.getSTACollections();
        return new ResponseEntity<>(collectionsList, HttpStatus.OK);
    }

    @ApiOperation(value = "Collection provided", notes = "Get the description of a certain collection. Collection is used to describe the descriptive information of a Cube and supports the STAC Cube Extension.")
    @GetMapping(value = "/collections/{collectionId}")
    public ResponseEntity<?> getCollection(@PathVariable String collectionId) {
        if (collectionId.contains("temp")) {
            CollectionInfo modifiedCollection = cubeService.getWorkflowCollectionInfo(collectionId);
            return new ResponseEntity<>(modifiedCollection, HttpStatus.OK);
        } else {
            Collection collection = stacService.getSTACCollection(collectionId);
            return new ResponseEntity<>(collection, HttpStatus.OK);
        }

    }

    @ApiOperation(value = "Queryables provided", notes = "Get queryable items of the collection. The dimensions of the Cube are available as queryable items.")
    @GetMapping(value = "/collections/{collectionId}/queryables")
    public ResponseEntity<Queryables> getCollectionQueryables(@PathVariable String collectionId) {
        Queryables queryables = stacService.getSTACQueryables(collectionId);
        return new ResponseEntity<>(queryables, HttpStatus.OK);
    }

    @ApiOperation(value = "Collection items provided", notes = "STAC items list. Item corresponds to the metadata of a scene resulting from combining one dimension member from each dimension of the Cube (except the spatial dimension). This scene serves as an asset for external access." +
            "This endpoint supports dimension queries using cql2, but it only supports `cql-text` encoding")
    @GetMapping(value = "/collections/{collectionId}/items")
    public ResponseEntity<STACItems> getCollectionItems(@PathVariable String collectionId,
                                                        @ApiParam(name = "bbox", value = "Spatial bounding box with format 'minx,miny,maxx,maxy'")
                                                        @RequestParam(value = "bbox", required = false) String bbox,
                                                        @ApiParam(value = "Temporal extent is specified by RFC 3339, eg. 2016-10-28T00:01:00Z/2016-10-28T01:01:00Z")
                                                        @RequestParam(value = "datetime", required = false) String datetime,
                                                        @ApiParam(value = "CQL2")
                                                        @RequestParam(value = "filter", required = false) String filter,
                                                        @RequestParam(value = "filter-lang", required = false, defaultValue = "cql-text") String filterLang,
                                                        @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                                        @RequestParam(value = "pageNum", required = false, defaultValue = "1") @ApiIgnore Integer pageNum) {
        STACItems items = stacService.getSTACItems(collectionId, bbox, filter, datetime, pageNum, limit);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @ApiOperation(value = "A certain collection item provided", notes = "Get a certain STAC item. Item corresponds to the metadata of a scene resulting from combining one dimension member from each dimension of the Cube (except the spatial dimension). This scene serves as an asset for external access.", position = 5)
    @GetMapping(value = "/collections/{collectionId}/items/{featureId}")
    public ResponseEntity<STACItem> getCollectionItems(@PathVariable String collectionId, @PathVariable String featureId) {
        STACItem item = stacService.getSTACItem(collectionId, featureId);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @ApiIgnore
    @GetMapping(value = "/collections/{collectionId}/image/{featureId}")
    public ResponseEntity<org.springframework.core.io.Resource> getAsset(@PathVariable String collectionId, @PathVariable String featureId) {
        try {
            org.springframework.core.io.Resource configResource = resourceLoader.getResource("classpath:imageFile/file_config.json");
            InputStream inputStream = configResource.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String jsonContent = stringBuilder.toString();
            JSONObject configObject = JSONObject.parseObject(jsonContent, com.alibaba.fastjson.parser.Feature.DisableSpecialKeyDetect);
            if (configObject.containsKey(collectionId)) {
                String filePathStr = "hdfs://gisweb1:9000" + configObject.getString(collectionId) + featureId + ".tif";
                Path filePath = new Path(filePathStr);
                Configuration conf = new Configuration();
                conf.set("fs.defaultFS", "hdfs://gisweb1:9000"); // 设置HDFS集群地址
                conf.set("dfs.client.use.datanode.hostname", "true"); // 如果集群配置为使用datanode主机名，则设置为true
                FileSystem fileSystem = FileSystem.get(conf);
                if (fileSystem.exists(filePath)) {
                    org.springframework.core.io.Resource resource = new InputStreamResource(fileSystem.open(filePath));
                    String filename = featureId + ".tif";
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    headers.setContentDispositionFormData("attachment", URLEncoder.encode(filename, "UTF-8"));

                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(resource);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.status(500).build();
            }


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Get the coverage
     *
     * @param collectionId cube id
     * @param bbox         bbox
     * @param datetime     datetime
     * @param subset       subset (spatial temporal)
     * @param properties   different bands or measurements
     * @param scaleFactor  scale factor
     * @return ResponseEntity<org.springframework.core.io.Resource>
     */
    @ApiOperation(value = "Return the coverage", notes = "Return the coverage. It is highly recommended to include relevant selection parameters such as bbox, datetime, properties, or subset when retrieving data using the this endpoint to reduce unnecessary data retrieval. When no selection parameters are provided, a predefined preview image will be returned." +
            " When specifying the file type to be `tif`, if the filtered results yield time-series data or have multiple dimension members in other dimensions(except the band dimension), only the first matching data will be returned." +
            " Files downloaded through Swagger may sometimes be unable to open. This is due to a bug in Swagger itself. To overcome this issue, you can test this endpoint using a browser or tools like Postman to view the data.", produces = "application/octet-stream")
    @GetMapping(value = "/collections/{collectionId}/coverage")
    public ResponseEntity<?> getCoverage(
            @ApiParam(name = "collectionId", example = "ECMWF_ht3e") @PathVariable("collectionId") String collectionId,
            @ApiParam(name = "bbox", value = "Spatial bounding box with format 'minx,miny,maxx,maxy', eg. 0,35,40,70", example = "0,35,40,70")
            @RequestParam(value = "bbox", required = false) String bbox,
            @ApiParam(value = "Temporal extent is specified by RFC 3339, eg. 2016-10-28T00:01:00Z/2016-10-28T01:01:00Z", example = "2016-10-28T01:01:00Z/2016-10-28T02:01:00Z") @RequestParam(value = "datetime", required = false) String datetime,
            @ApiParam(value = "Supports cube cutting or slicing through dimensional filtering", example = "pressure(1000)") @RequestParam(value = "subset", required = false) String subset,
            @ApiParam(value = "Supports cube cutting or slicing through band filtering", example = "Divergence") @RequestParam(value = "properties", required = false) String properties,
            @ApiParam(value = "Scale-factor, number") @RequestParam(value = "scale-factor", required = false) Double scaleFactor,
            @ApiParam(value = "Scale-size") @RequestParam(value = "scale-size", required = false) String scaleSize,
            @ApiParam(value = "Scale-axes") @RequestParam(value = "scale-axes", required = false) String scaleAxes,
            @ApiParam(value = "The format of the output, only support tif and netcdf", example = "tif") @RequestParam(value = "f", required = false, defaultValue = "tif") String f) {
        try {
            if ((!collectionId.contains("temp")) && (bbox == null && datetime == null && subset == null && properties == null && scaleFactor == null && scaleSize == null && scaleAxes == null)) {
                org.springframework.core.io.Resource configResource = resourceLoader.getResource("classpath:imageFile/preview_config.json");
                InputStream inputStream = configResource.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String jsonContent = stringBuilder.toString();
                JSONObject configObject = JSONObject.parseObject(jsonContent, com.alibaba.fastjson.parser.Feature.DisableSpecialKeyDetect);
                if (configObject.containsKey(collectionId)) {
                    String filePathStr = configObject.getString(collectionId);
                    Path filePath = new Path(filePathStr);
                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS", "hdfs://gisweb1:9000"); // 设置HDFS集群地址
                    conf.set("dfs.client.use.datanode.hostname", "true"); // 如果集群配置为使用datanode主机名，则设置为true
                    FileSystem fileSystem = FileSystem.get(conf);
                    if (fileSystem.exists(filePath)) {
                        org.springframework.core.io.Resource resource = new InputStreamResource(fileSystem.open(filePath));
                        String filename = filePath.getName();
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                        headers.setContentDispositionFormData("attachment", URLEncoder.encode(filename, "UTF-8"));

                        return ResponseEntity.ok()
                                .headers(headers)
                                .body(resource);
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                } else {
                    return ResponseEntity.status(500).body("There is no data corresponding to the collectionId");
                }
            }
            CoverageSubset coverageSubset = new CoverageSubset();
            coverageSubset.setSubset(subset);
            coverageSubset.setProperties(properties);
            coverageSubset.setScaleFactor(scaleFactor);
            coverageSubset.setScaleSize(scaleSize);
            coverageSubset.setScaleAxes(scaleAxes);
            Boolean isSuccess;
            if (collectionId.contains("temp")) {
                String workflowJson = redisUtil.getValueByKey(collectionId);
                String secondLevelDir = UUID.randomUUID().toString();
                Random random = new Random();
                JSONObject dagObj = GDCTrigger.runMetaAnalysis(workflowJson, Integer.toString(random.nextInt()));
                ModifyParam modifyParam = dagObj.toJavaObject(ModifyParam.class);
                String outputDir = address.getLocalDataRoot() + collectionId.replace("temp_", "") + "/" + secondLevelDir + "/";
                File jobIdFile = new File(address.getLocalDataRoot() + collectionId.replace("temp_", ""));
                if (!jobIdFile.exists()) jobIdFile.mkdir();
                File jobIdFile2 = new File(address.getLocalDataRoot() + collectionId.replace("temp_", "") + "/" + secondLevelDir);
                if (!jobIdFile2.exists()) jobIdFile2.mkdir();
                isSuccess = cubeService.executeWorkflowByCoverage(workflowJson, collectionId.replace("temp_", ""), modifyParam.getCollection(), bbox, datetime, coverageSubset, outputDir, f);
                log.info("isSuccess: " + isSuccess);
                if (isSuccess) {
                    String resultPath = fileUtil.matchResultFile(outputDir);
                    if (resultPath != null) {
                        return fileUtil.downloadFile(resultPath);
                    } else {
                        return ResponseEntity.status(500).body("An error occurred during processing, result path is null");
                    }
                } else {
                    return ResponseEntity.status(500).body("An error occurred during processing");
                }
            } else {
                String jobId = UUID.randomUUID().toString();
                String outputDir = address.getLocalDataRoot() + jobId + "/";
                File jobIdFile = new File(address.getLocalDataRoot() + jobId);
                if (!jobIdFile.exists()) jobIdFile.mkdir();
                isSuccess = cubeService.getCoverageBySubmitSpark(collectionId, jobId, bbox, datetime, coverageSubset, outputDir, f);
                if (isSuccess) {
                    String resultPath = fileUtil.matchResultFile(outputDir);
                    if (resultPath != null) {
                        return fileUtil.downloadFile(resultPath);
                    } else {
                        return ResponseEntity.status(500).body("An error occurred  retrieving data");
                    }
                } else {
                    return ResponseEntity.status(500).body("An error occurred  retrieving data");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Abnormal error occurred");
        }
    }


    /**
     * Retrieve a coverage domainset, use content
     * negotiation to request HTML or GeoJSON.
     *
     * @param collectionId the cube id
     * @return a coverage domainset
     */
    @ApiOperation(value = "Domainset of a coverage", notes = "Domainset of a coverage", position = 7)
    @GetMapping(value = "/collections/{collectionId}/coverage/domainset")
    public ResponseEntity<DomainSet> getCoverageDomainSet(@PathVariable("collectionId") String collectionId) {
        CollectionInfo collectionInfo;
        if (collectionId.contains("temp")) {
            collectionInfo = cubeService.getWorkflowCollectionInfo(collectionId);
        } else {
            collectionInfo = cubeService.getCollectionByCubeName(collectionId);
        }
        return new ResponseEntity<>(collectionInfo.getDomainSet(), HttpStatus.OK);
    }

    /**
     * Retrieve a coverage rangetype, use content
     * negotiation to request HTML or GeoJSON.
     *
     * @param collectionId the cube id
     * @return a coverage rangetype
     */
    @ApiOperation(value = "Rangetype of a coverage", notes = "Rangetype of a coverage", position = 8)
    @GetMapping(value = "/collections/{collectionId}/coverage/rangetype")
    public ResponseEntity<RangeType> getCoverageRangetype(@PathVariable("collectionId") String collectionId) {
        CollectionInfo collectionInfo;
        if (collectionId.contains("temp")) {
            collectionInfo = cubeService.getWorkflowCollectionInfo(collectionId);
        } else {
            collectionInfo = cubeService.getCollectionByCubeName(collectionId);
        }
        return new ResponseEntity<>(collectionInfo.getRangeType(), HttpStatus.OK);
    }

    /**
     * Get the rangeSet of the coverage
     *
     * @param collectionId cube id
     * @param bbox         bbox
     * @param datetime     datetime
     * @param subset       subset (spatial temporal)
     * @param properties   different bands or measurements
     * @param scaleFactor  scale factor
     * @param scaleSize    scale size
     * @param scaleAxes    scale axes
     * @param f            the format of coverage
     * @return ResponseEntity<org.springframework.core.io.Resource>
     */
    @ApiOperation(value = "Return the coverage", notes = "Return the coverage. It is highly recommended to include relevant selection parameters such as bbox, datetime, properties, or subset when retrieving data using the this endpoint to reduce unnecessary data retrieval. When no selection parameters are provided, a predefined preview image will be returned." +
            " When specifying the file type to be `tif`, if the filtered results yield time-series data or have multiple dimension members in other dimensions(except the band dimension), only the first matching data will be returned." +
            " Files downloaded through Swagger may sometimes be unable to open. This is due to a bug in Swagger itself. To overcome this issue, you can test this endpoint using a browser or tools like Postman to view the data.")
    @GetMapping(value = "/collections/{collectionId}/coverage/rangeset")
    public ResponseEntity<?> getCoverageRangeSet(@ApiParam(name = "collectionId", example = "ECMWF_ht3e") @PathVariable("collectionId") String collectionId,
                                                 @ApiParam(name = "bbox", value = "Spatial bounding box in String format 'minx,miny,maxx,maxy', eg. 0,35,40,70", example = "0,35,40,70")
                                                 @RequestParam(value = "bbox", required = false) String bbox,
                                                 @ApiParam(value = "Temporal extent is specified by RFC 3339, eg. 2016-10-28T00:01:00Z/2016-10-28T01:01:00Z", example = "2016-10-28T01:01:00Z/2016-10-28T02:01:00Z") @RequestParam(value = "datetime", required = false) String datetime,
                                                 @ApiParam(value = "Supports cube cutting or slicing through dimensional filtering", example = "pressure(1000)") @RequestParam(value = "subset", required = false) String subset,
                                                 @ApiParam(value = "Supports cube cutting or slicing through band filtering", example = "Divergence") @RequestParam(value = "properties", required = false) String properties,
                                                 @ApiParam(value = "Scale-factor, number") @RequestParam(value = "scale-factor", required = false) Double scaleFactor,
                                                 @ApiParam(value = "Scale-size") @RequestParam(value = "scale-size", required = false) String scaleSize,
                                                 @ApiParam(value = "Scale-axes") @RequestParam(value = "scale-axes", required = false) String scaleAxes,
                                                 @ApiParam(value = "The format of the output, only support tif and netcdf", example = "tif") @RequestParam(value = "f", required = false, defaultValue = "tif") String f) {
        return getCoverage(collectionId, bbox, datetime, subset, properties, scaleFactor, scaleSize, scaleAxes, f);
    }


    /**
     * Get the workflow request by the collectionId
     *
     * @param collectionId the id of the collection
     * @return the workflow request
     */
    @ApiIgnore
    @GetMapping(value = "/collections/{collectionId}/workflow")
    public ResponseEntity<?> getWorkflowOfCollection(@PathVariable("collectionId") String collectionId) {
        String workflowStr = redisUtil.getValueByKey(collectionId);
        if (workflowStr == null) {
            return ResponseEntity.status(500).body("The " + collectionId + " has been timeout");
        } else {
            JSONObject requestBody = JSON.parseObject(workflowStr);
            return ResponseEntity.ok(requestBody);
        }
    }

}
