package hisense.fdp.cfa.mqmsghandler;

import hisense.fdp.cfa.core.Context;
import hisense.fdp.cfa.dao.EquipNodeDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InitMsgHandler implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(InitMsgHandler.class);
	//运维数据库DAO
	private EquipNodeDao dao = new EquipNodeDao();

	public InitMsgHandler() {
		log.info("启动获取‘已上报的故障预警’初始化进程！");
	}

	@Override
	public void run() {
		try {
			dao.getIncidentStatusToInit("1");
			dao.getIncidentStatusToInit("2");
			Context.reviceFlag = true;
			log.info("初始化已上报故障预警数据完成！");
		} catch (Exception e) {
			log.error("启动获取‘已上报的故障预警’初始化进程 报错！",e);
		}
	}
}
