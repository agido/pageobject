# 0.3.0
* local drivers will take screenshots on test failure #22
* added firefox to travis test build #18
* retry on ElementNotInteractableException (when element is not visible) #19
* fixed snaptshot builds maven metadata #15
* added Element.scrollIntoView #17
* added optional cause to `TestHelper.timeoutTest`
* changed `click on` from duration/time to `PatienceConfig`
* added required description string to `WaitFor`
* added logging of `WaitFor` retry attempts
* changed `WaitFor` to use force explicit timeout and interval arguments
* show milliseconds instead of nanoseconds in `WaitFor`
* added traced remote web driver support for local web drivers
* added `PAGEOBJECT_CHROMEDRIVER_PATH`
* `DefaultDriverFactoryList` supports now VNC (linux only)
* removed scala 2.10 support
* removed jdk 7 support
* removed selenium 2 support
* fixed some scala 2.12 warnings
* snapshot builds will now be build from develop branch
* updated selenium to 3.4.0
* updated selenium-server-standalone.jar to 3.4.0
* updated sbt to 0.13.15
* updated chromedriver to 2.29
* updated htmlunit to 2.26
* updated logback to 1.2.2
* updated slf4j to 1.7.25
* updated jetty to 9.4.4
* updated scala to 2.11.11/2.12.2
* added typesafe config
* removed `WaitFor.PatienceConfig` type alias closes #11
* added `FixedTagName` and `InputTagName` refs #9
* throw an exception if the element is not of the excepted type
* code style fixes

# 0.2.0
* updated to scalatest 3.0.1
* updated to sbt 0.13.13
* updated to tigervnc 1.7.0
* updated to chromedriver 2.25
* updated to jetty 9.2.19.v20160908
* updated to scala 2.12.1
* added VNC Server stop command
* added VNC Server view command
* added `PAGEOBJECT_SELENIUM_HOST`
* added `PAGEOBJECT_VNC_DURATION`
* added `PAGEOBJECT_VNC_LIMIT`
* added `PAGEOBJECT_SELENIUM_SCRIPT`
* added maven support #7
* added `trait PageHolderWatcher`
* added `Element.rect`
* added `click after animation` DSL
* retry `createRealWebDriver` on `SessionNotCreatedException`
* changed Browser Limits
* removed `ParallelTestExecution` from `DriverLaunchWrapper` (bug in scalatest)
* enabled `TRACE_REMOTE_WEB_DRIVER` as default
* log when test startup has failed
* use `DefaultVncDriverFactoryList` as default on Linux
* fixed documentation of QueryDsl #2
* removed `click on element` #2
* removed `ConfigureableParallelTestLimit` #3
* added `Element.webElement`
* added new `trait WaitPage` to allow customisation of wait time
* added new `trait CookieLogger` to dump all cookies of a page
* added `all cookies` DSL
* added `LogLevel` to Logging trait
* added the active page to logger NDC
* improved VNC logging
* improved `TracedRemoteWebDriver` logging
* improved error handling in vnc.sh
* fixed some test warnings

# 0.1.0
* Initial Release
