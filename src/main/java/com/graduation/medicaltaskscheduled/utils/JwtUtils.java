package com.graduation.medicaltaskscheduled.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author RabbitFaFa
 * @date 2023/2/4
 */
public class JwtUtils {
    //token过期时间为 24h 后
    public static final long EXPIRE = 1000 * 60 * 60 * 24;
    //token生成的密钥
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";

    /**
     * 生成token字符串
     *
     * @param id
     * @param nickname
     * @return
     */
    public static String getJwtToken(String id, String nickname) {

        String JwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("expert")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .claim("id", id)
                .claim("nickname", nickname)
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();

        return JwtToken;
    }

    /**
     * 判断token是否存在与有效
     *
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断token是否存在与有效
     *
     * @param request
     * @return true-凭证没问题  false-无凭证或凭证有问题
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");
            if (!isExistToken(request)) return false;
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /***
     * 查看请求中是否有token凭证
     * @param request
     * @return
     */
    public static boolean isExistToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("token");
        if (StringUtils.isEmpty(jwtToken)) return false;
        return true;
    }

    /***
     * 获取请求中的token
     * @param request
     * @return
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("token");
    }

    /**
     * 根据token获取会员id
     *
     * @param request
     * @return
     */
    public static String getDataIdByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("token");
        if (StringUtils.isEmpty(jwtToken)) return "";
        return getDataIdByTokenStr(jwtToken);
    }

    /***
     *根据token字符串获取数据id
     *
     * @param token
     * @return
     */
    public static String getDataIdByTokenStr(String token) {
        if (StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("id");
    }
}
