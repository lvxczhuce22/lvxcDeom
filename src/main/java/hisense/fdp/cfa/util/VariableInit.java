package hisense.fdp.cfa.util;

import hisense.WS.FdpParams.constants.ComParam;
import hisense.fdp.cfa.job.ISendMsg;
import hisense.fdp.cfa.job.sendMsg.SendMsgToMQ;
import hisense.fdp.constants.MyPropertiesConstants;
import hisense.fdp.utils.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Map;

import javax.rmi.CORBA.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VariableInit {
	public static final Logger log = LoggerFactory.getLogger(Util.class);

	/** 初始化[配置文件config.properties]数据 */
	public static String ADAPTER_NAME = "EMA";//
	public static String MQ_SEND_URL = "";// ** MQ发送 地址，用户名，密码 */
	public static String MQ_SEND_USER = "";
	public static String MQ_SEND_PWD = "";
	public static String MQ_HEART_URL = "";// ** MQ发送 地址，用户名，密码 */
	public static String MQ_HEART_USER = "";
	public static String MQ_HEART_PWD = "";
	public static String MQ_MAINTAININF_TOPIC = "";// ** 向mq发送 新维修单 提醒的TOPIC */
	public static String MQ_HEART_BEAT_TOPIC = "";// ** MQ连接器心跳topic，用于连接是否出现问题。 */
	public static String MSG_HANDLER_POOL_SIZE = "";//
	public static String NODE_TREE_INTERVAL = "0 0 0 * * ?";
	public static String CHECK_NODE_FAULT_INTERVAL = "0/1 * * * * ?";
	public static String HEARTBEAT_TRIGGER_CORN = "0 0/1 * * * ?";
	public static int SLEEP_TIMES = 30;//秀芬时间  默认30秒
	public static String ADAPTER_ID="";
	/** 初始化[数据库]数据 */
	public static String HIATMP_VERSION = null;// ** 数据版本1贵阳版本2青岛版本 */
	public static Map<String, String> LAN_MAP = null;// ** 车道信息 */

	/** 初始化[方法] */
	public static ISendMsg SEND_MSG_CLASS = null;// ** MQ发送消息接口方法 */
	public static int REPEAT_COUNT_MAX = 0;//
	public static String PROJECT_ID = "-1";//
	public static String SEND_MQ_TYPE = "TOPIC";
	public static String levels_order = "";
	public static String UPDATE_FILTERCODE_INTERVAL = "0 0/1 * * * ?";
	public static String UPDATE_INCIDENT_STATUS_INTERVAL = "0 0/1 * * * ?";
	public static String SEND_TYPE = "MQ";
	public static int OUT_HOURS = 0;
	public static int NO_CHANGE_MINS = 0;
	public static String HANDLE_TYPE = "";
	/**
	 * 变量初始化,实例变量查询Dao，查询枚举和系统参数放到全局Map，创建发MQ或写文件的接口实例。
	 */
	public VariableInit() {
		log.debug("VariableInit begin...");

		/**
		 * 初始化[配置文件config.properties]数据
		 */
		if (this.getPropertyValue("ADAPTER_NAME") != null && !this.getPropertyValue("ADAPTER_NAME").equals("")) {
			ADAPTER_NAME = this.getPropertyValue("ADAPTER_NAME");
			if (ADAPTER_NAME.length() > 15) {
				ADAPTER_NAME = ADAPTER_NAME.substring(0, 15);
			}
		}
		ADAPTER_NAME = ADAPTER_NAME + "@" + getLocalIP();// 配置文件ADAPTER_NAME@适配器所在服务器IP
		log.info("[适配器名称]" + ADAPTER_NAME + " (配置文件ADAPTER_NAME@适配器所在服务器IP)");
		
		MQ_MAINTAININF_TOPIC = this.getPropertyValue("MQ_MAINTAININF_TOPIC");
		MQ_HEART_BEAT_TOPIC = this.getPropertyValue("MQ_HEART_BEAT_TOPIC");
		MSG_HANDLER_POOL_SIZE = this.getPropertyValue("MSG_HANDLER_POOL_SIZE");
		
		REPEAT_COUNT_MAX = Integer.valueOf(this.getPropertyValue("REPEAT_COUNT_MAX"));
		NODE_TREE_INTERVAL = this.getPropertyValue("NODE_TREE_INTERVAL");
		CHECK_NODE_FAULT_INTERVAL = this.getPropertyValue("CHECK_NODE_FAULT_INTERVAL");
		SLEEP_TIMES = Integer.valueOf(this.getPropertyValue("SLEEP_TIMES"));
		ADAPTER_ID = this.getPropertyValue("ADAPTER_ID");
		PROJECT_ID = this.getPropertyValue("PROJECT_ID");
		SEND_MQ_TYPE = this.getPropertyValue("SEND_MQ_TYPE");
		levels_order = this.getPropertyValue("levels_order");
		UPDATE_FILTERCODE_INTERVAL = this.getPropertyValue("UPDATE_FILTERCODE_INTERVAL");
		UPDATE_INCIDENT_STATUS_INTERVAL = this.getPropertyValue("UPDATE_INCIDENT_STATUS_INTERVAL");
		SEND_TYPE = this.getPropertyValue("SEND_TYPE");
		OUT_HOURS = Integer.valueOf(this.getPropertyValue("OUT_HOURS"));
		NO_CHANGE_MINS = Integer.valueOf(this.getPropertyValue("NO_CHANGE_MINS"));
		HANDLE_TYPE = this.getPropertyValue("HANDLE_TYPE");
		// juxingliang edit begin 2018-07-09
		String isRuntimeParamsGetFromDb = MyPropertiesConstants.IS_RUNTIME_PARAMS_GET_FROM_DB;
		if (StringUtils.isNotEmpty(isRuntimeParamsGetFromDb)
				&& isRuntimeParamsGetFromDb.equals("1")){
			log.info("MyPropertiesConstants.IS_RUNTIME_PARAMS_GET_FROM_DB=1");
			MQ_SEND_URL = ComParam.getItsomMqUrl();
			MQ_SEND_USER = ComParam.getItsomMqUsername();
			MQ_SEND_PWD = ComParam.getItsomMqPassword();
			MQ_HEART_URL = ComParam.getNetworkMqUrl();
			MQ_HEART_USER = ComParam.getNetworkMqUsername();
			MQ_HEART_PWD = ComParam.getNetworkMqPassword();
		} else {
			log.info("MyPropertiesConstants.IS_RUNTIME_PARAMS_GET_FROM_DB<>1");
			MQ_SEND_URL = this.getPropertyValue("MQ_SEND_URL");
			MQ_SEND_USER = this.getPropertyValue("MQ_SEND_USER");
			MQ_SEND_PWD = this.getPropertyValue("MQ_SEND_PWD");
			MQ_HEART_URL = this.getPropertyValue("MQ_HEART_URL");
			MQ_HEART_USER = this.getPropertyValue("MQ_HEART_USER");
			MQ_HEART_PWD = this.getPropertyValue("MQ_HEART_PWD");
		}
		log.info("MQ_SEND_URL="+MQ_SEND_URL);
		// juxingliang edit end 2018-07-09
		
		/**
		 * 初始化[方法]
		 */
		SEND_MSG_CLASS = SendMsgToMQ.getInstance();// 发送MQ消息接口

		log.debug("VariableInit end...");
	}

	/**
	 * 读取配置文件中Key对应的Value值，并做统一的简单校验
	 */
	public String getPropertyValue(String propKey) {
		String propValue = ConfigPropertiesUtil.getInstance().getPropertyValue(propKey);
		if (propValue == null || propValue.equals("")) {
			log.error("[ERROR]配置文件" + ConfigPropertiesUtil.getInstance().propertiesUrl + "中[" + propKey + "]配置出错!");
		}
		return propValue;
	}

	/**
	 * 获取本机ip地址，自动区分Windows还是linux操作系统
	 */
	public static String getLocalIP() {
		String sIP = "";
		InetAddress ip = null;
		try {
			if (isWindowsOS()) {// 如果是Windows操作系统
				ip = InetAddress.getLocalHost();
			} else {// 如果是Linux操作系统
				boolean findTrueIP = false;// 是否已经找到正确IP
				Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
				while (netInterfaces.hasMoreElements()) {
					if (findTrueIP) {
						break;
					}
					NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
					// ----------特定情况，可以考虑用ni.getName判断
					// 遍历所有ip
					Enumeration<InetAddress> ips = ni.getInetAddresses();
					while (ips.hasMoreElements()) {
						ip = (InetAddress) ips.nextElement();
						if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() // 127.开头的都是lookback地址
								&& ip.getHostAddress().indexOf(":") == -1) {
							findTrueIP = true;
							break;
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null != ip) {
			sIP = ip.getHostAddress();
		}
		return sIP;
	}

	/**
	 * 判断当前是否为Windows系统
	 */
	public static boolean isWindowsOS() {
		boolean isWindowsOS = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") > -1) {
			isWindowsOS = true;
		}
		return isWindowsOS;
	}

}
