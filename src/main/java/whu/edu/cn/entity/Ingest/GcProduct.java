package whu.edu.cn.entity.Ingest;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import whu.edu.cn.entity.Ingest.conf.AdditionalDimensionValueConf;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description: 产品表
 * @Author: jeecg-boot
 * @Date:   2020-08-29
 * @Version: V1.0
 */
@Data
@TableName("gc_product")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="gc_product对象", description="产品表")
public class GcProduct {
    
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**productKey*/

    @ApiModelProperty(value = "productKey")
	private Integer productKey;
	/**productName*/

    @ApiModelProperty(value = "productName")
	private Object productName;
	/**productIdentification*/

    @ApiModelProperty(value = "productIdentification")
	private Object productIdentification;
	/**productType*/

    @ApiModelProperty(value = "productType")
	private String productType;
	/**sensorKey*/

    @ApiModelProperty(value = "sensorKey")
	private Integer sensorKey;
	/**resolutionKey*/

    @ApiModelProperty(value = "resolutionKey")
	private Integer resolutionKey;
	/**crs*/

    @ApiModelProperty(value = "crs")
	private String crs;
	/**phenomenonTime*/
    @ApiModelProperty(value = "phenomenonTime")
	private Date phenomenonTime;
	/**resultTime*/
    @ApiModelProperty(value = "resultTime")
	private Date resultTime;
	/**geom*/

    @ApiModelProperty(value = "geom")
	private Object geom;
	/**upperLeftLat*/

    @ApiModelProperty(value = "upperLeftLat")
	private java.math.BigDecimal upperLeftLat;
	/**upperLeftLong*/

    @ApiModelProperty(value = "upperLeftLong")
	private java.math.BigDecimal upperLeftLong;
	/**upperRightLat*/

    @ApiModelProperty(value = "upperRightLat")
	private java.math.BigDecimal upperRightLat;
	/**upperRightLong*/

    @ApiModelProperty(value = "upperRightLong")
	private java.math.BigDecimal upperRightLong;
	/**lowerLeftLat*/

    @ApiModelProperty(value = "lowerLeftLat")
	private java.math.BigDecimal lowerLeftLat;
	/**lowerLeftLong*/

    @ApiModelProperty(value = "lowerLeftLong")
	private java.math.BigDecimal lowerLeftLong;
	/**lowerRightLat*/

    @ApiModelProperty(value = "lowerRightLat")
	private java.math.BigDecimal lowerRightLat;
	/**lowerRightLong*/

    @ApiModelProperty(value = "lowerRightLong")
	private java.math.BigDecimal lowerRightLong;
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

	private String measurementName;

	private String  fileDir;

    private List<AdditionalDimensionValueConf> additionalDimensionValueConfList;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProductKey() {
		return productKey;
	}

	public void setProductKey(Integer productKey) {
		this.productKey = productKey;
	}

	public Object getProductName() {
		return productName;
	}

	public void setProductName(Object productName) {
		this.productName = productName;
	}

	public Object getProductIdentification() {
		return productIdentification;
	}

	public void setProductIdentification(Object productIdentification) {
		this.productIdentification = productIdentification;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public Integer getSensorKey() {
		return sensorKey;
	}

	public void setSensorKey(Integer sensorKey) {
		this.sensorKey = sensorKey;
	}

	public Integer getResolutionKey() {
		return resolutionKey;
	}

	public void setResolutionKey(Integer resolutionKey) {
		this.resolutionKey = resolutionKey;
	}

	public String getCrs() {
		return crs;
	}

	public void setCrs(String crs) {
		this.crs = crs;
	}

	public Date getPhenomenonTime() {
		return phenomenonTime;
	}

	public void setPhenomenonTime(Date phenomenonTime) {
		this.phenomenonTime = phenomenonTime;
	}

	public Date getResultTime() {
		return resultTime;
	}

	public void setResultTime(Date resultTime) {
		this.resultTime = resultTime;
	}

	public Object getGeom() {
		return geom;
	}

	public void setGeom(Object geom) {
		this.geom = geom;
	}

	public BigDecimal getUpperLeftLat() {
		return upperLeftLat;
	}

	public void setUpperLeftLat(BigDecimal upperLeftLat) {
		this.upperLeftLat = upperLeftLat;
	}

	public BigDecimal getUpperLeftLong() {
		return upperLeftLong;
	}

	public void setUpperLeftLong(BigDecimal upperLeftLong) {
		this.upperLeftLong = upperLeftLong;
	}

	public BigDecimal getUpperRightLat() {
		return upperRightLat;
	}

	public void setUpperRightLat(BigDecimal upperRightLat) {
		this.upperRightLat = upperRightLat;
	}

	public BigDecimal getUpperRightLong() {
		return upperRightLong;
	}

	public void setUpperRightLong(BigDecimal upperRightLong) {
		this.upperRightLong = upperRightLong;
	}

	public BigDecimal getLowerLeftLat() {
		return lowerLeftLat;
	}

	public void setLowerLeftLat(BigDecimal lowerLeftLat) {
		this.lowerLeftLat = lowerLeftLat;
	}

	public BigDecimal getLowerLeftLong() {
		return lowerLeftLong;
	}

	public void setLowerLeftLong(BigDecimal lowerLeftLong) {
		this.lowerLeftLong = lowerLeftLong;
	}

	public BigDecimal getLowerRightLat() {
		return lowerRightLat;
	}

	public void setLowerRightLat(BigDecimal lowerRightLat) {
		this.lowerRightLat = lowerRightLat;
	}

	public BigDecimal getLowerRightLong() {
		return lowerRightLong;
	}

	public void setLowerRightLong(BigDecimal lowerRightLong) {
		this.lowerRightLong = lowerRightLong;
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

	public List<AdditionalDimensionValueConf> getAdditionalDimensionValueConfList() {
		return additionalDimensionValueConfList;
	}

	public void setAdditionalDimensionValueConfList(List<AdditionalDimensionValueConf> additionalDimensionValueConfList) {
		this.additionalDimensionValueConfList = additionalDimensionValueConfList;
	}

	public String getMeasurementName() {
		return measurementName;
	}

	public void setMeasurementName(String measurementName) {
		this.measurementName = measurementName;
	}

	public String getFileDir() {
		return fileDir;
	}

	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}
}
