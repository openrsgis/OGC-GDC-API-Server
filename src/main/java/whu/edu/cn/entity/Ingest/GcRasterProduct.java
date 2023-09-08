package whu.edu.cn.entity.Ingest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author thf
 * @version 1.0
 * @date 2020/12/11 20:55
 */

@NoArgsConstructor
@Data
public class GcRasterProduct {

    /**
     * productName : LC08_L1TP_SCENE
     * description : Landsat 8 EO Product
     * type : EO
     * metadata : {"platformName":"LANDSAT_8","instrument":"OLI_TIRS","processingLevel":"LEVEL1","format":"GeoTiff"}
     * storage : {"crs":"EPSG:32651","resolution":[30,30]}
     * measurements : [{"name":"Coastal","dtype":"Float32","nodata":"Float.NaN","units":"1","aliases":"[band_1,Coastal]"},{"name":"Blue","dtype":"Float32","nodata":"Float.NaN","units":"1","aliases":"[band_2,Blue]"},{"name":"Green","dtype":"Float32","nodata":"Float.NaN","units":"1","aliases":"[band_3,Green]"},{"name":"Red","dtype":"Float32","nodata":"Float.NaN","units":"1","aliases":"[band_4,Red]"},{"name":"Infrared","dtype":"Float32","nodata":"Float.NaN","units":"1","aliases":"[band_5,Near-Infrared]"},{"name":"SWIR 1","dtype":"Float32","nodata":"Float.NaN","units":"1","aliases":"[band_6,SWIR 1]"},{"name":"SWIR 2","dtype":"Float32","nodata":"Float.NaN","units":"1","aliases":"[band_7,SWIR 2]"},{"name":"Pan","dtype":"Float32","nodata":"Float.NaN","units":"1","aliases":"[band_8,Pan]"},{"name":"Cirrus","dtype":"Float32","nodata":"Float.NaN","units":"1","aliases":"[band_9,Cirrus]"},{"name":"TIRS 1","dtype":"Float32","nodata":"Float.NaN","units":"1","aliases":"[band_10,TIRS 1]"},{"name":"TIRS 2","dtype":"Float32","nodata":"Float.NaN","units":"1","aliases":"[band_11,TIRS 2]"},{"name":"Quality Assessment","dtype":"uint16raw","nodata":"Float.NaN","units":"1","aliases":"[band_QA,Quality Assessment]"}]
     */

    @JsonProperty("productName")
    private String productName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("type")
    private String type;
    @JsonProperty("metadata")
    private MetadataDTO metadata;
    @JsonProperty("storage")
    private StorageDTO storage;
    @JsonProperty("measurements")
    private List<MeasurementsDTO> measurements;

    @NoArgsConstructor
    @Data
    public static class MetadataDTO {
        /**
         * platformName : LANDSAT_8
         * instrument : OLI_TIRS
         * processingLevel : LEVEL1
         * format : GeoTiff
         */

        @JsonProperty("platformName")
        private String platformName;
        @JsonProperty("instrument")
        private String instrument;
        @JsonProperty("processingLevel")
        private String processingLevel;
        @JsonProperty("format")
        private String format;
    }

    @NoArgsConstructor
    @Data
    public static class StorageDTO {
        /**
         * crs : EPSG:32651
         * resolution : [30,30]
         */

        @JsonProperty("crs")
        private String crs;
        @JsonProperty("resolution")
        private List<Integer> resolution;
    }

    @NoArgsConstructor
    @Data
    public static class MeasurementsDTO {
        /**
         * name : Coastal
         * dtype : Float32
         * nodata : Float.NaN
         * units : 1
         * aliases : [band_1,Coastal]
         */

        @JsonProperty("name")
        private String name;
        @JsonProperty("dtype")
        private String dtype;
        @JsonProperty("nodata")
        private String nodata;
        @JsonProperty("units")
        private String units;
        @JsonProperty("aliases")
        private String aliases;
    }
}
