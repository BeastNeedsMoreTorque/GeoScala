```
   _____                 _____              _        
  / ____|               / ____|            | |       
 | |  __   ___   ___   | (___    ___  __ _ | |  __ _ 
 | | |_ | / _ \ / _ \   \___ \  / __|/ _` || | / _` |
 | |__| ||  __/| (_) |  ____) || (__| (_| || || (_| |
  \_____| \___| \___/  |_____/  \___|\__,_||_| \__,_|
```

A geo library written by Java and Scala, contains GPS 2d-spherical geometry, GPS track compression and heat-map.

## Documents
See <a href="https://github.com/TsingJyujing/GeoScala/wiki">GeoScala - wiki</a>

## Maven
<del>Will deploy in Central Maven Server in the future, but now, use `mvn clean install` to install to local.</del>
Now, you can use it on maven directly:

Set the scala version you wanna to use in properties:

```xml
<properties>
    <scala.version.main>2.10</scala.version.main>
</properties>
```

And set dependency:

```xml
<dependencies>
    <dependency>
        <groupId>com.github.tsingjyujing</groupId>
        <artifactId>geo-library-${scala.version.main}</artifactId>
        <version>RELEASE</version>
    </dependency>
</dependencies>
```

