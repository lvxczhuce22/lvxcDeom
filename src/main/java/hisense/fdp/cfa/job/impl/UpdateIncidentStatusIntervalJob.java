package hisense.fdp.cfa.job.impl;

import hisense.fdp.cfa.dao.EquipNodeDao;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateIncidentStatusIntervalJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(UpdateIncidentStatusIntervalJob.class);
	private EquipNodeDao dao = new EquipNodeDao();
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("初始化内存中的预警周期信息... 开始 ...");
		dao.getIncidentStatusInterval();
		log.info("初始化内存中的预警周期信息... 结束 ...");
	}

}
