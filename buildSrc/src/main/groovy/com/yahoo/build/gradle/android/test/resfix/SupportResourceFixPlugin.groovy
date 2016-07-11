/*
 * Copyright 2016, Yahoo Inc.
 * Copyrights licensed under the MIT License.
 * See the accompanying LICENSE file for terms.
 */

package com.yahoo.build.gradle.android.test.resfix

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import com.google.common.base.CaseFormat
import groovy.util.slurpersupport.GPathResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

/**
 * Created by nickwph on 5/17/16.
 */
class SupportResourceFixPlugin implements Plugin<Project> {

    private static final Map<String, String> SUPPORT_PACKAGE_MAP = new HashMap<String, String>() {
        {
            // v4
            put("support-v4", "android.support.v4")
            // v7
            put("appcompat-v7", "android.support.v7.appcompat")
            put("cardview-v7", "android.support.v7.cardview")
            put("gridlayout-v7", "android.support.v7.gridlayout")
            put("mediarouter-v7", "android.support.v7.mediarouter")
            put("palette-v7", "android.support.v7.palette")
            put("recyclerview-v7", "android.support.v7.recyclerview")
            put("preference-v7", "android.support.v7.preference")
            // v13
            put("support-v13", "android.support.v13")
            // v14
            put("preference-v7", "android.support.v7.preference")
            // leanback
            put("preference-leanback-v17", "android.support.v17.preference")
            put("leanback-v17", "android.support.v17.leanback")
            // other
            put("design", "android.support.design")
            put("customtabs", "android.support.customtabs")
            put("percent", "android.support.percent")
            put("recommendation", "android.support.recommendation")
        }
    }

    private Project project
    private LibraryExtension android

    void apply(Project project) {
        if (project.pluginManager.hasPlugin('com.android.library')) {
            this.project = project
            this.android = project.extensions.getByName("android") as LibraryExtension
            project.afterEvaluate { createTasks() }
        } else {
            System.out.println("Android library plugin not applied.")
        }
    }

    private createTasks() {
        android.libraryVariants.each { LibraryVariant variant ->
            for (Dependency dependency : getIncludedSupportLibraryList(variant)) {
                String projectPackageName = getProjectPackageName(variant)
                String variantNameCapitalized = variant.name.capitalize()
                Task copyTask = createCopyTask(variant, dependency, projectPackageName)
                Task deleteTask = createDeleteTask(variant, dependency)
                Task processResourcesTask = project.tasks.getByName("generate${variantNameCapitalized}Sources")
                processResourcesTask.finalizedBy copyTask, deleteTask
                project.gradle.taskGraph.whenReady { TaskExecutionGraph graph ->
                    Task compileUnitTestTask = project.tasks.getByName("compile${variantNameCapitalized}UnitTestSources")
                    boolean shouldRun = graph.hasTask(compileUnitTestTask)
                    copyTask.enabled = shouldRun
                    deleteTask.enabled = shouldRun
                    if (shouldRun) {
                        compileUnitTestTask.finalizedBy deleteTask
                    }
                }
            }

        }
    }

    private List<Dependency> getIncludedSupportLibraryList(LibraryVariant variant) {
        List<Dependency> dependencyList = new ArrayList<>()
        Configuration configuration = project.configurations.getByName("default")
        configuration.allDependencies.each { Dependency dependency ->
            if (dependency.group == 'com.android.support' && SUPPORT_PACKAGE_MAP.containsKey(dependency.name)) {
                dependencyList.add(dependency)
            }
        }
        return dependencyList
    }

    private String getProjectPackageName(LibraryVariant variant) {
        File manifestFile = android.sourceSets.getByName(variant.name).manifest.srcFile
        if (!manifestFile.exists()) {
            manifestFile = android.sourceSets.getByName("main").manifest.srcFile
        }
        GPathResult manifest = new XmlSlurper().parse(manifestFile)
        return manifest.@package.text()
    }

    private static String getLastSegmentCapitalized(String packageName) {
        return packageName.split('\\.').last().capitalize()
    }

    private static String packageNameToPath(String packageName) {
        return packageName.replaceAll("\\.", "/")
    }

    private String getGeneratedResPath() {
        return "${project.buildDir}/generated/source/r"
    }

    private Task createCopyTask(BaseVariant variant, Dependency dependency, String projectPackageName) {
        String dependencyPackageName = SUPPORT_PACKAGE_MAP.get(dependency.name)
        String dependencyPackagePath = packageNameToPath(dependencyPackageName)
        String dependencyName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, dependency.name)
        String name = "copy${variant.name.capitalize()}${dependencyName}Resource"
        String projectPackagePath = packageNameToPath(projectPackageName)
        return project.tasks.create(name: name, type: Copy) {
            from "${getGeneratedResPath()}/${variant.name}/${projectPackagePath}"
            into "${getGeneratedResPath()}/${variant.name}/${dependencyPackagePath}"
            include 'R.java'
            filter { line -> line.contains("package ${projectPackageName};") ? "package ${dependencyPackageName};" : line }
            outputs.upToDateWhen { false }
        }
    }

    private Task createDeleteTask(BaseVariant variant, Dependency dependency) {
        String dependencyPackageName = SUPPORT_PACKAGE_MAP.get(dependency.name)
        String dependencyPackagePath = packageNameToPath(dependencyPackageName)
        String dependencyName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, dependency.name)
        String name = "delete${variant.name.capitalize()}${dependencyName}Resources"
        return project.tasks.create(name: name, type: Delete) {
            dependsOn "test${variant.name.capitalize()}UnitTest"
            delete "${getGeneratedResPath()}/${variant.name}/${dependencyPackagePath}"
            outputs.upToDateWhen { false }
        }
    }
}
