package hisense.fdp.cfa.observer.impl;

import hisense.fdp.cfa.model.ConnectorEvent;
import hisense.fdp.cfa.observer.IObserver;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mq观察者，直接把mq消息存入队列,用于入库详细分类数据
 */
public class MQMsgToStorageObserver implements IObserver {
	public static final Logger log = LoggerFactory.getLogger(MQMsgToStorageObserver.class);
	// 按照高峰期，3小时900万流量计算，600万的缓存可确保2小时的数据不会出现阻塞。 不需要数据持久化，实时性不强，为保证在数据库性能低时服务的稳定，将容量降为50万
	public static final int TASK_CACHE_CAPACITY = 500000;
	// 存储mq事件的队列
	private final BlockingQueue<ConnectorEvent> eventQueue = new LinkedBlockingQueue<ConnectorEvent>(TASK_CACHE_CAPACITY);

	/**
	 * 收到mq消息，直接入队列
	 */
	@Override
	public void handleAction(Object obj) {
		eventQueue.offer((ConnectorEvent) obj);
		log.info("[消息已入队列] 队列当前长度（DB）：" + eventQueue.size());
	}

	/**
	 * 根据优先级获取mq发送的消息
	 * 
	 * @return mq事件
	 */
	public synchronized ConnectorEvent getConnectorEventByPriority() {
		if (getEventQueueLength() == 0) {
			return null;
		}

		return eventQueue.poll();
	}

	/**
	 * 获取事件队列长度
	 * 
	 * @return 队列长度
	 */
	public int getEventQueueLength() {
		return eventQueue.size();
	}
}
