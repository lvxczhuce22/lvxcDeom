package hisense.fdp.cfa;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import hisense.fdp.cfa.core.AllFaultData;
import hisense.fdp.cfa.core.Context;
import hisense.fdp.cfa.model.EquipStatusMsgVO;
import hisense.fdp.cfa.util.DateUtil;
import hisense.fdp.cfa.util.VariableInit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test_Demo {
	private static final Logger log = LoggerFactory.getLogger(Test_Demo.class);
	
//	public static void main(String[] args) {
//		log.info("启动开始");
//		
//		Test_Demo d = new Test_Demo();
////		String cur_levels = "005,008,007,009,011,012";
////		String max_level = d.getMaxLevel(cur_levels, "");
////		System.out.println("max_level: "+max_level);
//		
////		EquipStatusMsgVO svo = new EquipStatusMsgVO();
////		svo.setAdapterName("name");
////		svo.setFaultType("12");
////		svo.setDeviceState("2");
////		
////		String ft = svo.getFaultType();
////		System.out.println("kaishi:"+ft);
////		
////		svo.setFaultType("100");
////		System.out.println("jieshu:"+svo.getFaultType());
////		System.out.println("ft:"+ft);
////		log.info("启动结束");
//		
////		String levels_order = "10,15,12,20,18,19,18,23,16,17,14";
////		String[] orders = levels_order.split(",");
////		for(String order:orders){
////			System.out.println(""+order);
////		}
//		
////		AllFaultData fd = Context.getUniqueInstance().getLocalDeviceState("151515151515");
////		String old_result = "";
////		if(fd == null){
//		
//		/*VariableInit.levels_order = "10,15,12,20,18,19,13,23,16,17,14";
//		EquipStatusMsgVO esMsgVo = new EquipStatusMsgVO();
//		esMsgVo.setFaultType("13");
//		esMsgVo.setDeviceState("2");
//		esMsgVo.setDeviceId("151515151515");
//		AllFaultData fd = new AllFaultData(esMsgVo);
//		
//		EquipStatusMsgVO esMsgVo2 = new EquipStatusMsgVO();
//		esMsgVo2.setFaultType("15");
//		esMsgVo2.setDeviceState("2");
//		esMsgVo2.setDeviceId("151515151515");
//		fd.updateMenData(esMsgVo2);
//		Context.getUniqueInstance().updateLocalDeviceStateMap("151515151515", fd);
//		
//		AllFaultData fd_1 = Context.getUniqueInstance().getLocalDeviceState("151515151515");
//		EquipStatusMsgVO svo = fd_1.getFault();
//		svo.setLastSendTime(new Date());
//		
//		AllFaultData fd_2 = Context.getUniqueInstance().getLocalDeviceState("151515151515");
//		EquipStatusMsgVO svo2 = fd_2.getFault();
//		System.out.println("time: "+DateUtil.format2Str(svo2.getLastSendTime()));*/
//	}
	
	/*public static void main(String[] args) {   
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 200, TimeUnit.MILLISECONDS,
//                new ArrayBlockingQueue<Runnable>(50));
         
        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 200, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>());
        
        for(int i=0;i<100;i++){
            MyTask myTask = new MyTask(i);
            executor.execute(myTask);
            log.info("next:"+DateUtil.currentDate());
//            System.out.println("线程池中线程数目："+executor.getPoolSize()+"，队列中等待执行的任务数目："+
//            executor.getQueue().size()+"，已执行玩别的任务数目："+executor.getCompletedTaskCount());
        }
        
        while(true){
        	System.out.println("线程池中线程数目："+executor.getPoolSize()+"，队列中等待执行的任务数目："+
                    executor.getQueue().size()+"，已执行玩别的任务数目："+executor.getCompletedTaskCount());
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
//        executor.shutdown();
    }*/

	
	public static void main(String[] args){
		/*String line = "java CLASS_PATH=\".:hifdpcfa_10035_003.jar:plugin/slf4j-api-1.7.21.jar:plugin/logback-classic-1.1.7.jar:plugin/logback-core-1.1.7.jar:plugin/HiFdpCommon.jar:plugin/HiFdpAlmpCore.jar\"";
		String result = "";
		if(line!=null&&(line.indexOf("java")>-1)){
			String s = line.substring(line.indexOf("java"));
			args = s.split(":");
			for(String adapter_name:args){
				if(adapter_name.toLowerCase().indexOf("hifdp")>-1&&adapter_name.toLowerCase().indexOf("lib")<0){
					result += "'"+adapter_name+"',";
				}
			}
//			log.info("--"+line);
		}else{
			log.info("null");
		}
		result = result.replaceAll(".jar", "");
		result += "'adapter_null_000_000'";
		System.out.println("---"+result);*/
		
		testMap();
	}
	
	public static void testMap(){
		Map<String,String> map = new HashMap<String,String>();
		for(int i=0; i<10; i++){
			map.put("0"+i, i+"0");
		}
		System.out.println("a："+map.size());
		
		map.remove("05");
		
		System.out.println("b："+map.size());
		
		
	}
	
	/**
	 * 筛选优先级最高的故障现象编号
	 * @param cur_levels 当前存在的、待筛选的所有‘故障现象编号’,多故障预警时用英文逗号隔开
	 * @param levels_order 已配置好的，有序的‘故障现象编号’，越靠左优先级越高
	 * @return 优先级最高的‘故障现象编号’
	 */
	public String getMaxLevel(String cur_levels, String levels_order){
		
		//TODO 优先级顺序暂时写死，以后从配置文件中读取 
		levels_order = "003,001,002,012,013,009,004,005,006,007,008,010,011";
		
		if(cur_levels==null||"".equals(cur_levels)){
			return "";
		}
		
		String[] orders = levels_order.split(",");
		for(int i=0; i<orders.length; i++){
			if(cur_levels.indexOf(orders[i])!=-1){
				//如果当前优先级在cur_levels中存在，则结束，找到最高优先级的编号
				return orders[i];
			}
		}
		return "";
	}
	
}
