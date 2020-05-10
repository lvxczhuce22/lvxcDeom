package hisense.fdp.cfa.job.impl;

import hisense.fdp.cfa.observersubject.impl.MqMsgSenderForHeartBeat;
import hisense.fdp.cfa.util.DateUtil;
import hisense.fdp.cfa.util.UUIDGenerator;
import hisense.fdp.cfa.util.VariableInit;

import java.util.Date;




import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(HeartBeatJob.class);
	
	public void execute(JobExecutionContext context) {
		try {
			String msg = generateHeartBeatMsg().toString();
			MqMsgSenderForHeartBeat.sendMessage(VariableInit.MQ_HEART_BEAT_TOPIC, msg);
		} catch (Exception e) {
			log.error("心跳job启动失败！", e);
		}
	}
	private static String generateHeartBeatMsg(){
		StringBuilder msg = new StringBuilder();
        msg.append("FAULT,");
        msg.append("1.0,");
        msg.append("0001,");
        msg.append(UUIDGenerator.generate32BitUUID() + ",");
        msg.append("04,");
        msg.append(VariableInit.ADAPTER_ID + ",");
        msg.append("综合分析适配器,");
        msg.append(VariableInit.getLocalIP() + ",");
        msg.append(",");
        msg.append("-1,");
        msg.append("适配器正常运行,");
        msg.append(DateUtil.currentDate() + ",");
        msg.append(DateUtil.currentDate());
        log.info("发送心跳："+msg.toString());
        return msg.toString();
    }
	
	public static void main(String[] args) {
		HeartBeatJob job = new HeartBeatJob();
		job.execute(null);
	}
}
