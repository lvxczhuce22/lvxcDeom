package hisense.fdp.cfa;

import java.util.Date;
import java.util.Properties;

import hisense.fdp.cfa.job.impl.CheckParentNodeFaultJob;
import hisense.fdp.cfa.job.impl.CheckParentNodeFaultTO_QUEUEJob;
import hisense.fdp.cfa.job.impl.HeartBeatJob;
import hisense.fdp.cfa.job.impl.UpdateFilterCodeJob;
import hisense.fdp.cfa.job.impl.UpdateIncidentStatusIntervalJob;
import hisense.fdp.cfa.job.impl.UpdateNodeTreeJob;
import hisense.fdp.cfa.job.manager.QuartzSchedulerManager;
import hisense.fdp.cfa.mqmsghandler.MsgHandlePoolManager;
import hisense.fdp.cfa.observer.impl.MQMsgReceiveObserver;
import hisense.fdp.cfa.observer.impl.MQMsgToStorageObserver;
import hisense.fdp.cfa.observersubject.impl.ConnectorMqManager;
import hisense.fdp.cfa.util.VariableInit;
import hisense.fdp.cfa.xmlparser.MqConnectorTaskConfig;
//import hisense.fdp.constants.RcmoesConstants;
//import hisense.fdp.utils.HeartbeatUtil;
//import hisense.fdp.utils.PropertyUtil;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by juxingliang on 2017-5-12.
 */
public class HiFdpCfa {
    private static final Logger log = LoggerFactory.getLogger(HiFdpCfa.class);

    /**
     * 故障综合分析  的入口函数
     *
     * Comprehensive fault analysis
     *
     * @param args
     */
    public static void main(String[] args) {
		
        HiFdpCfa main = new HiFdpCfa();
		main.adapterStart();
    }
    
    private void adapterStart() {
		log.info("**********适配器启动开始！**********");
		
		//加载适配器属性文件
//		Properties obj = new PropertyUtil().loadProperties4my(RcmoesConstants.MY_PROPERTY);
//		if(obj==null){
//			log.error("加载适配器属性文件异常，程序退出！");
//			System.exit(-1);
//		}
		
		try{
			
			// 变量初始化,实例变量查询Dao，查询枚举和系统参数放到全局Map，创建发MQ或写文件的接口实例。
			new VariableInit();
	
			// 解析ConnectorTask.xml文件，获取mq连接信息列表 List<ConnectorTask> CONNECTOR_CONFIG_LIST。
			// 获取MQ消息解析接口集合 Map<String,IMsgHandlerService> MSG_HANDLER_SERVICE_MAP
			MqConnectorTaskConfig.getConnectConfigTaskList();
	
			// 初始化MQ消息处理(mq观察者)。实现接口IObserver，新建BlockingQueue<ConnectorEvent> eventQueue 长度50W。
			// 提供添加队列方法handleAction(Object obj),取出数据方法ConnectorEvent getConnectorEventByPriority()
			MQMsgReceiveObserver mQMsgReceiveObserver = new MQMsgReceiveObserver();
			MQMsgToStorageObserver mQMsgToStorageObserver = new MQMsgToStorageObserver();
			// 初始化mq连接管理类extends Thread继承线程，sleep一小时跑一次，1.通过构造函数接收MQ连接信息列表。
			ConnectorMqManager connectorMqManager = new ConnectorMqManager(MqConnectorTaskConfig.CONNECTOR_CONFIG_LIST);
			// 2.通过add方法接收消息处理类。（注册mq观察者，用于处理从mq接收的消息）  TODO 注释该功能
			if("DB".equals(VariableInit.HANDLE_TYPE)){
				connectorMqManager.registerObserver(mQMsgToStorageObserver);
			}else{
				connectorMqManager.registerObserver(mQMsgReceiveObserver);
			}
			// 启动MQ连接管理类：对每个MQ配置信息(ConnectorTask)均新建MQ连接类new ConnectorMQ，并将MQ消息处理类(观察者)添加进MQ连接类。
			// 启动所有MQ连接类 ConnectorMQ,在消息处理onMessage()方法中封装所有MQ消息，并调用MQ消息处理类(通知观察者)将MQ消息封装体加入队列
			connectorMqManager.start();
	
			if("DB".equals(VariableInit.HANDLE_TYPE)){
				// 初始化批量处理MQ消息解析类，通过构造函数传递MQ消息处理类和MQ解析类列表（用线性池启动多个线程同时处理多个MQ消息）
				MsgHandlePoolManager msgStoragePoolManager = new MsgHandlePoolManager(mQMsgToStorageObserver, MqConnectorTaskConfig.MSG_HANDLER_SERVICE_MAP);
				// 根据线程池大小，批量启动MQ消息解析方法 MsgHandler
				msgStoragePoolManager.msgLotHandle();
			}else{
				// 初始化批量处理MQ消息解析类，通过构造函数传递MQ消息处理类和MQ解析类列表（用线性池启动多个线程同时处理多个MQ消息）
				MsgHandlePoolManager msgHandlePoolManager = new MsgHandlePoolManager(mQMsgReceiveObserver, MqConnectorTaskConfig.MSG_HANDLER_SERVICE_MAP);
				// 根据线程池大小，批量启动MQ消息解析方法 MsgHandler
				msgHandlePoolManager.msgLotHandle();
			}
			
			//更新链路结构
			QuartzSchedulerManager.scheduleJob("UpdateNodeTreeJob", "UpdateTrigger", "UpdateGroup", UpdateNodeTreeJob.class, VariableInit.NODE_TREE_INTERVAL);
			
			QuartzSchedulerManager.scheduleJob("UpdateFilterJob", "UpdateFilterTrigger", "UpdateFilterGroup", UpdateFilterCodeJob.class, VariableInit.UPDATE_FILTERCODE_INTERVAL);
			
			QuartzSchedulerManager.scheduleJob("UpdateIncidentStatusIntervalJob", "UpdateIncidentStatusIntervalTrigger", "UpdateIncidentStatusIntervalGroup", UpdateIncidentStatusIntervalJob.class, VariableInit.UPDATE_INCIDENT_STATUS_INTERVAL);
			
			//heartbeat
			QuartzSchedulerManager.scheduleJob("HeartBeatJob", "HeartBeatTrigger", "HeartBeatGroup",
					HeartBeatJob.class, VariableInit.HEARTBEAT_TRIGGER_CORN);
			log.info("启动成功");
		}catch(Exception e){
//			HeartbeatUtil.sendHeartbeatStatus(obj.getProperty(RcmoesConstants.MY_DEVICE_SN),RcmoesConstants.DATATYPE_RUNNING_ERROR,obj.getProperty(RcmoesConstants.MY_DEVICE_RECORDID)+RcmoesConstants.Start_fault);
			log.error("启动失败："+e.toString());
		}

	}
}
