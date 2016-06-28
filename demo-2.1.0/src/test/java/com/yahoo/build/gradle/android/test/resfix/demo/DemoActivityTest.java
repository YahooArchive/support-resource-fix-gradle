/*
 * Copyright 2016, Yahoo Inc.
 * Copyrights licensed under the MIT License.
 * See the accompanying LICENSE file for terms.
 */

package com.yahoo.build.gradle.android.test.resfix.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

/**
 * Created by nickwph on 6/27/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class DemoActivityTest {

    @Test
    public void testCreateActivity() throws Exception {
        assertNotNull(Robolectric.buildActivity(DemoActivity.class).create().get());
    }
}