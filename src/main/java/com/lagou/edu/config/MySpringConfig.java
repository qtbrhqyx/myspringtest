package com.lagou.edu.config;

import com.lagou.edu.dao.impl.JdbcAccountDaoImpl;
import com.lagou.edu.service.TransferService;
import com.lagou.edu.service.impl.TransferServiceImpl;
import com.lagou.edu.utils.ConnectionUtils;
import com.lagou.edu.utils.TransactionManagerImpl;
import com.lagou.edu.zuoye.annotation.MyComponentScan;
import com.lagou.edu.zuoye.factory.AnnotationBeanFactory;
import org.junit.Test;

//@MyComponentScan({"com.lagou.edu.dao","com.lagou.edu.service","com.lagou.edu.utils","com.lagou.edu.zuoye"})
@MyComponentScan({"com.lagou.edu"})
public class MySpringConfig {


    @Test
    public void testMySpring(){
        AnnotationBeanFactory annotationBeanFactory = null;
        try {
            annotationBeanFactory = new AnnotationBeanFactory(MySpringConfig.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        TransferService transferService = (TransferService) annotationBeanFactory.getBean("transferService");
    }
}
