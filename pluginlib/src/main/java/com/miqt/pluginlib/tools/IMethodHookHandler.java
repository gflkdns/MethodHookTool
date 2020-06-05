package com.miqt.pluginlib.tools;

/**
 * @author miqt
 */
public interface IMethodHookHandler {
    /**
     * ��������
     *
     * @param thisObj    �������ڵĶ����������Ϊstatic��������Ϊnull
     * @param className  �������ڵ�����
     * @param methodName ����������
     * @param returnType �����ķ���ֵ����
     * @param args       ������ǰ����ʱ���Ĳ���
     */
    void onMethodEnter(Object thisObj,
                       String className,
                       String methodName,
                       String argsType,
                       String returnType,
                       Object... args
    );

    /**
     * �����˳�
     *
     * @param returnObj  �������صĶ������Ϊvoid���򷵻�null
     * @param thisObj    �������ڵĶ����������Ϊstatic��������Ϊnull
     * @param className  �������ڵ�����
     * @param methodName ����������
     * @param returnType �����ķ���ֵ����
     * @param args       ������ǰ����ʱ���Ĳ���
     */
    void onMethodReturn(Object returnObj,
                        Object thisObj,
                        String className,
                        String methodName,
                        String argsType,
                        String returnType,
                        Object... args);
}
