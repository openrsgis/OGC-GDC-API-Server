package whu.edu.cn.service.impl;

import whu.edu.cn.entity.Ingest.GcProduct;
import whu.edu.cn.mapper.GcProductMapper;
import whu.edu.cn.service.IGcProductService;
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
 * @Description: 产品表
 * @Author: jeecg-boot
 * @Date:   2020-08-29
 * @Version: V1.0
 */
@Service
public class GcProductServiceImpl extends ServiceImpl<GcProductMapper, GcProduct> implements IGcProductService {
    @Resource
    private GcProductMapper gcProductMapper;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertBatch(List<GcProduct> entityList) {
        return insertBatch(entityList, 1000);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertBatch(List<GcProduct> entityList, int batchSize) {
        long startTime=System.currentTimeMillis();
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        }
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            int size = entityList.size();
            System.out.println("=====product 开始插入共"+size+"个数据=====");
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
    public Integer getMaxProductKey(String cubeId) {
        return gcProductMapper.getMaxProductKey(cubeId);
    }

    @Override
    public Integer getMaxProductId(String cubeId) {
        return gcProductMapper.getMaxProductId(cubeId);
    }

    @Override
    public List<GcProduct> getProductFromCombinedParam(String combinedParam, String cubeId) {
        return gcProductMapper.getProductFromCombinedParam(combinedParam, cubeId);
    }
}
