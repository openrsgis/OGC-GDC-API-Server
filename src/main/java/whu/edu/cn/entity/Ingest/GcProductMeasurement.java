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
 * @Description: 产品波段对照表
 * @Author: jeecg-boot
 * @Date:   2020-06-28
 * @Version: V1.0
 */
@Data
@TableName("gc_product_measurement")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="gc_product_measurement对象", description="产品波段对照表")
public class GcProductMeasurement {
    
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**productKey*/

    @ApiModelProperty(value = "productKey")
	private Integer productKey;
	/**measurementKey*/

    @ApiModelProperty(value = "measurementKey")
	private Integer measurementKey;
	/**dtype*/

    @ApiModelProperty(value = "dtype")
	private String dtype;
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
