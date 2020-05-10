package hisense.fdp.cfa.util;

/**
 * 常量
 * 
 */
public interface Constant {
	/** 适配器简称 */
	String ADAPTER_NAME = "EMA";// EquipMaintainAdapter

	/** 设备状态-正常 */
	String EQUIPMENT_NORMAL = "1";
	/** 设备状态-故障 */
	String EQUIPMENT_BREAKDOWN = "4";

	/** 故障来源 ping 设备在线离线 */
	String FAULT_FROM_PING = "01";
	/** 故障来源，设备状态采集适配器报障 */
	String FAULT_FROM_AUTO = "03";

	/** 设备故障类型总分类：故障（10）、预警（20）、需求（30）；具体分类:11手动上报 12设备主动上报，数据预警13离线检查14数据降效15其他 */
	String EQUIP_FAULT_TYPE = "12";

	/**
	 * 事件类型:RECEIVE_DATA消息事件; CONNEDTED连接事件; EXCEPTION异常事件; DISCONNNECTED断连事件;
	 */
	public enum EVENT_TYPE {
		RECEIVE_DATA, EXCEPTION, DISCONNNECTED, CONNECTED
	}

}
