package hisense.fdp.cfa.mqmsghandler;

import hisense.fdp.cfa.HiFdpCfa;
import hisense.fdp.cfa.core.Context;
import hisense.fdp.cfa.model.ConnectorEvent;
import hisense.fdp.cfa.observer.IObserver;
import hisense.fdp.cfa.observer.impl.MQMsgReceiveObserver;
import hisense.fdp.cfa.service.msghandler.IMsgHandlerService;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * mq消息处理类
 * 
 * 
 * 
 */
public class MsgHandler implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(MsgHandler.class);
	// 任务处理线程无待处理任务时线程等待时间，单位毫秒
	public static final long TIME_WAIT_WORKING = 1000;
	// 接收mq消息的观察者
	private final MQMsgReceiveObserver mqMsgReceiveObserver;
	// 处理消息接口集合
	private final Map<String, IMsgHandlerService> msgHandlerServiceMap;

	public MsgHandler(IObserver mqMsgReceiveObserver, Map<String, IMsgHandlerService> msgHandlerServiceMap) {
		this.mqMsgReceiveObserver = (MQMsgReceiveObserver)mqMsgReceiveObserver;
		this.msgHandlerServiceMap = msgHandlerServiceMap;
	}

	@Override
	public void run() {
		while (true) {
			try {
				while(!Context.reviceFlag){
					log.info("正在结算分析，暂停读取消息队列!!!");
					Thread.sleep(5*TIME_WAIT_WORKING);
				}
				if (mqMsgReceiveObserver.getEventQueueLength() == 0) {
					Thread.sleep(TIME_WAIT_WORKING);
				} else {
					// 多线程同时取可能存在判断队列长度时不为0，但取消息时已被其他线程取走，需要再次判空
					ConnectorEvent event = mqMsgReceiveObserver.getConnectorEventByPriority();
					if (null == event) {
						Thread.sleep(TIME_WAIT_WORKING);
					} else {
						// 根据 ConnectorEvent 内封装的对应解析类的接口取出解析类，调用解析方法进行解析。
						// TODO 新增2.0协议时方便接入,可类似违法过车分开处理。能够根据FactoryName不同进行区分。）
						IMsgHandlerService msgHandlerImpl = msgHandlerServiceMap.get(event.getConnectorTask().getFactoryName());
						msgHandlerImpl.msgParser(event);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
