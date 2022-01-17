# restart_jenkins.sh
# chmod 777 ./*
# netstat -nat | grep 92
# netstat -nat | grep :::9

jar_name=jenkins_2.319.1.war
port=9299

killpid=`ps aux |grep $jar_name |grep -v "restart_jenkins.sh" |grep -v "grep" |awk '{print $2}'`
if [ "$killpid" != "" ]; then
kill -9 $killpid
fi

nohup java -Xmn256m -Xms256m -Xmx512m -XX:MetaspaceSize=256M -XX:+HeapDumpOnOutOfMemoryError -jar $jar_name --httpPort=$port > ./out.log 2>&1 &

echo "end"