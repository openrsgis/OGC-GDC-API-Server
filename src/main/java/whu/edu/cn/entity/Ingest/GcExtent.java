package whu.edu.cn.entity.Ingest;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Description: 空间范围表
 * @Author: jeecg-boot
 * @Date: 2020-11-25
 * @Version: V1.0
 */
@Data
@TableName("gc_extent")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "gc_extent对象", description = "空间范围表")
public class GcExtent {

    /**
     * id
     */
    @TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
    private Integer id;
    /**
     * extentKey
     */

    @ApiModelProperty(value = "extentKey")
    private Integer extentKey;
    /**
     * gridCode
     */

    @ApiModelProperty(value = "gridCode")
    private String gridCode;
    /**
     * cityCode
     */

    @ApiModelProperty(value = "cityCode")
    private String cityCode;
    /**
     * cityName
     */

    @ApiModelProperty(value = "cityName")
    private String cityName;
    /**
     * provinceName
     */

    @ApiModelProperty(value = "provinceName")
    private String provinceName;
    /**
     * provinceCode
     */

    @ApiModelProperty(value = "provinceCode")
    private String provinceCode;
    /**
     * districtName
     */

    @ApiModelProperty(value = "districtName")
    private String districtName;
    /**
     * districtCode
     */

    @ApiModelProperty(value = "districtCode")
    private String districtCode;
    /**
     * extent
     */

    @ApiModelProperty(value = "extent")
    private Object extent;
    /**
     * resolutionKey
     */

    @ApiModelProperty(value = "resolutionKey")
    private Integer resolutionKey;
    /**
     * createBy
     */

    @ApiModelProperty(value = "createBy")
    private String createBy;
    /**
     * createTime
     */
    @ApiModelProperty(value = "createTime")
    private Date createTime;
    /**
     * updateBy
     */

    @ApiModelProperty(value = "updateBy")
    private String updateBy;
    /**
     * updateTime
     */
    @ApiModelProperty(value = "updateTime")
    private Date updateTime;

    public GcExtent() {
    }

    public GcExtent(Integer id, Integer extentKey, String gridCode, String cityCode, String cityName, String provinceName,
                    String provinceCode, String districtName, String districtCode, Object extent, Integer resolutionKey,
                    String createBy, Date createTime, String updateBy, Date updateTime) {
        this.id = id;
        this.extentKey = extentKey;
        this.gridCode = gridCode;
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.provinceName = provinceName;
        this.provinceCode = provinceCode;
        this.districtName = districtName;
        this.districtCode = districtCode;
        this.extent = extent;
        this.resolutionKey = resolutionKey;
        this.createBy = createBy;
        this.createTime = createTime;
        this.updateBy = updateBy;
        this.updateTime = updateTime;
    }
}
