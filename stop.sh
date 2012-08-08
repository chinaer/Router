#!/bin/sh
## 2012-06-21
#exec >> /usr/local/logs/router_error.log

export JAVA_HOME=/usr/local/platform/jdk
export JAVA_PID=/usr/local/logs/router.pid

[ ! -s $JAVA_PID ] && { echo "PID file not found, Stop aborted."; exit; }

sudo -u derby kill -9 `cat "$JAVA_PID"` >/dev/null 2>&1
sudo -u derby rm -f $JAVA_PID
