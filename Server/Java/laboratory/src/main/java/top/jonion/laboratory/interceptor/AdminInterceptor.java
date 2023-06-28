package top.jonion.laboratory.interceptor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.jonion.laboratory.utils.JwtTokenUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 拦截器，拦截所有请求
 */
public class AdminInterceptor implements HandlerInterceptor {
    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Logger logger = LoggerFactory.getLogger(AdminInterceptor.class);
        try {

            String tokenHeader = request.getHeader("token");
            String tokenParameter = request.getParameter("token");
            String token=null;

            if(tokenHeader!=null&&!tokenHeader.equals("")){
                token = tokenHeader;
            }

            if(tokenParameter!=null&&!tokenParameter.equals("")){
                token = tokenParameter;
            }
            JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

            if(token==null||token.equals("")){
                response.sendRedirect(request.getContextPath()+"/");
                return false;
            }
            if (jwtTokenUtil.validateToken(token)){
                response.sendRedirect(request.getContextPath()+"/");
                return false;
            }else {
                String userNameFromToken = jwtTokenUtil.getUserNameFromToken(token);
                request.getSession().setAttribute("username",userNameFromToken);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
        //如果设置为true时，请求将会继续执行后面的操作
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
//         System.out.println("执行了TestInterceptor的postHandle方法");
    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        System.out.println("执行了TestInterceptor的afterCompletion方法");
    }
}

