package com.analysys.plugin.transform

import com.analysys.plugin.config.StringMixConfig
import com.analysys.plugin.tools.MappingPrinter
import com.analysys.plugin.vistor.StringFogClassVisitor
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.Sets
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

//import MappingPrinter

public class StringMixTransform extends Transform {
    Project project
    boolean islib;


    StringMixTransform(Project project, boolean islib) {
        this.project = project
        this.islib = islib


    }

    @Override
    String getName() {
        return "StringMix"
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
            );
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

        MappingPrinter mappingPrinter
        StringMixConfig config;
        config = project.stringmix

        if (config.enable) {
            mappingPrinter = new MappingPrinter(new File(project.buildDir, "/outputs/mapping/stringmix_mapping.txt"));
        } else {
            return
        }
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                if (directoryInput.file.isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->
                        def name = file.name
                        if (name.endsWith(".class") && !name.startsWith("R\$") &&
                                !"R.class".equals(name) && !"BuildConfig.class".equals(name)
                                && !name.contains("StringFog")) {

                            println(name)
                            //  mappingPrinter.log(name + '--> mix begin')

                            ClassReader cr = new ClassReader(file.bytes)
                            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                            ClassVisitor cv = new StringFogClassVisitor(config.key, config.impl, cw, mappingPrinter)

                            //   mappingPrinter.log(name + '--> mix end')

                            cr.accept(cv, EXPAND_FRAMES)

                            byte[] code = cw.toByteArray()

                            FileOutputStream fos = new FileOutputStream(
                                    file.parentFile.absolutePath + File.separator + name)
                            fos.write(code)
                            fos.close()
                            println(name)
                        }
                    }
                }

                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)


                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            input.jarInputs.each { JarInput jarInput ->
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }
}