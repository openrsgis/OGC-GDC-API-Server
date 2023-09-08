package whu.edu.cn.definition;

import whu.edu.cn.entity.Ingest.GcImageMetaData;
import whu.edu.cn.entity.Ingest.GcRasterIngest;
import whu.edu.cn.entity.Ingest.GcRasterProduct;
import lombok.Data;

import java.util.HashMap;

/**
 * @author czp
 * @version 1.0
 * @date 2021/2/18 21:44
 */
@Data
public class GcConf {
    private HashMap<String,HashMap<String,GcImageMetaData>> productsInfo;
    private GcRasterIngest gcRasterIngest;
    private GcRasterProduct gcRasterProduct;
}
