package com.bilibil.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Date:  2023/8/19
 */
@Configuration
public class JsonHttpMessageConverterConfig {

    @Bean
    @Primary  // 比较高优先级（注解来指明哪个实现类作为首选进行自动装配注入）
    // 对HTTP接口请求的做转化的工具类
    public HttpMessageConverters fastJsonHttpConverters() {
        FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        //对配置类
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        //对json序列化配置

        fastJsonConfig.setSerializerFeatures(
                //格式化输出
                SerializerFeature.PrettyFormat,
                // 如果这个数据不存在，返回空值
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteMapNullValue,
                // 对数据进行排序
                SerializerFeature.MapSortField,
                // 关闭循环引用，输出的时候相同的内容可以正常展示
                SerializerFeature.DisableCircularReferenceDetect
        );
        fastJsonConverter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastJsonConverter);
    }

}
