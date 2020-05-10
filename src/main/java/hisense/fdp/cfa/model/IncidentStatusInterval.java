package hisense.fdp.cfa.model;

import java.util.Date;

public class IncidentStatusInterval {

	private String id;
	private String device_sn;
	private String project_id;
	private String cycle_lengths;
	private String check_time;
	private long wait_lengths;
	private String alltimes_inaday;
	
	@Override
	public String toString() {
		return "IncidentStatusInterval [id=" + id + ", device_sn=" + device_sn
				+ ", project_id=" + project_id + ", cycle_lengths="
				+ cycle_lengths + ", check_time=" + check_time
				+ ", wait_lengths=" + wait_lengths + ", alltimes_inaday="
				+ alltimes_inaday + "]";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDevice_sn() {
		return device_sn;
	}
	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}
	public String getProject_id() {
		return project_id;
	}
	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}
	public String getCycle_lengths() {
		return cycle_lengths;
	}
	public void setCycle_lengths(String cycle_lengths) {
		this.cycle_lengths = cycle_lengths;
	}
	public String getCheck_time() {
		return check_time;
	}
	public void setCheck_time(String check_time) {
		this.check_time = check_time;
	}
	public long getWait_lengths() {
		return wait_lengths;
	}
	public void setWait_lengths(long wait_lengths) {
		this.wait_lengths = wait_lengths;
	}
	public String getAlltimes_inaday() {
		return alltimes_inaday;
	}
	public void setAlltimes_inaday(String alltimes_inaday) {
		this.alltimes_inaday = alltimes_inaday;
	}
	
}
