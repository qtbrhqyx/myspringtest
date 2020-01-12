package com.lagou.edu.zuoye.factory;

import com.lagou.edu.zuoye.annotation.*;
import com.lagou.edu.zuoye.pojo.Bean;
import com.lagou.edu.zuoye.util.PackageScanner;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class AnnotationBeanFactory implements BeanFactory {
    Map<String,Object> map = new HashMap<>();



    Set<Bean> beans = new HashSet<>();
    public AnnotationBeanFactory(Class<?> clazz) throws Exception {
        Class<?>[] componentClass = register(clazz);
        doBeans(componentClass);
        refresh(componentClass);
    }

    private void refresh(Class<?>[] componentClass) throws Exception {
        beanInstance();
        setDependencies();
        //查找事务注解
        doTransaction(componentClass);
    }

    private Class<?>[] register(Class<?> componentClass) throws Exception {
        String[] basePackage = new String[0];
        Annotation[] annotations = componentClass.getAnnotations();
        for(Annotation annotation:annotations){
            if(annotation instanceof MyComponentScan){
                MyComponentScan myComponentScan = (MyComponentScan)annotation;
                basePackage = myComponentScan.value();
            }
        }
        if(basePackage.length==0){
            throw new Exception("parameter exception");
        }
        //获取bean全限定名以及依赖关系
        List<Class<?>> list = new ArrayList();
        PackageScanner packageScanner = new PackageScanner() {

            @Override
            public void dealClass(Class<?> clazz) {
                list.add(clazz);
                System.out.println(clazz);
            }
        };
        for(String s:basePackage){
            packageScanner.packageScan(s);
        }
        System.out.println(list);

        return list.toArray(new Class<?>[list.size()]);

    }
    //获取class,然后
    private void doBeans(Class<?>[] componentClasses) throws Exception{
        for(Class clazz :componentClasses){
            if(clazz.getAnnotation(MyService.class)!=null){
                MyService myService = (MyService) clazz.getAnnotation(MyService.class);
                Bean bean = new Bean();
                if(StringUtils.isNotBlank(myService.value())){
                    bean.setId(myService.value());
                }else {
                    bean.setId(clazz.getSimpleName());
                }
                bean.setName(clazz.getName());
                //扫描属性是否有Autowirde注解，并设置依赖
                Field[] fields = clazz.getDeclaredFields();
                for(Field field:fields){
                    field.setAccessible(true);
                    if(field.getAnnotation(MyAutowired.class)!=null){
                        bean.getDependencies().add(Class.forName(field.getGenericType().getTypeName()));
                    }
                }
                beans.add(bean);
            }
        }
    }




    //初始化bean,放入缓存二
    public void beanInstance() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        for(Bean bean:beans){
            Class<?> aClass = Class.forName(bean.getName());
            Object instance = aClass.newInstance();

            //TODO id重复校验
            map.put(bean.getId(),instance);



        }
    }

    //设置依赖关系
    //需要考虑依赖还有依赖，保证是同一对象操作
    public void setDependencies() throws IllegalAccessException, InstantiationException, NoSuchFieldException, ClassNotFoundException {
        for(Bean bean:beans){

            Object instance = map.get(bean.getId());//当前bin对象
            Field[] fields = instance.getClass().getDeclaredFields();//对象内属性
//接口找到类
            for(Class clazz:bean.getDependencies()){
                for(Field field:fields){
                    field.setAccessible(true);
                    //判断有autowired注解
                    if(field.getAnnotation(MyAutowired.class)!=null){
                        Class<?> depClass = Class.forName(field.getGenericType().getTypeName());
                        //判断当前属性类型和依赖实例一致
                        //遍历所有bean,寻找
                        for(Bean bean1:beans){
                            Class<?> trueClass = Class.forName(bean1.getName());
                            if(depClass.isAssignableFrom(trueClass)){
                                Object depInstance;
                                //已建好的实例直接获取
                                if(map.containsKey(bean1.getId())){
                                    depInstance = map.get(bean1.getId());

                                }else {
                                    depInstance = trueClass.newInstance();
                                }
                                field.set(instance,depInstance);
                            }
                        }
                    }
                }
            }


        }
    }
    public Object getBean(String name){

        return map.get(name);
    }

    public Map getBeans(){
        return map;
    }

    void doTransaction(Class<?>[] componentClasses){
        for(Class clazz :componentClasses){

            if(clazz.getAnnotation(MyTransactional.class)!=null){

                MyService myService = (MyService) clazz.getAnnotation(MyService.class);
                String beanId = myService.value();



                MyTransactional myTransactional = (MyTransactional) clazz.getAnnotation(MyTransactional.class);
//                String txId = myTransactional.value();
                //当前实例需要被代理
                Object currentInstance = map.get(beanId);
                ProxyFactory proxyFactory = (ProxyFactory) map.get("proxyFactory");
                if(myTransactional.proxy()){
                   //jdk
                   map.put(beanId,proxyFactory.getJdkProxy(currentInstance));
                }else {
                    //cglib
                   map.put(beanId,proxyFactory.getCglibProxy(currentInstance));
                }

            }

        }
    }



}
