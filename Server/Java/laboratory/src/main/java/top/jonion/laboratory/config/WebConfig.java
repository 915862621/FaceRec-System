package top.jonion.laboratory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.jonion.laboratory.interceptor.AdminInterceptor;

/**
 * 网站配置文件
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    //图片存放根路径，从application.yml中读取upload
    @Value("${upload}")
    private String UPLOAD_PATH;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	//外部静态资源映射路径，用来上传文件
    	String filePath = "file:" + UPLOAD_PATH;
        registry.addResourceHandler("/upload/**").addResourceLocations(filePath);
    }
    
    //拦截器，
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册Interceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(new AdminInterceptor());
        registration.addPathPatterns("/**");                      //所有路径都被拦截
        registration.excludePathPatterns(                         //添加不拦截路径
                "/",
                "/upload/**",//不拦截上传文件路径
                "/AppletsController/**",
                "/FaceController/**",
                "/captcha",//验证码
                "/EntryDataController/uploadentrydata",
                "/UserController/login",
                "/home",
                "/static/**" //静态资源
        );
    }
}