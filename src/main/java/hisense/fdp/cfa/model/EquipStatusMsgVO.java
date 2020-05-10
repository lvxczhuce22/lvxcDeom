package hisense.fdp.cfa.model;

import java.util.Date;

import hisense.fdp.cfa.HiFdpCfa;
import hisense.fdp.cfa.util.DateUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EquipStatusMsgVO {

	@Override
	public String toString() {
		return "EquipStatusMsgVO [deviceId=" + deviceId+ ", deviceState=" + deviceState + ", faultType=" + faultType
				+ ", faultPhenomenonCode=" + faultPhenomenonCode
				+ ", adapterName=" + adapterName
				+ "]";
	}
	
	public String toNewString() {
		return "EquipStatusMsgVO [msgType=" + msgType + ", version=" + version
				+ ", msgId=" + msgId + ", projectId=" + projectId
				+ ", deviceId=" + deviceId + ", equipServiceFlag="
				+ equipServiceFlag + ", deviceType=" + deviceType
				+ ", deviceState=" + deviceState + ", faultType=" + faultType
				+ ", faultPhenomenonCode=" + faultPhenomenonCode
				+ ", faultDesc=" + faultDesc + ", filePath=" + filePath
				+ ", faultTime=" + faultTime + ", uploadTime=" + uploadTime
				+ ", checkTime=" + checkTime + ", adapterName=" + adapterName
				+ ", msglevel=" + msglevel + ", lanenum=" + lanenum
				+ ", lastSendTime=" + lastSendTime + "]";
	}
	public String toALLString() {
		return "EquipStatusMsgVO [deviceId=" + deviceId + ", deviceType=" + deviceType
				+ ", deviceState=" + deviceState + ", faultType=" + faultType
				+ ", faultPhenomenonCode=" + faultPhenomenonCode
				+ ", faultDesc=" + faultDesc + "]";
	}
	private String msgType; // 接口类型 此字段值固定为：“EQUIPSTATUS”
	private String version;// 接口版本 此字段值固定为：“1.0”
	private String msgId;// MSGID 唯一标识，要求采用UUID
	private String projectId;// 项目编号
	private String deviceId;// 设备编号
	private String equipServiceFlag;// 服务形态 1前端设备2中心服务
	private String deviceType;// 设备类型
	private String deviceState;// 当前状态 1正常2故障
	private String faultType;// 故障类型 总分类：故障（10）、预警（20）、需求（30）；具体分类:12数据预警（适配器主动检测）13设备主动上报（心跳）14数据降效15离线检查16内场检测17重复性故障
	private String faultPhenomenonCode;// 故障现象编码 参照故障上报协议 多个使用@符号分割。
	private String faultDesc;// 故障描述 对故障的描述
	private String filePath;// 故障文件地址 多个文件，用”;”分隔
	private String faultTime;// 故障发生时间
	private String uploadTime;// 数据上传时间
	private String checkTime;// 设备检测时间
	private String adapterName;// 设备检测时间
	private String msglevel;//报文级别  1：故障  2：预警  3：提醒
	private String lanenum;//第N车道  预警报文用
	private Date lastSendTime;//报文最后发送时间，仅用于判定该报文是否要发送
	private String recoverytime;
	private Date SendTimeStamp;
	private String installAddress;
	
	private Date lastWarningSendTime;//报文最后发送时间，仅用于判定该报文是否要发送
	//即墨预警级别功能，新增字段
	private String real_value;
	private String range;
		
	public String getInstallAddress() {
		return installAddress;
	}

	public void setInstallAddress(String installAddress) {
		this.installAddress = installAddress;
	}

	public Date getLastWarningSendTime() {
		return lastWarningSendTime;
	}

	public void setLastWarningSendTime(Date lastWarningSendTime) {
		this.lastWarningSendTime = lastWarningSendTime;
	}

	public String getReal_value() {
		return real_value;
	}

	public void setReal_value(String real_value) {
		this.real_value = real_value;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getRecoverytime() {
		return recoverytime;
	}

	public void setRecoverytime(String recoverytime) {
		this.recoverytime = recoverytime;
	}

	public Date getSendTimeStamp() {
		return SendTimeStamp;
	}

	public void setSendTimeStamp(Date sendTimeStamp) {
		SendTimeStamp = sendTimeStamp;
	}

	public Date getLastSendTime() {
		return lastSendTime;
	}
	public void setLastSendTime(Date lastSendTime) {
		this.lastSendTime = lastSendTime;
	}
	public String getLanenum() {
		return lanenum;
	}
	public void setLanenum(String lanenum) {
		this.lanenum = lanenum;
	}
	public String getMsglevel() {
		return msglevel;
	}
	public void setMsglevel(String msglevel) {
		this.msglevel = msglevel;
	}
	private static final Logger log = LoggerFactory.getLogger(EquipStatusMsgVO.class);
	
	public EquipStatusMsgVO() {
		super();
	}

	public EquipStatusMsgVO(String deviceId, String deviceState,
			String faultType, String faultDesc) {
		super();
		this.deviceId = deviceId;
		this.deviceState = deviceState;
		this.faultType = faultType;
		this.faultDesc = faultDesc;
	}
	
	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getEquipServiceFlag() {
		return equipServiceFlag;
	}

	public void setEquipServiceFlag(String equipServiceFlag) {
		this.equipServiceFlag = equipServiceFlag;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceState() {
		return deviceState;
	}

	public void setDeviceState(String deviceState) {
		this.deviceState = deviceState;
	}

	public String getFaultType() {
		return faultType;
	}

	public void setFaultType(String faultType) {
		this.faultType = faultType;
	}

	public String getFaultPhenomenonCode() {
		return faultPhenomenonCode;
	}

	public void setFaultPhenomenonCode(String faultPhenomenonCode) {
		this.faultPhenomenonCode = faultPhenomenonCode;
	}

	public String getFaultDesc() {
		return faultDesc;
	}

	public void setFaultDesc(String faultDesc) {
		this.faultDesc = faultDesc;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFaultTime() {
		return faultTime;
	}

	public void setFaultTime(String faultTime) {
		this.faultTime = faultTime;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public String getAdapterName() {
		return adapterName;
	}

	public void setAdapterName(String adapterName) {
		this.adapterName = adapterName;
	}
	public String toStatusMsgString(){
		StringBuilder msg = new StringBuilder();
		msg.append(this.getMsgType()+","); // 1接口类型 此字段值固定为：“EQUIPSTATUS”
		msg.append(this.getVersion()+",");// 2接口版本 此字段值固定为：“1.0”
		msg.append(this.getMsgId()+",");// 3MSGID 唯一标识，要求采用UUID
		msg.append(this.getProjectId()+",");// 4项目编号
		msg.append(this.getDeviceId()+",");// 5设备编号
		msg.append(this.getEquipServiceFlag()+",");// 6服务形态 1前端设备2中心服务
		msg.append(this.getDeviceType()+",");// 7设备类型
		msg.append(this.getDeviceState()+",");// 8当前状态 1正常2故障
		msg.append(this.getFaultType()+",");// 9故障类型 11手动上报12数据预警（适配器主动检测）13设备主动上报（心跳）14数据降效15离线检查16其他
		msg.append(this.getFaultPhenomenonCode()+",");// 10故障现象编码 参照故障上报协议 多个使用@符号分割。转化为平台使用替换@为,
		msg.append(("1".equals(this.getDeviceState())?"":this.getFaultDesc())+",");//11 故障描述 对故障的描述
		msg.append(this.getFilePath()+",");//12 故障文件地址 多个文件，用”;”分隔
		msg.append(this.getFaultTime()+",");// 13故障发生时间
		msg.append(this.getUploadTime()+",");//14 数据上传时间
		msg.append(this.getCheckTime()+",");//15 设备检测时间
		msg.append(this.getAdapterName()+",");//16 名称
		msg.append(this.getMsglevel()+",");//17 名称
		msg.append(this.getLanenum()+",");//18 车道
		msg.append(this.getReal_value()+",");//19 实际值
		msg.append(this.getRange()+",");//20 规则范围
		msg.append(this.getInstallAddress()+",");//21 安装位置
		msg.append(DateUtil.currentDate());//22 上传mq时间
		log.info("准备发送的报文格式为[{}]",msg);
		return msg.toString();
	}

}
