package com.qiao;

import java.lang.instrument.Instrumentation;

public class AgentLauncher {

    public static void premain(String args, Instrumentation inst) {
        main(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        main(args, inst);
    }

    private static void main(final String args, final Instrumentation inst) {
        System.out.println(args);
        System.out.println(inst);
    }

}
