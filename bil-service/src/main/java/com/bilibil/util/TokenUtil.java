package com.bilibil.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bilibil.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

public class TokenUtil {

    private static final String ISSUER = "李思";
    // 生成创建一个令牌（主要用来标识用户身份）   接入token
    public static String generateToken(Long userId) throws Exception{
        // Algorithm算法包，RSA256加密，加密公钥和私钥
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        // Calendar 日历类 生成时间
        Calendar calendar = Calendar.getInstance();
        // 获取当前时间
        calendar.setTime(new Date());
        // 生成过期时间 1小时
        calendar.add(Calendar.SECOND, 30);
        // 生成JWT withKeyId 存储用户ID
        return JWT.create().withKeyId(String.valueOf(userId))
                //withIssuer 签发者
                .withIssuer(ISSUER)
                // 设置过期时间
                .withExpiresAt(calendar.getTime())
                // 进行算法签名加密
                .sign(algorithm);
    }
    // 刷新token
    public static String generateRefreshToken(Long userId) throws Exception{
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // 过期时间7天
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }
    // 验证token（用来验证用户身份）
    public static Long verifyToken(String token){
        try{
            // Algorithm算法包，RSA256加密，加密公钥和私钥
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            // 生成验证类
            JWTVerifier verifier = JWT.require(algorithm).build();
            // 解密token
            DecodedJWT jwt = verifier.verify(token);
            // 获取 userId
            String userId = jwt.getKeyId();
            return Long.valueOf(userId);
        }catch (TokenExpiredException e){
            throw new ConditionException("555","token过期！");
        }catch (Exception e){
            throw new ConditionException("非法用户token！");
        }


    }


}
