#!/bin/bash
USER=ballovertime
APP_DIR=/home/$USER/app

# Change ownership of installation
chown -R $USER:$USER $APP_DIR

# Create empty properties file if needed
if [ ! -f $APP_DIR/conf/ballovertime.properties ]; then
    echo 'Creating empty properties file'
    echo '# Place app properties below' > $APP_DIR/conf/ballovertime.properties
fi

# Create default logj2.xml properties file if needed
if [ ! -f $APP_DIR/conf/log4j2.xml ]; then
    echo 'Creating default log4j2.xml'
    echo '# Place app properties below' > $APP_DIR/conf/ballovertime.properties
    echo '<?xml version="1.0" encoding="UTF-8"?>' > $APP_DIR/conf/log4j2.xml
    echo '<Configuration monitorInterval="60">' >> $APP_DIR/conf/log4j2.xml
    echo '    <Appenders>' >> $APP_DIR/conf/log4j2.xml
    echo '        <RollingFile name="AppLog" fileName="/home/ballovertime/app/logs/app.log"' >> $APP_DIR/conf/log4j2.xml
    echo '                filePattern="/home/ballovertime/app/logs/app-%d{MM-dd-yyyy}-%i.log.gz">' >> $APP_DIR/conf/log4j2.xml
    echo '            <PatternLayout>' >> $APP_DIR/conf/log4j2.xml
    echo '                <Pattern>%d{yyy/MM/dd HH:mm:ss.SSS}\t[%t]\t%-5level\t%logger{36}\t%msg%n' >> $APP_DIR/conf/log4j2.xml
    echo '                </Pattern>' >> $APP_DIR/conf/log4j2.xml
    echo '            </PatternLayout>' >> $APP_DIR/conf/log4j2.xml
    echo '            <Policies>' >> $APP_DIR/conf/log4j2.xml
    echo '                <TimeBasedTriggeringPolicy />' >> $APP_DIR/conf/log4j2.xml
    echo '                <SizeBasedTriggeringPolicy size="200MB" />' >> $APP_DIR/conf/log4j2.xml
    echo '            </Policies>' >> $APP_DIR/conf/log4j2.xml
    echo '            <DefaultRolloverStrategy fileIndex="min" max="7" />' >> $APP_DIR/conf/log4j2.xml
    echo '        </RollingFile>' >> $APP_DIR/conf/log4j2.xml
    echo '    </Appenders>' >> $APP_DIR/conf/log4j2.xml
    echo '    <Loggers>' >> $APP_DIR/conf/log4j2.xml
    echo '        <Root level="warn">' >> $APP_DIR/conf/log4j2.xml
    echo '            <AppenderRef ref="AppLog" />' >> $APP_DIR/conf/log4j2.xml
    echo '        </Root>' >> $APP_DIR/conf/log4j2.xml
    echo '        <Logger name="com.meridian" level="info" additivity="false">' >> $APP_DIR/conf/log4j2.xml
    echo '            <AppenderRef ref="AppLog" />' >> $APP_DIR/conf/log4j2.xml
    echo '        </Logger>' >> $APP_DIR/conf/log4j2.xml
    echo '    </Loggers>' >> $APP_DIR/conf/log4j2.xml
    echo '</Configuration>' >> $APP_DIR/conf/log4j2.xml
fi

systemctl daemon-reload
systemctl restart ballovertime.service

