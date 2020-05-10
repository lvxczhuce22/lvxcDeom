package hisense.fdp.cfa.service.msghandler.impl;

import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import hisense.fdp.cfa.core.AllFaultData;
import hisense.fdp.cfa.core.Context;
import hisense.fdp.cfa.dao.EquipNodeDao;
import hisense.fdp.cfa.model.ConnectorEvent;
import hisense.fdp.cfa.model.EquipStatusMsgVO;
import hisense.fdp.cfa.model.IncidentStatusInterval;
import hisense.fdp.cfa.model.IncidentWarningDelayDetect;
import hisense.fdp.cfa.observersubject.impl.MqMsgSender;
import hisense.fdp.cfa.observersubject.impl.MqQueueMsgSender;
import hisense.fdp.cfa.service.msghandler.IMsgHandlerService;
import hisense.fdp.cfa.util.DateUtil;
import hisense.fdp.cfa.util.VariableInit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author lvxianchao
 * 1、解析
 * 2、筛选延迟上报的预警
 * 3、更新内存（自动修复、覆盖等逻辑）
 * 4、判断是否发生变化，是否需要发送报文（优先级判定、最后发送时间等逻辑）
 * 5、发送报文的处理逻辑（过期数据处理等逻辑）↓↓↓
 * 6、确认为正常报文直接发送
 * 7、确认为故障报文的处理逻辑 ↓↓↓
 * 8、再次筛选延迟上报的预警，如果是延迟上报的，则需要重新从“更新内存”开始
 * 9、发送报文 or 入库
 * 10、根据检测时间判断是否过期  可配置
 * 11、30分钟内无变化不发送报文 可配置
 * 
 */
public class EquipStatusMsgHandlerImpl implements IMsgHandlerService {
	private static final Logger log = LoggerFactory.getLogger(EquipStatusMsgHandlerImpl.class);
	private EquipNodeDao equipNodeDao = new EquipNodeDao();
	
