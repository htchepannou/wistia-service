#!/bin/sh

SERVICE_PREFIX=__app__
SERVICE_NAME="$SERVICE_PREFIX-service"
SERVICE_VERSION="1.0"
SERVICE_PROFILE=test

# Copy the jar from S3
aws s3 cp s3://maven.com.tchepannou/release/com/tchepannou/$SERVICE_PREFIX/$SERVICE_NAME/$SERVICE_VERSION/$SERVICE_NAME-1.0-exec.jar $SERVICE_NAME-exec.jar

# Install application
if [ ! -d "/opt/$SERVICE_NAME" ]; then
  mkdir /opt/$SERVICE_NAME
fi

if [ ! -d "/opt/$SERVICE_NAME/log" ]; then
  mkdir /opt/$SERVICE_NAME/log
fi

# startup script
cat initd.sh |  sed -e "s/__ACTIVE_PROFILE__/$SERVICE_PROFILE/" > /etc/init.d/$SERVICE_NAME
chmod +x /etc/init.d/$SERVICE_NAME

/sbin/chkconfig --add $SERVICE_NAME
/sbin/chkconfig $SERVICE_NAME on

# application
cp $SERVICE_NAME-exec.jar /opt/$SERVICE_NAME/.

/etc/init.d/$SERVICE_NAME restart
