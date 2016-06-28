/*
 * Copyright 2016, Yahoo Inc.
 * Copyrights licensed under the MIT License.
 * See the accompanying LICENSE file for terms.
 */

package com.yahoo.build.gradle.android.test.resfix.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;

/**
 * Created by nickwph on 6/27/16.
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class ResourcesClassTest {

    private final String name;
    private final String klass;

    @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // v4
                {"support-v4", "android.support.v4.R"},
                // v7
                {"appcompat-v7", "android.support.v7.appcompat.R"},
                {"cardview-v7", "android.support.v7.cardview.R"},
                {"gridlayout-v7", "android.support.v7.gridlayout.R"},
                {"mediarouter-v7", "android.support.v7.mediarouter.R"},
                {"palette-v7", "android.support.v7.palette.R"},
                {"recyclerview-v7", "android.support.v7.recyclerview.R"},
                {"preference-v7", "android.support.v7.preference.R"},
                // v13
                {"support-v13", "android.support.v13.R"},
                // v14
                {"preference-v7", "android.support.v7.preference.R"},
                // leanback
                {"preference-leanback-v17", "android.support.v17.preference.R"},
                {"leanback-v17", "android.support.v17.leanback.R"},
                // other
                {"design", "android.support.design.R"},
                {"customtabs", "android.support.customtabs.R"},
                {"percent", "android.support.percent.R"},
                {"recommendation", "android.support.recommendation.R"}
        });
    }

    public ResourcesClassTest(String name, String klass) {
        this.name = name;
        this.klass = klass;
    }

    @Test
    public void testResources() throws Exception {
        assertNotNull(Class.forName(this.klass));
    }
}