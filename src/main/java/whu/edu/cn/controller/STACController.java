package whu.edu.cn.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import whu.edu.cn.entity.stac.*;
import whu.edu.cn.service.ISTACService;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * OGC API - Coverages.
 * Allows discovery, visualization and query of GeoCube.
 */
@Slf4j
@ApiIgnore
@Api(tags = "GeoCube-STAC API")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/geocube/stac")
public class STACController {
    @Resource
    private ISTACService stacService;

    @Resource
    private ResourceLoader resourceLoader;

    @ApiOperation(value = "Backend information", notes = "Backend information")
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
        backendInfo.setConformsTo(comformList);
        List<Endpoint> endpoints = new ArrayList<>();
        endpoints.add(new Endpoint("/collections", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/{collectionId}", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/{collectionId}/queryables", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/{collectionId}/items", java.util.Collections.singletonList("GET")));
        endpoints.add(new Endpoint("/collections/{collectionId}/items/{featureId}", java.util.Collections.singletonList("GET")));
        backendInfo.setEndpoints(endpoints);
        return new ResponseEntity<>(backendInfo, HttpStatus.OK);
    }

    @ApiOperation(value = "Backend information", notes = "Backend information")
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
        conformance.setConformsTo(comformList);
        return new ResponseEntity<>(conformance, HttpStatus.OK);
    }

    @ApiOperation(value = "Collections provided", notes = "Collections list")
    @GetMapping(value = "/collections")
    public ResponseEntity<Collections> getCollections() {
        Collections collectionsList = stacService.getSTACollections();
        return new ResponseEntity<>(collectionsList, HttpStatus.OK);
    }

    @ApiOperation(value = "Collection provided", notes = "Get a certain collection")
    @GetMapping(value = "/collections/{collectionId}")
    public ResponseEntity<Collection> getCollection(@PathVariable String collectionId) {
        Collection collection = stacService.getSTACCollection(collectionId);
        return new ResponseEntity<>(collection, HttpStatus.OK);
    }

    @ApiOperation(value = "Queryables provided", notes = "Get Queryables from collections")
    @GetMapping(value = "/collections/{collectionId}/queryables")
    public ResponseEntity<Queryables> getCollectionQueryables(@PathVariable String collectionId) {
        Queryables queryables = stacService.getSTACQueryables(collectionId);
        return new ResponseEntity<>(queryables, HttpStatus.OK);
    }

    @ApiOperation(value = "Collection items provided", notes = "Collection items list")
    @GetMapping(value = "/collections/{collectionId}/items")
    public ResponseEntity<STACItems> getCollectionItems(@PathVariable String collectionId,
                                                        @RequestParam(value = "bbox", required = false) String bbox,
                                                        @RequestParam(value = "datetime", required = false) String datetime,
                                                        @RequestParam(value = "filter", required = false) String filter,
                                                        @RequestParam(value = "filter-lang", required = false, defaultValue = "cql-text") String filterLang,
                                                        @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                                        @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        STACItems items = stacService.getSTACItems(collectionId, bbox, filter, datetime, pageNum, limit);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @ApiOperation(value = "A certain collection item provided", notes = "Get a certain collection item")
    @GetMapping(value = "/collections/{collectionId}/items/{featureId}")
    public ResponseEntity<STACItem> getCollectionItems(@PathVariable String collectionId, @PathVariable String featureId) {
        STACItem item = stacService.getSTACItem(collectionId, featureId);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @GetMapping(value = "/collections/{collectionId}/image/{featureId}")
    public ResponseEntity<org.springframework.core.io.Resource> getAsset(@PathVariable String collectionId, @PathVariable String featureId) {
        try {
            org.springframework.core.io.Resource configResource= resourceLoader.getResource("classpath:imageFile/file_config.json");
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
            if(configObject.containsKey(collectionId)){
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
            }else{
                return ResponseEntity.status(500).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

}
