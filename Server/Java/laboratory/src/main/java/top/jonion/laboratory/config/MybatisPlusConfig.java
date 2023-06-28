package top.jonion.laboratory.config;

import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数据库相关配置文件
 */
@EnableTransactionManagement(proxyTargetClass=true)//强迫事务使用CGLib代理方式,即基于类处理service层处理事务的问题
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //你的最大单页限制数量，默认 500 条，小于 0 如 -1 不受限制
        //paginationInterceptor.setLimit(2);
        return paginationInterceptor;
    }

}
