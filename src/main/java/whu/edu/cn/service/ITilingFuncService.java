package whu.edu.cn.service;

import java.io.IOException;

public interface ITilingFuncService {

    void submitTiling(String appResource, String type, String threadSize, String hbaseTableName,
                      Integer gridDimX, Integer gridDimY, Integer minX, Integer minY,
                      Integer maxX, Integer maxY, String fileName)
            throws IOException, InterruptedException;
}
