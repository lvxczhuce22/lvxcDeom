package hisense.fdp.cfa.model;

import java.util.Date;

public class EquipServerInfoVO {

	private String equipServiceFlag;// 设备形态
	private String deviceId;// 设备ID
	private String deviceName;// 设备名称
	private String deviceType;// 设备类型
	private String deviceState;// 设备状态
	private String checkType;// 检测类型
	private String serviceLevel;// 服务级别
	private String pointId;// 安装点
	private String belongtoProj;// 所属项目编码
	private String belongtoProjName;// 所属项目名称
	private String description;// 描述
	private String updateOperator;// 更新操作者
	private Date updateTime;// 更新时间

	// /** 维修处理相关参数 */
	// private String parentMaintainId;// 父维修单ID【注意】如果填写则新生成的维修单会挂载在此ID下

	public String getEquipServiceFlag() {
		return equipServiceFlag;
	}

	public void setEquipServiceFlag(String equipServiceFlag) {
		this.equipServiceFlag = equipServiceFlag;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
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

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

}
