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
 * @Description: 栅格事实表
 * @Author: jeecg-boot
 * @Date:   2020-08-29
 * @Version: V1.0
 */
@Data
@TableName("gc_raster_tile_fact")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="gc_raster_tile_fact对象", description="栅格事实表")
public class GcRasterTileFact {
    
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**factKey*/

    @ApiModelProperty(value = "factKey")
	private Integer factKey;
	/**productKey*/

    @ApiModelProperty(value = "productKey")
	private Integer productKey;
	/**extentKey*/

    @ApiModelProperty(value = "extentKey")
	private Integer extentKey;
	/**measurementKey*/

    @ApiModelProperty(value = "measurementKey")
	private Integer measurementKey;
	/**tileQualityKey*/

    @ApiModelProperty(value = "tileQualityKey")
	private Integer tileQualityKey;
	/**tileDataId*/

    @ApiModelProperty(value = "tileDataId")
	private Object tileDataId;
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
