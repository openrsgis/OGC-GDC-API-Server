package whu.edu.cn.config.mybatis;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.DynamicTableNameParser;
import com.baomidou.mybatisplus.extension.parsers.ITableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单数据源配置（jeecg.datasource.open = false时生效）
 * @Author zhoujf
 *
 */
@Configuration
@MapperScan(value={"cn.whu.geois.modules.**.mapper*"})
public class MybatisPlusConfig {
    public static ThreadLocal<String> cubeId=new ThreadLocal<>();

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor=new PaginationInterceptor();
        // 创建SQL解析器集合
        List<ISqlParser> sqlParserList = new ArrayList<>();
        // 动态表名SQL解析器
        DynamicTableNameParser dynamicTableNameParser = new DynamicTableNameParser();
        Map<String,ITableNameHandler> tableNameHandlerMap = new HashMap<>();
        tableNameHandlerMap.put("gc_extent", new ITableNameHandler() {
            @Override
            public String dynamicTableName(MetaObject metaObject, String sql, String tableName) {
                if (cubeId.get()==null){
                    return null;
                }else{
                    return "gc_extent_"+cubeId.get();
                }

            }
        });
        tableNameHandlerMap.put("gc_product", new ITableNameHandler() {
            @Override
            public String dynamicTableName(MetaObject metaObject, String sql, String tableName) {
                if (cubeId.get()==null){
                    return null;
                }else{
                    return "gc_product_"+cubeId.get();
                }

            }
        });
        tableNameHandlerMap.put("gc_product_measurement", new ITableNameHandler() {
            @Override
            public String dynamicTableName(MetaObject metaObject, String sql, String tableName) {
                if (cubeId.get()==null){
                    return null;
                }else{
                    return "gc_product_measurement_"+cubeId.get();
                }

            }
        });
        tableNameHandlerMap.put("gc_raster_tile_fact", new ITableNameHandler() {
            @Override
            public String dynamicTableName(MetaObject metaObject, String sql, String tableName) {
                if (cubeId.get()==null){
                    return null;
                }else{
                    return "gc_raster_tile_fact_"+cubeId.get();
                }

            }
        });
        dynamicTableNameParser.setTableNameHandlerMap(tableNameHandlerMap);
        sqlParserList.add(dynamicTableNameParser);
        paginationInterceptor.setSqlParserList(sqlParserList);


        return paginationInterceptor;
    }
}
