package hisense.fdp.cfa.service.msgparser;

import hisense.fdp.cfa.model.ConnectorEvent;

public interface IMsgParserService {
	Object msgParser(ConnectorEvent connectorEvent);
}
