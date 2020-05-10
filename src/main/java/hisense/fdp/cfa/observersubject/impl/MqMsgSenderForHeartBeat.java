package hisense.fdp.cfa.observersubject.impl;

import hisense.fdp.cfa.util.VariableInit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqMsgSenderForHeartBeat
{
	private static final Logger log = LoggerFactory.getLogger(MqMsgSenderForHeartBeat.class);
	/** 应用程序只占用一个MQ连接，不管有多少个主题 */
	private static Connection connection = null;
	private static ConnectionFactory connectionFactory = null;
	// 
	static
	{
		init();
	}

	/**
	 * 初始化MQ连接工厂及连接
	 */
	private static final void init()
	{
		try
		{
			log.info("初始化mq");
//			connectionFactory = new ActiveMQConnectionFactory("admin", "admin",
//					"failover:(nio://127.0.0.1:61616,nio://127.0.0.1:61616)?randomize=false");
			connectionFactory = new ActiveMQConnectionFactory(VariableInit.MQ_HEART_USER, VariableInit.MQ_HEART_PWD,
					VariableInit.MQ_HEART_URL);
			connection = connectionFactory.createConnection();
		} catch (JMSException e)
		{
			closeConnection();
		}
	}

	private static void closeConnection()
	{
		try
		{
			connection.close();
		} catch (JMSException e)
		{
		}
		connection = null;
	}

	/**
	 * 向MQ指定主题发送消息
	 * 
	 * @param destinationTopic
	 *            主题字符串
	 * @param message
	 *            消息内容
	 * @return true表示发送成功，失败、异常等：fasle
	 */
	public static boolean sendMessage(String destinationTopic, String message)
	{
		boolean success = false;
		if (connection == null)
		{
			init();
		}
		//
		Session session = null;
		MessageProducer producer = null;
		try
		{
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(destinationTopic);
			producer = session.createProducer(topic);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			TextMessage textMessage = session.createTextMessage(message);
			producer.send(textMessage);
			success = true;
		} catch (Exception e)
		{
			log.error("消息发送失败[destinationTopic:{}, message:{}]", new String[] { destinationTopic, message });
		} finally
		{
			closeIgnoreException(producer);
			closeIgnoreException(session);
		}
		return success;
	}

	public static void closeIgnoreException(MessageProducer producer)
	{
		try
		{
		    if(producer != null)
		    {
		        producer.close();
		    }
		} catch (JMSException e)
		{
		}
	}

	public static void closeIgnoreException(Session session)
	{
		try
		{
		    if(session != null)
		    {
		        session.close();
		    }
		} catch (JMSException e)
		{
		}
	}
}
