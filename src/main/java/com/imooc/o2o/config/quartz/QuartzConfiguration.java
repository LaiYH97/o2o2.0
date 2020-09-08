package com.imooc.o2o.config.quartz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.imooc.o2o.service.ProductSellDailyService;

@Configuration
public class QuartzConfiguration {

	@Autowired
    private ProductSellDailyService productSellDailyService;
    @Autowired
    private MethodInvokingJobDetailFactoryBean jobDetailFactoryBean;
    @Autowired
    private CronTriggerFactoryBean productSellDailyTriggerFactory;

    @Bean("jobDetailFactory")
    public MethodInvokingJobDetailFactoryBean createJobDetail() {
        //new 出JobDetailFactory对象，这个工厂主要用来生产一个joDetail，即制作一个任务
        //由于我们所做的定时任务根本上讲其实就是执行一个方法，所以用这个工厂比较方便
        MethodInvokingJobDetailFactoryBean jobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
        jobDetailFactoryBean.setName("product_sell_daily_job");
        jobDetailFactoryBean.setGroup("job_product_sell_daily_group");
        //对于相同的JobDetail中的任务，不会并发运行
        jobDetailFactoryBean.setConcurrent(false);
        jobDetailFactoryBean.setTargetObject(productSellDailyService);
        jobDetailFactoryBean.setTargetMethod("dailyCalculate");
        return jobDetailFactoryBean;
    }

    @Bean("productSellDailyTriggerFactory")
    public CronTriggerFactoryBean createProductSellDailyTrigger() {
        CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
        triggerFactoryBean.setName("product_sell_daily_trigger");
        triggerFactoryBean.setGroup("job_product_sell_daily_group");
        triggerFactoryBean.setJobDetail(jobDetailFactoryBean.getObject());
        triggerFactoryBean.setCronExpression("0 0 0 * * ? *");
        return triggerFactoryBean;
    }

    /**
     * 创建调度工厂并返回
     * @return
     */
    @Bean("schedulerFactory")
    public SchedulerFactoryBean createSchedulerFactory() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setTriggers(productSellDailyTriggerFactory.getObject());
        return schedulerFactoryBean;
    }
}
