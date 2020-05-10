package hisense.fdp.cfa.model;

public class ConnectorTask {
	private String serverStr;
	// 标识
	private String identify;
	// 反射工厂名称
	private String factoryName;
	// 来源编码
	private String fromEncode;
	// 转换编码
	private String toEncode;
	// 用户名
	private String user;
	// 密码
	private String password;
	// 优先级
	private Integer priority;

	public String getServerStr() {
		return serverStr;
	}

	public void setServerStr(String serverStr) {
		this.serverStr = serverStr;
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify(String identify) {
		this.identify = identify;
	}

	public String getFactoryName() {
		return factoryName;
	}

	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}

	public String getFromEncode() {
		return fromEncode;
	}

	public void setFromEncode(String fromEncode) {
		this.fromEncode = fromEncode;
	}

	public String getToEncode() {
		return toEncode;
	}

	public void setToEncode(String toEncode) {
		this.toEncode = toEncode;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
}
