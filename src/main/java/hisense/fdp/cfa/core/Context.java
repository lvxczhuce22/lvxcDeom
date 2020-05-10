package hisense.fdp.cfa.core;

import hisense.fdp.cfa.model.ConnectorEvent;
import hisense.fdp.cfa.model.EquipStatusMsgVO;
import hisense.fdp.cfa.model.IncidentStatusInterval;
import hisense.fdp.cfa.model.IncidentWarningDelayDetect;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Context
{
	private static final Logger log = LoggerFactory.getLogger(Context.class);
	
	//key是设备编号，value是所有的故障信息或正常状态信息
	private ConcurrentHashMap<String,AllFaultData> localFaultMap = new ConcurrentHashMap<String,AllFaultData>();
	private ConcurrentHashMap<String,AllFaultData> localFault_XianXiangMap = new ConcurrentHashMap<String,AllFaultData>();

	//重复性误报对象
	private Map<String,Integer> repeatFaultMap = new HashMap<String,Integer>();
	//节点对象
	private Map<String,String> nodeTreeMap = new HashMap<String,String>();
	//被过滤的故障现象编码 key=device_sn + faultcode
	private ConcurrentHashMap<String,IncidentWarningDelayDetect> filterFaultCodeMap = new ConcurrentHashMap<String,IncidentWarningDelayDetect>();
	//被过滤的设备 key = device_sn
	private ConcurrentHashMap<String,IncidentWarningDelayDetect> filterDeviceSnMap = new ConcurrentHashMap<String,IncidentWarningDelayDetect>();

	private ConcurrentHashMap<String, IncidentStatusInterval> IncidentStatusIntervalMap = new ConcurrentHashMap<String, IncidentStatusInterval>();
	
	private final BlockingQueue<EquipStatusMsgVO> sendMsgQueue = new LinkedBlockingQueue<EquipStatusMsgVO>();
	
	public static Boolean reviceFlag = false;
	public static Boolean reviceToDbFlag = false;

	/** Context 单例 */
	private static final Context uniqueInstance = new Context();


	public BlockingQueue<EquipStatusMsgVO> getSendMsgQueue() {
		return sendMsgQueue;
	}

	public void insertMsgQueue(EquipStatusMsgVO obj) {
		sendMsgQueue.offer(obj);
		log.info("[准备发送的消息已入队列] 队列当前长度：" + sendMsgQueue.size());
	}
	public synchronized EquipStatusMsgVO getMsgByPriority() {
		if (getMsgQueueLength() == 0) {
			return null;
		}

		return sendMsgQueue.poll();
	}
	public int getMsgQueueLength() {
		return sendMsgQueue.size();
	}
	/**
	 * 获取Context唯一实例
	 * 
	 * @return
	 */
	public static Context getUniqueInstance()
	{
		return uniqueInstance;
	}

	/**
	 * 打印日志并退出应用程序，当检测到程序不能正常运行应该调用该方法退出应用程序。 如入必要的配置参数不存在、工作线程无法创建等
	 */
	public void exitApplicationWithLog(String logMessage)
	{
		log.error("程序退出:{}", logMessage);
		System.exit(1);
	}

	public synchronized ConcurrentHashMap<String, IncidentWarningDelayDetect> getFilterFaultCodeMap() {
		return filterFaultCodeMap;
	}

	public synchronized void setFilterFaultCodeMap(
			ConcurrentHashMap<String, IncidentWarningDelayDetect> filterFaultCodeMap) {
		this.filterFaultCodeMap = filterFaultCodeMap;
	}

	public synchronized ConcurrentHashMap<String, IncidentWarningDelayDetect> getFilterDeviceSnMap() {
		return filterDeviceSnMap;
	}

	public synchronized void setFilterDeviceSnMap(
			ConcurrentHashMap<String, IncidentWarningDelayDetect> filterDeviceSnMap) {
		this.filterDeviceSnMap = filterDeviceSnMap;
	}

	public ConcurrentHashMap<String, IncidentStatusInterval> getIncidentStatusIntervalMap() {
		return IncidentStatusIntervalMap;
	}

	public void setIncidentStatusIntervalMap(
			ConcurrentHashMap<String, IncidentStatusInterval> incidentStatusIntervalMap) {
		IncidentStatusIntervalMap = incidentStatusIntervalMap;
	}
	public ConcurrentHashMap<String,AllFaultData> loadLocal_XianXiangFaultMap()
	{
		return this.localFault_XianXiangMap;
	}
	
	public AllFaultData getLocal_XianXiangDeviceState(String deviceKey)
	{
//		log.debug("故障树  获取内存中全部状态信息前打印localFaultMap大小及内容[{}][{}]",localFaultMap.size(),localFaultMap.toString());
		return localFault_XianXiangMap.get(deviceKey);
	}
//	public void removeLocalDeviceState(String deviceKey){
//		localFaultMap.remove(deviceKey);
//	}

	public synchronized void updateLocal_XianXiangDeviceStateMap(String deviceKey, AllFaultData fd)
	{
		localFault_XianXiangMap.put(deviceKey, fd);
	}

	public ConcurrentHashMap<String,AllFaultData> loadLocalFaultMap()
	{
//		log.debug("故障树  父节点检测前获取内存中全部状态信息，打印localFaultMap大小及内容[{}][{}]",localFaultMap.size(),localFaultMap.toString());
		return this.localFaultMap;
	}
	
	public AllFaultData getLocalDeviceState(String deviceKey)
	{
//		log.debug("故障树  获取内存中全部状态信息前打印localFaultMap大小及内容[{}][{}]",localFaultMap.size(),localFaultMap.toString());
		return localFaultMap.get(deviceKey);
	}
	public void removeLocalDeviceState(String deviceKey){
		localFaultMap.remove(deviceKey);
	}

	public synchronized void updateLocalDeviceStateMap(String deviceKey, AllFaultData esMsgVo)
	{
		localFaultMap.put(deviceKey, esMsgVo);
	}

	/**
	 * 清空设备检测状态在本地的缓存
	 */
	public synchronized void clearLocalDeviceStateMap()
	{
		localFaultMap.clear();
	}
	
	public Integer getRepeatFault(String deviceKey)
	{
		return repeatFaultMap.get(deviceKey);
	}

	public synchronized void updateRepeatFaultMap(String deviceKey, Integer ind)
	{
		repeatFaultMap.put(deviceKey, ind);
	}

	/**
	 * 清空设备检测状态在本地的缓存
	 */
	public synchronized void clearRepeatFaultMap()
	{
		repeatFaultMap.clear();
	}
	
	public String getNodeTree(String deviceKey)
	{
//		log.info("节点树  nodeTreeMap大小及内容[{}][{}]",nodeTreeMap.size(),nodeTreeMap.toString());
		log.info("节点树  nodeTreeMap大小及内容[{}][{}]",nodeTreeMap.size(),"太多了不展示");
		return nodeTreeMap.get(deviceKey);
	}

	public synchronized void updateNodeTreeMap(String deviceKey, String ind)
	{
		nodeTreeMap.put(deviceKey, ind);
	}
	
	public ConcurrentHashMap<String,AllFaultData> loadNodeTreeMap()
	{
		log.info("节点树  开始父节点检测前打印nodeTreeMap，打印nodeTreeMap大小及内容[{}][{}]",nodeTreeMap.size(),"太多了这里也不展示");
		return this.localFaultMap;
	}
}
