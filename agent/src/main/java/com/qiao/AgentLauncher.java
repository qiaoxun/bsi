package com.qiao;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

public class AgentLauncher {

    public static void premain(String args, Instrumentation inst) throws Exception {
        main(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        main(args, inst);
    }

    private static void main(final String args, final Instrumentation inst) throws Exception {
        System.out.println("args = " + args);
        System.out.println("inst = " + inst);
        Class bsiServerClass = Class.forName("com.qiao.server.BSIServer");
        Method method = bsiServerClass.getMethod("toString");
        method.invoke(bsiServerClass);
    }

}