	public String getKey(EquipStatusMsgVO vo){
		
		return vo.getFaultPhenomenonCode()  +"-" + vo.getDeviceState();
	}
	public void msgDetailParser(ConnectorEvent connectorEvent) {
		/** 解析报文 */
		EquipStatusMsgVO esMsgVo = EquipStatusMsgParser(connectorEvent);
		Boolean isExist = true;
		if(esMsgVo == null){
			return;
		}
		if((!"-1".equals(VariableInit.PROJECT_ID))&&("".equals(esMsgVo.getProjectId())||VariableInit.PROJECT_ID.indexOf(esMsgVo.getProjectId())==-1)){
			return;
		}
		//key:440614000000010240-54
		AllFaultData fd = Context.getUniqueInstance().getLocal_XianXiangDeviceState(esMsgVo.getDeviceId()+"-"+esMsgVo.getFaultType());
		
		String old_3_result = "";
//		log.info("解析完报文后放入内存，打印AllFaultData大小和内容[{}][{}]",Context.getUniqueInstance().loadLocalFaultMap().size(),Context.getUniqueInstance().loadLocalFaultMap().toString());
		try{
			if(fd == null){
				isExist = false;
				AllFaultData allFD = new AllFaultData(esMsgVo,3);
				Context.getUniqueInstance().updateLocal_XianXiangDeviceStateMap(esMsgVo.getDeviceId()+"-"+esMsgVo.getFaultType(), allFD);
				fd = allFD;
				
			}else{
				EquipStatusMsgVO svo_temp = fd.getFaultCode_XianXiangMap().get(esMsgVo.getFaultPhenomenonCode());
				if(svo_temp==null){
					isExist = false;
					old_3_result = "";
				}else{
					//判断状态变化
					old_3_result = getKey(svo_temp);
				}
				fd.updateMenData_XianXiang(esMsgVo);
			}
		}catch(Exception eee){
			log.error("更新内存",eee);
		}
		
		
		
		EquipStatusMsgVO svo = fd.getFaultCode_XianXiangMap().get(esMsgVo.getFaultPhenomenonCode());
		Date now = new Date();
		try{
			//判断状态变化
//			if(old_3_result.equals(svo.getFaultPhenomenonCode()  +"-" + svo.getDeviceState() + "-" + svo.getReal_value())&&svo.getLastWarningSendTime()!=null&&((now.getTime()-svo.getLastWarningSendTime().getTime())<VariableInit.NO_CHANGE_MINS*60*1000)){
			if(old_3_result.equals(getKey(svo))){
				//状态未变化、并且30分钟之内已经发送过了，不再发送；
				//否则发送,即：状态变化了、或者状态未变化但是距离上一次发送完已经过去30分钟
				log.info("【DB前后状态无变化、并且 "+DateUtil.format2Str(svo.getLastWarningSendTime())+" 已经发送过报文】 不再发送报文，该设备[{}]处理结束！",esMsgVo.getDeviceId());
				return;
			}
			//最后发送时间，不是故障时间，也不是状态变化时间，所以需要重新建一个时间字段标记
			svo.setLastWarningSendTime(now);
		}catch(Exception e1){
			log.error("DB变化的判断："+svo.getLastWarningSendTime()==null?"null":DateUtil.format2Str(svo.getLastWarningSendTime()),e1);
			return;
		}
//		log.info("【前后状态发生变化】每一个进入该解析的对象都打印：【"+svo.toALLString()+"】");
		try{
			Date checkdate = DateUtil.str2Date(svo.getCheckTime());
			if((new Date().getTime()-checkdate.getTime())>24*60*60*1000){
				log.error("DB["+svo.toString()+"]该设备检测状态信息过期，超过24小时没有更新(已从内存中清理)！！");
				Context.getUniqueInstance().loadLocal_XianXiangFaultMap().get(svo.getDeviceId()+"-"+svo.getFaultType()).getFaultCode_XianXiangMap().remove(svo.getFaultPhenomenonCode());
				return;
			}
		}catch(Exception e){
			log.error("[检测时间格式有误，["+svo.toALLString()+"]",e);
			Context.getUniqueInstance().loadLocal_XianXiangFaultMap().get(svo.getDeviceId()+"-"+svo.getFaultType()).getFaultCode_XianXiangMap().remove(svo.getFaultPhenomenonCode());
			return;
		}
		
		//TODO 数据库操作,优化(每一个变化的都要insert到历史表)
		try {
			equipNodeDao.msgRecordBaseHistory(svo);
		} catch (Exception e) {
			log.error("DB报文插入历史表失败！"+svo.toNewString(),e);
		}
		
		synchronized(this){
			try {
				if(isExist){//实时表中存在 ，则更新
					equipNodeDao.msgRecordBaseUpdate(svo);
				}else{//实时表中不存在，则插入
					equipNodeDao.msgRecordBaseInsert(svo);
				}
			} catch (Exception e) {
				log.error("DB报文插入/更新实时表失败！"+svo.toNewString(),e);
			}
		}
	}
	/**
	 * 处理MQ
	 * 
	 * @param connectorEvent封装的MQ消息事件
	 */

