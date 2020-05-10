package hisense.fdp.cfa.model;

import java.util.Date;

public class EquipmentInfoVO {

	private String deviceId;

	private String deviceType;

	private String deviceState;

	private String updateOperator;

	private Date updateTime;

	private int passThreshold;

	private int illegalThreshold;

	private String deviceCode;

	private String pointId;

	private String belongtoProj;

	private String belongtoProjName;

	private String deviceName;

	private String checkType;

	private String description;

	private String startLan;

	private String serviceLevel;

	private int lanNum;

	private String thirdsyscode;

	private String direction;

	private String serviceProviderId;

	private String volumeType; // 流量子类型

	private int excepThreshold; // 异常数据阈值

	private String departmentId;

	// 设备表中无此字段
	private Date faultTime;
	/*
	 * 故障现象编码 表equip_fault_Phenomenon;多个用逗号拼接
	 */
	private String faultPhenomenonCode;

	private String equipServiceFlag;

	// private List<EquipmentStateVO> statesList = new ArrayList<EquipmentStateVO>();

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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

	public String getUpdateOperator() {
		return updateOperator;
	}

	public void setUpdateOperator(String updateOperator) {
		this.updateOperator = updateOperator;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public int getPassThreshold() {
		return passThreshold;
	}

	public void setPassThreshold(int passThreshold) {
		this.passThreshold = passThreshold;
	}

	public int getIllegalThreshold() {
		return illegalThreshold;
	}

	public void setIllegalThreshold(int illegalThreshold) {
		this.illegalThreshold = illegalThreshold;
	}

	public String getStartLan() {
		return startLan;
	}

	public void setStartLan(String startLan) {
		this.startLan = startLan;
	}

	public int getLanNum() {
		return lanNum;
	}

	public void setLanNum(int lanNum) {
		this.lanNum = lanNum;
	}

	// public List<EquipmentStateVO> getStatesList() {
	// return statesList;
	// }
	//
	// public void setStatesList(List<EquipmentStateVO> statesList) {
	// this.statesList = statesList;
	// }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getPointId() {
		return pointId;
	}

	public void setPointId(String pointId) {
		this.pointId = pointId;
	}

	public String getBelongtoProj() {
		return belongtoProj;
	}

	public void setBelongtoProj(String belongtoProj) {
		this.belongtoProj = belongtoProj;
	}

	public String getBelongtoProjName() {
		return belongtoProjName;
	}

	public void setBelongtoProjName(String belongtoProjName) {
		this.belongtoProjName = belongtoProjName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getThirdsyscode() {
		return thirdsyscode;
	}

	public void setThirdsyscode(String thirdsyscode) {
		this.thirdsyscode = thirdsyscode;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	public String getServiceProviderId() {
		return serviceProviderId;
	}

	public void setServiceProviderId(String serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}

	public Date getFaultTime() {
		return faultTime;
	}

	public void setFaultTime(Date faultTime) {
		this.faultTime = faultTime;
	}

	public String getVolumeType() {
		return volumeType;
	}

	public void setVolumeType(String volumeType) {
		this.volumeType = volumeType;
	}

	public int getExcepThreshold() {
		return excepThreshold;
	}

	public void setExcepThreshold(int excepThreshold) {
		this.excepThreshold = excepThreshold;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getEquipServiceFlag() {
		return equipServiceFlag;
	}

	public void setEquipServiceFlag(String equipServiceFlag) {
		this.equipServiceFlag = equipServiceFlag;
	}

	public String getFaultPhenomenonCode() {
		return faultPhenomenonCode;
	}

	public void setFaultPhenomenonCode(String faultPhenomenonCode) {
		this.faultPhenomenonCode = faultPhenomenonCode;
	}

}
