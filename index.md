---
layout: default
title: Home
permalink: /
order: 0
---

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
Currently no public release is available. Please check out this repository and run

```
$ sbt publishLocal
```

if you want to try it.

## Download PageObject
The code is hosted and developed in the [PageObject GitHub repository](https://github.com/agido/pageobject/).
