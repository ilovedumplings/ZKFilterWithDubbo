package com.dubbo.filter.reference;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.dubbo.filter.config.ReferenceLocalConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DaTou
 * @Description bean工厂过滤器,只在dev开发环境下生效 used on ervice
 * @Date 2020/8/27
 **/
@ConditionalOnProperty(name = "spring.profiles.active",havingValue = "dev")
@Configuration
public class ReferenceXMLLocalConfig implements BeanFactoryPostProcessor {

    public ReferenceXMLLocalConfig(){

    }

    public ReferenceXMLLocalConfig(List<Class> excludeInterfaceList){
        for (Class aClass:excludeInterfaceList){
            this.excludeInterfaceList.add(aClass.getName());
        }
    }

    private List<String> excludeInterfaceList = new ArrayList<>();
    /**
     * 指定要调用服务的端口号
     */
//    protected static final String URL = "dubbo://localhost:21880";


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        customizeDubboConfig((DefaultListableBeanFactory) beanFactory);
    }

    /**
     * dubbo的reference
     * @param defaultListableBeanFactory 顶级bean工厂
     */
    private void customizeDubboConfig(DefaultListableBeanFactory defaultListableBeanFactory){
        String[] beanDefinitionNames = defaultListableBeanFactory.getBeanDefinitionNames();

        for (String beanName:beanDefinitionNames){
            BeanDefinition beanDefinition = defaultListableBeanFactory.getBeanDefinition(beanName);
            /**
             *获取使用xml配置的referenceBean的BeanDefiniton,
             * 使用api配置的beanDefiniton不好区分,改在ReferenceFilter.class中获取
             */
            if(ReferenceBean.class.getName().equalsIgnoreCase(beanDefinition.getBeanClassName())){
                PropertyValue propertyValue ;
                if ((propertyValue=beanDefinition.getPropertyValues().getPropertyValue("interface"))!=null){
                    if (excludeInterfaceList.contains(propertyValue.getValue())) {
                        return;
                    }
                }

                MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
                mutablePropertyValues.addPropertyValue("url",ReferenceLocalConfig.URL);
                defaultListableBeanFactory.registerBeanDefinition(beanName,beanDefinition);
            }
        }
    }

//    String pattern = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";

}
