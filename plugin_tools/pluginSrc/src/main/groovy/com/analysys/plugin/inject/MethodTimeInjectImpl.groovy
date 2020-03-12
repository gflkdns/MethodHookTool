package com.analysys.plugin.inject

import com.analysys.plugin.config.MethodTimerConfig
import com.analysys.plugin.vistor.MethodTimerVisitor
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

public class MethodTimeInjectImpl implements Inject {

    MethodTimerConfig config

    Project project;

    MethodTimeInjectImpl(Project project) {
        this.project = project
    }

    void setConfig(MethodTimerConfig config) {
        this.config = config
    }

    @Override
    public boolean isInject(File classFile) {
        return isInjectImpl(classFile.name)
    }

    private boolean isInjectImpl(String name) {
        if (name.endsWith(".class") && !name.startsWith("R\$") &&
                !"R.class".equals(name) && !"BuildConfig.class".equals(name) && !name.contains("TimePrint")) {
            return true
        }
        return false
    }

    @Override
    byte[] injectClass(byte[] clazzBytes) {

        ClassReader cr = new ClassReader(clazzBytes)
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        ClassVisitor cv = new MethodTimerVisitor(cw, config, project)

        cr.accept(cv, EXPAND_FRAMES)

        byte[] code = cw.toByteArray()

        return code

    }

    @Override
    File injectJar(File jarFile, File tempDir) {
        /**
         * 读取原jar
         */
        def file = new JarFile(jarFile)
        /** 设置输出到的jar */
        def hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8)
        def outputJar = new File(tempDir, hexName + jarFile.name)
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar))
        Enumeration enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            InputStream inputStream = file.getInputStream(jarEntry)

            String entryName = jarEntry.getName()

            ZipEntry zipEntry = new ZipEntry(entryName)

            jarOutputStream.putNextEntry(zipEntry)

            byte[] modifiedClassBytes = null
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)
            if (isInjectImpl(entryName)) {
                modifiedClassBytes = injectClass(sourceClassBytes)
            }
            if (modifiedClassBytes == null) {
                jarOutputStream.write(sourceClassBytes)
            } else {
                jarOutputStream.write(modifiedClassBytes)
            }
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()
        return outputJar
    }
}