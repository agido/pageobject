#!/bin/bash
base=$(readlink -f $(dirname $0))
exec java -Djava.security.egd=file:/dev/./urandom -Dwebdriver.chrome.driver=$base/../selenium/chromedriver -Dwebdriver.firefox.bin=$(which firefox) -jar $base/../selenium/selenium-server-standalone.jar "$@"
