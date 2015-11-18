#!/bin/sh
BIN_DIR=$(dirname $(readlink -f "$0"))
java -Duser.language=de -Duser.country=DE -jar "$BIN_DIR/hibiscus-watcher.jar" $*
