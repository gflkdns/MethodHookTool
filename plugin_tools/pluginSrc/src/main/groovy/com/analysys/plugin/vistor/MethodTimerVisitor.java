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

        final Type[] argsTypes = Type.getArgumentTypes(desc);
        final Type returnType = Type.getReturnType(desc);
        final boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;

        final MethodVisitor innerMv = cv.visitMethod(access, name, desc, signature, exceptions);
        final MethodVisitor remv = new AdviceAdapter(Opcodes.ASM5, innerMv, access, name, desc) {

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
                    getArgs();
                    innerMv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/miqt/pluginlib/tools/TimePrint",
                            "enter",
                            "(Ljava/lang/Object;" +
                                    "Ljava/lang/String;" +
                                    "Ljava/lang/String;" +
                                    "Ljava/lang/String;" +
                                    "Ljava/lang/String;" +
                                    "[Ljava/lang/Object;)V",
                            false);
                    if (config.isMapping()) {
                        mappingPrinter.log("\t[MethodEnter]" + classname + name);
                    }
                }
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
                    getArgs();
                    innerMv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/miqt/pluginlib/tools/TimePrint",
                            "exit",
                            "(Ljava/lang/Object;" +
                                    "Ljava/lang/String;" +
                                    "Ljava/lang/String;" +
                                    "Ljava/lang/String;" +
                                    "Ljava/lang/String;" +
                                    "[Ljava/lang/Object;)V",
                            false);
                    if (config.isMapping()) {
                        mappingPrinter.log("\t[MethodExit]" + classname + name);
                    }
                }
            }

            private void getArgs() {
                if (isStatic) {
                    mv.visitInsn(Opcodes.ACONST_NULL);//null
                } else {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);//this
                }
                mv.visitLdcInsn(classname);//className
                mv.visitLdcInsn(name);//methodbName
                mv.visitLdcInsn(getArgsType());//argsTypes
                mv.visitLdcInsn(returnType.getClassName());//returntype

                getICONST(argsTypes == null ? 0 : argsTypes.length);
                mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
                for (int i = 0; i < argsTypes.length; i++) {
                    mv.visitInsn(Opcodes.DUP);
                    getICONST(i);
                    getOpCodeLoad(argsTypes[i], isStatic ? (i) : (i + 1));
                    mv.visitInsn(Opcodes.AASTORE);
                }

            }

            private void getOpCodeLoad(Type type, int argIndex) {
                if (type.equals(Type.INT_TYPE)) {
                    mv.visitVarInsn(ILOAD, argIndex);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                    return;
                }
                if (type.equals(Type.BOOLEAN_TYPE)) {
                    mv.visitVarInsn(ILOAD, argIndex);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                    return;
                }
                if (type.equals(Type.CHAR_TYPE)) {
                    mv.visitVarInsn(ILOAD, argIndex);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                    return;
                }
                if (type.equals(Type.SHORT_TYPE)) {
                    mv.visitVarInsn(ILOAD, argIndex);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                    return;
                }
                if (type.equals(Type.BYTE_TYPE)) {
                    mv.visitVarInsn(ILOAD, argIndex);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                    return;
                }

                if (type.equals(Type.LONG_TYPE)) {
                    mv.visitVarInsn(LLOAD, argIndex);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                    return;
                }
                if (type.equals(Type.FLOAT_TYPE)) {
                    mv.visitVarInsn(FLOAD, argIndex);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                    return;
                }
                if (type.equals(Type.DOUBLE_TYPE)) {
                    mv.visitVarInsn(DLOAD, argIndex);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                    return;
                }
                mv.visitVarInsn(ALOAD, argIndex);
            }

            private void getICONST(int i) {
                if (i == 0) {
                    innerMv.visitInsn(Opcodes.ICONST_0);
                } else if (i == 1) {
                    innerMv.visitInsn(Opcodes.ICONST_1);
                } else if (i == 2) {
                    innerMv.visitInsn(Opcodes.ICONST_2);
                } else if (i == 3) {
                    innerMv.visitInsn(Opcodes.ICONST_3);
                } else if (i == 4) {
                    innerMv.visitInsn(Opcodes.ICONST_4);
                } else if (i == 5) {
                    innerMv.visitInsn(Opcodes.ICONST_5);
                } else {
                    innerMv.visitIntInsn(Opcodes.BIPUSH, i);
                }
            }

            private String getArgsType() {
                if (argsTypes == null) {
                    return "null";
                }

                int iMax = argsTypes.length - 1;
                if (iMax == -1) {
                    return "[]";
                }

                StringBuilder b = new StringBuilder();
                b.append('[');
                for (int i = 0; ; i++) {
                    b.append(argsTypes[i].getClassName());
                    if (i == iMax) {
                        return b.append(']').toString();
                    }
                    b.append(", ");
                }
            }
        };
        return remv;
    }
}
