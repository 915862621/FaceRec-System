package top.jonion.laboratory.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *JWTToken工具类
 *
 */

@Component
public class JwtTokenUtil {



   // @Value("${secret")
    //加密密码
    public String secret="jonion";
//    @Value("${jwt.expiration}")
    //过期时间
    public int expiration=604800;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    private static final String CLAIM_KEY_USERNAME="name";
    private static final String CLAIM_KEY_CREATED="created";


    /**
     * 根据username产生token
     * @param name
     * @return Token
     */

    public String getToken(String name){
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, name);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return getToken(claims);
    }

    /**
     * 验证Token
     * @param token
     * @return
     */
    public boolean validateToken(String token){
        Claims claims = getClaimsFormToken(token);
        if(claims.isEmpty()||claims.equals(null)||!isTokenExpired(token)){
            return false;
        }else return true;
    }

    /**
     * 从token中获取登录用户名
     * @param token
     * @return
     */
    public String getUserNameFromToken(String token){
        String username;
        try {
            Claims claims = getClaimsFormToken(token);
            username = (String) claims.get("name");
        } catch (Exception e) {
            username = null;
        }
        return username;
    }



    /**
     * 判断token是否可以被刷新
     * @param token
     * @return
     */
    public boolean canRefresh(String token){
        return !isTokenExpired(token);
    }


    /**
     * 刷新token
     * @param token
     * @return
     */
    public String refreshToken(String token){
        Claims claims = getClaimsFormToken(token);
        claims.put(CLAIM_KEY_CREATED,new Date());
        return getToken(claims);
    }
    /**
     * 私有的根据荷载生成JWT TOKEN
     *
     * @param claims
     * @return
     */

    private String getToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                //加入过期时间

                .setExpiration(getExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    /**
     * 判断token是否失效
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
        Date expireDate = getExpiredDateFromToken(token);
        return expireDate.before(new Date());
    }

    /**
     * 从token中获取过期时间
     * @param token
     * @return
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFormToken(token);
        return claims.getExpiration();
    }

    /**
     * 从token中获取荷载
     * @param token
     * @return
     */
    private Claims getClaimsFormToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }



    /**
     * 生成token失效时间
     *
     * @return
     */
    private Date getExpirationDate() {

        return new Date(System.currentTimeMillis() + expiration * 1000);

    }
}
