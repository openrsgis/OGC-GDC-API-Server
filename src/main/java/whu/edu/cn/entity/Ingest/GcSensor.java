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
 * @Description: 传感器表
 * @Author: jeecg-boot
 * @Date:   2020-04-26
 * @Version: V1.0
 */
@Data
@TableName("gc_sensor")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="gc_sensor对象", description="传感器表")
public class GcSensor {
    
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**sensorKey*/

    @ApiModelProperty(value = "sensorKey")
	private Integer sensorKey;
	/**sensorName*/

    @ApiModelProperty(value = "sensorName")
	private String sensorName;
	/**platformName*/

    @ApiModelProperty(value = "platformName")
	private String platformName;
	/**imagingLength*/

    @ApiModelProperty(value = "imagingLength")
	private Integer imagingLength;
	/**imagingWidth*/

    @ApiModelProperty(value = "imagingWidth")
	private Integer imagingWidth;
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
}
