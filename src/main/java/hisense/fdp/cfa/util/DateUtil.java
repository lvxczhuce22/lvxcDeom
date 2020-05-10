package hisense.fdp.cfa.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {

	private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

	/**
	 * 判断日期是否合法，YYYY-MM-DD HH:MI:SS
	 * 
	 * @param date
	 *            给定的日期
	 * @return 合法：true，反之亦然
	 */
	public static boolean isValidDate(String date) {
		return true;
	}

	/**
	 * 
	 * @param str
	 *            yyyy-MM-dd HH:ss:mm
	 */
	public static Date str2Date(String str) {
		Date date = null;
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		try {
			date = sdf.parse(str);
		} catch (Exception e) {
			log.error("[ERROR]字符串转时间(格式:" + dateFormat + ")时失败:", e);
		}
		return date;
	}

	/**
	 * 
	 * 返回当前日期的字符串格式"yyyy-MM-dd HH:mm:ss"
	 * 
	 * @return
	 */
	public static String currentDate() {
		return format2Str(new Date());
	}

	/**
	 * 
	 * 返回当前日期的字符串格式"yyyy-MM-dd HH:mm:ss"
	 * 
	 * @return
	 */
	public static String format2Str(Date date) {
		String ret = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			ret = sdf.format(date);
		} catch (Exception _e) {
			log.warn("DateUtil.currentDate():" + _e.toString());
		}
		return ret;
	}

	/**
	 * 
	 * 返回当前日期的字符串格式"yyyy-MM-dd HH:mm:ss"
	 * 
	 * @return
	 */
	public static String format2StrCode(Date date) {
		String ret = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			ret = sdf.format(date);
		} catch (Exception _e) {
			log.warn("DateUtil.currentDate():" + _e.toString());
		}
		return ret;
	}

	/**
	 * 
	 * 返回当前日期的字符串格式"yyyyMMdd HHmmssSSS"
	 * 
	 * @return
	 */
	public static String format3StrCode(Date date) {
		String ret = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		try {
			ret = sdf.format(date);
		} catch (Exception _e) {
			log.warn("DateUtil.currentDate():" + _e.toString());
		}
		return ret;
	}

	/**
	 * 
	 * 返回毫秒
	 * 
	 * @return
	 */
	public static long getMillis(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.getTimeInMillis();
	}

	/**
	 * 
	 * 返回两个日期相差秒数
	 * 
	 * @return
	 */
	public static long getDateDiffBySecond(Date date1, Date date2) {
		return (getMillis(date1) - getMillis(date2)) / (1000);
	}

	public static void main(String[] args) {

	}
}
