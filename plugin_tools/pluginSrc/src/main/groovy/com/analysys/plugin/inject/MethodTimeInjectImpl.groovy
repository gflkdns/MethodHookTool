package com.analysys.plugin.inject

import com.analysys.plugin.config.MethodTimerConfig
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.*

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

        if (config.impl != null && !"".equals(config.impl)) {
            if (classFile.absolutePath.contains(config.impl.replace(".", File.separator) + ".class")) {
                println("[class]" + classFile.name + " not inject.")
                return false
            }
        }
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
        ClassVisitor cv = new TryCVisitor(Opcodes.ASM5, cw)

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

    File injectSelfJar(File jarFile, File tempDir) {
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

            if (entryName.equals("com/miqt/pluginlib/tools/TimePrint.class")) {
                modifiedClassBytes = dump(config.impl)
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

    /**
     * dump TimePrint
     * @param impl
     * @return
     * @throws Exception
     */
    public static byte[] dump(String impl) throws Exception {
        impl = impl.replace(".", "/")
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, "com/miqt/pluginlib/tools/TimePrint", null, "java/lang/Object", null);

        fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "M_PRINT", "Lcom/miqt/pluginlib/tools/ITimePrint;", null, null);
        fv.visitEnd();


        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();


        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "start", "(Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "com/miqt/pluginlib/tools/TimePrint", "M_PRINT", "Lcom/miqt/pluginlib/tools/ITimePrint;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/miqt/pluginlib/tools/ITimePrint", "onMethodEnter", "(Ljava/lang/String;)V", true);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();


        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "end", "(Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "com/miqt/pluginlib/tools/TimePrint", "M_PRINT", "Lcom/miqt/pluginlib/tools/ITimePrint;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/miqt/pluginlib/tools/ITimePrint", "onMethodReturn", "(Ljava/lang/String;)V", true);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();


        mv = cw.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();
        mv.visitTypeInsn(Opcodes.NEW, impl);
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, impl, "<init>", "()V", false);
        mv.visitFieldInsn(Opcodes.PUTSTATIC, "com/miqt/pluginlib/tools/TimePrint", "M_PRINT", "Lcom/miqt/pluginlib/tools/ITimePrint;");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 0);
        mv.visitEnd();

        cw.visitEnd();

        return cw.toByteArray();
    }
}