	@Override
	public void msgParser(ConnectorEvent connectorEvent) {
		/** 解析报文 */
		EquipStatusMsgVO esMsgVo = EquipStatusMsgParser(connectorEvent);
		
		if(esMsgVo == null){
			return;
		}
		if((!"-1".equals(VariableInit.PROJECT_ID))&&("".equals(esMsgVo.getProjectId())||VariableInit.PROJECT_ID.indexOf(esMsgVo.getProjectId())==-1)){
			return;
		}
		AllFaultData fd = Context.getUniqueInstance().getLocalDeviceState(esMsgVo.getDeviceId());
		
		//根据禁止上传的故障现象编码 过滤
		if(filterMethod(esMsgVo)==1){
			Context.getUniqueInstance().removeLocalDeviceState(esMsgVo.getDeviceId());
			log.info("内存中删除检测结果(整个设备)：[{}]",esMsgVo.toALLString());
			return;
		}else if(filterMethod(esMsgVo)==2){
			log.info("内存中删除检测结果(故障编码)：[{}]",esMsgVo.toALLString());
			if(fd!=null){
				ConcurrentHashMap<String,EquipStatusMsgVO> faultMap = fd.getFaultMap();
				for(Entry<String, EquipStatusMsgVO> vo:faultMap.entrySet()){
					if(esMsgVo.getDeviceId().equals(vo.getValue().getDeviceId())&&esMsgVo.getFaultPhenomenonCode().equals(vo.getValue().getFaultPhenomenonCode())){
						faultMap.remove(vo.getKey());
						log.info("找到并已删除[{}]",esMsgVo.toALLString());
					}
				}
			}
			return;
		}
		String old_result = "";
//		log.info("解析完报文后放入内存，打印AllFaultData大小和内容[{}][{}]",Context.getUniqueInstance().loadLocalFaultMap().size(),Context.getUniqueInstance().loadLocalFaultMap().toString());
		try{
			if(fd == null){//如果没有上传该设备的‘14’故障，那‘14’对应的value为空
				AllFaultData allFD = new AllFaultData(esMsgVo);
				Context.getUniqueInstance().updateLocalDeviceStateMap(esMsgVo.getDeviceId(), allFD);
				fd = allFD;
				
			}else{
				EquipStatusMsgVO svo_temp = fd.getFault();
				if(svo_temp==null){
					old_result = "";
				}else{
					//判断状态变化
				    
				    if("1".equals(svo_temp.getDeviceState())){
				    	old_result = "all-"+svo_temp.getDeviceState();
				    }else{
				    	old_result = svo_temp.getFaultType()+"-"+svo_temp.getFaultPhenomenonCode()+"-"+svo_temp.getDeviceState();
				    }
				}
				fd.updateMenData(esMsgVo);
			}
		}catch(Exception eee){
			log.error("更新内存",eee);
		}
		
		try{
			log.info("每一个进入该解析的对象都打印[esMsgVo.getDeviceId()]设备编号："+esMsgVo.getDeviceId());
			
			//1207 用fd.isFault()来判断状态变化 【时间和状态变化都不能用之前的，之前的用限定条件，不适用】
			
			
			EquipStatusMsgVO svo = fd.getFault();
			Date now = new Date();
			try{
				String new_result = "";
				if("1".equals(svo.getDeviceState())){
					new_result = "all-"+svo.getDeviceState();
			    }else{
			    	new_result = svo.getFaultType()+"-"+svo.getFaultPhenomenonCode()+"-"+svo.getDeviceState();
			    }
				//判断状态变化
				if(old_result.equals(new_result)&&svo.getLastSendTime()!=null&&((now.getTime()-svo.getLastSendTime().getTime())<VariableInit.NO_CHANGE_MINS*60*1000)){
					//状态未变化、并且30分钟之内已经发送过了，不再发送；
					//否则发送,即：状态变化了、或者状态未变化但是距离上一次发送完已经过去30分钟
					log.info("【前后状态 无变化、并且 "+DateUtil.format2Str(svo.getLastSendTime())+" 已经发送过报文】 不再发送报文，该设备[{}]处理结束！",esMsgVo.getDeviceId());
					return;
				}
				log.info(svo.getMsgId()+","+svo.getDeviceId()+",1:"+old_result.equals(new_result)+"4:"+old_result+","+new_result);
				log.info(svo.getMsgId()+","+svo.getDeviceId()+",2:"+(svo.getLastSendTime()!=null));
				if((svo.getLastSendTime()!=null)){
					log.info(svo.getMsgId()+","+svo.getDeviceId()+",3:"+((now.getTime()-svo.getLastSendTime().getTime())<30*60*1000));
				}

				//最后发送时间，不是故障时间，也不是状态变化时间，所以需要重新建一个时间字段标记
				svo.setLastSendTime(now);
				//1207  放在这里或者放在发送mq的地方，需要再斟酌,二次里也需要添加
				//fd.setLastFaultTime(now);
			}catch(Exception e1){
				log.error("变化的判断："+svo.getLastSendTime()==null?"null":DateUtil.format2Str(svo.getLastSendTime()),e1);
				return;
			}
//			log.info("【前后状态发生变化】每一个进入该解析的对象都打印：【"+svo.toALLString()+"】");
			try{
				Date checkdate = DateUtil.str2Date(svo.getCheckTime());
				if((new Date().getTime()-checkdate.getTime())/60/60/1000L>VariableInit.OUT_HOURS*1L){
					log.error("["+svo.toString()+"]该设备检测状态信息过期["+svo.getCheckTime()+"]，超过"+VariableInit.OUT_HOURS+"小时没有更新(已从内存中清理)！！");
//							Context.getUniqueInstance().loadLocalFaultMap().get(devicekey).clear14Fault();
//							TODO !!!!!!!!!!!多线程同步出现问题
					Context.getUniqueInstance().loadLocalFaultMap().get(esMsgVo.getDeviceId()).getFaultMap().remove(svo.getFaultType());
					return;
				}
			}catch(Exception e){
				log.error("[检测时间格式有误，["+svo.toALLString()+"]",e);
				Context.getUniqueInstance().loadLocalFaultMap().get(esMsgVo.getDeviceId()).getFaultMap().remove(svo.getFaultType());
				return;
			}
			
			if(fd.isFault() == false){//正常
				//TODO  装封装的对象直接发MQ到故障收集,发AllFaultData := entry.getValue()
				//entry.getValue()
				//处理正常情况下怎么从map中取 报文msg对象
				log.info("正常报文出口！！！");
				fd.addChangeSize();
				
				//30分钟以后，也没有收到故障报文，说明内存中一直是正常，则会定时发正常报文
				IncidentStatusInterval isi = Context.getUniqueInstance().getIncidentStatusIntervalMap().get(svo.getDeviceId());
				//isi为空，或者变为故障的时间据当前时间超过  周期     ，则需要修复
				//isi不为空，即预警数量过多了，而却短时间内收到了正常报文，则不修复
				//半个小时之内报不出来，半个小时之后，如果想报出来，还得看看周期已没有超过30min
				if(isi!=null&&(new Date().getTime()-fd.getLastFaultTime().getTime())/1000/60<isi.getWait_lengths()){
					log.info("[{}]故障处理时间为[{}],小于周期时长[{}]",svo.getDeviceId(),DateUtil.format2Str(fd.getLastFaultTime()),""+isi.getWait_lengths());
					return;
				}
				sendMsg(svo);
			}else{//故障 需要查询根节点
				log.info("故障报文出口！！！");
				
				if(filterMethod(svo)==2){
					log.info("【发送前】内存中删除检测结果(故障编码)：[{}]",svo.toALLString());
					if(fd!=null){
						ConcurrentHashMap<String,EquipStatusMsgVO> faultMap = fd.getFaultMap();
						for(Entry<String, EquipStatusMsgVO> vo:faultMap.entrySet()){
							if(svo.getDeviceId().equals(vo.getValue().getDeviceId())&&svo.getFaultPhenomenonCode().equals(vo.getValue().getFaultPhenomenonCode())){
								faultMap.remove(vo.getKey());
								log.info("【发送前】找到并已删除[{}]",svo.toALLString());
							}
						}
					}
					secondSendMethod(fd,old_result);
					return;
				}
				//虽然故障，但是状态没有变化，不需要标记故障时间
				if((now.getTime()-svo.getLastSendTime().getTime())>=30*60*1000){
					
				}else{//由正常变为故障了，记录当前发生故障的时间
					log.info("上报预警过多，标记上报故障时间[{}],[{}]",svo.getDeviceId(),DateUtil.format2Str(new Date()));
					fd.setLastFaultTime(new Date());
				}
				
				fd.addChangeSize();
				sendMsg(svo);
				Context.getUniqueInstance().loadLocalFaultMap().get(esMsgVo.getDeviceId()).clear14Fault();
			}
		}catch(Exception ee){
			log.error("分析发送报文的结果时异常：[{}]+ee"+ee);
		}
		
//		log.debug("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓纯粹是为了打印内存中的数据↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
//		Context.getUniqueInstance().getLocalDeviceState(esMsgVo.getDeviceId());
//		log.debug("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑纯粹是为了打印内存中的数据↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");

	}
	public void sendMsg(EquipStatusMsgVO svo){
		if("MQ".equals(VariableInit.SEND_TYPE)){
			
			if("TOPIC".equals(VariableInit.SEND_MQ_TYPE)){
				MqMsgSender.sendMessage(VariableInit.MQ_MAINTAININF_TOPIC,svo.toStatusMsgString());
			}else{//QUEUE
				MqQueueMsgSender.sendMessage(VariableInit.MQ_MAINTAININF_TOPIC,svo.toStatusMsgString());
			}
		}else{
			try {
				equipNodeDao.msgDetailInsert(svo);
			} catch (Exception e) {
				log.error("报文插入数据库失败！"+svo.toNewString(),e);
			}
		}
	}
	public void secondSendMethod(AllFaultData fd,String old_result){
		try{
			EquipStatusMsgVO svo = fd.getFault();
			Date now = new Date();
			String new_result = "";
			if("1".equals(svo.getDeviceState())){
				new_result = "all-"+svo.getDeviceState();
		    }else{
		    	new_result = svo.getFaultType()+"-"+svo.getDeviceState();
		    }
			//判断状态变化
			if(old_result.equals(new_result)&&((now.getTime()-svo.getLastSendTime().getTime())<VariableInit.NO_CHANGE_MINS*60*1000)){
				//状态未变化、并且30分钟之内已经发送过了，不再发送；
				//否则发送,即：状态变化了、或者状态未变化但是距离上一次发送完已经过去30分钟
				log.info("【再次】【前后状态无变化、并且 "+DateUtil.format2Str(svo.getLastSendTime())+" 已经发送过报文】 不再发送报文，该设备[{}]处理结束！",svo.getDeviceId());
				return;
			}
			svo.setLastSendTime(now);
//			log.info("【前后状态发生变化】每一个进入该解析的对象都打印：【"+svo.toALLString()+"】");
			try{
				Date checkdate = DateUtil.str2Date(svo.getCheckTime());
				if((new Date().getTime()-checkdate.getTime())>VariableInit.OUT_HOURS*60*60*1000){
					log.error("【再次】["+svo.toString()+"]该设备检测状态信息过期，超过"+VariableInit.OUT_HOURS+"小时没有更新(已从内存中清理)！！");
//							Context.getUniqueInstance().loadLocalFaultMap().get(devicekey).clear14Fault();
//							TODO !!!!!!!!!!!多线程同步出现问题
					Context.getUniqueInstance().loadLocalFaultMap().get(svo.getDeviceId()).getFaultMap().remove(svo.getFaultType());
					return;
				}
			}catch(Exception e){
				log.error("【再次】[检测时间格式有误，["+svo.toALLString()+"]",e);
				Context.getUniqueInstance().loadLocalFaultMap().get(svo.getDeviceId()).getFaultMap().remove(svo.getFaultType());
				return;
			}
			if(fd.isFault() == false){//正常
				//TODO  装封装的对象直接发MQ到故障收集,发AllFaultData := entry.getValue()
				//entry.getValue()
				//处理正常情况下怎么从map中取 报文msg对象
				log.info("【再次】正常报文出口！！！");
				fd.addChangeSize();
				//30分钟以后，也没有收到故障报文，说明内存中一直是正常，则会定时发正常报文
				IncidentStatusInterval isi = Context.getUniqueInstance().getIncidentStatusIntervalMap().get(svo.getDeviceId());
				//isi为空，或者变为故障的时间据当前时间超过  周期     ，则需要修复
				//isi不为空，即预警数量过多了，而却短时间内收到了正常报文，则不修复
				if(isi!=null&&(fd.getLastFaultTime().getTime()-new Date().getTime())/1000/60<isi.getWait_lengths()){
					return;
				}
				sendMsg(svo);
			}else{//故障 需要查询根节点
				log.info("【再次】故障报文出口！！！");
				fd.addChangeSize();
				//虽然故障，但是状态没有变化，不需要标记故障时间
				if((now.getTime()-svo.getLastSendTime().getTime())>=30*60*1000){
					
				}else{//由正常变为故障了，记录当前发生故障的时间
					log.info("上报预警过多，标记上报故障时间[{}]",svo.getDeviceId());
					fd.setLastFaultTime(new Date());
				}
				sendMsg(svo);
				Context.getUniqueInstance().loadLocalFaultMap().get(svo.getDeviceId()).clear14Fault();
			}
		}catch(Exception e){
			log.error("【再次】分析发送报文的结果时异常：\n",e);
		}
	}
	/**
	 * 0：未禁止     1：禁止整个设备      2：禁止故障编码
	 * @param esMsgVo
	 * @return
	 */
	public int filterMethod(EquipStatusMsgVO esMsgVo ){
		int result = 0;
		
		ConcurrentHashMap<String,IncidentWarningDelayDetect> filterFaultCodeMap = Context.getUniqueInstance().getFilterFaultCodeMap();
		if(filterFaultCodeMap.get(esMsgVo.getDeviceId())!=null){
			log.info("被禁止上报的检测结果(整个设备)：[{}]",esMsgVo.toALLString());
			return 1;
		}else if(filterFaultCodeMap.get(esMsgVo.getDeviceId()+"-"+esMsgVo.getFaultPhenomenonCode())!=null){
			log.info("被禁止上报的检测结果(故障编码)：[{}]",esMsgVo.toALLString());
			return 2;
		}
		
		return result;
	}

