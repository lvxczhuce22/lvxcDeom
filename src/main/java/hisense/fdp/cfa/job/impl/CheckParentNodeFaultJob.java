package hisense.fdp.cfa.job.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import hisense.fdp.cfa.HiFdpCfa;
import hisense.fdp.cfa.core.AllFaultData;
import hisense.fdp.cfa.core.Context;
import hisense.fdp.cfa.model.EquipStatusMsgVO;
import hisense.fdp.cfa.mqmsghandler.FaultHandler;
import hisense.fdp.cfa.observersubject.impl.MqMsgSender;
import hisense.fdp.cfa.observersubject.impl.MqQueueMsgSender;
import hisense.fdp.cfa.observersubject.impl.ReceiveMsgMQ;
import hisense.fdp.cfa.util.CloneUtils;
import hisense.fdp.cfa.util.DateUtil;
import hisense.fdp.cfa.util.VariableInit;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckParentNodeFaultJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(CheckParentNodeFaultJob.class);
	private Map<String,AllFaultData> faultMapTemp = new HashMap<String, AllFaultData>();
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Date d = new Date();
		log.info("开始进行父节点故障检测(TO_TOPIC)[{}]",DateUtil.format2Str(d));
		Context.reviceFlag = false;
		checkParentNodeMethod();
		Context.reviceFlag = true;
		log.info("结束进行父节点故障检测(TO_TOPIC)[{}]",DateUtil.format2Str(d));
	}
	//修复逻辑
	public void checkParentNodeMethod(){
		log.info("==========================");
		faultMapTemp.putAll(Context.getUniqueInstance().loadLocalFaultMap());
//		faultMapTemp = CloneUtils.clone(Context.getUniqueInstance().loadLocalFaultMap());
		Context.getUniqueInstance().loadNodeTreeMap();
//		log.info("==========================");
		
		if(faultMapTemp == null){
			return;
		}
		log.info("需要进行重复性检测的设备故障信息共[{}]",faultMapTemp.size());
//		//如果数量太多走多线程
//		if(faultMapTemp.size()>20000){
//			//临时解决长沙天网项目的52000设备需要检测的问题！！！
////			log.info("数量太大，单线程执行效率慢，已更改外多线程！");
//			
//			FaultHandler faulthandler = new FaultHandler(faultMapTemp,20000);
//			Thread t1 = new Thread(faulthandler);
//			t1.start();
//			
//			return;
//		}
		
		String devicekey = "";
		for(Entry<String, AllFaultData> entry : faultMapTemp.entrySet())
		{
			try{
				log.info("每一个进入该循环的对象都打印[entry.getKey()]设备编号："+entry.getKey());
				EquipStatusMsgVO svo = entry.getValue().getFault();
				log.info("每一个进入该循环的对象都打印：【"+svo.toALLString()+"】");
				try{
					Date checkdate = DateUtil.str2Date(svo.getCheckTime());
					if((new Date().getTime()-checkdate.getTime())>30*60*60*1000){
						log.error("["+svo.toString()+"]该设备检测状态信息过期，超过30小时没有更新(已从内存中清理)！！");
	//					Context.getUniqueInstance().loadLocalFaultMap().get(devicekey).clear14Fault();
						Context.getUniqueInstance().loadLocalFaultMap().get(entry.getKey()).getFaultMap().remove(svo.getFaultType());
						continue;
					}
				}catch(Exception e){
					log.error("[检测时间格式有误，["+svo.toALLString()+"]",e);
					Context.getUniqueInstance().loadLocalFaultMap().get(entry.getKey()).getFaultMap().remove(svo.getFaultType());
					continue;
				}
				if(entry.getValue().isFault() == false){//正常
					//TODO  装封装的对象直接发MQ到故障收集,发AllFaultData := entry.getValue()
					//entry.getValue()
					//处理正常情况下怎么从map中取 报文msg对象
					log.info("正常报文出口！！！");
//					ReceiveMsgMQ.getInstance().sendMsgToMQ(svo.toStatusMsgString());
					MqMsgSender.sendMessage(VariableInit.MQ_MAINTAININF_TOPIC,svo.toStatusMsgString());
				}else{//故障 需要查询根节点
					devicekey = queryParentNodeByTree(entry.getKey());
					log.info("当前故障设备[{}] de 父节点查询结果为[{}]",entry.getKey(),devicekey);
					if(devicekey == null){//未找到
						//entry.getValue();
//						ReceiveMsgMQ.getInstance().sendMsgToMQ(svo.toStatusMsgString());
						MqMsgSender.sendMessage(VariableInit.MQ_MAINTAININF_TOPIC,svo.toStatusMsgString());
						Context.getUniqueInstance().loadLocalFaultMap().get(entry.getKey()).clear14Fault();
					}else if(faultMapTemp.get(devicekey)==null){//如果内存表中不存在该设备状态的信息，则跳过等待下一次；
						//在queryParentNodeByTree中已经排除了这种情况
						continue;
					}else{
						//TODO  装封装的对象直接发MQ到故障收集,发AllFaultData := faultMapTemp.get(entry.getKey())
						//faultMapTemp.get(entry.getKey());
						MqMsgSender.sendMessage(VariableInit.MQ_MAINTAININF_TOPIC, faultMapTemp.get(devicekey).getFault().toStatusMsgString());
	//					ReceiveMsgMQ.getInstance().sendMsgToMQ(faultMapTemp.get(devicekey).getFault().toStatusMsgString());
						
						Context.getUniqueInstance().loadLocalFaultMap().get(devicekey).clear14Fault();
					}
				}
			}catch(Exception e){
				log.error("分析发送报文的结果时异常：\n",e);
			}
		}
	}
	
