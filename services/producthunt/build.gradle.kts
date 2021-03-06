/*
 * Copyright (c) 2018 Zac Sweers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
}

apply {
  from(rootProject.file("gradle/config-kotlin-sources.gradle"))
}

android {
  compileSdkVersion(deps.android.build.compileSdkVersion)
  buildToolsVersion(deps.android.build.buildToolsVersion)

  defaultConfig {
    minSdkVersion(deps.android.build.minSdkVersion)
    targetSdkVersion(deps.android.build.targetSdkVersion)
    vectorDrawables.useSupportLibrary = true
    buildConfigField("String", "PRODUCT_HUNT_DEVELOPER_TOKEN",
        "\"${project.properties["catchup_product_hunt_developer_token"].toString()}\"")
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  lintOptions {
    setLintConfig(file("lint.xml"))
    isAbortOnError = true
    check("InlinedApi")
    check("NewApi")
    fatal("NewApi")
    fatal("InlinedApi")
    enable("UnusedResources")
    isCheckReleaseBuilds = true
    textReport = deps.build.ci
    textOutput("stdout")
    htmlReport = !deps.build.ci
    xmlReport = !deps.build.ci
  }
  libraryVariants.all {
    generateBuildConfigProvider?.configure {
      // Can't enable until we have somewhere else for the token
//      enabled = false
    }
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = build.standardFreeKotlinCompilerArgs
    jvmTarget = "1.8"
  }
}

kapt {
  correctErrorTypes = true
  useBuildCache = true
  mapDiagnosticLocations = true
  arguments {
    arg("moshi.generated", "javax.annotation.Generated")
    arg("dagger.formatGeneratedSource", "disabled")
    arg("dagger.gradle.incremental")
  }
}

dependencies {
  kapt(project(":service-registry:service-registry-compiler"))
  kapt(deps.crumb.compiler)
  kapt(deps.dagger.apt.compiler)
  kapt(deps.moshi.compiler)

  implementation(project(":libraries:util"))
  implementation(deps.misc.okio)
  implementation(deps.moshi.core)
  implementation(deps.retrofit.core)
  implementation(deps.retrofit.moshi)
  implementation(deps.retrofit.rxJava2)
  implementation(deps.okhttp.core)
  implementation(deps.misc.moshiLazyAdapters)

  api(project(":service-api"))
  api(deps.android.androidx.annotations)
  api(deps.dagger.runtime)
  api(deps.misc.lazythreeten)
  api(deps.rx.java)
}
