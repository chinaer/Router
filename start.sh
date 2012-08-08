#!/bin/sh
## 2012-06-21

export JAVA_HOME=/usr/local/platform/jdk
export JAVA_PID=/usr/local/logs/router.pid

[ -s $JAVA_PID ] && { echo "Existing PID file found during start."; exit; }

sudo -u derby /bin/bash -c "
	$JAVA_HOME/bin/java -jar router-1.0-SNAPSHOT.jar hosts.properties  2>&1 \
	| /usr/sbin/cronolog --link=/usr/local/logs/router.log /usr/local/logs/router.log.%w >> /dev/null &
"

PID=`$JAVA_HOME/bin/jps | grep 'jar' | awk '{print $1}'`
sudo -u derby /bin/bash -c "echo $PID > \"$JAVA_PID\""
