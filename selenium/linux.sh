#!/bin/bash
base=$(readlink -f $(dirname $0))
exec java -Djava.security.egd=file:/dev/./urandom -Dwebdriver.chrome.driver=$base/../selenium/chromedriver_linux -Dwebdriver.gecko.driver=$base/../selenium/geckodriver_linux -jar $base/../selenium/selenium-server-standalone.jar "$@"
