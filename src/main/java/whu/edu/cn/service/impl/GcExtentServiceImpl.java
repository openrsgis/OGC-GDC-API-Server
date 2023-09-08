package whu.edu.cn.service.impl;

import whu.edu.cn.entity.Ingest.GcExtent;
import whu.edu.cn.mapper.GcExtentMapper;
import whu.edu.cn.service.IGcExtentService;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 空间范围表
 * @Author: jeecg-boot
 * @Date:   2020-11-25
 * @Version: V1.0
 */
@Service
public class GcExtentServiceImpl extends ServiceImpl<GcExtentMapper, GcExtent> implements IGcExtentService {
    @Resource
    private GcExtentMapper gcExtentMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertBatch(List<GcExtent> entityList) {
        return insertBatch(entityList, 1000);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertBatch(List<GcExtent> entityList, int batchSize) {
        long startTime=System.currentTimeMillis();
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        }
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            int size = entityList.size();
            System.out.println("=====extent 开始插入共"+size+"个数据=====");
            String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
            for (int i = 0; i < size; i++) {
                batchSqlSession.insert(sqlStatement, entityList.get(i));
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                long endTime=System.currentTimeMillis();
                if (i%1000==0){
                    System.out.println("插入"+i+"个耗时"+(endTime-startTime));
                }
                if (i==size-1){
                    System.out.println("总共插入"+size+"个耗时"+(endTime-startTime));
                }
            }
            batchSqlSession.flushStatements();
        } catch (Throwable e) {
            throw new MybatisPlusException("Error: Cannot execute insertBatch Method. Cause", e);
        }
        return true;
    }

    @Override
    public Integer getMaxExtentId() {
        return gcExtentMapper.getMaxExtentId();
    }

    @Override
    public Integer getMaxExtentKey() {
        return gcExtentMapper.getMaxExtentKey();
    }

    @Override
    public List<GcExtent> getExtentFromCombinedParam(String combinedParam, String cubeId) {
        return gcExtentMapper.getExtentFromCombinedParam(combinedParam, cubeId);
    }

    @Override
    public List<GcExtent> getTMSExtentFromCombinedParam(String combinedParam) {
        return gcExtentMapper.getTMSExtentFromCombinedParam(combinedParam);
    }

}
