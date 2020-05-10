package hisense.fdp.cfa.observersubject.impl;

import hisense.fdp.cfa.model.ConnectorTask;
import hisense.fdp.cfa.observer.IObserver;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorMqManager extends Thread {
	private static final Logger log = LoggerFactory.getLogger(ConnectorMqManager.class);
	// MQ连接检测时间间隔，单位毫秒，默认1分钟 TODO 回头优化成可配置的，最好优化到ConnectorTask配置中
	private static final long TIME_WAIT_HEATBEAT = 60000;
	// mq连接任务列表
	private final List<ConnectorTask> connectorTaskList;
	// mq列表
	private final List<ConnectorMQ> connectorMqList = new ArrayList<ConnectorMQ>();
	// 观察者列表
	private final List<IObserver> mQMsgReceiveObserverList = new ArrayList<IObserver>();

	/**
	 * 构造函数
	 * 
	 * @param connectorTaskList
	 *            mq连接任务列表
	 */
	public ConnectorMqManager(List<ConnectorTask> connectorTaskList) {
		this.connectorTaskList = connectorTaskList;

	}

	/**
	 * 注册观察者列表，目前只有一个，写死在Main中，connectorMqManager.registerObserver(mQMsgReceiveObserver);
	 */
	public void registerObserver(IObserver observer) {
		mQMsgReceiveObserverList.add(observer);
	}

	@Override
	public void run() {
		initAllConnectorMQ();

		while (true) {
			try {
				// 每隔一个小时检查mq是否连接成功，否则重新连接
				for (ConnectorMQ connector : connectorMqList) {
					if (!connector.checkAvailable()) {
						connector.startConnector();
					}
				}
				//间隔TIME_WAIT_HEATBEAT跑一次,60000
				sleep(TIME_WAIT_HEATBEAT);
			} catch (Exception e) {
				log.error("启动连接器出现错误。", e.fillInStackTrace());
			}
		}
	}

	/**
	 * 初始化所有mq连接信息
	 * 
	 * @info 为每个配置文件中的MQ连接信息创建实际的连接MQ的ConnectorMQ实例，
	 * @info 存储每个配置信息对应建立的MQ连接connection，session，消费者，发送者等信息。
	 * @info 并将观察者列表写入实例中。
	 */
	public void initAllConnectorMQ() {
		for (ConnectorTask connectorTask : connectorTaskList) {// 为每个配置信息
			ConnectorMQ connectorMQ = new ConnectorMQ(connectorTask.getIdentify());// 创建实例
			connectorMQ.setConnectorTask(connectorTask);// 存储配置信息
			for (IObserver observer : mQMsgReceiveObserverList) {// 将观察者列表存储进实例
				connectorMQ.registerObserver(observer);//
			}
			connectorMqList.add(connectorMQ);// 连接实例 列表
		}
	}
}
