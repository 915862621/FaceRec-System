package top.jonion.laboratory.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * 获取yml自定义的值
 */



//设置get，只能获取
@Getter
@Component
public class ApplicationValues {

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secret}")
    private String secret;

    //注入python服务器的请求地址
    @Value("${python_url}")
    private String pythonUrl;

    @Value("${http_pool.max_total}")
    private int maxTotal;

    @Value("${http_pool.default_max_per_route}")
    private int maxPerRoute;

    @Value("${http_pool.connect_timeout}")
    private int connTimeOut;

    @Value("${http_pool.connection_request_timeout}")
    private int connReqTimeOut;

    @Value("${http_pool.socket_timeout}")
    private int socketTimeout;

    @Value("${http_pool.validate_after_inactivity}")
    private int inactivity;
}
