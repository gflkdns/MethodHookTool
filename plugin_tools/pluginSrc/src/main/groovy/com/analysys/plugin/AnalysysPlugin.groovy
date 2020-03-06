package com.analysys.plugin

import com.analysys.plugin.config.MethodTimerConfig
import com.analysys.plugin.config.StringMixConfig
import com.analysys.plugin.transform.MethodTimerTransform
import com.analysys.plugin.transform.StringMixTransform
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AnalysysPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("methodtimer", MethodTimerConfig)
        project.extensions.create("stringmix", StringMixConfig)
        def android = project.extensions.android
        boolean islib = true
        if (android instanceof AppExtension) {
            islib = false
        }

        android.registerTransform(new MethodTimerTransform(project, islib))
        //  android.registerTransform(new StringMixTransform(project, islib))
    }


}
