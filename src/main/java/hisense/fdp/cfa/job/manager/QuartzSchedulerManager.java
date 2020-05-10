package hisense.fdp.cfa.job.manager;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务调度管理器
 */
public class QuartzSchedulerManager {
	//
	private static final Logger log = LoggerFactory.getLogger(QuartzSchedulerManager.class);

	private static SchedulerFactory schedulerFactory;
	private static Scheduler scheduler;

	/* 类加载时即初始化 Quartz Scheduler */
	static {
		initQuartzScheduler();
	}

	/**
	 * 初始化 Quartz Scheduler
	 */
	private static void initQuartzScheduler() {
		try {
			schedulerFactory = new StdSchedulerFactory();
			scheduler = schedulerFactory.getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			log.error("Quartz Scheduler 初始化失败，程序退出", e);
			try {
				scheduler.shutdown();
			} catch (Exception ex) {
			}
			System.exit(0);
		}
	}

	/**
	 * 配置并执行基本定时任务(Job)，Job不接受任何参数
	 * 
	 * @param jobName
	 *            定时任务名 quartz框架需要
	 * @param triggerName
	 *            触发器名 quartz框架需要
	 * @param groupName
	 *            定时任务组名 quartz框架需要
	 * @param jobClazz
	 *            定时任务实现类，必须实现 org.quartz.Job 接口
	 * @param cronExpressiion
	 *            cron表达式，用于配置定时Job何时被执行
	 * @return Job配置成功返回ture，否则返回false
	 */
	public static boolean scheduleJob(String jobName, String triggerName, String groupName, Class<?> jobClazz, String cronExpressiion) {
		boolean scheduleSucceed = false;
		try {
			// 创建定时任务Job
			JobDetail job = new JobDetail();
			job.setName(jobName);
			job.setGroup(groupName);
			job.setJobClass(jobClazz);
			// 创建触发器
			CronTrigger trigger = new CronTrigger();
			trigger.setName(triggerName);
			trigger.setCronExpression(cronExpressiion);
			// 配置并执行定时任务Job
			scheduler.scheduleJob(job, trigger);
			//
			scheduleSucceed = true;
			log.info("定时任务[{},{}]启动成功", jobName, cronExpressiion);
		} catch (Exception ex) {
			String errorJobInfo = "jobClass:" + jobClazz + ", cronExpressiion:" + cronExpressiion;
			log.error("定时任务配置失败[{}]", errorJobInfo, ex);
			scheduleSucceed = false;
		}
		return scheduleSucceed;
	}

	/**
	 * 配置并执行设备状态自动检测定时任务(Job)，Job接收两个参数，DEVICETYPE和CHECKSOURCETYPE， 见EquipmentAutoCheckTask.xml配置文件
	 * 
	 * @param jobName
	 * @param triggerName
	 * @param groupName
	 * @param className
	 * @param cronExpr
	 * @param typeCode
	 * @param checkSourceType
	 */
	public static boolean scheduleEquipCheckJob(String jobName, String triggerName, String groupName, Class<?> className, String cronExpr, String typeCode,
			String checkSourceType) {
		boolean scheduleSucceed = false;
		try {
			// 创建定时任务Job
			JobDetail job = new JobDetail();
			job.setName(jobName);
			job.setGroup(groupName);
			job.setJobClass(className);
			job.getJobDataMap().put("DEVICETYPE", typeCode);
			job.getJobDataMap().put("CHECKSOURCETYPE", checkSourceType);
			// 创建触发器
			CronTrigger trigger = new CronTrigger();
			trigger.setName(triggerName);
			trigger.setCronExpression(cronExpr);
			// 配置并执行定时任务Job
			scheduler.scheduleJob(job, trigger);
			//
			scheduleSucceed = true;
			log.info("定时任务[{},{}]启动成功", jobName, cronExpr);
		} catch (Exception ex) {
			String errorJobInfo = "jobClass:" + className + ", cronExpressiion:" + cronExpr;
			log.error("定时任务配置失败[{}]", errorJobInfo, ex);
			scheduleSucceed = false;
		}
		return scheduleSucceed;

	}

	/**
	 * 初始化定时任务，见CalibrationCheckTask.xml
	 * 
	 * @param jobName
	 * @param triggerName
	 * @param groupName
	 * @param className
	 * @param cronExpr
	 * @param typeCode
	 * @param autoCheck
	 */
	public static boolean scheduleCalibrationWarnJob(String jobName, String triggerName, String groupName, Class<?> className, String cronExpr,
			String typeCode, String autoCheck) {
		boolean scheduleSucceed = false;
		try {
			// 创建定时任务Job
			JobDetail job = new JobDetail();
			job.setName(jobName);
			job.setGroup(groupName);
			job.setJobClass(className);
			job.getJobDataMap().put("CALIBRATIONWARNTYPE", typeCode);
			job.getJobDataMap().put("CALIBRATIONTASKAUTO", autoCheck);
			// 创建触发器
			CronTrigger trigger = new CronTrigger();
			trigger.setName(triggerName);
			trigger.setCronExpression(cronExpr);
			// 配置并执行定时任务Job
			scheduler.scheduleJob(job, trigger);
			//
			scheduleSucceed = true;
			log.info("定时任务[{},{}]启动成功", jobName, cronExpr);
		} catch (Exception ex) {
			String errorJobInfo = "jobClass:" + className + ", cronExpressiion:" + cronExpr;
			log.error("定时任务配置失败[{}]", errorJobInfo, ex);
			scheduleSucceed = false;
		}
		return scheduleSucceed;

	}
}
