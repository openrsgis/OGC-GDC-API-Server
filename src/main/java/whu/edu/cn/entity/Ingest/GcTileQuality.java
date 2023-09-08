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
 * @Description: 瓦片质量表
 * @Author: jeecg-boot
 * @Date:   2020-04-26
 * @Version: V1.0
 */
@Data
@TableName("gc_tile_quality")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="gc_tile_quality对象", description="瓦片质量表")
public class GcTileQuality {
    
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**tileQualityKey*/

    @ApiModelProperty(value = "tileQualityKey")
	private Integer tileQualityKey;
	/**cloud*/

    @ApiModelProperty(value = "cloud")
	private java.math.BigDecimal cloud;
	/**cloudshadow*/

    @ApiModelProperty(value = "cloudshadow")
	private java.math.BigDecimal cloudshadow;
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
