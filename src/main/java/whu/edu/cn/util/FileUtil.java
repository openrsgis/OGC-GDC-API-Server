package whu.edu.cn.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Component
@Slf4j
public class FileUtil {

    /**
     * Gets the matching result from the output folder whose file type is tif, nc and png
     * @param outputDir the output directory
     * @return the matching file path, if not match return null
     */
    public String matchResultFile(String outputDir) {
        File folder = new File(outputDir);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".tif")
                            || name.toLowerCase().endsWith(".nc")
                            || name.toLowerCase().endsWith(".png");
                }
            });

            if (files != null && files.length > 0) {
                return files[0].getAbsolutePath();
            } else {
                System.out.println("No matching files found in the folder.");
                return null;
            }
        } else {
            System.out.println("Invalid folder path.");
            return null;
        }
    }

    public HttpHeaders matchContentType(String fileName){
        HttpHeaders headers = new HttpHeaders();
        log.info("fileName is :" + fileName);
        if(fileName.endsWith(".tif")){
            log.info("This is a tif");
            headers.setContentType(MediaType.valueOf("image/tiff; application=geotiff"));
        }else if(fileName.endsWith(".nc")){
            headers.setContentType(MediaType.valueOf("application/x-netcdf"));
        }else if(fileName.endsWith(".png")){
            headers.setContentType(MediaType.IMAGE_PNG);
        }
        return headers;
    }

    /**
     * Downloads a file from the specified file path and returns it as a ResponseEntity object.
     * The method creates a Path object from the file path and a UrlResource object from the Path object.
     * If the resource exists, the method returns a ResponseEntity object with the resource as the body and a content disposition header that specifies that the file should be downloaded as an attachment.
     * Otherwise, the method returns a ResponseEntity object with a not found status.
     * @param filePath the file path of the file to download
     * @return a ResponseEntity object with the downloaded file as the body and a content disposition header that specifies that the file should be downloaded as an attachment, or a not found status if the file does not exist
     * @throws MalformedURLException if the file path is not a valid URL
     */
    public ResponseEntity<Resource> downloadFile(String filePath) throws MalformedURLException {
        Path file = Paths.get(filePath);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists()) {
            HttpHeaders headers = matchContentType(file.getFileName().toString());
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(Objects.requireNonNull(resource.getFilename())).build());
            return ResponseEntity.ok().headers(headers)
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
