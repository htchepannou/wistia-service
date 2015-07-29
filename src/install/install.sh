#!/bin/sh

#
# Usage: ./install.sh
#

SERVICE_NAME=wistia-service
SERVICE_VERSION="1.0"
SERVICE_PROFILE=test

# Get from maven repo
rm -f $SERVICE_NAME-*.jar
wget https://s3-us-west-2.amazonaws.com/maven.com.tchepannou/release/com/tchepannou/wistia/wistia-service/$SERVICE_VERSION/$SERVICE_NAME-$SERVICE_VERSION-exec.jar

# Create user
id -u webapp &>/dev/null || useradd webapp

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
cp $SERVICE_NAME-$SERVICE_VERSION-exec.jar /opt/$SERVICE_NAME/$SERVICE_NAME-exec.jar

# permission
chown -R webapp:webapp /opt/$SERVICE_NAME

/etc/init.d/$SERVICE_NAME stop
/etc/init.d/$SERVICE_NAME start
