package com.dubbo.filter.reference;

import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.dubbo.filter.config.ReferenceLocalConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;


/**
 * @author DaTou
 * @Description used on web
 * @Date 2020/8/25
 **/
@ConditionalOnProperty(name = "spring.profiles.active",havingValue = "dev")
@Configuration
public class ReferenceApiLocalConfig implements BeanPostProcessor {


    public ReferenceApiLocalConfig(){

    }

    private List<Class> excludeInterfaceList;

    private Map<Class,String> excludeInterfaceMap;

    public ReferenceApiLocalConfig(List<Class> excludeInterfaceList){
        for (Class aClass:excludeInterfaceList){
            if (!aClass.isInterface()) {
                throw new RuntimeException("接口容器必须是接口类型");
            }
        }
        this.excludeInterfaceList = excludeInterfaceList;
    }

    /**
     *
     * @param map key:接口类 value:url地址
     */
    public ReferenceApiLocalConfig(Map<Class,String> map){
        if (map==null) {
            throw new RuntimeException("参数不能为空");
        }
        for (Map.Entry entry:map.entrySet()){
            if (entry.getKey()==null){
                throw new RuntimeException("传入得接口不能为空");
            }
            String url = entry.getValue().toString();
            if (StringUtils.isEmpty(url)){
                throw new RuntimeException("dubbo注册地址URL不能为空");
            }
        }
        this.excludeInterfaceMap = map;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String s) throws BeansException {
        if (bean instanceof ReferenceBean){

            //得到接口类
            Class dubboInterface = ((ReferenceBean) bean).getInterfaceClass();

            //
            if (!CollectionUtils.isEmpty(excludeInterfaceList) && excludeInterfaceList.contains(dubboInterface)){
                return bean;
            }

            if (!CollectionUtils.isEmpty(excludeInterfaceMap) && excludeInterfaceMap.containsKey(dubboInterface)){
                String url = this.excludeInterfaceMap.get(dubboInterface);
                RegistryConfig registryConfig = new RegistryConfig();
                registryConfig.setProtocol("dubbo");
                registryConfig.setAddress(url);
                ((ReferenceBean) bean).setRegistry(registryConfig);
            }else {
                ((ReferenceBean) bean).setUrl(ReferenceLocalConfig.URL);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
        return bean;
    }
}

