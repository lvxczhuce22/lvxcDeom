package hisense.fdp.cfa.job.impl;

import hisense.fdp.cfa.dao.EquipNodeDao;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateNodeTreeJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(UpdateNodeTreeJob.class);
	private EquipNodeDao dao = new EquipNodeDao();
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("初始化内存中的链路节点信息... 开始 ...");
		dao.getNodeTree();
		log.info("初始化内存中的链路节点信息... 结束 ...");
	}

}
