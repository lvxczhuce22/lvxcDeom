package hisense.fdp.cfa.job.sendMsg;

import hisense.fdp.cfa.job.ISendMsg;
import hisense.fdp.cfa.observersubject.impl.ReceiveMsgMQ;

/**
 * 协议发送到mq处理类
 * 
 * 
 */
public class SendMsgToMQ implements ISendMsg {
	private static SendMsgToMQ sengMsgToMQInstance = new SendMsgToMQ();

	/**
	 * 私有化构造函数，其他类不能初始化
	 */
	private SendMsgToMQ() {
	}

	/**
	 * 获取类唯一实例
	 * 
	 * @return 类实例
	 */
	public static SendMsgToMQ getInstance() {
		if (sengMsgToMQInstance == null) {
			synchronized (SendMsgToMQ.class) {
				if (sengMsgToMQInstance == null) {
					sengMsgToMQInstance = new SendMsgToMQ();
				}
			}
		}

		return sengMsgToMQInstance;
	}

	@Override
	public void sendMsg(String message) {
		ReceiveMsgMQ.getInstance().sendMsgToMQ(message);
	}
}
