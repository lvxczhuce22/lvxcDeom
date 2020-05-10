package hisense.fdp.cfa.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接池定义(对DPCP连接池进行包装以便对其进行单例控制)，该类是单例模式(在应用程序中只能有一个实例)。 配置文件为classpath:dbcp-config.properties
 * 
 * @author FXG
 * 
 */
public class DataBaseUtil {
	private static final Logger log = LoggerFactory.getLogger(DataBaseUtil.class);
	// 用类变量来保存唯一实例
	private volatile static DataBaseUtil uniqueInstance;
	// DBCP连接池配置文件定义
	private final String configration = "config/dbcp-config.properties";
	// 连接数据库数据源
	private DataSource dataSource = null;

	/* 构造器私有化，以确保不能随便创建该类的实例 */
	private DataBaseUtil() {
		try {
			log.debug("初始化DBCP连接池...");
			// 获取DBCP连接池属相值
			Properties properties = createConfigrationProperties();
			// 数据源赋值
			dataSource = BasicDataSourceFactory.createDataSource(properties);
			log.debug("初始化DBCP连接池完毕.");
		} catch (Exception e) {
			log.error("初始化DBCP数据库连接池时出现异常[{}],程序退出。", configration, e);
//			System.exit(-1);
		}
	}

	/**
	 * 使用者只能通过此方法获取 DBCPConnectionPool 的唯一实例。注意：当无法初始化数据库连接池时程序会退出.
	 * 
	 * @return DBCPConnectionPool 的唯一实例
	 */
	public static DataBaseUtil getInstance() {
		if (uniqueInstance == null) {
			synchronized (DataBaseUtil.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new DataBaseUtil();
				}
			}
		}

		return uniqueInstance;
	}

	/**
	 * 从连接池中获取一个数据库连接(Connection)，此Connection是DBCP框架对原生连接进行封装过的。 获取的Connection使用完毕后要放回到连接池(通过调用Connection的close方法)
	 * 
	 * @return Connection对象，使用完毕后要调用close方法以便将其放回到连接池
	 * @throws Exception
	 *             连接不能保证总能正确获取
	 */
	public Connection getConnection() throws Exception {
		Connection conn = dataSource.getConnection();
		printDataSource();
		return conn;
	}

	/**
	 * 加载DBCP配置文件为 Properties 对象
	 * 
	 * @return DBCP配置文件加载以后的 Properties 对象
	 * @throws Exception
	 *             文件不存在或读取错误时抛出异常
	 */
	private Properties createConfigrationProperties() throws Exception {
		Properties properties = new Properties();
		InputStream in = null;
		try {
			in = DataBaseUtil.class.getResourceAsStream("/" + configration);
			properties.load(in);
		} catch (Exception e) {
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return properties;
	}

	/**
	 * 以debug等级打印当前连接池信息[当前活动连接、当前Idle连接]
	 */
	private void printDataSource() {
		StringBuffer poolInfo = new StringBuffer("");
		BasicDataSource basicDataSource = (BasicDataSource) getInstance().dataSource;
		poolInfo.append("DBCP连接池信息[");
		poolInfo.append("numActive:");
		poolInfo.append(basicDataSource.getNumActive());
		poolInfo.append(", numIdle:");
		poolInfo.append(basicDataSource.getNumIdle());
		poolInfo.append("]");
		log.debug(poolInfo.toString());
	}

	/**
	 * 关闭ResultSet对象，忽略任何异常
	 * 
	 * @param rs
	 *            ResultSet对象
	 */
	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				log.error("closeResultSet failed!", e);
			}
		}
	}

	/**
	 * 关闭ResultSet对象，忽略任何异常
	 * 
	 * @param rs
	 *            ResultSet对象
	 */
	public void closePreparedStatement(PreparedStatement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				log.error("closePreparedStatement failed!", e);
			}
		}
	}

	/**
	 * 关闭ResultSet对象，忽略任何异常
	 * 
	 * @param rs
	 *            ResultSet对象
	 */
	public void closePreparedStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				log.error("closePreparedStatement failed!", e);
			}
		}
	}

	/**
	 * 关闭Connection对象，忽略任何异常
	 * 
	 * @param conn
	 *            Connection对象
	 */
	public void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				log.error("closeConnection failed!", e);
			}
		}
	}

	/**
	 * 回滚Connection上的事务，忽略任何异常
	 * 
	 * @param conn
	 *            Connection对象
	 */
	public void transactionRollbak(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				log.error("transactionRollbak failed!", e);
			}
		}
	}

	/**
	 * 提交Connection上的事务，忽略任何异常
	 * 
	 * @param conn
	 *            Connection对象
	 */
	public void transactionCommit(Connection conn) {
		if (conn != null) {
			try {
				conn.commit();
			} catch (SQLException e) {
				log.error("transactionCommit failed!", e);
			}
		}
	}

	public static void main(String[] args) {
		try {
			// Connection con = DataBaseUtil.getInstance().getConnection();
			System.out.println("aaa");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
