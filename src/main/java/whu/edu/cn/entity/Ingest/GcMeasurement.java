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
 * @Description: 波段表
 * @Author: jeecg-boot
 * @Date: 2020-04-26
 * @Version: V1.0
 */
@Data
@TableName("gc_measurement")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "gc_measurement对象", description = "波段表")
public class GcMeasurement {

    /**
     * id
     */
    @TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
    private Integer id;

    /**
     * measurementKey
     */
    @ApiModelProperty(value = "measurementKey")
    private Integer measurementKey;

    /**
     * measurementName
     */
    @ApiModelProperty(value = "measurementName")
    private String measurementName;

    /**
     * polarisation
     */
    @ApiModelProperty(value = "polarisation")
    private String polarisation;

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

    /**
     * unit
     */
    @ApiModelProperty(value = "unit")
    private String unit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMeasurementKey() {
        return measurementKey;
    }

    public void setMeasurementKey(Integer measurementKey) {
        this.measurementKey = measurementKey;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public String getPolarisation() {
        return polarisation;
    }

    public void setPolarisation(String polarisation) {
        this.polarisation = polarisation;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
