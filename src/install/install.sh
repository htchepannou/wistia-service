#!/bin/sh

#
# Usage: ./install.sh
#

SERVICE_NAME=__artifactId__

# Install application
if [ ! -d "/opt/$SERVICE_NAME" ]; then
  mkdir /opt/$SERVICE_NAME
fi

if [ ! -d "/opt/$SERVICE_NAME/log" ]; then
  mkdir /opt/$SERVICE_NAME/log
fi

# startup script
cp initd.sh /etc/init.d/$SERVICE_NAME
chmod +x /etc/init.d/$SERVICE_NAME

/sbin/chkconfig --add $SERVICE_NAME
/sbin/chkconfig $SERVICE_NAME on

# application
cp $SERVICE_NAME-exec.jar /opt/$SERVICE_NAME/.

/etc/init.d/$SERVICE_NAME restart
