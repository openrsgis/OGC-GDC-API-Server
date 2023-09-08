package whu.edu.cn.entity.Ingest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author thf
 * @version 1.0
 * @date 2020/12/11 20:50
 */
@NoArgsConstructor
@Data
public class GcRasterIngest {
    /**
     * sourceType : LC08_L1TP_SCENE
     * outputType : LC08_L1TP_ARD
     * description : Landsat 8 ARD PRODUCT
     * globalAttributes : {"title":"GeoCube Landsat Surface Reflectance","summary":"Landsat 8 ARD data published by WHU.","orgnization":"WHU","instrument":"OLI_TIRS","platform":"LANDSAT-8"}
     * storage : {"driver":"Array[byte]","crs":"EPSG:4326","tileSize":{"x":4000,"y":4000},"resolution":{"x":25,"y":25}}
     * measurements : [{"name":"blue","dtype":"Float32","nodata":"Float.NaN","resamplingMethod":"nearest","attrs":{"description":"Surface Reflectance 0.45-0.52 microns (Blue)","alias":"band_1"}},{"name":"green","dtype":"Float32","nodata":"Float.NaN","resamplingMethod":"nearest","attrs":{"description":"Surface Reflectance 0.52-0.60 microns (Green)","alias":"band_2"}}]
     */

    @JsonProperty("cubeId")
    private Integer cubeId;

    @JsonProperty("sourceType")
    private String sourceType;
    @JsonProperty("outputType")
    private String outputType;
    @JsonProperty("description")
    private String description;
    @JsonProperty("globalAttributes")
    private GlobalAttributesDTO globalAttributes;
    @JsonProperty("storage")
    private StorageDTO storage;
    @JsonProperty("measurements")
    private List<MeasurementsDTO> measurements;
    @JsonProperty("metaConfPath")
    private List<String> metaConfPath;
    @JsonProperty(" pyramid")
    private String  pyramid;

    @NoArgsConstructor
    @Data
    public static class GlobalAttributesDTO {
        /**
         * title : GeoCube Landsat Surface Reflectance
         * summary : Landsat 8 ARD data published by WHU.
         * orgnization : WHU
         * instrument : OLI_TIRS
         * platform : LANDSAT-8
         */

        @JsonProperty("title")
        private String title;
        @JsonProperty("summary")
        private String summary;
        @JsonProperty("orgnization")
        private String orgnization;
        @JsonProperty("instrument")
        private String instrument;
        @JsonProperty("platform")
        private String platform;
    }

    @NoArgsConstructor
    @Data
    public static class StorageDTO {
        /**
         * driver : Array[byte]
         * crs : EPSG:4326
         * tileSize : {"x":4000,"y":4000}
         * resolution : {"x":25,"y":25}
         */

        @JsonProperty("driver")
        private String driver;
        @JsonProperty("crs")
        private String crs;
        @JsonProperty("tileSize")
        private TileSizeDTO tileSize;

        @JsonProperty("tileReSize")
        private TileReSizeDTO tileReSize;

        @JsonProperty("resolution")
        private ResolutionDTO resolution;

        @NoArgsConstructor
        @Data
        public static class TileSizeDTO {
            /**
             * x : 1
             * y : 1
             */

            @JsonProperty("x")
            private Integer x;
            @JsonProperty("y")
            private Integer y;
        }
        @NoArgsConstructor
        @Data
        public static class TileReSizeDTO {
            /**
             * x : 4000
             * y : 4000
             */

            @JsonProperty("x")
            private Integer x;
            @JsonProperty("y")
            private Integer y;
        }
        @NoArgsConstructor
        @Data
        public static class ResolutionDTO {
            /**
             * x : 25.0
             * y : 25.0
             */

            @JsonProperty("x")
            private Double x;
            @JsonProperty("y")
            private Double y;
        }
    }

    @NoArgsConstructor
    @Data
    public static class MeasurementsDTO {
        /**
         * name : blue
         * dtype : Float32
         * nodata : Float.NaN
         * resamplingMethod : nearest
         * attrs : {"description":"Surface Reflectance 0.45-0.52 microns (Blue)","alias":"band_1"}
         */

        @JsonProperty("name")
        private String name;
        @JsonProperty("dtype")
        private String dtype;
        @JsonProperty("nodata")
        private String nodata;
        @JsonProperty("resamplingMethod")
        private String resamplingMethod;
        @JsonProperty("attrs")
        private AttrsDTO attrs;

        @NoArgsConstructor
        @Data
        public static class AttrsDTO {
            /**
             * description : Surface Reflectance 0.45-0.52 microns (Blue)
             * alias : band_1
             */

            @JsonProperty("description")
            private String description;
            @JsonProperty("alias")
            private String alias;
        }
    }
}
