package hisense.fdp.cfa.core;

import hisense.fdp.cfa.model.EquipStatusMsgVO;
import hisense.fdp.cfa.util.DateUtil;
import hisense.fdp.cfa.util.VariableInit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllFaultData
{
	@Override
	public String toString() {
		return faultMap!=null?faultMap.toString():"faultMap是空。";
	}

	private static final Logger log = LoggerFactory.getLogger(AllFaultData.class);

	//12主动检测  13设备主动上报 14数据降效 15离线检测（网络） 16内场 17重复  18 延迟
	//通过EquipStatusMsgVO.FaultType区分
	//key是故障现象编码，value是故障报文封装的对象,
	private ConcurrentHashMap<String,EquipStatusMsgVO> faultMap = new ConcurrentHashMap<String,EquipStatusMsgVO>();
	//key是故障现象编码，value是故障报文封装的对象,
	private ConcurrentHashMap<String,EquipStatusMsgVO> faultCode_XianXiangMap = new ConcurrentHashMap<String,EquipStatusMsgVO>();
	//记录设备状态变化的次数
	private int changeSize = 0;
	//计算得出的需要等待的次数
	private int wait_times = 0;
	//当前已等待的次数
	private int cur_waiting_times = 0;
	//最后被处理的故障时间
	private Date lastFaultTime = new Date();
	
	public ConcurrentHashMap<String, EquipStatusMsgVO> getFaultCode_XianXiangMap() {
		return faultCode_XianXiangMap;
	}
	public void setFaultCode_XianXiangMap(
			ConcurrentHashMap<String, EquipStatusMsgVO> faultCode_XianXiangMap) {
		this.faultCode_XianXiangMap = faultCode_XianXiangMap;
	}
	public int getWait_times() {
		return wait_times;
	}
	public void setWait_times(int wait_times) {
		this.wait_times = wait_times;
	}
	public int getCur_waiting_times() {
		return cur_waiting_times;
	}
	public void setCur_waiting_times(int cur_waiting_times) {
		this.cur_waiting_times = cur_waiting_times;
	}
	public Date getLastFaultTime() {
		return lastFaultTime;
	}
	public void setLastFaultTime(Date lastFaultTime) {
		this.lastFaultTime = lastFaultTime;
	}
	public int getChangeSize(){
		return this.changeSize;
	}
	public void setChangeSize(int changeSize){
		this.changeSize = changeSize;
	}
	public void addChangeSize(){
		this.changeSize++;
	}
	public void initSize(){
		this.changeSize = 0;
	}
	public AllFaultData(EquipStatusMsgVO faultinfo,int flag){
		/* TODO (暂时不考虑一下问题)
		 * 采用故障现象编码的分类方式，需要考虑这个对象的初始化，故障的情况给otherFault指定值
		 * 
		 *   1、主动上报存在多个故障现象编码的情况
		 *   2、精确到二级故障才能区分一类的情况
		 *   3、修复不好区分
		 *   4、中心服务类故障处理情况
		 *   5、王丽萍那边不仅要处理故障的，还要处理正常的
		 */
		
		//正常和故障都要存，存正常是为了修复已经存在的维修单
		if(flag==1){
			
			faultMap.put(faultinfo.getFaultType(), faultinfo);
		}else if(flag==3){
			try{
				faultCode_XianXiangMap.put(faultinfo.getFaultPhenomenonCode(), faultinfo);
			}catch(Exception e){
				log.error("dingwei:"+faultinfo.getDeviceId()+"-"+faultinfo.getMsgId());
				log.error("faultCode_XianXiangMap:"+(faultCode_XianXiangMap==null));
				log.error("2:"+(faultinfo.getFaultPhenomenonCode()==null));
			}	
		}

		//faultinfo.getFaultType()=-1表示正常
	}
	public Boolean isFault() throws Exception{
		
		String[] orders = VariableInit.levels_order.split(",");
		for(String order:orders){
			if(faultMap.get(order)!=null&&"2".equals(faultMap.get(order).getDeviceState())){
				return true;
			}
		}
		return false;
	}
	public EquipStatusMsgVO getFault() throws Exception{
		
		String[] orders = VariableInit.levels_order.split(",");
		for(String order:orders){
//			log.info("order: "+order);
			if(faultMap.get(order)!=null&&"2".equals(faultMap.get(order).getDeviceState())){
				return faultMap.get(order);
			}
		}
		EquipStatusMsgVO temp = null;
		EquipStatusMsgVO errorVo = null;
		try{
			for(Entry<String, EquipStatusMsgVO> vo:faultMap.entrySet()){
				errorVo = vo.getValue();
				if(temp==null||(DateUtil.str2Date(vo.getValue().getCheckTime()).getTime()-DateUtil.str2Date(temp.getCheckTime()).getTime())>0){//历史报文为null，或者当前报文检测时间最大
					temp = vo.getValue();
				}
			}
		}catch(Exception e){
			if(errorVo!=null){
				log.info("时间报错问题errorVo："+errorVo.toNewString());
			}else{
				log.error("errorVo is null!");
			}
			if(temp!=null){
				log.info("时间报错问题temp："+temp.toNewString());
			}else{
				log.error("temp is null!");
			}
			return null;
		}
		return temp;
	}
	//只有调用isFault()方法返回true，才可以执行该方法
	public EquipStatusMsgVO getFault_old(){
		
		//10 网电感知故障 
		if(faultMap.get("10")!=null&&"2".equals(faultMap.get("10").getDeviceState())){
			return faultMap.get("10");
		}
		else if(faultMap.get("15")!=null&&"2".equals(faultMap.get("15").getDeviceState())){
			return faultMap.get("15");
		}
		else if(faultMap.get("12")!=null&&"2".equals(faultMap.get("12").getDeviceState())){
			return faultMap.get("12");
		}
		
		else if(faultMap.get("20")!=null&&"2".equals(faultMap.get("20").getDeviceState())){
			return faultMap.get("20");
		}
		else if(faultMap.get("18")!=null&&"2".equals(faultMap.get("18").getDeviceState())){
			return faultMap.get("18");
		}
		else if(faultMap.get("19")!=null&&"2".equals(faultMap.get("19").getDeviceState())){
			return faultMap.get("19");
		}
		else if(faultMap.get("13")!=null&&"2".equals(faultMap.get("13").getDeviceState())){
			return faultMap.get("13");
		}
		//23 网电感知预警
		else if(faultMap.get("23")!=null&&"2".equals(faultMap.get("23").getDeviceState())){
			return faultMap.get("23");
		}
		else if(faultMap.get("16")!=null&&"2".equals(faultMap.get("16").getDeviceState())){
			return faultMap.get("16");
		}
		else if(faultMap.get("17")!=null&&"2".equals(faultMap.get("17").getDeviceState())){
			return faultMap.get("17");
		}
		else if(faultMap.get("14")!=null&&"2".equals(faultMap.get("14").getDeviceState())){
			return faultMap.get("14");
		}
		else{
			/*
			 * 正常  1:map为空(这种暂时可以排除，初始化时会有一条值)  2:map中故障协议状态全部为‘1’
			 * 初始化的第一条要不要处理？？？要不要发！！！当然不发！！！
			 * 假设不初始化第一条，当执行一定时间以内，一定会有所有设备的状态，包括正常和故障！！
			 * 可以设置该综合分析适配器启动10小时以后，在开始分析内存表数据，这样内存表中肯定存在了该设备的状态，不存在map为空的情况！！！
			 * 
			 * 长沙天网 检测一轮10小时，大部分都超期了，remove后返回null
			 */
			EquipStatusMsgVO temp = null;
			for(Entry<String, EquipStatusMsgVO> vo:faultMap.entrySet()){
//				return vo.getValue();
				if(temp==null||(DateUtil.str2Date(vo.getValue().getCheckTime()).getTime()-DateUtil.str2Date(temp.getCheckTime()).getTime())>0){//历史报文为null，或者当前报文检测时间最大
					temp = vo.getValue();
				}
			}
			return temp;
		}
//		return null;
	}
	
	public void clear14Fault(){
	}
	
	public AllFaultData(EquipStatusMsgVO faultinfo){
		/* TODO (暂时不考虑一下问题)
		 * 采用故障现象编码的分类方式，需要考虑这个对象的初始化，故障的情况给otherFault指定值
		 * 
		 *   1、主动上报存在多个故障现象编码的情况
		 *   2、精确到二级故障才能区分一类的情况
		 *   3、修复不好区分
		 *   4、中心服务类故障处理情况
		 *   5、王丽萍那边不仅要处理故障的，还要处理正常的
		 */
		
		//正常和故障都要存，存正常是为了修复已经存在的维修单
		faultMap.put(faultinfo.getFaultType(), faultinfo);

		//faultinfo.getFaultType()=-1表示正常
	}
	
	public synchronized void updateMenData(EquipStatusMsgVO faultinfo){
//		log.info("排查更新内存的先后顺序：[{}]",faultinfo.toALLString());
		//1：正常   2：故障
		if("1".equals(faultinfo.getDeviceState())){//消除逻辑
			
			EquipStatusMsgVO vo = faultMap.put(faultinfo.getFaultType(), faultinfo);
			if(vo!=null){
				faultinfo.setLastSendTime(vo.getLastSendTime());
			}
//			if("12,13,16".indexOf(faultinfo.getFaultType())>-1&&faultMap.get("15")!=null){
			/*if("13".indexOf(faultinfo.getFaultType())>-1&&faultMap.get("15")!=null){

				faultMap.get("15").setDeviceState("1");
				faultMap.get("15").setFaultDesc(""+faultinfo.getFaultDesc());
				faultMap.get("15").setFaultPhenomenonCode(""+faultinfo.getDeviceType()+".-1");
				faultMap.get("15").setFaultTime(faultinfo.getFaultTime());
				faultMap.get("15").setUploadTime(faultinfo.getUploadTime());
				faultMap.get("15").setCheckTime(faultinfo.getCheckTime());
			}*/
//			if("15".indexOf(faultinfo.getFaultType())>-1&&faultMap.get("10")!=null){
//
//				faultMap.get("10").setDeviceState("1");
//				faultMap.get("10").setFaultDesc(""+faultinfo.getFaultDesc());
//				faultMap.get("10").setFaultPhenomenonCode(""+faultinfo.getDeviceType()+".-1");
//				faultMap.get("10").setFaultTime(faultinfo.getFaultTime());
//				faultMap.get("10").setUploadTime(faultinfo.getUploadTime());
//				faultMap.get("10").setCheckTime(faultinfo.getCheckTime());
//			}
			if("10".indexOf(faultinfo.getFaultType())>-1&&faultMap.get("23")!=null){

				faultMap.get("23").setDeviceState("1");
				faultMap.get("23").setFaultDesc(""+faultinfo.getFaultDesc());
				faultMap.get("23").setFaultPhenomenonCode(""+faultinfo.getDeviceType()+".-1");
				faultMap.get("23").setFaultTime(faultinfo.getFaultTime());
				faultMap.get("23").setUploadTime(faultinfo.getUploadTime());
				faultMap.get("23").setCheckTime(faultinfo.getCheckTime());
			}
			if("23".indexOf(faultinfo.getFaultType())>-1&&faultMap.get("10")!=null){

				faultMap.get("10").setDeviceState("1");
				faultMap.get("10").setFaultDesc(""+faultinfo.getFaultDesc());
				faultMap.get("10").setFaultPhenomenonCode(""+faultinfo.getDeviceType()+".-1");
				faultMap.get("10").setFaultTime(faultinfo.getFaultTime());
				faultMap.get("10").setUploadTime(faultinfo.getUploadTime());
				faultMap.get("10").setCheckTime(faultinfo.getCheckTime());
			}
			/*if("15".indexOf(faultinfo.getFaultType())>-1&&faultMap.get("18")!=null){

				faultMap.get("18").setDeviceState("1");
				faultMap.get("18").setFaultDesc(""+faultinfo.getFaultDesc());
				faultMap.get("18").setFaultPhenomenonCode(""+faultinfo.getDeviceType()+".-1");
				faultMap.get("18").setFaultTime(faultinfo.getFaultTime());
				faultMap.get("18").setUploadTime(faultinfo.getUploadTime());
				faultMap.get("18").setCheckTime(faultinfo.getCheckTime());
			}*/
		}else{//新增（覆盖）逻辑
			
			EquipStatusMsgVO vo = faultMap.put(faultinfo.getFaultType(), faultinfo);
			if(vo!=null){
				faultinfo.setLastSendTime(vo.getLastSendTime());
			}
			
			if("10".indexOf(faultinfo.getFaultType())>-1&&faultMap.get("23")!=null){

				faultMap.get("23").setDeviceState("1");
//				faultMap.get("23").setFaultDesc("感知器恢复");
				faultMap.get("23").setFaultDesc(""+faultinfo.getFaultDesc());
				faultMap.get("23").setFaultPhenomenonCode(""+faultinfo.getDeviceType()+".-1");
				faultMap.get("23").setFaultTime(faultinfo.getFaultTime());
				faultMap.get("23").setUploadTime(faultinfo.getUploadTime());
				faultMap.get("23").setCheckTime(faultinfo.getCheckTime());
			}
			if("23".indexOf(faultinfo.getFaultType())>-1&&faultMap.get("10")!=null){

				faultMap.get("10").setDeviceState("1");
				faultMap.get("10").setFaultDesc(""+faultinfo.getFaultDesc());
				faultMap.get("10").setFaultPhenomenonCode(""+faultinfo.getDeviceType()+".-1");
				faultMap.get("10").setFaultTime(faultinfo.getFaultTime());
				faultMap.get("10").setUploadTime(faultinfo.getUploadTime());
				faultMap.get("10").setCheckTime(faultinfo.getCheckTime());
			}
			/*if("15".indexOf(faultinfo.getFaultType())>-1&&faultMap.get("18")!=null){

				faultMap.get("18").setDeviceState("1");
				faultMap.get("18").setFaultDesc(""+faultinfo.getFaultDesc());
				faultMap.get("18").setFaultPhenomenonCode(""+faultinfo.getDeviceType()+".-1");
				faultMap.get("18").setFaultTime(faultinfo.getFaultTime());
				faultMap.get("18").setUploadTime(faultinfo.getUploadTime());
				faultMap.get("18").setCheckTime(faultinfo.getCheckTime());
			}*/
			/*if("18".indexOf(faultinfo.getFaultType())>-1&&faultMap.get("15")!=null){

				faultMap.get("15").setDeviceState("1");
				faultMap.get("15").setFaultDesc(""+faultinfo.getFaultDesc());
				faultMap.get("15").setFaultPhenomenonCode(""+faultinfo.getDeviceType()+".-1");
				faultMap.get("15").setFaultTime(faultinfo.getFaultTime());
				faultMap.get("15").setUploadTime(faultinfo.getUploadTime());
				faultMap.get("15").setCheckTime(faultinfo.getCheckTime());
			}*/
		}
		
	}
	public synchronized void updateMenData_XianXiang(EquipStatusMsgVO faultinfo){
		log.info("排查更新内存的先后顺序：[{}]",faultinfo.toALLString());
		//1：正常   2：故障
		if("1".equals(faultinfo.getDeviceState())){//消除逻辑
			
			EquipStatusMsgVO vo = faultCode_XianXiangMap.put(faultinfo.getFaultPhenomenonCode(), faultinfo);
			if(vo!=null){
				faultinfo.setLastWarningSendTime(vo.getLastWarningSendTime());
			}
		
			for(Entry<String, EquipStatusMsgVO> msgvo:faultCode_XianXiangMap.entrySet()){
				msgvo.getValue().setDeviceState("1");
				msgvo.getValue().setRecoverytime(faultinfo.getFaultTime());
			}
		}else{//新增（覆盖）逻辑
			
			EquipStatusMsgVO vo = faultCode_XianXiangMap.put(faultinfo.getFaultPhenomenonCode(), faultinfo);
			if(vo!=null){
				faultinfo.setLastWarningSendTime(vo.getLastWarningSendTime());
			}
			for(Entry<String, EquipStatusMsgVO> msgvo:faultCode_XianXiangMap.entrySet()){
				if(!faultinfo.getFaultPhenomenonCode().equals(msgvo.getKey())){
					msgvo.getValue().setDeviceState("1");
					msgvo.getValue().setRecoverytime(faultinfo.getFaultTime());
				}
				try{
					
					String[] str = msgvo.getValue().getFaultPhenomenonCode().split("\\.");
					if((str[0]+".-1").equals(msgvo.getKey())){
						msgvo.getValue().setDeviceState("2");
						msgvo.getValue().setRecoverytime(null);
					}
				}catch(Exception e){
					log.error("更新内存失败："+ faultinfo.toALLString(),e);
				}
			}
		}
		
	}
	
	
	public ConcurrentHashMap<String, EquipStatusMsgVO> getFaultMap() {
		return faultMap;
	}

	public void setFaultMap(ConcurrentHashMap<String, EquipStatusMsgVO> faultMap) {
		this.faultMap = faultMap;
	}
	
	public static void main(String[] args){
		Map<String,String> map = new HashMap<String,String>();
		map.put("a", "a-");
		map.put("b", "b-");
		map.put("c", "c-");
		map.put("d", "d-");
		map.put("r", "r-");
		
		System.out.println("1---"+map.toString());
		System.out.println("2---"+map.remove("c"));
		System.out.println("3---"+map.toString());
	}
}
