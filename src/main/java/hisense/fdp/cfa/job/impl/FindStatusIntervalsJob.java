package hisense.fdp.cfa.job.impl;

import hisense.fdp.cfa.dao.EquipNodeDao;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindStatusIntervalsJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(FindStatusIntervalsJob.class);
	private EquipNodeDao dao = new EquipNodeDao();
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("初始化内存中的状态变化周期信息... 开始 ...");
		dao.getFilterCode();
		log.info("初始化内存中的状态变化周期信息... 结束 ...");
	}

}
