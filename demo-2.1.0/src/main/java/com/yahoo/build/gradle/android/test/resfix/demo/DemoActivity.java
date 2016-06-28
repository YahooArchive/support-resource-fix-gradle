/*
 * Copyright 2016, Yahoo Inc.
 * Copyrights licensed under the MIT License.
 * See the accompanying LICENSE file for terms.
 */

package com.yahoo.build.gradle.android.test.resfix.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CardView(this));
    }
}
