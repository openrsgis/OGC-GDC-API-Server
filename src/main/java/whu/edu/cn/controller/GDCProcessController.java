package whu.edu.cn.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import whu.edu.cn.config.Address;
import whu.edu.cn.config.spark.SparkAppParas;
import whu.edu.cn.entity.process.*;
import whu.edu.cn.service.ISparkApplicationService;
import whu.edu.cn.util.FileUtil;
import whu.edu.cn.util.RedisUtil;

/**
 * OGC API - Processes - Part1/Part3.
 */
@Api(tags = "GDC API - Data Process: OGC API - Processes - Part1/Part3")
@Slf4j
@RestController
@RequestMapping("/geocube/gdc_api_t19")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GDCProcessController {

    @Autowired
    private ISparkApplicationService sparkApplicationService;
    @Autowired
    SparkAppParas sparkAppParas;
    @Autowired
    Address address;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private FileUtil fileUtil;

    /**
     * List the processes this API offers.
     *
     * @return A list of processes in the GeoCube
     */
    @ApiOperation(value = "Processes provided", notes = "Processes list")
    @GetMapping(value = "/processes")
    public ProcessesDesc getProcesses() throws IOException {
        List<ProcessDesc> processDescList = new ArrayList<>();
        org.springframework.core.io.Resource configResource = resourceLoader.getResource("classpath:processDescription/process_t19_description.json");
        InputStream inputStream = configResource.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        String jsonContent = stringBuilder.toString();
        JSONObject jsonObject = JSONObject.parseObject(jsonContent, Feature.OrderedField, com.alibaba.fastjson.parser.Feature.DisableSpecialKeyDetect);
        for (String key : jsonObject.keySet()) {
            JSONObject processObj = jsonObject.getJSONObject(key);
            ProcessDesc processDesc = JSON.toJavaObject(processObj, ProcessDesc.class);
            processDescList.add(processDesc);
        }
        List<Link> processesLinks = new ArrayList<>();
        processesLinks.add(new Link(address.getProcessApiUrl() + "/processes", "self", "application/json", "the list of the process description"));
        return new ProcessesDesc(processDescList, processesLinks);
    }


    /**
     * Retrieve a process description.
     *
     * @param name Process name
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "Process description", notes = "Process description")
    @GetMapping(value = "/processes/{processId}")
    public ResponseEntity<ProcessDesc> getProcessDescription(@PathVariable("processId") String name) throws IOException {
        org.springframework.core.io.Resource configResource = resourceLoader.getResource("classpath:processDescription/process_t19_description.json");
        InputStream inputStream = configResource.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        String jsonContent = stringBuilder.toString();
        JSONObject jsonObject = JSONObject.parseObject(jsonContent, Feature.OrderedField, com.alibaba.fastjson.parser.Feature.DisableSpecialKeyDetect);
        ProcessDesc processDesc;
        if (jsonObject.containsKey(name)) {
            processDesc = JSON.toJavaObject(jsonObject.getJSONObject(name), ProcessDesc.class);
            return ResponseEntity.ok(processDesc);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Execute a process.
     *
     * @param processName        Process name
     * @param processRequestBody the processRequestBody
     * @return the job instance
     * @throws IOException          the IO exception
     * @throws InterruptedException the InterruptedException
     */
    @ApiOperation(value = "Execute a process", notes = "Return the job information. When `response = collection`, it redirects to the `/collections/{collectionId}` endpoint of OGC API-Coverages")
    @PostMapping(value = "/processes/{processId}/execution")
    public ResponseEntity<?> execute(@PathVariable("processId") String processName,
                                     @RequestBody ProcessRequestBody processRequestBody,
                                     @RequestParam(value = "response", required = false) String response)
            throws IOException, InterruptedException {
        log.info(processName + " process is runing...");
        String jobId = UUID.randomUUID().toString();
        if (response == null) {
            String localOutputDir = address.getLocalDataRoot() + jobId + "/";
            File jobIdFile = new File(address.getLocalDataRoot() + jobId);
            if (!jobIdFile.exists()) jobIdFile.mkdir();
            Job job = new Job();
            job.setProcessID(processName);
            job.setJobID(jobId);
            job.setStatus("accepted");
            job.setMessage("Process started");
            job.setProgress(0);
            LocalDateTime now = LocalDateTime.now();
            String formatted = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            job.setCreated(formatted);
            job.setLinks(Collections.singletonList(new Link(address.getGdcApiUrl() + "/jobs/" + jobId,
                    "status", "application/json", "Job status")));
            redisUtil.saveKeyValue("currentJob", jobId, 60 * 60 * 24);
            redisUtil.saveKeyValue(jobId, processName, 60 * 60 * 24);
            redisUtil.saveKeyValue(processName + "_" + jobId + "_state", "STARTED,0%", 60 * 60 * 24);
            redisUtil.saveKeyValue(processName + "_" + jobId, JSON.toJSONString(job), 60 * 60 * 24);
            String processRequestStr = JSON.toJSONString(processRequestBody);
//            sparkApplicationService.submitGDCWorkflow(sparkAppParas, processName, processRequestStr, jobId, localOutputDir, "false", "");
            sparkApplicationService.submitGDCWorkflowByLivy(jobId, processName, processRequestStr, localOutputDir, "false", "");
            return ResponseEntity.status(201).body(job);
        } else if (response.equals("collection")) {
            String tempCollection = "temp_" + jobId;
            redisUtil.saveKeyValue(tempCollection, JSON.toJSONString(processRequestBody), 60 * 60 * 24);
            return ResponseEntity.status(303).header("Location", address.getGdcApiUrl() + "/collections/" + tempCollection).build();
//            return ResponseEntity.status(302).header("Location", "http://125.220.153.26:8386/geocube/gdc_api_t19" + "/collections/" + tempCollection).build();
        } else {
            return ResponseEntity.status(500).body("Illegal parameter 'response'!");
        }
    }

    /**
     * Retrieve the status of a job.
     *
     * @return Job job
     */
    @ApiOperation(value = "Get the status of a job", notes = "Return the job status")
    @GetMapping("/jobs")
    public ResponseEntity<?> getJobList() {
        List<Job> jobs = new ArrayList<>();
        String currentJobId = redisUtil.getValueByKey("currentJob");
        if (currentJobId != null) {
            Job job = getJobStatus(currentJobId);
            if (job != null) {
                jobs.add(job);
            }
        }
        JobList jobList = new JobList();
        jobList.setJobs(jobs);
        List<Link> links = new ArrayList<>();
        links.add(new Link(address.getProcessApiUrl() + "/jobs", "self", "application/json", "the list of the job status"));
        jobList.setLinks(links);
        return ResponseEntity.ok(jobList);
    }

    /**
     * Retrieve the status of a job.
     *
     * @param jobId the job id
     * @return Job job
     */
    @ApiOperation(value = "Get the status of a job", notes = "Return the job status")
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<?> getStatus(@PathVariable("jobId") String jobId) {
        Job job = getJobStatus(jobId);
        if (job != null) {
            return ResponseEntity.ok(job);
        } else {
            return ResponseEntity.status(500).body(jobId + "has been timeout");
        }
    }

    /**
     * Get the status of the job
     *
     * @param jobId the id of the job
     * @return Job instance
     */
    public Job getJobStatus(String jobId) {
        String processName = redisUtil.getValueByKey(jobId);
        if (processName == null) {
            return null;
        }
        String jobStatus = redisUtil.getValueByKey(processName + "_" + jobId + "_state");
        String jobStr = redisUtil.getValueByKey(processName + "_" + jobId);
        if (jobStr == null || jobStatus == null) {
            log.error(jobId + "has been timeout");
            return null;
        } else {
            String status;
            String message;
            String statusRet = jobStatus.split(",")[0];
            Job job = JSON.parseObject(jobStr, Job.class);
            switch (statusRet) {
                case "STARTED":
                case "CONNECTED":
                case "SUBMITTED":
                    status = "accepted";
                    message = "Process started";
                    break;
                case "RUNNING":
                    status = "running";
                    message = "Process is running";
                    break;
                case "FINISHED":
                    status = "successful";
                    message = "Process is executed successfully";
                    break;
                case "FAILED":
                    status = "failed";
                    message = "Process failed";
                    break;
                default:
                    status = "dismissed";
                    message = "Process dismissed";
                    break;
            }
            job.setProcessID(processName);
            job.setStatus(status);
            job.setMessage(message);
            job.setProgress(Integer.valueOf(jobStatus.split(",")[1].replace("%", "")));
            List<Link> links = new ArrayList<>();
            links.add(new Link(address.getGdcApiUrl() + "/jobs/" + jobId,
                    "status", "application/json", "Job status"));
            if (statusRet.equals("FINISHED")) {
                links.add(new Link(address.getGdcApiUrl() + "/jobs/" + jobId + "/results",
                        "http://www.opengis.net/def/rel/ogc/1.0/results", "application/json", "Job result"));
            }
            job.setLinks(links);
            return job;
        }
    }

    /**
     * Retrieve the result(s) of a job/
     *
     * @param jobId the job id
     * @return Map<String, Object> here only the output "cube"
     * @throws IOException
     */
    @ApiOperation(value = "Get the result of a job", notes = "Return the job result")
    @GetMapping("/jobs/{jobId}/results")
    public ResponseEntity<?> getResults(@PathVariable("jobId") String jobId) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        String processName = redisUtil.getValueByKey(jobId);
        String status = redisUtil.getValueByKey(processName + "_" + jobId + "_state");
        if (status == null) {
            return ResponseEntity.status(500).body("The maximum retention period has been exceeded.");
        }
        String statusRet = status.split(",")[0];
        if (statusRet.equals("STARTED") || statusRet.equals("CONNECTED") || statusRet.equals("SUBMITTED") || statusRet.equals("RUNNING")) {
            return ResponseEntity.status(200).body(jobId + " is being processed");
        } else if (statusRet.equals("FINISHED")) {
            String resultPath = fileUtil.matchResultFile(address.getLocalDataRoot() + "/" + jobId);
            if (resultPath != null) {
//                String fileName = new File(resultPath).getName();
//                JSONObject imageObj = new JSONObject();
//                imageObj.put("href", "http://oge.whu.edu.cn/api/oge-data/data/gdc_api/" + jobId + "/" + fileName);
//                imageObj.put("type", "application/tiff; application=geotiff");
//                resultMap.put("cube", imageObj);
//                return ResponseEntity.ok(resultMap);
                return fileUtil.downloadFile(resultPath);
            } else {
                return ResponseEntity.status(500).body("An error occurred  retrieving data");
            }
        } else {
            return ResponseEntity.status(500).body("An error occurred during job " + jobId + " processing");
        }
    }

    /**
     * Cancel a job execution, remove a finished job.
     *
     * @param processName the process name
     * @param jobId       the job id
     * @return the Map<String, Object>
     */
    @ApiIgnore
    @ApiOperation(value = "Cancel a job execution", notes = "Cancel a job execution")
    @DeleteMapping("/processes/{processId}/jobs/{jobId}")
    public Job dismiss(@PathVariable("processId") String processName,
                       @PathVariable("jobId") String jobId) {
        String sparkAppId = redisUtil.getValueByKey(processName + "_" + jobId + "_sparkAppId");
        log.info("sparkAppId is " + sparkAppId);
        String jobStr = redisUtil.getValueByKey(processName + "_" + jobId);
        if (sparkAppId != null && jobStr != null) {
            Job job = JSON.parseObject(jobStr, Job.class);
            Runtime run = Runtime.getRuntime();
            String cmd = "curl -X POST \"http://125.220.153.26:9090/app/kill/?id=" + sparkAppId + "&terminate=true\"";
            try {
                Process process = run.exec(cmd);
                int status = process.waitFor();
                if (status != 0) {
                    job.setMessage("Fail to dismiss the process");
                } else {
                    job.setMessage("Process dismissed");
                    job.setStatus("dismissed");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return job;
        } else {
            return null;
        }
    }

    /**
     * Used to view the results.
     *
     * @param sessionId
     * @param jobId
     * @param resultName
     * @return
     * @throws IOException
     */
    @ApiIgnore
    @GetMapping(value = "/results/view/{sessionId}/{jobId}/{resultName}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] viewResults(@PathVariable("sessionId") String sessionId,
                              @PathVariable("jobId") String jobId,
                              @PathVariable("resultName") String resultName) throws IOException {
        String localDataPath = address.getLocalDataRoot() + sessionId + "/" + jobId + "/" + resultName;
        File file = new File(localDataPath);
        byte[] imgBytes = Files.readAllBytes(file.toPath());
        return imgBytes;
    }

}
