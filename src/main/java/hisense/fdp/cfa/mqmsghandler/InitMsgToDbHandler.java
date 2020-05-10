package hisense.fdp.cfa.mqmsghandler;

import hisense.fdp.cfa.core.Context;
import hisense.fdp.cfa.dao.EquipNodeDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InitMsgToDbHandler implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(InitMsgToDbHandler.class);
	//运维数据库DAO
	private EquipNodeDao dao = new EquipNodeDao();

	public InitMsgToDbHandler() {
		log.info("启动获取‘已入库检测结果’初始化进程！");
	}

	@Override
	public void run() {
		try {
			dao.getMsgRecordBaseToInit();
//			dao.getIncidentStatusToInit("2");
			Context.reviceToDbFlag = true;
			log.info("初始化实时数据表检测结果完成！");
		} catch (Exception e) {
			log.error("启动获取‘实时数据表检测结果’初始化进程 报错！",e);
		}
	}
}
