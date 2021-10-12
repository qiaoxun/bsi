package com.qiao;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class BSILauncher {

    public BSILauncher(String[] args) throws Exception {
        Configure configure = analyzeConfigure(args);
        attachAgent(configure);
    }

    public static void main(String[] args) throws Exception {
        BSILauncher bsiLauncher = new BSILauncher(args);
    }

    /*
     * Configure
     */
    private Configure analyzeConfigure(String[] args) {
        final OptionParser parser = new OptionParser();
        parser.accepts("pid").withRequiredArg().ofType(int.class).required();
        parser.accepts("target").withOptionalArg().ofType(String.class);
        parser.accepts("multi").withOptionalArg().ofType(int.class);
        parser.accepts("core").withOptionalArg().ofType(String.class);
        parser.accepts("agent").withOptionalArg().ofType(String.class);

        final OptionSet os = parser.parse(args);
        final Configure configure = new Configure();

        if (os.has("target")) {
            final String[] strSplit = ((String) os.valueOf("target")).split(":");
            configure.setTargetIp(strSplit[0]);
            configure.setTargetPort(Integer.valueOf(strSplit[1]));
        }

        configure.setJavaPid((Integer) os.valueOf("pid"));
        configure.setBsiAgent((String) os.valueOf("agent"));
        configure.setBsiCore((String) os.valueOf("core"));

        return configure;
    }

    private void attachAgent(Configure configure) throws Exception {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Class<?> vmClass = loader.loadClass("com.sun.tools.attach.VirtualMachine");

        Object vmObj = null;
        try {
            vmObj = vmClass.getMethod("attach", String.class).invoke(null, "" + configure.getJavaPid());
            vmClass.getMethod("loadAgent", String.class, String.class).invoke(vmObj, configure.getBsiAgent(), configure.getBsiCore() + ";" + configure.toString());
        } finally {
            if (null != vmObj) {
                vmClass.getMethod("detach", (Class<?>[]) null).invoke(vmObj, (Object[]) null);
            }
        }
    }

}
