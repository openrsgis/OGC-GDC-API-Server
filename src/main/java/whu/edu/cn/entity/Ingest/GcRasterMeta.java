package whu.edu.cn.entity.Ingest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author thf
 * @version 1.0
 * @date 2020/12/11 11:49
 */
@NoArgsConstructor
@Data
public class GcRasterMeta {
    /**
     * productName : LC08_L1TP_SCENE
     * crs : PROJCS["WGS 84 / UTM zone 52N",GEOGCS["WGS 84",DATUM["WGS_1984",SPHEROID["WGS 84",6378137,298.257223563,AUTHORITY["EPSG","7030"]],AUTHORITY["EPSG","6326"]],PRIMEM["Greenwich",0,AUTHORITY["EPSG","8901"]],UNIT["degree",0.0174532925199433,AUTHORITY["EPSG","9122"]],AUTHORITY["EPSG","4326"]],PROJECTION["Transverse_Mercator"],PARAMETER["latitude_of_origin",0],PARAMETER["central_meridian",129],PARAMETER["scale_factor",0.9996],PARAMETER["false_easting",500000],PARAMETER["false_northing",0],UNIT["metre",1,AUTHORITY["EPSG","9001"]],AXIS["Easting",EAST],AXIS["Northing",NORTH],AUTHORITY["EPSG","32652"]]
     * geometry : {"type":"Polygon","coordinates":"[[37.00713, 127.92884], [37.00022, 130.67005],[35.07522, 130.62988], [35.08167, 35.08167], [37.00713, 127.92884]]"}
     * extent : [187800,3076500,419700,3312900]
     * location : hdfs://host:port/Gc_raster_metadata.json
     * measurements : [{"name":"blue","path":"B1.TIF"},{"name":"green","path":"B2.TIF"},{"name":"red","path":"B3.TIF"},{"name":"NIR","path":"B4.TIF"},{"name":"SWIR1","path":"B5.TIF"},{"name":"TIR1","path":"B6_VCID_1.TIF"},{"name":"TIR2","path":"B6_VCID_2.TIF"},{"name":"SWIR2","path":"B7.TIF"},{"name":"PAN","path":"B8.TIF"}]
     * properties : {"phenomenonTime":"2017-4-30T02:01:31.7343647Z","format":"GeoTIFF","resultTime":"2017-4-30T02:01:31.7343647Z"}
     */

    @JsonProperty("productName")
    private String productName;
    @JsonProperty("crs")
    private String crs;
    @JsonProperty("geometry")
    private GeometryDTO geometry;
    @JsonProperty("location")
    private String location;
    @JsonProperty("properties")
    private PropertiesDTO properties;
    @JsonProperty("extent")
    private List<Double> extent;
    @JsonProperty("measurements")
    private List<MeasurementsDTO> measurements;

    @NoArgsConstructor
    @Data
    public static class GeometryDTO {
        /**
         * type : Polygon
         * coordinates : [[37.00713, 127.92884], [37.00022, 130.67005],[35.07522, 130.62988], [35.08167, 35.08167], [37.00713, 127.92884]]
         */

        @JsonProperty("type")
        private String type;
        @JsonProperty("coordinates")
        private String coordinates;
    }

    @NoArgsConstructor
    @Data
    public static class PropertiesDTO {
        /**
         * phenomenonTime : 2017-4-30T02:01:31.7343647Z
         * format : GeoTIFF
         * resultTime : 2017-4-30T02:01:31.7343647Z
         */

        @JsonProperty("phenomenonTime")
        private String phenomenonTime;
        @JsonProperty("format")
        private String format;
        @JsonProperty("resultTime")
        private String resultTime;
    }

    @NoArgsConstructor
    @Data
    public static class MeasurementsDTO {
        /**
         * name : blue
         * path : B1.TIF
         */

        @JsonProperty("name")
        private String name;
        @JsonProperty("path")
        private String path;
    }
}