	/**
	 * 设备状态报障报文解析方法。
	 */
	/**
	 * 设备状态报障报文解析方法。
	 */
	public EquipStatusMsgVO EquipStatusMsgParser(ConnectorEvent connectorEvent) {
		String msg = connectorEvent.getConnectorMsg()+"";
		String[] msgStr = msg.split(",");
		EquipStatusMsgVO esMsgVO = null;

		// TODO 回头将所有字段 逐个验证合法性。（最好将所有验证抽离新方法。不抽也行，那就算是一个类搞定。看情况。）
		if (msg.indexOf("EQUIPSTATUS") != 0) {
			log.warn("[ERROR]报文未以“EQUIPSTATUS”开始:" + msg);
			return null;
		}
//		if (msgStr.length != 17) {
//			log.warn("[ERROR]“报文长度不为17”错误:" + msg);
//			return null;
//		}
		if (!msgStr[12].equals("") && DateUtil.str2Date(msgStr[12]) == null) {// 此字段有数据但不是时间格式时(无数据时不验证)
			log.warn("[ERROR]“故障发生时间”错误:" + msg);
			return null;
		}
		if (!msgStr[13].equals("") && DateUtil.str2Date(msgStr[13]) == null) {// 此字段有数据但不是时间格式时(无数据时不验证)
			log.warn("[ERROR]“数据上传时间”错误:" + msg);
			return null;
		}
		if (!msgStr[14].equals("") && DateUtil.str2Date(msgStr[14]) == null) {// 此字段有数据但不是时间格式时(无数据时不验证)
			log.warn("[ERROR]“设备检测时间”错误:" + msg);
			return null;
		}
//		if ("2".equals(msgStr.length>=17?msgStr[16]:"1")) {// 此字段有数据但不是时间格式时(无数据时不验证)
//			log.warn("[ERROR]“设备故障级别”错误,综合分析不处理预警报文:" + msg);
//			return null;
//		}

		esMsgVO = new EquipStatusMsgVO();
		esMsgVO.setMsgType(msgStr[0]); // 接口类型 此字段值固定为：“EQUIPSTATUS”
		esMsgVO.setVersion(msgStr[1]);// 接口版本 此字段值固定为：“1.0”
		esMsgVO.setMsgId(msgStr[2]);// MSGID 唯一标识，要求采用UUID
		esMsgVO.setProjectId(msgStr[3]);// 项目编号
		esMsgVO.setDeviceId(msgStr[4]);// 设备编号
		esMsgVO.setEquipServiceFlag(msgStr[5]);// 服务形态 1前端设备2中心服务
		esMsgVO.setDeviceType(msgStr[6]);// 设备类型
		esMsgVO.setDeviceState(msgStr[7]);// 当前状态 1正常2故障
		esMsgVO.setFaultType(msgStr[8]);// 故障类型 11手动上报12数据预警（适配器主动检测）13设备主动上报（心跳）14数据降效15离线检查16其他
		esMsgVO.setFaultPhenomenonCode(msgStr[9]);// 故障现象编码 参照故障上报协议 多个使用@符号分割。转化为平台使用替换@为,
		esMsgVO.setFaultDesc(msgStr[10]);// 故障描述 对故障的描述
		esMsgVO.setFilePath(msgStr[11]);// 故障文件地址 多个文件，用”;”分隔
		esMsgVO.setFaultTime(msgStr[12]);// 故障发生时间
		esMsgVO.setUploadTime(msgStr[13]);// 数据上传时间
		esMsgVO.setCheckTime(msgStr[14]);// 设备检测时间
		esMsgVO.setAdapterName(msgStr[15]);// 适配器名称
		esMsgVO.setMsglevel(msgStr.length>=17?msgStr[16]:"1");//如果不足17位，默认为故障报文，级别
		esMsgVO.setLanenum(msgStr.length>=18?msgStr[17]:" ");//如果不足18位，默认为故障报文，车道
		esMsgVO.setReal_value(msgStr.length>=19?msgStr[18]:"");
		esMsgVO.setRange(msgStr.length>=20?msgStr[19]:"");
		esMsgVO.setInstallAddress(msgStr.length>=21?msgStr[20]:"");
		return esMsgVO;
	}

}
