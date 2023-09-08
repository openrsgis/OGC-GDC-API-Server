package whu.edu.cn.controller;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.annotations.Param;
import springfox.documentation.annotations.ApiIgnore;
import whu.edu.cn.entity.Ingest.conf.GcCubeConf;
import whu.edu.cn.entity.Ingest.conf.ProductConf;
import whu.edu.cn.entity.query.Result;
import whu.edu.cn.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author wkx
 * @version 2.0
 * @date 2023/8/28 11:03
 */
@ApiIgnore
@CrossOrigin(origins = "*", maxAge = 3600)//解决跨域问题
@Slf4j
@RestController
@RequestMapping("/geocube")
public class IngestController {

    @Resource
    private IGcFileService gcFileService;

    @Resource
    private IGcCubeStructureService gcCubeStructureService;

    /**
     * Creat a cube
     * @param gcCubeConf a GcCubeConf instance
     * @return Result instance
     */
    @PostMapping("/createCube")
    @ResponseBody
    public Result<String> creatCube(@RequestBody GcCubeConf gcCubeConf) {
        boolean flag = gcCubeStructureService.createCube(gcCubeConf);
        Result<String> result = new Result<>();
        if (!flag) {
            result.error500("cube插入表失败！");
        } else {
            result.setResult("cube插入表成功！");
            result.setSuccess(true);
        }
        return null;
    }

    /**
     * import data into the cube with product conf
     * @param cubeId the cube id
     * @param productConf the productConf instance, containing the params of the data waiting to be imported to the cube
     * @return Result instance
     */
    @PostMapping("/importDataWithConf")
    @ResponseBody
    public Result<String> importDataWithConf(@Param("cubeId") Integer cubeId, @RequestBody ProductConf productConf) {
        boolean flag = gcFileService.readRasterFileAndPartition(String.valueOf(cubeId), 500, "30", productConf);
        Result<String> result = new Result<>();
        if (!flag) {
            result.error500("Error occurred during data import into the cube!");
        } else {
            result.setResult("Import the data into the cube successfully!");
            result.setSuccess(true);
        }
        return null;
    }

    /**
     * import data into the cube with product config file
     * @param cubeId the cube id
     * @param confPath the product config file path
     * @return Result instance
     * @throws IOException an error occurred while reading the file
     */
    @PostMapping("/importDataWithConfPath")
    @ResponseBody
    public Result<String> uploadDataWithConfPath(@Param("cubeId") Integer cubeId, @Param("confPath") String confPath) throws IOException {
        String jsonString = new String(Files.readAllBytes(Paths.get(confPath)));
        ProductConf productConf = JSON.parseObject(jsonString, ProductConf.class);
        return importDataWithConf(cubeId, productConf);
    }

}
