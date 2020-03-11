package com.analysys.plugin.vistor;


import com.analysys.plugin.config.MethodTimerConfig;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * @Copyright 2019 analysys Inc. All rights reserved.
 * @Description: 方法耗时统计插桩器
 * @Version: 1.0
 * @Create: 2019-11-13 11:19:08
 * @author: miqt
 * @mail: miqingtang@analysys.com.cn
 */
public class MethodTimerVisitor extends ClassVisitor {

    String classname;
    MethodTimerConfig config;

    public MethodTimerVisitor(ClassVisitor classVisitor, MethodTimerConfig config) {
        super(Opcodes.ASM5, classVisitor);
        this.config = config;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        classname = name.replace("/", ".");
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
                    mv.visitLdcInsn(getName(name));
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/miqt/pluginlib/tools/TimePrint", "start", "(Ljava/lang/String;)V", false);
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
                return config.isEnable() && (config.isAll() || inject);
            }

            @Override
            protected void onMethodExit(int opcode) {
                if (isInject()) {
                    mv.visitLdcInsn(getName(name));
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/miqt/pluginlib/tools/TimePrint", "end", "(Ljava/lang/String;)V", false);
                }
            }
        };
        return mv;
    }
}
