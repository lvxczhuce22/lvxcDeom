package hisense.fdp.cfa.dao;

import hisense.fdp.cfa.core.AllFaultData;
import hisense.fdp.cfa.core.Context;
import hisense.fdp.cfa.model.EquipStatusMsgVO;
import hisense.fdp.cfa.model.IncidentStatusInterval;
import hisense.fdp.cfa.model.IncidentWarningDelayDetect;
import hisense.fdp.cfa.util.DataBaseUtil;
import hisense.fdp.cfa.util.DateUtil;
import hisense.fdp.cfa.util.UUIDGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EquipNodeDao {
	private static final Logger log = LoggerFactory.getLogger(EquipNodeDao.class);
	// 数据库连接池
	private static final DataBaseUtil connectionPool = DataBaseUtil.getInstance();

	public void getNodeTree() {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;

		try {
//			String sql = "select t.device_id,t.device_parent_id,t.device_ip,t.device_parent_ip,t.device_type from TOPOLOGY_DEVNODE_TREE_2 t";
			String sql = "select t.device_id,t.device_parent_id,t.device_ip,t.device_parent_ip,t.device_type from TOPOLOGY_DEVNODE_TREE t";
			conn = connectionPool.getConnection();
			stmt = conn.createStatement();
			log.info("获取节点sql语句："+sql);
			rs = stmt.executeQuery(sql);

			while(rs.next()) {
				if((rs.getString(1)!=null)&&(!"".equals(rs.getString(1)))){
					Context.getUniqueInstance().updateNodeTreeMap(rs.getString(1), rs.getString(2));
				}
			}
//			log.info("初始化节点结束，内存中的节点树在下面展示↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓",Context.getUniqueInstance().loadNodeTreeMap());
		} catch (Exception e) {
			log.error("EquipNodeDao.EquipNodeDao方法执行异常", e);
		} finally {
			connectionPool.closeResultSet(rs);
			connectionPool.closePreparedStatement(stmt);
			connectionPool.closeConnection(conn);
		}
	}
	
	public void getFilterCode() {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;

		try {
//			String sql = "select t.device_id,t.device_parent_id,t.device_ip,t.device_parent_ip,t.device_type from TOPOLOGY_DEVNODE_TREE_2 t";
			String sql = "select DELAYDETECT_ID, INCIDENT_ID, DELAYDETECTTIME, REMARK, PROTOOL_VERISON, PROJECT_ID, DEVICE_SN, INCIDENT_TYPE, RESPONSE_TIME, CLOSE_TIME_EXPECTED, DESCRIPTION_ID, CLOSE_TIME from INCIDENT_WARNING_DELAYDETECT t where t.close_time > sysdate";
			conn = connectionPool.getConnection();
			stmt = conn.createStatement();
			log.info("获取过滤的故障现象sql语句："+sql);
			rs = stmt.executeQuery(sql);

			Context.getUniqueInstance().setFilterFaultCodeMap(new ConcurrentHashMap<String,IncidentWarningDelayDetect>());
			while(rs.next()) {
				IncidentWarningDelayDetect iwd = new IncidentWarningDelayDetect();
				iwd.setDevice_sn(rs.getString(7));
				iwd.setProject_id(rs.getString(6));
				iwd.setDescription_id(rs.getString(11));
				
				
				if((iwd.getDescription_id()!=null)){
					Context.getUniqueInstance().getFilterFaultCodeMap().put(iwd.getDevice_sn()+"-"+iwd.getDescription_id(), iwd);
				}else{
					Context.getUniqueInstance().getFilterFaultCodeMap().put(iwd.getDevice_sn(), iwd);
				}
			}
			log.info("初始化被过滤的故障编码数[{}]",Context.getUniqueInstance().getFilterFaultCodeMap().size());

		} catch (Exception e) {
			log.error("EquipNodeDao.getFilterCode方法执行异常", e);
		} finally {
			connectionPool.closeResultSet(rs);
			connectionPool.closePreparedStatement(stmt);
			connectionPool.closeConnection(conn);
		}
	}
	public void getIncidentStatusInterval() {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;

		try {
//			String sql = "select t.device_id,t.device_parent_id,t.device_ip,t.device_parent_ip,t.device_type from TOPOLOGY_DEVNODE_TREE_2 t";
			String sql = "select t.id,t.device_sn,t.project_id,t.cycle_lengths,t.check_time,t.wait_lengths,t.alltimes_inaday from INCIDENT_STATUS_INTERVAL t where t.check_time = to_char(sysdate-1,'yyyymmdd')";
			conn = connectionPool.getConnection();
			stmt = conn.createStatement();
			log.info("获取预警周期sql语句："+sql);
			rs = stmt.executeQuery(sql);

			Context.getUniqueInstance().setIncidentStatusIntervalMap(new ConcurrentHashMap<String,IncidentStatusInterval>());
			while(rs.next()) {
				IncidentStatusInterval iti = new IncidentStatusInterval();
				iti.setId(rs.getString(1));
				iti.setDevice_sn(rs.getString(2));
				iti.setProject_id(rs.getString(3));
				iti.setCycle_lengths(rs.getString(4));
				iti.setCheck_time(rs.getString(5));
				iti.setWait_lengths(Long.parseLong(rs.getString(6)));
				iti.setAlltimes_inaday(rs.getString(7));
//				log.info(iti.toString());
				Context.getUniqueInstance().getIncidentStatusIntervalMap().put(iti.getDevice_sn(), iti);
			}
			log.info("初始化预警周期数[{}]",Context.getUniqueInstance().getIncidentStatusIntervalMap().size());

		} catch (Exception e) {
			log.error("EquipNodeDao.getIncidentStatusInterval方法执行异常", e);
		} finally {
			connectionPool.closeResultSet(rs);
			connectionPool.closePreparedStatement(stmt);
			connectionPool.closeConnection(conn);
		}
	}
	public Date loadDBTime() {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		Date dbTime = null;

		try {
			String sql = "select systimestamp from dual";
			conn = connectionPool.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				dbTime = rs.getTimestamp(1);
				System.out.println(dbTime);
			}
		} catch (Exception e) {
			log.error("EquipNodeDao.loadDBTime方法执行异常", e);
		} finally {
			connectionPool.closeResultSet(rs);
			connectionPool.closePreparedStatement(stmt);
			connectionPool.closeConnection(conn);
		}
		return dbTime;
	}
	
	public void msgRecordBaseUpdate(EquipStatusMsgVO vo) throws Exception {
		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer sql3 = new StringBuffer();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = connectionPool.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			
			if("2".equals(vo.getDeviceState())){
				
				sql = new StringBuffer();
				sql.append(" update MSG_RECORD_BASE mr set mr.state = '2',mr.incident_time = to_date('"+(vo.getFaultTime()==null||"".equals(vo.getFaultTime())?vo.getCheckTime():vo.getFaultTime())+"', 'yyyy-mm-dd hh24:mi:ss'),mr.last_mod_time = sysdate,mr.recovery_time = null,mr.ramark = '"+vo.getFaultDesc()+"',mr.abnormal_value = '"+(vo.getReal_value()==null?"":vo.getReal_value().replaceAll("'", "''"))+"' ");
				sql.append(" where mr.device_id = '"+vo.getDeviceId()+"' and mr.fault_detection_mode = '"+vo.getFaultType()+"' and mr.fault_code = '"+vo.getFaultPhenomenonCode()+"' ");
				log.info("执行更新【故障实时数据】的sql[{}]",sql.toString());
				stmt.executeUpdate(sql.toString());
				
				sql2 = new StringBuffer();
				sql2.append(" update MSG_RECORD_BASE mr set mr.state = '1',mr.incident_time = to_date('"+(vo.getFaultTime()==null||"".equals(vo.getFaultTime())?vo.getCheckTime():vo.getFaultTime())+"', 'yyyy-mm-dd hh24:mi:ss'),mr.last_mod_time = sysdate,mr.recovery_time = sysdate ");
				sql2.append(" where mr.device_id = '"+vo.getDeviceId()+"' and mr.fault_detection_mode = '"+vo.getFaultType()+"' and mr.fault_code != '"+vo.getFaultPhenomenonCode()+"' ");
				log.info("执行更新【故障实时数据】的sql2[{}]",sql2.toString());
				stmt.executeUpdate(sql2.toString());
				
				sql3 = new StringBuffer();
				sql3.append(" update MSG_RECORD_BASE mr set mr.state = '2',mr.incident_time = to_date('"+(vo.getFaultTime()==null||"".equals(vo.getFaultTime())?vo.getCheckTime():vo.getFaultTime())+"', 'yyyy-mm-dd hh24:mi:ss'),mr.last_mod_time = sysdate,mr.recovery_time = null ");
				sql3.append(" where mr.device_id = '"+vo.getDeviceId()+"' and mr.fault_detection_mode = '"+vo.getFaultType()+"' and mr.fault_code like '%.-1' ");
				log.info("执行更新【故障实时数据】的sql3[{}]",sql3.toString());
				stmt.executeUpdate(sql3.toString());
			}else{
				sql = new StringBuffer();
				sql.append(" update MSG_RECORD_BASE mr set mr.state = '1',mr.incident_time = to_date('"+(vo.getFaultTime()==null||"".equals(vo.getFaultTime())?vo.getCheckTime():vo.getFaultTime())+"', 'yyyy-mm-dd hh24:mi:ss'),mr.last_mod_time = sysdate,mr.recovery_time = sysdate  ");
				sql.append(" where mr.device_id = '"+vo.getDeviceId()+"' and mr.fault_detection_mode = '"+vo.getFaultType()+"' ");
				log.info("执行更新【正常实时数据】的sql[{}]",sql.toString());
				stmt.executeUpdate(sql.toString());
			}
			
			
			conn.commit();
			conn.setAutoCommit(true);
			log.info("[报文"+vo.getDeviceId()+"数据执行成功！]");
		} catch (Exception e) {
			log.error("msgRecordBaseUpdate 执行失败", e);
			connectionPool.transactionRollbak(conn);
			throw e;
		} finally {
			connectionPool.transactionCommit(conn);
			connectionPool.closePreparedStatement(stmt);
			connectionPool.closeConnection(conn);
		}
	}
	
	public void msgRecordBaseInsert(EquipStatusMsgVO vo) throws Exception {
		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer sql3 = new StringBuffer();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = connectionPool.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			
			if("2".equals(vo.getDeviceState())){
				
				sql = new StringBuffer();
				sql.append(" insert into MSG_RECORD_BASE (DEVICE_ID, RAMARK, INCIDENT_TIME, LAST_MOD_TIME, STATE, FAULT_DETECTION_MODE, FAULT_DETECTOR, FAULT_CODE, ABNORMAL_VALUE, PROTOCOL_VERSION, RECOVERY_TIME, FAULT_LEVEL, PROJECT_ID)   ");
				sql.append(" values ('"+vo.getDeviceId()+"', '"+vo.getFaultDesc()+"', to_date('"+(vo.getFaultTime()==null||"".equals(vo.getFaultTime())?vo.getCheckTime():vo.getFaultTime())+"', 'yyyy-mm-dd hh24:mi:ss'), sysdate, '"+vo.getDeviceState()+"', '"+vo.getFaultType()+"', '"+vo.getAdapterName()+"', '"+vo.getFaultPhenomenonCode()+"', '"+(vo.getReal_value()==null?"":vo.getReal_value().replaceAll("'", "''"))+"','V2.1',null , '"+vo.getMsglevel()+"','"+vo.getProjectId()+"') ");
				log.info("执行插入【故障实时数据】的sql[{}]",sql.toString());
				stmt.executeUpdate(sql.toString());
				
				sql2 = new StringBuffer();
				sql2.append(" update MSG_RECORD_BASE mr set mr.state = '1',mr.incident_time = to_date('"+(vo.getFaultTime()==null||"".equals(vo.getFaultTime())?vo.getCheckTime():vo.getFaultTime())+"', 'yyyy-mm-dd hh24:mi:ss'),mr.last_mod_time = sysdate,mr.recovery_time = sysdate ");
				sql2.append(" where mr.device_id = '"+vo.getDeviceId()+"' and mr.fault_detection_mode = '"+vo.getFaultType()+"' and mr.fault_code != '"+vo.getFaultPhenomenonCode()+"' ");
				log.info("执行更新【故障实时数据】的sql2[{}]",sql2.toString());
				stmt.executeUpdate(sql2.toString());
				
				sql3 = new StringBuffer();
				sql3.append(" update MSG_RECORD_BASE mr set mr.state = '2',mr.incident_time = to_date('"+(vo.getFaultTime()==null||"".equals(vo.getFaultTime())?vo.getCheckTime():vo.getFaultTime())+"', 'yyyy-mm-dd hh24:mi:ss'),mr.last_mod_time = sysdate,mr.recovery_time = null ");
				sql3.append(" where mr.device_id = '"+vo.getDeviceId()+"' and mr.fault_detection_mode = '"+vo.getFaultType()+"' and mr.fault_code like '%.-1' ");
				log.info("执行更新【故障实时数据】的sql3[{}]",sql3.toString());
				stmt.executeUpdate(sql3.toString());
			}else{
				sql = new StringBuffer();
				sql.append(" insert into MSG_RECORD_BASE (DEVICE_ID, RAMARK, INCIDENT_TIME, LAST_MOD_TIME, STATE, FAULT_DETECTION_MODE, FAULT_DETECTOR, FAULT_CODE, ABNORMAL_VALUE, PROTOCOL_VERSION, RECOVERY_TIME, FAULT_LEVEL, PROJECT_ID)   ");
				sql.append(" values ('"+vo.getDeviceId()+"', '"+vo.getFaultDesc()+"', to_date('"+(vo.getFaultTime()==null||"".equals(vo.getFaultTime())?vo.getCheckTime():vo.getFaultTime())+"', 'yyyy-mm-dd hh24:mi:ss'), sysdate, '"+vo.getDeviceState()+"', '"+vo.getFaultType()+"', '"+vo.getAdapterName()+"', '"+vo.getFaultPhenomenonCode()+"', '"+(vo.getReal_value()==null?"":vo.getReal_value().replaceAll("'", "''"))+"','V2.1',null , '"+vo.getMsglevel()+"','"+vo.getProjectId()+"') ");
				log.info("执行插入【正常实时数据】的sql[{}]",sql.toString());
				stmt.executeUpdate(sql.toString());
				
				sql2 = new StringBuffer();
				sql2.append(" update MSG_RECORD_BASE mr set mr.state = '1',mr.incident_time = to_date('"+(vo.getFaultTime()==null||"".equals(vo.getFaultTime())?vo.getCheckTime():vo.getFaultTime())+"', 'yyyy-mm-dd hh24:mi:ss'),mr.last_mod_time = sysdate,mr.recovery_time = sysdate ");
				sql2.append(" where mr.device_id = '"+vo.getDeviceId()+"' and mr.fault_detection_mode = '"+vo.getFaultType()+"' ");
				log.info("执行更新【正常实时数据】的sql2[{}]",sql2.toString());
				stmt.executeUpdate(sql2.toString());
			}
			
			
			conn.commit();
			conn.setAutoCommit(true);
			log.info("[报文"+vo.getDeviceId()+"数据执行成功！]");
		} catch (Exception e) {
			log.error("msgRecordBaseInsert 执行失败", e);
			connectionPool.transactionRollbak(conn);
			throw e;
		} finally {
			connectionPool.transactionCommit(conn);
			connectionPool.closePreparedStatement(stmt);
			connectionPool.closeConnection(conn);
		}
	}
	

	public void msgRecordBaseHistory(EquipStatusMsgVO vo) throws Exception {
		StringBuffer sql = new StringBuffer();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = connectionPool.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			sql = new StringBuffer();
			sql.append(" insert into MSG_RECORD_BASE_HISTORY (DEVICE_ID, RAMARK, INCIDENT_TIME, LAST_MOD_TIME, STATE, FAULT_DETECTION_MODE, FAULT_DETECTOR, FAULT_CODE, ABNORMAL_VALUE, PROTOCOL_VERSION, RECOVERY_TIME, FAULT_LEVEL, PROJECT_ID) ");
			sql.append(" values ('"+vo.getDeviceId()+"', '"+vo.getFaultDesc()+"', to_date('"+(vo.getFaultTime()==null||"".equals(vo.getFaultTime())?vo.getCheckTime():vo.getFaultTime())+"', 'yyyy-mm-dd hh24:mi:ss'), sysdate, '"+vo.getDeviceState()+"', '"+vo.getFaultType()+"', '"+vo.getAdapterName()+"', '"+vo.getFaultPhenomenonCode()+"', '"+(vo.getReal_value()==null?"":vo.getReal_value().replaceAll("'", "''"))+"','V2.1',null , '"+vo.getMsglevel()+"','"+vo.getProjectId()+"') ");

			log.info("插入历史数据的sql[{}]",sql.toString());
			stmt.executeUpdate(sql.toString());
			conn.commit();
			conn.setAutoCommit(true);
			log.info("[报文"+vo.getDeviceId()+"历史数据插入成功！]");
		} catch (Exception e) {
			log.error("msgRecordBaseHistory insert失败", e);
			connectionPool.transactionRollbak(conn);
			throw e;
		} finally {
			connectionPool.transactionCommit(conn);
			connectionPool.closePreparedStatement(stmt);
			connectionPool.closeConnection(conn);
		}
	}
	
	public void msgDetailInsert(EquipStatusMsgVO vo) throws Exception {
		StringBuffer sql = new StringBuffer();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = connectionPool.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			sql = new StringBuffer();
			sql.append(" insert into MSG_RECORD_DETAIL (MSGTYPE, VERSION, MSGID, PROJECTID, DEVICE_SN, DEVICETYPE, DEVICESTATE, FAULTTYPE, FAULTPHENOMENONCODE, FAULTDESC, FILEPATH, FAULTTIME, UPLOADTIME, CHECKTIME, ADAPTERNAME, MSGLEVEL, LANENUM, ABNORMAL_VALUE, RANGE, LASTSENDTIME) ");
			sql.append(" values ('"+vo.getMsgType()+"', '"+vo.getVersion()+"', '"+vo.getMsgId()+"', '"+vo.getProjectId()+"', '"+vo.getDeviceId()+"', '"+vo.getDeviceType()+"', '"+vo.getDeviceState()+"', '"+vo.getFaultType()+"', '"+vo.getFaultPhenomenonCode()+"', '"+vo.getFaultDesc()+"', '"+vo.getFilePath()+"', to_date('"+vo.getFaultTime()+"','yyyy-mm-dd hh24:mi:ss'), to_date('"+vo.getUploadTime()+"','yyyy-mm-dd hh24:mi:ss'), to_date('"+vo.getCheckTime()+"','yyyy-mm-dd hh24:mi:ss'), '"+vo.getAdapterName()+"', '"+vo.getMsglevel()+"', '"+vo.getLanenum()+"', '', '', to_date('"+DateUtil.currentDate()+"','yyyy-mm-dd hh24:mi:ss')) ");

			log.info("插入综合分析待发送报文的sql[{}]",sql.toString());
			stmt.executeUpdate(sql.toString());
			conn.commit();
			conn.setAutoCommit(true);
			log.info("[报文"+vo.getDeviceId()+"数据插入成功！]");
		} catch (Exception e) {
			log.error("msgDetailInsert insert失败", e);
			connectionPool.transactionRollbak(conn);
			throw e;
		} finally {
			connectionPool.transactionCommit(conn);
			connectionPool.closePreparedStatement(stmt);
			connectionPool.closeConnection(conn);
		}
	}
	
	/**
	 * 1:故障  2：预警
	 * @param fault_level
	 */
	public void getIncidentStatusToInit(String fault_level) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;

		try {
//			String sql = "select t.device_id,t.device_parent_id,t.device_ip,t.device_parent_ip,t.device_type from TOPOLOGY_DEVNODE_TREE_2 t";
			String sql = " select 'EQUIPSTATUS', i.protocol_version,i.incident_id,i.project_id,c.device_sn,'',c.devicetype_id,'2' as device_status,i.fault_detection_mode,i.description_id,i.remark,i.img_1_path,i.incident_time,i.check_time,i.check_time,i.fault_detector,"+fault_level+" as msg_level,i.lanenum ";
			if("1".equals(fault_level)){
				sql += " from incident i ";
			}else{
				sql += " from incident_warning i ";
			}
			sql	+= " left join incident_device_relation r on i.incident_id = r.incident_id ";
			sql += " left join config_device c on r.device_sn = c.device_sn ";
			sql += " where i.state = '2'  and c.device_sn is not null and i.fault_detection_mode is not null and i.protocol_version = 'V2.1' ";
			conn = connectionPool.getConnection();
			stmt = conn.createStatement();
			log.info("获取已上报故障预警的sql语句："+sql);
			rs = stmt.executeQuery(sql);

			while(rs.next()) {
				EquipStatusMsgVO esMsgVO = new EquipStatusMsgVO();
				esMsgVO.setMsgType(rs.getString(1)); // 接口类型 此字段值固定为：“EQUIPSTATUS”
				esMsgVO.setVersion(rs.getString(2));// 接口版本 此字段值固定为：“1.0”
				esMsgVO.setMsgId(rs.getString(3));// MSGID 唯一标识，要求采用UUID
				esMsgVO.setProjectId(rs.getString(4));// 项目编号
				esMsgVO.setDeviceId(rs.getString(5));// 设备编号
				esMsgVO.setEquipServiceFlag("");// 服务形态 1前端设备2中心服务
				esMsgVO.setDeviceType(rs.getString(7));// 设备类型
				esMsgVO.setDeviceState(rs.getString(8));// 当前状态 1正常2故障
				esMsgVO.setFaultType(rs.getString(9));// 故障类型 11手动上报12数据预警（适配器主动检测）13设备主动上报（心跳）14数据降效15离线检查16其他
				esMsgVO.setFaultPhenomenonCode(rs.getString(10));// 故障现象编码 参照故障上报协议 多个使用@符号分割。转化为平台使用替换@为,
				esMsgVO.setFaultDesc(rs.getString(11));// 故障描述 对故障的描述
				esMsgVO.setFilePath(rs.getString(12));// 故障文件地址 多个文件，用”;”分隔
				esMsgVO.setFaultTime(rs.getString(13));// 故障发生时间
				esMsgVO.setUploadTime(rs.getString(14));// 数据上传时间
				esMsgVO.setCheckTime(rs.getString(15));// 设备检测时间
				esMsgVO.setAdapterName(rs.getString(16));// 设备检测时间
				esMsgVO.setMsglevel(rs.getString(17));//如果不足17位，默认为故障报文
				esMsgVO.setLanenum(rs.getString(18));//如果不足18位，默认为故障报文
				log.info("初始化的信息："+esMsgVO.toNewString());
				AllFaultData fd = null;
				try{
					fd = Context.getUniqueInstance().getLocalDeviceState(esMsgVO.getDeviceId());
				}catch(Exception e){
					log.error("getLocalDeviceState方法出错！:\n"+esMsgVO.toNewString());
				}
				if(fd == null){//如果没有上传该设备的‘14’故障，那‘14’对应的value为空
					AllFaultData allFD = new AllFaultData(esMsgVO);
					Context.getUniqueInstance().updateLocalDeviceStateMap(esMsgVO.getDeviceId(), allFD);
					fd = allFD;
					
				}else{
					fd.updateMenData(esMsgVO);
				}
				
				
			}
			log.info("初始化获取已上报的故障预警数[{}]",Context.getUniqueInstance().loadNodeTreeMap().size());

		} catch (Exception e) {
			log.error("EquipNodeDao.getIncidentStatusToInit方法执行异常", e);
		} finally {
			connectionPool.closeResultSet(rs);
			connectionPool.closePreparedStatement(stmt);
			connectionPool.closeConnection(conn);
		}
	}
	
	public void getMsgRecordBaseToInit() {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;

		try {
//			String sql = "select t.device_id,t.device_parent_id,t.device_ip,t.device_parent_ip,t.device_type from TOPOLOGY_DEVNODE_TREE_2 t";
			String sql = " select t.device_id,t.ramark,to_char(t.incident_time,'yyyy-mm-dd hh24:mi:ss'), to_char(t.last_mod_time,'yyyy-mm-dd hh24:mi:ss'), t.state,t.fault_detection_mode,t.fault_detector,t.fault_code,t.abnormal_value,t.protocol_version,to_char(t.recovery_time,'yyyy-mm-dd hh24:mi:ss'),t.fault_level from MSG_RECORD_BASE t ";
			conn = connectionPool.getConnection();
			stmt = conn.createStatement();
			log.info("获取初始化实时数据检测结果的sql语句："+sql);
			rs = stmt.executeQuery(sql);

			while(rs.next()) {
				EquipStatusMsgVO esMsgVO = new EquipStatusMsgVO();
				esMsgVO.setMsgType("EQUIPSTATUS"); // 接口类型 此字段值固定为：“EQUIPSTATUS”
				esMsgVO.setVersion("V2.1");// 接口版本 此字段值固定为：“1.0”
				esMsgVO.setMsgId(UUIDGenerator.generate32BitUUID());// MSGID 唯一标识，要求采用UUID
				esMsgVO.setDeviceId(rs.getString(1));// 设备编号
				esMsgVO.setFaultDesc(rs.getString(2));// 故障描述 对故障的描述
				esMsgVO.setFaultTime(rs.getString(3));// 故障发生时间
				esMsgVO.setUploadTime(rs.getString(3));// 数据上传时间
				esMsgVO.setCheckTime(rs.getString(3));// 设备检测时间
				esMsgVO.setProjectId("");// 项目编号---------------------------
				esMsgVO.setEquipServiceFlag("");// 服务形态 1前端设备2中心服务
				esMsgVO.setDeviceState(rs.getString(5));// 当前状态 1正常2故障
				esMsgVO.setFaultType(rs.getString(6));// 故障类型 11手动上报12数据预警（适配器主动检测）13设备主动上报（心跳）14数据降效15离线检查16其他
				esMsgVO.setDeviceType("");// 设备类型--------------------------------------------
				esMsgVO.setAdapterName(rs.getString(7));// 设备检测时间
				esMsgVO.setFaultPhenomenonCode(rs.getString(8));// 故障现象编码 参照故障上报协议 多个使用@符号分割。转化为平台使用替换@为,
				esMsgVO.setReal_value(rs.getString(9));
				esMsgVO.setFilePath("");// 故障文件地址 多个文件，用”;”分隔-------------------------------
				esMsgVO.setMsglevel(rs.getString(12));//如果不足17位，默认为故障报文
				esMsgVO.setLastWarningSendTime(DateUtil.str2Date(rs.getString(4)));
			//	esMsgVO.setLanenum(rs.getString(18));//如果不足18位，默认为故障报文
				log.debug("初始化的信息："+esMsgVO.toNewString());
				AllFaultData fd = null;
				try{
//					fd = Context.getUniqueInstance().getLocalDeviceState(esMsgVO.getDeviceId());
					fd = Context.getUniqueInstance().getLocal_XianXiangDeviceState(esMsgVO.getDeviceId()+"-"+esMsgVO.getFaultType());
				}catch(Exception e){
					log.error("getLocal_XianXiangDeviceState方法出错！:\n"+esMsgVO.toNewString());
				}
				if(fd == null){
//					AllFaultData allFD = new AllFaultData(esMsgVO);
//					Context.getUniqueInstance().updateLocalDeviceStateMap(esMsgVO.getDeviceId(), allFD);
//					fd = allFD;
					AllFaultData allFD = new AllFaultData(esMsgVO,3);
					Context.getUniqueInstance().updateLocal_XianXiangDeviceStateMap(esMsgVO.getDeviceId()+"-"+esMsgVO.getFaultType(), allFD);
					fd = allFD;
					
				}else{
//					fd.updateMenData(esMsgVO);
					fd.updateMenData_XianXiang(esMsgVO);
				}
				
				
			}
//			log.info("初始化获取已上报的故障预警数[{}]",Context.getUniqueInstance().loadNodeTreeMap().size());
			log.info("获取初始化实时数据检测结果-完成");

		} catch (Exception e) {
			log.error("EquipNodeDao.getMsgRecordBaseToInit方法执行异常", e);
		} finally {
			connectionPool.closeResultSet(rs);
			connectionPool.closePreparedStatement(stmt);
			connectionPool.closeConnection(conn);
		}
	}
}
