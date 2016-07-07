#!/bin/bash
java -Djava.security.egd=file:/dev/./urandom -Dwebdriver.chrome.driver=selenium/chromedriver -Dwebdriver.firefox.bin=$(which firefox) -jar selenium/selenium-server-standalone-2.53.1.jar "$@"
