package hisense.fdp.cfa.model;

import hisense.fdp.cfa.util.Constant.EVENT_TYPE;

/**
 * 连接器事件
 * 
 * 
 */
public class ConnectorEvent {

	// EventID,唯一标识一个消息。
	private String eventId;
	// 信息主体
	private String connectorMsg;
	// MQ链接源
	private ConnectorTask connectorTask;
	// 类型
	private EVENT_TYPE eventType;
	// 描述
	private String desc;
	// 事件优先级
	private Integer priority;

	public String getConnectorMsg() {
		return connectorMsg;
	}

	public void setConnectorMsg(String connectorMsg) {
		this.connectorMsg = connectorMsg;
	}

	public ConnectorTask getConnectorTask() {
		return connectorTask;
	}

	public void setConnectorTask(ConnectorTask connectorTask) {
		this.connectorTask = connectorTask;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setEventType(EVENT_TYPE eventType) {
		this.eventType = eventType;
	}

	public EVENT_TYPE getEventType() {
		return eventType;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
}
