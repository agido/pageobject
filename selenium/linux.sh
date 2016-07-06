java -Djava.security.egd=file:/dev/./urandom -Dwebdriver.chrome.driver=selenium/chromedriver -Dwebdriver.firefox.bin=/usr/bin/firefox -jar selenium-server-standalone-2.53.1.jar "$@"
