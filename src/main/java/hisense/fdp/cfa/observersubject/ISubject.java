package hisense.fdp.cfa.observersubject;

import hisense.fdp.cfa.observer.IObserver;

/**
 * 被观察者接口,当有多个需要被观察对象，实现此接口
 * 
 * 
 */
public interface ISubject {
	/**
	 * 注册观察者
	 * 
	 * @param observer
	 *            观察者
	 */
	void registerObserver(IObserver observer);

	/**
	 * 注销观察者
	 * 
	 * @param observer
	 *            观察者
	 */
	void unregisterObserver(IObserver observer);

	/**
	 * 通知所有观察者处理消息
	 * 
	 * @param obj
	 *            通知发送消息或者被观察者本身
	 */
	void nofifyAllObservers(Object obj);
}
