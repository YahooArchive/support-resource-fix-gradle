support-resource-fix-gradle
=============

## Issue
Android Support Library R.class does not get included in the proper build folder when running unit tests on a library module. If your using Robolectric for unit testing, these resources are not available when robolectric is trying to resolve those values and will instantly fail your test (Ex. Inflating a stock CardView). This plugin will copy the resource files into the correct build path for Robolectric to properly resolve the values and clean up after unit tests have run.


## Useage //Unimplemented
build.gradle
```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.yahoo.build.gradle.android.test.resfix:1.0.0'
    }
}

apply plugin: 'com.yahoo.build.gradle.android.test.resfix'`
```

## License
Code licensed under the MIT License. See LICENSE file for terms.
