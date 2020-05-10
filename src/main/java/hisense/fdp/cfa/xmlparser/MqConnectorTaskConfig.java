package hisense.fdp.cfa.xmlparser;

import hisense.fdp.cfa.model.ConnectorTask;
import hisense.fdp.cfa.service.msghandler.IMsgHandlerService;
import hisense.fdp.cfa.util.VariableInit;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 获取MQ连接任务列表操作类
 * 
 * 
 */
public class MqConnectorTaskConfig {
	private static final Logger log = LoggerFactory.getLogger(MqConnectorTaskConfig.class);
	/** MQ连接配置文件 */
	public static final String MQ_CONFIG_XML = "config/ConnectorTask.xml";
	/** mq连接列表 */
	public static List<ConnectorTask> CONNECTOR_CONFIG_LIST = new ArrayList<ConnectorTask>();
	/** mq消息处理接口集合 */
	public static Map<String, IMsgHandlerService> MSG_HANDLER_SERVICE_MAP = new HashMap<String, IMsgHandlerService>();

	/**
	 * 获取mq连接任务列表和mq消息处理类集合
	 */
	public static void getConnectConfigTaskList() {
		try {
			// 得到DOM解析器的工厂实例
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			// 从DOM工厂中获得DOM解析器
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			InputStream inputStream = MqConnectorTaskConfig.class.getResourceAsStream("/" + MQ_CONFIG_XML);
			Document doc = dbBuilder.parse(inputStream);
			NodeList nList = doc.getElementsByTagName("config");

			for (int i = 0; i < nList.getLength(); i++) {
				ConnectorTask config = new ConnectorTask();
				Element node = (Element) nList.item(i);
				// juxingliang edit begin 2018-07-09
				//config.setServerStr(node.getAttribute("serverStr"));
				config.setServerStr(VariableInit.MQ_SEND_URL);
				// juxingliang edit end 2018-07-09
				config.setIdentify(node.getAttribute("identity"));
				config.setFactoryName(node.getAttribute("factoryName"));
				config.setFromEncode(node.getAttribute("fromEncode"));
				config.setToEncode(node.getAttribute("toEncode"));
				// juxingliang edit begin 2018-07-09
				//config.setUser(node.getAttribute("user"));
				//config.setPassword(node.getAttribute("password"));
				config.setUser(VariableInit.MQ_SEND_USER);
				config.setPassword(VariableInit.MQ_SEND_PWD);
				// juxingliang edit end 2018-07-09
				config.setPriority(Integer.parseInt(node.getAttribute("priority")));
				CONNECTOR_CONFIG_LIST.add(config);
				MSG_HANDLER_SERVICE_MAP.put(node.getAttribute("factoryName"), (IMsgHandlerService) Class.forName(node.getAttribute("factoryName"))
						.newInstance());
			}
		} catch (Exception e) {
			log.error("初始化MQ连接配置失败" + e);
		}

		log.debug("getConnectConfigTaskList end...");
	}
}