//	public String queryParentNodeByTree(String deviceid){
//		String parentId = Context.getUniqueInstance().getNodeTree(deviceid);
//		if(parentId==null){
//			return null;
//		}
//		//如果父节点正常，则发送该设备id
//		//如果检测到正常的设备，则停止；或者检测到-1
//		//如果父节点在内存中不存在检测的故障信息，则返回deviceid，不返回父节点
//		if(faultMapTemp.get(parentId)==null||faultMapTemp.get(parentId).isFault()==false){
//			return deviceid;
////		}else if(faultMapTemp.get(parentId)==null||deviceid.equals(faultMapTemp.get(parentId))||"".equals(faultMapTemp.get(parentId))||"-1".equals(faultMapTemp.get(parentId))){
//		}else if(deviceid.equals(parentId)||"-1".equals(parentId)||"".equals(parentId)){
//			return parentId;
//		}else{
//			parentId = queryParentNodeByTree(parentId);
//		}
//		return parentId;
//	}
	public String queryParentNodeByTree(String deviceid){
		String parentId = Context.getUniqueInstance().getNodeTree(deviceid);
		if(parentId==null){
			return null;
		}
		//如果父节点正常，则发送该设备id
		//如果检测到正常的设备，则停止；或者检测到-1
		//如果父节点在内存中不存在检测的故障信息，则返回deviceid，不返回父节点
		
		//有父节点的，内存中也有该父节点的检测报文，父节点所有检测方式方式中存在 9:2
//		if(faultMapTemp.get(parentId)==null||!"2".equals(faultMapTemp.get(parentId).getFaultMap().get("9"))){
		if(faultMapTemp.get(parentId)==null||faultMapTemp.get(parentId).getFaultMap().get("10")==null||!( "801.D02.012".equals(faultMapTemp.get(parentId).getFaultMap().get("10").getFaultType())||"801.D02.009".equals(faultMapTemp.get(parentId).getFaultMap().get("10").getFaultType()))){
			return deviceid;
//		}else if(faultMapTemp.get(parentId)==null||deviceid.equals(faultMapTemp.get(parentId))||"".equals(faultMapTemp.get(parentId))||"-1".equals(faultMapTemp.get(parentId))){
		}else if(deviceid.equals(parentId)||"-1".equals(parentId)||"".equals(parentId)){
			return parentId;
		}else{
			parentId = queryParentNodeByTree(parentId);
		}
		return parentId;
	}
	
	public static void main(String[] args) {
		MqQueueMsgSender.sendMessage("HIATMP.HISENSE.EQUIPSTATUS.ENDPOINT", "fadfadfasdf");
		
	}

}
