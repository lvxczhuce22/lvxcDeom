package hisense.fdp.cfa.observersubject.impl;

import hisense.fdp.cfa.model.ConnectorEvent;
import hisense.fdp.cfa.model.ConnectorTask;
import hisense.fdp.cfa.observer.IObserver;
import hisense.fdp.cfa.observersubject.ISubject;
import hisense.fdp.cfa.util.Constant;
import hisense.fdp.cfa.util.DateUtil;
import hisense.fdp.cfa.util.UUIDGenerator;
import hisense.fdp.cfa.util.VariableInit;
import hisense.fdp.cfa.util.Constant.EVENT_TYPE;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MQ被观察者实现类
 */
public class ConnectorMQ implements ISubject, MessageListener {
	// 打印日志
	public static Logger log = LoggerFactory.getLogger(ConnectorMQ.class);
	// mq连接信息，为ConnectorTask.xml配置信息
	private ConnectorTask connectorTask;
	// 处理mq消息的观察者
	private final List<IObserver> observerList = new ArrayList<IObserver>();
	// Connection ：JMS 客户端到JMS Provider的连接
	private Connection connection = null;
	// Session： 一个发送或接收消息的线程
	private Session session = null;
	// Destination ：消息的目的地;消息发送给谁.
	private Destination destination = null;
	// 消费者，消息接收者
	private MessageConsumer consumer = null;
	// MessageProducer：消息发送者
	private MessageProducer producer = null;
	// MessageProducer：心跳发送者
	private MessageProducer heartbeatProducer = null;
	private final String mqName;

	public ConnectorTask getConnectorTask() {
		return connectorTask;
	}

	public void setConnectorTask(ConnectorTask connectorTask) {
		this.connectorTask = connectorTask;
	}

	public ConnectorMQ(String mqName) {
		this.mqName = mqName;
	}

