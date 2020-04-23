package com.analysys.plugin.inject

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class TryCVisitor extends ClassVisitor {

    def className = null;

    TryCVisitor(int i, ClassVisitor classVisitor) {
        super(i, classVisitor)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name.replace("/", ".")
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        Type[] argsTypes = Type.getArgumentTypes(descriptor)
        Type returnType = Type.getReturnType(descriptor)
        boolean isStatic = (access & Opcodes.ACC_STATIC) != 0

        def mv = cv.visitMethod(access, name, descriptor, signature, exceptions)
        mv = new AdviceAdapter(Opcodes.ASM5, mv, access, name, descriptor) {
            @Override
            protected void onMethodEnter() {
                getArgs()
                mv.visitMethodInsn(org.objectweb.asm.Opcodes.INVOKESTATIC, "com/miqt/pluginlib/tools/TimePrint",
                        "enter",
                        "(Ljava/lang/Object;" +
                                "Ljava/lang/String;" +
                                "Ljava/lang/String;" +
                                "Ljava/lang/String;" +
                                "Ljava/lang/String;" +
                                "[Ljava/lang/Object;" +
                                ")V",
                        false);
            }

            @Override
            protected void onMethodExit(int opcode) {
                getArgs()
                mv.visitMethodInsn(org.objectweb.asm.Opcodes.INVOKESTATIC, "com/miqt/pluginlib/tools/TimePrint",
                        "exit",
                        "(Ljava/lang/Object;" +
                                "Ljava/lang/String;" +
                                "Ljava/lang/String;" +
                                "Ljava/lang/String;" +
                                "Ljava/lang/String;" +
                                "[Ljava/lang/Object;" +
                                ")V",
                        false);
            }

            private void getArgs() {
                if (isStatic) {
                    mv.visitInsn(Opcodes.ACONST_NULL);//null
                } else {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);//this
                }
                mv.visitLdcInsn(className);//className
                mv.visitLdcInsn(name);//methodbName
                mv.visitLdcInsn(getArgsType());//argsTypes
                mv.visitLdcInsn(returnType.className);//returntype

                getICONST(argsTypes == null ? 0 : argsTypes.length);
                mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
                for (int i = 0; i < argsTypes.length; i++) {
                    mv.visitInsn(Opcodes.DUP);
                    getICONST(i);
                    getOpCodeLoad(argsTypes[i], isStatic ? (i) : (i + 1));
                    mv.visitInsn(Opcodes.AASTORE);
                }
            }

            private void getICONST(int i) {
                if (i == 0) {
                    mv.visitInsn(Opcodes.ICONST_0);
                } else if (i == 1) {
                    mv.visitInsn(Opcodes.ICONST_1);
                } else if (i == 2) {
                    mv.visitInsn(Opcodes.ICONST_2);
                } else if (i == 3) {
                    mv.visitInsn(Opcodes.ICONST_3);
                } else if (i == 4) {
                    mv.visitInsn(Opcodes.ICONST_4);
                } else if (i == 5) {
                    mv.visitInsn(Opcodes.ICONST_5);
                } else {
                    mv.visitIntInsn(Opcodes.BIPUSH, i);
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

            private String getArgsType() {
                if (argsTypes == null)
                    return "null";

                int iMax = argsTypes.length - 1;
                if (iMax == -1)
                    return "[]";

                StringBuilder b = new StringBuilder();
                b.append('[');
                for (int i = 0; ; i++) {
                    b.append(String.valueOf(argsTypes[i].className));
                    if (i == iMax)
                        return b.append(']').toString();
                    b.append(", ");
                }
            }

        }
    }
}