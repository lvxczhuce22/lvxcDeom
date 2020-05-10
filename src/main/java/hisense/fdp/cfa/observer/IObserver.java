package hisense.fdp.cfa.observer;

/**
 * 观察者接口，用于处理观察者（subject）发布消息，需要有多个观察者时实现此接口
 * 
 * 
 */
public interface IObserver {
	/**
	 * 处理被观察者发送的消息
	 * 
	 * @param obj
	 *            事件消息
	 */
	void handleAction(Object obj);
}
