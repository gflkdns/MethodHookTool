package com.analysys.plugin.vistor;


import com.analysys.plugin.config.MethodTimerConfig;
import com.analysys.plugin.tools.MappingPrinter;

import org.gradle.api.Project;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.File;
import java.util.regex.Pattern;


public class MethodTimerVisitor extends ClassVisitor {

    String classname;
    MethodTimerConfig config;
    Project project;

    MappingPrinter mappingPrinter;

    public MethodTimerVisitor(ClassVisitor classVisitor, MethodTimerConfig config, Project project) {
        super(Opcodes.ASM5, classVisitor);
        this.config = config;
        this.project = project;
        if (config.isMapping()) {
            mappingPrinter = new MappingPrinter(new File(project.getBuildDir(), "/outputs/mapping/methodtimer_mapping.txt"));
        }
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        classname = name.replace("/", ".");
        if (config.isMapping()) {
            mappingPrinter.log("[class]" + classname);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, final String name, final String desc, String signature,
                                     String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        mv = new AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {

            private boolean inject = false;

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                if ("Lcom/miqt/pluginlib/annotation/PrintTime;".equals(desc)) {
                    inject = true;
                }
                return super.visitAnnotation(desc, visible);
            }

            @Override
            protected void onMethodEnter() {
                if (isInject()) {
                    String mn = getName(name);
                    mv.visitLdcInsn(mn);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/miqt/pluginlib/tools/TimePrint", "start", "(Ljava/lang/String;)V", false);
                    if (config.isMapping()) {
                        mappingPrinter.log("\t[MethodEnter]" + mn);
                    }
                }
            }

            private String getName(String name) {
                Type[] types = Type.getArgumentTypes(desc);
                Type returnType = Type.getReturnType(desc);
                String type = "";
                for (int i = 0; i < types.length; i++) {
                    type = type.concat(types[i].getClassName());
                    if (i != types.length - 1) {
                        type = type.concat(",");
                    }
                }
                name = classname.concat(".").concat(name).concat("(").concat(type).concat(") type:").concat(returnType.getClassName());
                return name;
            }

            private boolean isInject() {
                if (!config.isEnable()) {
                    return false;
                }
                if (config.isAll()) {
                    return true;
                }
                if (inject) {
                    return true;
                }
                for (String value : config.getClassRegexs()) {
                    if (Pattern.matches(value, classname)) {
                        for (String mv : config.getMethodRegexs()) {
                            if (Pattern.matches(mv, name)) {
                                return true;
                            }
                        }
                    }
                }
//                for (String value : config.getMethodRegexs()) {
//                    if (Pattern.matches(value, name)) {
//                        return true;
//                    }
//                }

                return false;
            }

            @Override
            protected void onMethodExit(int opcode) {
                if (isInject()) {
                    String mn = getName(name);
                    mv.visitLdcInsn(mn);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/miqt/pluginlib/tools/TimePrint", "end", "(Ljava/lang/String;)V", false);
                    if (config.isMapping()) {
                        mappingPrinter.log("\t[MethodExit]" + mn);
                    }
                }
            }
        };
        return mv;
    }
}
