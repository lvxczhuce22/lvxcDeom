package hisense.fdp.cfa.model;

import java.util.Date;

public class IncidentWarningDelayDetect {

	private String delaydetect_id;
	private String incident_id;
	private String delaydetecttime;
	private String remark;
	private String protool_verison;
	private String project_id;
	private String device_sn;
	private String incident_type;
	private String response_time;
	private String close_time_expected;
	private String description_id;
	private Date close_time;
	
	
	public String getDelaydetect_id() {
		return delaydetect_id;
	}
	public void setDelaydetect_id(String delaydetect_id) {
		this.delaydetect_id = delaydetect_id;
	}
	public String getIncident_id() {
		return incident_id;
	}
	public void setIncident_id(String incident_id) {
		this.incident_id = incident_id;
	}
	public String getDelaydetecttime() {
		return delaydetecttime;
	}
	public void setDelaydetecttime(String delaydetecttime) {
		this.delaydetecttime = delaydetecttime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getProtool_verison() {
		return protool_verison;
	}
	public void setProtool_verison(String protool_verison) {
		this.protool_verison = protool_verison;
	}
	public String getProject_id() {
		return project_id;
	}
	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}
	public String getDevice_sn() {
		return device_sn;
	}
	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}
	public String getIncident_type() {
		return incident_type;
	}
	public void setIncident_type(String incident_type) {
		this.incident_type = incident_type;
	}
	public String getResponse_time() {
		return response_time;
	}
	public void setResponse_time(String response_time) {
		this.response_time = response_time;
	}
	public String getClose_time_expected() {
		return close_time_expected;
	}
	public void setClose_time_expected(String close_time_expected) {
		this.close_time_expected = close_time_expected;
	}
	public String getDescription_id() {
		return description_id;
	}
	public void setDescription_id(String description_id) {
		this.description_id = description_id;
	}
	public Date getClose_time() {
		return close_time;
	}
	public void setClose_time(Date close_time) {
		this.close_time = close_time;
	}
	@Override
	public String toString() {
		return "IncidentWarningDelayDetect [delaydetect_id=" + delaydetect_id
				+ ", incident_id=" + incident_id + ", delaydetecttime="
				+ delaydetecttime + ", remark=" + remark + ", protool_verison="
				+ protool_verison + ", project_id=" + project_id
				+ ", device_sn=" + device_sn + ", incident_type="
				+ incident_type + ", response_time=" + response_time
				+ ", close_time_expected=" + close_time_expected
				+ ", description_id=" + description_id + ", close_time="
				+ close_time + "]";
	}
	
	

}
