#!/bin/bash
RUN_WITH_DRIVERS=org.pageobject.core.driver.vnc.DefaultVncDriverFactoryList FIREFOX_LIMIT=0 sbt "$@ test"
