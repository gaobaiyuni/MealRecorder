#!/bin/sh
#
# Copyright © 2015-2021 the original authors.
# Gradle wrapper script.

APP_HOME=$(cd "${0%/*}" && pwd -P) || exit

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ ! -f "$CLASSPATH" ]; then
    echo "gradle-wrapper.jar not found, downloading..."
    mkdir -p "$APP_HOME/gradle/wrapper"
    (cd "$APP_HOME/gradle/wrapper" && curl -sL -o gradle-wrapper.jar "https://raw.githubusercontent.com/gradle/gradle/v8.7.0/gradle/wrapper/gradle-wrapper.jar")
fi

# Determine the Java command
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD=$JAVA_HOME/jre/sh/java
    else
        JAVACMD=$JAVA_HOME/bin/java
    fi
else
    JAVACMD=java
fi

exec "$JAVACMD" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"
