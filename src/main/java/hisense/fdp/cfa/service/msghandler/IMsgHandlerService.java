package hisense.fdp.cfa.service.msghandler;

import hisense.fdp.cfa.model.ConnectorEvent;

public interface IMsgHandlerService {
	/**
	 * 消息解析方法
	 * 
	 * @param connectorEvent消息事件的封装
	 */
	public void msgDetailParser(ConnectorEvent connectorEvent);
	public void msgParser(ConnectorEvent connectorEvent);

}