	/**
	 * 启动mq连接
	 */
	public void startConnector() {
		log.debug("[创建MQ连接] 启动连接(" + this.mqName + ")...");
		try {
			ActiveMQConnectionFactory connectionFactory;
			String user = connectorTask.getUser();
			String password = connectorTask.getPassword();
			log.debug("[MQ登录信息] MQuser:" + user + " MQpw:" + password + " MQurl:" + connectorTask.getServerStr());
			// 如果activemq设定了用户名密码，就使用设定在用户名密码，否则就使用默认的。
			if (user != "" && user != null && password != "" && password != null) {
				connectionFactory = new ActiveMQConnectionFactory(user, password, connectorTask.getServerStr());
			} else {
				connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD,
						connectorTask.getServerStr());
			}
			// 创建连接
			connection = connectionFactory.createConnection();
			// 创建Session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			if (connectorTask.getIdentify() != null && !connectorTask.getIdentify().equals("")) {
				// 创建目标，就创建主题也可以创建队列
				destination = session.createTopic(connectorTask.getIdentify());
				// 创建消息消费者
				consumer = session.createConsumer(destination);
				consumer.setMessageListener(this);
			}
			// 得到消息生成者【发送者】
			producer = session.createProducer(session.createTopic(VariableInit.MQ_MAINTAININF_TOPIC));
			heartbeatProducer = session.createProducer(session.createTopic(VariableInit.MQ_HEART_BEAT_TOPIC));
			// 设置不持久化
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			connection.start();
		} catch (Exception e) {
			log.error(connectorTask.getIdentify() + "启动连接异常:", e);
		}
	}

	/**
	 * 消息处理函数,每当mq有消息发送过来自动执行该方法
	 */
	@Override
	public void onMessage(Message msg) {
		if (!(msg instanceof TextMessage)) {
			return;
		}

		try {
			String messageBef = ((TextMessage) msg).getText();
			String messageAft = messageBef;
			if (messageAft == null) {
				log.error("[error]MQ接收到的数据为Null");
				return;
			}

			// 对协议进行转码
			if (connectorTask.getFromEncode() != null && connectorTask.getToEncode() != null
					&& !connectorTask.getFromEncode().trim().equals(connectorTask.getToEncode().trim())) {
				messageAft = new String(messageAft.getBytes(connectorTask.getFromEncode()), connectorTask.getToEncode());
			}

			// 判断报文是否有乱码
			if (messageAft.indexOf("??") > -1 || messageAft.indexOf("？？") > -1) {
				log.error(connectorTask.getIdentify() + "的数据异常，异常消息转码前为：" + messageBef + "== 转码后：" + messageAft + "=== MQ服务器为：" + connectorTask.getServerStr());
				return;
			}

			String eventId = getEventId(msg);

			if (eventId == null) {
				log.error("获取eventId失败,消息为：" + messageAft);
				return;
			}

			log.info("[接收MQ消息] 主题[" + this.mqName + "] 消息内容:" + messageAft);
			// 生成消息事件
			ConnectorEvent event = new ConnectorEvent();
			event.setConnectorMsg(messageAft);
			event.setEventId(eventId);
			event.setConnectorTask(connectorTask);
			event.setPriority(connectorTask.getPriority());
			event.setEventType(EVENT_TYPE.RECEIVE_DATA);

			// 通知mq观察者处理消息事件
			nofifyAllObservers(event);
		} catch (Exception e) {
			log.error("消息处理异常:", e);
		}
	}

	// 通知mq观察者处理消息事件
	@Override
	public void nofifyAllObservers(Object event) {
		for (IObserver observer : observerList) {
			observer.handleAction(event);
		}
	}

	/**
	 * 判断mq是否连接正常
	 * 
	 * @return 是否正常标识
	 */
	public boolean checkAvailable() {
		boolean result = true;

		try {
//			if (session == null || heartbeatProducer == null || connection == null || producer == null) {
			if (session == null || connection == null || producer == null) {
				return false;
			}

			if (connectorTask.getIdentify() != null && !connectorTask.getIdentify().equals("") && consumer == null) {
				return false;
			}

			String heartBeatStr = generateHeartBeatMsg();
//			log.info("[检测MQ连接,发送心跳] " + heartBeatStr);
			heartbeatProducer.send(session.createTextMessage(heartBeatStr));
		} catch (Exception e) {
			log.error(e.toString());
			this.closeConnector();
			result = false;
		}

		return result;
	}
	
	private static String generateHeartBeatMsg(){
//        //
//        StringBuilder msg = new StringBuilder();
//        // 1 接口类型 此字段值固定为：“EQUIPSTATUS”
//        msg.append("FAULT,");
//        // 2 接口版本 此字段值固定为：“1.0”
//        msg.append("2.1,");
//        // 3 厂商id
//        msg.append(",");
//        // 4 MSGID 唯一标识，要求采用UUID
//        msg.append(UUIDGenerator.generate32BitUUID() + ",");
//        // 5 设备编号
//        msg.append("042,");
//        // 6 
//        msg.append(",");
//        // 7 
//        msg.append(",");
//        // 8 
//        msg.append(",");
//        // 9 监控点名称
//        msg.append("综合分析,");
//        // 10 设备编号
//        msg.append( VariableInit.ADAPTER_ID+",");
//        // 11 故障时间
//        msg.append(",");
//        // 12 上传时间
//        msg.append(DateUtil.currentDate());
//        log.info("发送心跳："+msg.toString());
//        return msg.toString();
        StringBuilder msg = new StringBuilder();
        msg.append("FAULT,");
        msg.append("1.0,");
        msg.append("0001,");
        msg.append(UUIDGenerator.generate32BitUUID() + ",");
        msg.append("04,");
        msg.append(VariableInit.ADAPTER_ID + ",");
        msg.append("综合分析适配器,");
        msg.append(VariableInit.getLocalIP() + ",");
        msg.append(",");
        msg.append("-1,");
        msg.append("适配器正常运行,");
        msg.append(DateUtil.currentDate() + ",");
        msg.append(DateUtil.currentDate());
        log.info("发送心跳："+msg.toString());
        return msg.toString();
    }

	/**
	 * 生成事件id
	 * 
	 * @param msg
	 *            mq发送的消息
	 * @return 事件id
	 */
	public String getEventId(Message msg) {
		String eventId = Constant.ADAPTER_NAME;
		try {
			Timestamp ts = new Timestamp(msg.getJMSTimestamp());
			DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			eventId += sdf.format(ts);
			eventId += String.valueOf(msg.getJMSMessageID().hashCode()).trim();
		} catch (Exception e) {
			log.error("创建eventId异常！", e);
		}

		return eventId;
	}

	@Override
	public void registerObserver(IObserver observer) {
		observerList.add(observer);
	}

	@Override
	public void unregisterObserver(IObserver observer) {
		observerList.remove(observer);
	}

	/**
	 * 向mq发送消息
	 * 
	 * @param message
	 *            消息
	 */
	public void sendMsgToMQ(String message) {
		try {
			producer.send(session.createTextMessage(message));
			log.debug("[发送MQ消息] 向MQ[" + this.mqName + "]发送消息成功！message:" + message);
		} catch (Exception e) {
			log.error("sendMsgToMQ failed!mq:" + connectorTask.getServerStr() + ",the message:" + message + "", e);
		}
	}

	/**
	 * 关闭mq连接
	 */
	public void closeConnector() {
		log.info("关闭连接结束...");
		try {
			if (consumer != null) {
				consumer.close();
				consumer = null;
			}
			if (producer != null) {
				producer.close();
				producer = null;
			}
			if (heartbeatProducer != null) {
				heartbeatProducer.close();
				heartbeatProducer = null;
			}
			if (session != null) {
				session.close();
				session = null;
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (Exception e) {
			log.error("关闭连接异常:", e);
			consumer = null;
			producer = null;
			heartbeatProducer = null;
			session = null;
			connection = null;
		}
	}

}
