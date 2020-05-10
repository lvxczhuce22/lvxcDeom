package hisense.fdp.cfa.observersubject.impl;

import hisense.fdp.cfa.model.ConnectorTask;
import hisense.fdp.cfa.util.VariableInit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 接收设备状态变化协议mq处理类
 */
public class ReceiveMsgMQ {
	// 打印日志
	public static Logger log = LoggerFactory.getLogger(ConnectorMQ.class);
	// mq实例
	private ConnectorMQ connectorMQ = null;
	// 接收设备状态变化协议mq唯一实例
	private static ReceiveMsgMQ recevieMsgInstance = new ReceiveMsgMQ(VariableInit.MQ_MAINTAININF_TOPIC);

	/**
	 * 默认构造函数
	 */
	private ReceiveMsgMQ(String mqName) {
		connectorMQ = new ConnectorMQ(mqName);
		connectorMQ.setConnectorTask(initConnectorTask());
	}

	/**
	 * 获取实例
	 * 
	 * @return 实例
	 */
	public static ReceiveMsgMQ getInstance() {
		return recevieMsgInstance;
	}

	/**
	 * 初始化mq连接任务
	 * 
	 * @return mq连接任务
	 */
	private ConnectorTask initConnectorTask() {
		ConnectorTask connectorTask = new ConnectorTask();
		connectorTask.setServerStr(VariableInit.MQ_SEND_URL);
		connectorTask.setUser(VariableInit.MQ_SEND_USER);
		connectorTask.setPassword(VariableInit.MQ_SEND_PWD);
		connectorTask.setIdentify("");
		return connectorTask;
	}

	/**
	 * 向mq发送消息协议
	 * 
	 * @param message
	 *            消息协议
	 */
	public void sendMsgToMQ(String message) {
		//TODO 与每分钟短线重现检测重复！！！
//		if (!connectorMQ.checkAvailable()) {
//			connectorMQ.startConnector();
//		}

		connectorMQ.sendMsgToMQ(message);
	}
}
