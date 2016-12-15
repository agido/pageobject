---
layout: default
title: Home
permalink: /
order: 0
---

[ ![Build Status] [travis-image] ] [travis]
[ ![License] [license-image] ] [license]
[ ![Dependencies] [dependencies-image] ] [dependencies]
[ ![Coverage Status] [coverage-image] ] [coverage]

# PageObject
PageObject is a Page Object Pattern implementation written in Scala.

PageObject is a free, open-source ([Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt)) extension for test runners implementing the Page Object Pattern.
Currently only ScalaTest 3 is supported, but other testing frameworks should be easy to add.

## Design Goals
The PageObject library was developed with this goals in mind:

* PageObject should be easy to use
* A clear structure
  * PageObjects for pages
  * PageModules for page content
* write just tests, not infrastructure
* prevent boilerplate code where possible
* no need to manage browsers yourself
* browser selection at runtime, not at class level
* direct selenium access should not be needed (but possible if wanted)

## PageObject Tour
We created an overview presentation showing you how to use PageObject to create tests using ScalaTest:

* [Google Slides: PageObject Tour](https://docs.google.com/presentation/d/1mHCZD6UgvoET_VxLZaWqUxPKpiZzHj9M9ua6ASSRrn0) (online, you can send us comments)
* [Download as PDF](/downloads/PageObjectTour.pdf) (download hosted on pageobject.org).

## Using PageObject
The current version of PageObject is 0.2.0.

Scala versions 2.10.x, 2.11.x and 2.12.x are supported.

To use PageObject, add this lines to yours build.sbt:

```
libraryDependencies += "org.pageobject" %% "scalatest" % "0.2.0"
```

## Download PageObject
The code is hosted and developed in the [PageObject GitHub repository](https://github.com/agido/pageobject/).

[travis]: https://travis-ci.org/agido/pageobject
[travis-image]: https://travis-ci.org/agido/pageobject.svg?branch=master
[license-image]: http://img.shields.io/badge/license-Apache--2-brightgreen.svg?style=flat
[license]: http://www.apache.org/licenses/LICENSE-2.0
[dependencies]: https://app.updateimpact.com/latest/755117671372165120/pageobject
[dependencies-image]: https://app.updateimpact.com/badge/755117671372165120/pageobject.svg?config=compile
[coverage]: https://coveralls.io/github/agido/pageobject?branch=master
[coverage-image]: https://coveralls.io/repos/github/agido/pageobject/badge.svg?branch=master
