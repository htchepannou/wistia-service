#!/bin/bash
#
# Startup script for a spring boot project
#
# chkconfig: - 84 16
# description: spring boot project

SERVICE_NAME=wistia-service
ACTIVE_PROFILE=__ACTIVE_PROFILE__

SERVICE_DIR=/opt/$SERVICE_NAME
PATH_TO_JAR=$SERVICE_DIR/$SERVICE_NAME-exec.jar
PID_PATH_NAME=$SERVICE_DIR/$SERVICE_NAME.pid
LOG_FILE=$SERVICE_DIR/log/$SERVICE_NAME.log
SERVICE_USER=webapp

case $1 in
    start)
        echo "Starting $SERVICE_NAME ..."
        if [ ! -f $PID_PATH_NAME ]; then
            JAVA_OPTS="--Xms256m -Xmx256m --spring.profiles.active=$ACTIVE_PROFILE --logging.file=$LOG_FILE --spring.pidfile=$PID_PATH_NAME"
            echo "Running: java -jar $PATH_TO_JAR $JAVA_OPTS"
            su -m $SERVICE_USER -c "java -jar $PATH_TO_JAR $JAVA_OPTS > /dev/null 2>&1  &"
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;

    restart)
        $0 stop
        $0 start
    ;;

    *)
        echo "Usage: $0 {start|stop|restart}"
        exit 1
esac
