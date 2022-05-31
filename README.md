[![Build](https://github.com/skycavemc/VoteSystem/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/skycavemc/VoteSystem/actions/workflows/maven.yml)
[![JitPack](https://jitpack.io/v/skycavemc/VoteSystem.svg)](https://jitpack.io/#skycavemc/VoteSystem)
![License](https://img.shields.io:/github/license/skycavemc/VoteSystem)
[![CodeQL](https://github.com/skycavemc/VoteSystem/actions/workflows/codeql-analysis.yml/badge.svg?branch=master)](https://github.com/skycavemc/VoteSystem/actions/workflows/codeql-analysis.yml)
# VoteSystem
Plugin for Skybee, rewarding players for voting for the server.

## Adding VoteSystem to your dependencies
If you use Maven:
```xml
<repositories>
  <repository>
    <id>jitpack</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.skycavemc</groupId>
    <artifactId>VoteSystem</artifactId>
    <version>v3.2.0</version>
  </dependency>
</dependencies>
```

If you use Gradle:
```groovy
repositories {
  maven {
    url 'https://jitpack.io'
  }
}

dependencies {
  implementation 'com.github.skycavemc:VoteSystem:v3.2.0'
}
```
