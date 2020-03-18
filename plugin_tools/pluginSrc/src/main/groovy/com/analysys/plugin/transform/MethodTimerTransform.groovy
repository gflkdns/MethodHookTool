package com.analysys.plugin.transform

import com.analysys.plugin.config.MethodTimerConfig
import com.analysys.plugin.inject.MethodTimeInjectImpl
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.Sets
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.regex.Pattern

class MethodTimerTransform extends Transform {
    Project project
    boolean islib;
    MethodTimerConfig mtc

    MethodTimerTransform(Project project, boolean islib) {
        this.project = project
        this.islib = islib
    }

    @Override
    String getName() {
        return "MethodTimer"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        if (islib) {
            Sets.immutableEnumSet(
                    QualifiedContent.Scope.PROJECT,
            )
        } else {
            TransformManager.SCOPE_FULL_PROJECT
        }
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider,
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
        println '//================================================//'
        println '//===============Method Timer start===============//'
        println '//================================================//'
        mtc = project.methodtimer
        println("[Config]:" + mtc.toString())
        if (!mtc.enable) {
            return
        }
        MethodTimeInjectImpl impl = new MethodTimeInjectImpl(project)
        impl.setConfig(mtc)

        inputs.each { TransformInput input ->
            eachClass(input, impl, outputProvider)
            eachJar(context, input, impl, outputProvider)
        }

        println '//================================================//'
        println '//===============Method Timer success=============//'
        println '//================================================//'
    }

    private List eachJar(Context context, TransformInput input, MethodTimeInjectImpl impl, outputProvider) {
        input.jarInputs.each { JarInput jarInput ->
            def jarName = jarInput.name

            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
            def file = jarInput.file

            if (impl.config.impl != null && !"".equals(impl.config.impl) &&
                    jarName.contains("me.miqt.plugin.tools:pluginlib")) {
                file = impl.injectSelfJar(jarInput.file, context.getTemporaryDir())
            } else {
                mtc.jarRegexs.each { def regexStr ->
                    def isM = Pattern.matches(regexStr, jarName)
                    if (isM) {
                        if (mtc.log) {
                            println("[jar][regex]:$regexStr $jarName is injected.")
                        }
                        file = impl.injectJar(jarInput.file, context.getTemporaryDir())
                        if (mtc.replaceJar && file != null) {
                            jarInput.file.delete()
                            FileUtils.copyFile(file, jarInput.file)
                        }
                    } else if (mtc.log) {
                        println("[jar][regex]:$regexStr $jarName not mc.")
                    }
                }
            }

            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)

            FileUtils.copyFile(file, dest)
        }
    }

    private List eachClass(TransformInput input, MethodTimeInjectImpl impl, outputProvider) {
        input.directoryInputs.each { DirectoryInput directoryInput ->

            if (directoryInput.file.isDirectory()) {
                directoryInput.file.eachFileRecurse { File file ->

                    if (impl.isInject(file)) {

                        byte[] code = impl.injectClass(file.bytes)

                        FileOutputStream fos = new FileOutputStream(
                                file.parentFile.absolutePath + File.separator + file.name)
                        fos.write(code)
                        fos.close()

                        println "[class]" + file.name + ' is injected.'
                    }
                }
            } else {
                println "[directory]" + directoryInput.file.name + ' not inject.'
            }


            def dest = outputProvider.getContentLocation(directoryInput.name,
                    directoryInput.contentTypes, directoryInput.scopes,
                    Format.DIRECTORY)
            FileUtils.copyDirectory(directoryInput.file, dest)
        }
    }
}