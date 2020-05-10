package hisense.fdp.cfa.mqmsghandler;

import hisense.fdp.cfa.core.AllFaultData;
import hisense.fdp.cfa.core.Context;
import hisense.fdp.cfa.model.EquipStatusMsgVO;
import hisense.fdp.cfa.observer.IObserver;
import hisense.fdp.cfa.observer.impl.MQMsgReceiveObserver;
import hisense.fdp.cfa.observer.impl.MQMsgToStorageObserver;
import hisense.fdp.cfa.service.msghandler.IMsgHandlerService;
import hisense.fdp.cfa.util.VariableInit;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 批量处理mq消息类
 * 
 * 
 */
public class MsgHandlePoolManager {
	public static Logger log = LoggerFactory.getLogger(MsgHandlePoolManager.class);
	// 接收mq消息的观察者
	private final IObserver iObserver;
	// 处理mq消息接口集合
	private final Map<String, IMsgHandlerService> msgHandlerServiceMap;

	public MsgHandlePoolManager(IObserver iObserver, Map<String, IMsgHandlerService> msgHandlerServiceMap) {
		this.iObserver = iObserver;
		this.msgHandlerServiceMap = msgHandlerServiceMap;
	}

	/**
	 * 利用线性池批量处理mq消息
	 */
	public void msgLotHandle() {
		try {
			// 获取配置文件中配置的线程池大小
			String poolSizeStr = VariableInit.MSG_HANDLER_POOL_SIZE;
			int poolSize = Integer.parseInt(poolSizeStr);
			ExecutorService cachedThreadPool = Executors.newFixedThreadPool(poolSize+1);
			
			if(iObserver instanceof MQMsgReceiveObserver){
				log.info("启动发送mq的线程池");
				//初始化内存中的已上报的故障、预警
				cachedThreadPool.execute(new InitMsgHandler());
				for (int i = 0; i < poolSize; i++) {
					cachedThreadPool.execute(new MsgHandler(iObserver, msgHandlerServiceMap));
				}
			
			}else if(iObserver instanceof MQMsgToStorageObserver){
				log.info("启动入DB的线程池");
				//初始化内存中的已上报的故障、预警
				cachedThreadPool.execute(new InitMsgToDbHandler());
				for (int i = 0; i < poolSize; i++) {
					cachedThreadPool.execute(new MsgToDbHandler(iObserver, msgHandlerServiceMap));
				}
				
			}
		} catch (Exception e) {
			log.error("MsgHandlePoolManager.msgLotHandle failed!", e);
		}
	}
	
	/**
	 * 在王丽萍代码中实现 TODO
	 */
	public void repeatCheck(String deviceid, AllFaultData data) {
		try {
			Map<String,EquipStatusMsgVO> faultMap = data.getFaultMap();
			
			//TODO 同一设备，不同故障来源，叠不叠加故障次数！！！
			Integer ind = Context.getUniqueInstance().getRepeatFault(deviceid);
			if(data.isFault()){
				if(ind == null ){
					Context.getUniqueInstance().updateRepeatFaultMap(deviceid, 1);
				}else{//“认定”故障了，才统计重复次数
					ind++;
				}
			}else{
				Context.getUniqueInstance().updateRepeatFaultMap(deviceid, 0);
			}
			
			if(VariableInit.REPEAT_COUNT_MAX != 0&&ind > VariableInit.REPEAT_COUNT_MAX){
				//置为重复误报故障，故障类型为17(新定义的)
			}
			
			
		} catch (Exception e) {
			log.error("MsgHandlePoolManager.repeatCheck failed!", e);
		}
	}
}
