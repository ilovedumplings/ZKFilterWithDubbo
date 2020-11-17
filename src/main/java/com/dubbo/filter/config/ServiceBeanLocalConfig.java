package com.dubbo.filter.config;

import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author DaTou
 * @Description 使用时需要配置dubbo服务延迟暴露
 * @Date 2020/9/9
 **/
@ConditionalOnProperty(name = "spring.profiles.active",havingValue = "dev")
@Configuration
public class ServiceBeanLocalConfig implements BeanFactoryPostProcessor {
    /**
     * dubbo服务注册到本机的端口号
     */
    public static final String PORT = "21880";
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        customizeDubboServiceConfig((DefaultListableBeanFactory) beanFactory);
    }

    /**
     * dubbo的service
     * @param defaultListableBeanFactory 顶级bean工厂
     */
    private void customizeDubboServiceConfig(DefaultListableBeanFactory defaultListableBeanFactory){
        String[] beanDefinitionNames = defaultListableBeanFactory.getBeanDefinitionNames();

        for (String beanName:beanDefinitionNames){
            BeanDefinition beanDefinition = defaultListableBeanFactory.getBeanDefinition(beanName);
            if (RegistryConfig.class.getName().equalsIgnoreCase(beanDefinition.getBeanClassName())){
                MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
                mutablePropertyValues.addPropertyValue("register",false);
                defaultListableBeanFactory.registerBeanDefinition(beanName,beanDefinition);
            }
            if (ProtocolConfig.class.getName().equalsIgnoreCase(beanDefinition.getBeanClassName())){
                MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
                mutablePropertyValues.addPropertyValue("port",PORT);
                defaultListableBeanFactory.registerBeanDefinition(beanName,beanDefinition);
            }
        }
    }

}
