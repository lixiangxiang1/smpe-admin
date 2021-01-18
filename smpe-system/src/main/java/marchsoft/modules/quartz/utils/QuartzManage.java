package marchsoft.modules.quartz.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import marchsoft.exception.BadRequestException;
import marchsoft.modules.quartz.entity.QuartzJob;
import marchsoft.modules.system.entity.Job;
import marchsoft.utils.SecurityUtils;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Component;
import static org.quartz.TriggerBuilder.newTrigger;
import javax.annotation.Resource;
import java.sql.Struct;
import java.util.Date;

/**
 * @author lixiangxiang
 * @description 定时任务管理类
 * @date 2021/1/14 18:57
 */

@Slf4j
@Component
public class QuartzManage {

    private static final String JOB_NAME = "SMPE_TASK_";

    @Resource(name = "scheduler")
    private Scheduler scheduled;

    /**
     * description: 添加一个任务
     *
     * @author: lixiangxiang
     * @param quartzJob /
     * @return void
     * @date 2021/1/14 21:01
     */
     public void addJob(QuartzJob quartzJob){
         try {
             // 构建jobDetail,并与ExecutionJob类绑定(Job执行内容)
             JobDetail jobDetail = JobBuilder.newJob(ExecutionJob.class).
                     withIdentity(JOB_NAME+quartzJob.getId()).build();

             //通过触发器名和cron表达式创建Trigger
             Trigger cronTrigger = newTrigger()
                     //设置触发器的名字 作为任务标识
                     .withIdentity(JOB_NAME + quartzJob.getId())
                     //立即执行
                     .startNow()
                     //创建cron
                     .withSchedule(CronScheduleBuilder.cronSchedule(quartzJob.getCronExpression()))
                     .build();
             //把JOB_KEY和job信息放入jobDataMap中
             cronTrigger.getJobDataMap().put(QuartzJob.JOB_KEY,quartzJob);

             //重置启动时间
             ((CronTriggerImpl)cronTrigger).setStartTime(new Date());

             //执行定时任务
             scheduled.scheduleJob(jobDetail,cronTrigger);

             //如果设置暂停，暂停任务
             if (quartzJob.getIsPause()){
                pauseJob(quartzJob);
             }
         } catch (Exception e) {
             log.error(StrUtil.format("【创建定时任务失败】操作人id: {} 定时任务id：{}",
                     SecurityUtils.getCurrentUser()),quartzJob.getId() ,e);
             throw new BadRequestException("创建定时任务失败");
         }
     }

    /**
     * description: 暂停一个job
     *
     * @author: lixiangxiang
     * @param quartzJob /
     * @return void
     * @date 2021/1/15 18:30
     */
    private void pauseJob(QuartzJob quartzJob) {
        try {
            JobKey jobKey = JobKey.jobKey(JOB_NAME+quartzJob.getId());
            scheduled.pauseJob(jobKey);
        } catch (Exception e) {
          log.error(StrUtil.format("【定时任务暂停失败】操作人id: {}，定时任务id：{}",
                  SecurityUtils.getCurrentUser(),quartzJob.getId()),e);
          throw new BadRequestException("定时任务暂停失败");
        }
    }

}
