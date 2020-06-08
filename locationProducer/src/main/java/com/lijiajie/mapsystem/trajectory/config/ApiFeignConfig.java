package com.lijiajie.mapsystem.trajectory.config;
import com.lijiajie.mapsystem.trajectory.service.DecisionEngineService;
import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiFeignConfig {

    /**
     * 请求地址 默认为http://iot.isclab.top
     */
    @Value("http://iot.isclab.top")
    private String url;


    /**
     * TestFeignClient 配置
     * @return
     */
    @Bean
    DecisionEngineService testFeignClient() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                //超时时间
                .options(new Request.Options(60000, 60000))
                //重试机制
                .retryer(new Retryer.Default(5000, 5000, 3))
                .target(DecisionEngineService.class, url);
    }
}

