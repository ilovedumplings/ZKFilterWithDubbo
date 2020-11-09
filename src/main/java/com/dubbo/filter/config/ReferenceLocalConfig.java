package com.dubbo.filter.config;

import com.dubbo.filter.reference.ReferenceApiLocalConfig;
import com.dubbo.filter.reference.ReferenceXMLLocalConfig;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DaTou
 * @Description  服务端本地化配置
 * @Date 2020/9/27
 **/
@ConditionalOnProperty(name = "spring.profiles.active",havingValue = "dev")
@Configuration
@Data
public class ReferenceLocalConfig {

    /**
     * 使用配置文件中zk地址的接口容器
     */
    public static List<Class> excludeInterfaceList = new ArrayList<>();

    /**
     * zk注册中心独立于配置文件的地址,默认本机21880端口,可以直接覆盖
     */
    public static String URL = "dubbo://localhost:21880";


    /**
     * 为每个接口配置独立的URL,只支持dubbo协议
     * @return
     */
    public static Map<Class,String> map = new HashMap<>();

    @Bean
    public ReferenceApiLocalConfig referenceApiLocalConfig(){
        if (CollectionUtils.isEmpty(excludeInterfaceList)){
            return new ReferenceApiLocalConfig();
        }else {
            return new ReferenceApiLocalConfig(excludeInterfaceList);
        }
    }

    @Bean
    public ReferenceXMLLocalConfig referenceXMLLocalConfig(){
        if (CollectionUtils.isEmpty(excludeInterfaceList)){
            return new ReferenceXMLLocalConfig();
        }else {
            return new ReferenceXMLLocalConfig(excludeInterfaceList);
        }
    }

}

