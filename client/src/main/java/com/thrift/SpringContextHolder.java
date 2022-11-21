package com.thrift;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;
    private static final Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

    /**
     * 取得存储在静态变量中的ApplicationContext.
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 实现ApplicationContextAware接口, 注入Context到静态变量中.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
        System.out.println("SpringContextHolder注入成功");
    }

    /**
     * 清除SpringContextHolder中的ApplicationContext为Null.
     */
    public static void clearHolder() {
        if (logger.isDebugEnabled()) {
            logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
        }
        applicationContext = null;
    }

    /**
     * 发布事件
     *
     * @param event
     */
    public static void publishEvent(ApplicationEvent event) {
        if (applicationContext == null) {
            return;
        }
        applicationContext.publishEvent(event);
    }

    /**
     * 实现DisposableBean接口, 在Context关闭时清理静态变量.
     */
    @Override
    public void destroy() {
        SpringContextHolder.clearHolder();
    }

    /**
     * 获取配置信息.
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key);
    }

    /**
     * 获取配置信息.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperty(String key, String defaultValue) {
        return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    /**
     * 把bean注入到spring上下文
     *
     * @param beanName  类的注册到spring的key
     * @param classPath 类的全路径
     */
    public static void setBean(String beanName, String classPath) {
        BeanDefinition bean = new GenericBeanDefinition();
        //类的全路径
        bean.setBeanClassName(classPath);
        DefaultListableBeanFactory fty = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        //注册Bean
        fty.registerBeanDefinition(beanName, bean);
    }

    /**
     * 注册bean
     * @param beanName bean的名称
     * @param obj bean的实体类
     */
    public static void setBean(String beanName, Object obj) {
        DefaultListableBeanFactory fty = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        //注册Bean
        fty.registerSingleton(beanName,obj);

    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 获取对象
     *
     * @param classz
     * @return T 一个以所给名字注册的bean的实例
     * @throws BeansException
     */
    public static <T> T getBean(Class<T> classz) throws BeansException {
        return applicationContext.getBean(classz);
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @param name
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }


    /**
     * 销毁bean
     * @param beanName
     * @return
     */
    public static void destroyBean(String beanName){
        DefaultListableBeanFactory fty = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        fty.destroySingleton(beanName);
    }

}
