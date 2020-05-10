#!/bin/bash

JAVA_BIN=java
#cd
#如果使用导出JAR包方式此处名称要一致，com文件夹的方式打包时也别删，做为进程标识。
CLASS_PATH=".:HiFdpCfa.jar:../lib/activemq-all-5.4.3.jar:../lib/commons-collections-3.2.1.jar:../lib/commons-dbcp-1.4.jar:../lib/commons-httpclient-3.0.1.jar:../lib/commons-logging-1.0.3.jar:../lib/commons-pool-1.6.jar:../lib/dom4j-1.6.1.jar:../lib/engine.jar:../lib/fastjson-1.1.32.jar:../lib/gson-2.1.jar:./plugin/HiFdpCommon.jar:./plugin/HiFdpAlmpCore.jar:../lib/jakarta.jar:../lib/jaxen-1.1-beta-7.jar:./plugin/logback-classic-1.1.7.jar:./plugin/logback-core-1.1.7.jar:../lib/lombok.jar:../lib/ojdbc14.jar:../lib/poi-3.12.jar:../lib/quartz-all-1.6.0.jar:./plugin/slf4j-api-1.7.21.jar:../lib/ttjdbc6.jar:../lib/spring-core-4.3.0.RELEASE.jar:../lib/sigar.jar:../lib/commons-codec-1.6.jar"

SERVICE_CLASS="hisense.fdp.cfa.HiFdpCfa"

#echo $CLASS_PATH

#正常启动命令
#nohup ${JAVA_BIN} -Djava.ext.dirs="../lib/" -cp $CLASS_PATH ${SERVICE_CLASS} 2>&1 1>/dev/null &
nohup ${JAVA_BIN} -cp $CLASS_PATH ${SERVICE_CLASS} 2>&1 1>/dev/null &

#无法启动或启动报错时使用下行，打印启动系统日志
#nohup ${JAVA_BIN} -Djava.ext.dirs="../lib/" -cp $CLASS_PATH ${SERVICE_CLASS} 2>&1 1>>AdapterLog.log &

exit 0
