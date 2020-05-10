package hisense.fdp.cfa.util;

import java.util.UUID;

public class UUIDGenerator {
	/**
	 * 生成唯一字符串,长度为32位
	 * 
	 * @return 唯一字符串
	 */
	public static String generate32BitUUID() {
		String uuid = null;
		UUID generator = UUID.randomUUID();
		uuid = generator.toString();
		uuid = uuid.replace("-", "");
		return uuid;
	}

	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(generate32BitUUID());
		System.out.println(generate32BitUUID());
		System.out.println(generate32BitUUID());
	}
}
