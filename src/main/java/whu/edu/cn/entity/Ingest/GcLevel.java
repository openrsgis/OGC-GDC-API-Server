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
 * @Description: 层级表
 * @Author: jeecg-boot
 * @Date:   2020-04-26
 * @Version: V1.0
 */
@Data
@TableName("gc_level")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="gc_level对象", description="层级表")
public class GcLevel {
    
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private Integer id;

	/**resolutionKey*/
    @ApiModelProperty(value = "resolutionKey")
	private Integer resolutionKey;

	/**tileSize*/
    @ApiModelProperty(value = "tileSize")
	private Integer tileSize;

	/**tileExtent*/
	@ApiModelProperty(value = "tileExtent")
	private Double tileExtent;

	/**cellRes*/
    @ApiModelProperty(value = "cellRes")
	private String cellRes;

	/**level*/
    @ApiModelProperty(value = "level")
	private Integer level;

	/**createBy*/
    @ApiModelProperty(value = "createBy")
	private String createBy;

	/**createTime*/
    @ApiModelProperty(value = "createTime")
	private Date createTime;

	/**updateBy*/
    @ApiModelProperty(value = "updateBy")
	private String updateBy;

	/**updateTime*/
    @ApiModelProperty(value = "updateTime")
	private Date updateTime;

	/**crs*/
	@ApiModelProperty(value = "crs")
	private String crs;

	/**tileExtent*/
	@ApiModelProperty(value = "unit")
	private String unit;



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getResolutionKey() {
		return resolutionKey;
	}

	public void setResolutionKey(Integer resolutionKey) {
		this.resolutionKey = resolutionKey;
	}

	public Integer getTileSize() {
		return tileSize;
	}

	public void setTileSize(Integer tileSize) {
		this.tileSize = tileSize;
	}

	public String getCellRes() {
		return cellRes;
	}

	public void setCellRes(String cellRes) {
		this.cellRes = cellRes;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
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

	public Double getTileExtent() {
		return tileExtent;
	}

	public void setTileExtent(Double tileExtent) {
		this.tileExtent = tileExtent;
	}

	public String getCrs() {
		return crs;
	}

	public void setCrs(String crs) {
		this.crs = crs;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
