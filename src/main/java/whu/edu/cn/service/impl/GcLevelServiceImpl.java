package whu.edu.cn.service.impl;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.Ingest.GcLevel;
import whu.edu.cn.entity.Ingest.conf.GcCubeConf;
import whu.edu.cn.mapper.GcLevelMapper;
import whu.edu.cn.service.IGcLevelService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GcLevelServiceImpl implements IGcLevelService {

    @Resource
    private GcLevelMapper gcLevelMapper;

    /**
     * get the max resolution key
     *
     * @return the max resolution key
     */
    @Override
    public Integer getMaxResolutionKey() {
        return gcLevelMapper.getMaxResolutionKey();
    }

    /**
     * insert a record into the gc_level table
     *
     * @param gcLevel the GcLevel object
     * @return if successful return true else return false
     */
    @Override
    public boolean insertGcLevel(GcLevel gcLevel) {
        return gcLevelMapper.insertGcLevel(gcLevel);
    }

    /**
     * get the GcLevel from gcCubeConf
     *
     * @param gcCubeConf the cube conf object
     * @return GcLevel object
     */
    @Override
    public GcLevel getGcLevelByParam(GcCubeConf gcCubeConf) {
        GcLevel gcLevel = new GcLevel();
        Integer maxResolutionKey = getMaxResolutionKey();
        gcLevel.setId(gcLevelMapper.getMaxId() + 1);
        gcLevel.setResolutionKey(maxResolutionKey + 1);
        gcLevel.setLevel(maxResolutionKey + 1);
        gcLevel.setCrs(gcCubeConf.getCrs());
        gcLevel.setTileSize(gcCubeConf.getCellRes());
        gcLevel.setTileExtent(gcCubeConf.getCellSize());
        if (gcLevel.getCrs().equals("EPSG:4326")) {
            gcLevel.setUnit("°");
            gcLevel.setCellRes("(" + gcLevel.getTileExtent() + "/" + gcLevel.getTileSize() + ")°" + "×(" +
                    gcLevel.getTileExtent() + "/" + gcLevel.getTileSize() + ")°");
        }
        return gcLevel;
    }

    /**
     * get the record of the gc_level
     *
     * @param tileSize   the pixel size of the tile
     * @param tileExtent the extent of the tile
     * @param crs        the crs code
     * @return boolean if exist return true else return false
     */
    public List<GcLevel> getExistGcLevel(Integer tileSize, Double tileExtent, String crs) {
        return gcLevelMapper.getGcLevel(tileSize, tileExtent, crs);
    }


    /**
     * get the cell pixel size and crs size from cellRes
     *
     * @param cellRes the cellRes from table (0.1/1200)°×(0.1/1200)° ---> [0.1 1200 0.1 1200]
     * @return
     */
    public List<Double> getCellSizeAndRes(String cellRes) {

        String pattern = "\\((\\d+\\.\\d+)/(\\d+)\\)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(cellRes);
        List<Double> cellResList = new ArrayList<>();
        while (matcher.find()) {
            String group1 = matcher.group(1);
            String group2 = matcher.group(2);
            cellResList.add(Double.parseDouble(group1));
            cellResList.add(Double.parseDouble(group2));
        }
        return cellResList;
    }


}
