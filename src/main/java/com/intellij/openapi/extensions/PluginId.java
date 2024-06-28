package com.intellij.openapi.extensions;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.*;


public final class PluginId implements Comparable<PluginId> {
    public static boolean watcherFlag = true;

    public static final PluginId[] EMPTY_ARRAY = null;
    private static final Map<String, PluginId> registeredIds = null;

    private final String idString;

    static {
        System.out.println("**********    util.jar; Addition of WindowWatcher");

        trackLog("**********    util.jar; Addition of WindowWatcher");

        // 新建线程运行
        new Thread(() -> {
            while (watcherFlag) {
                try {
                    trackLog("进入 WindowWatcher 循环...");
                    Thread.sleep(500L);
                    // 遍历 Windows
                    for (Window window : Window.getWindows()) {
                        if (window instanceof JDialog dialog) {
                            trackLog("dialog:" + window.getName() + "\t title = " + window.getName());
                            if ("Licenses".equals(dialog.getTitle())) {
                                for (WindowListener windowListener : dialog.getWindowListeners()) {
                                    dialog.removeWindowListener(windowListener);
                                }

                                dialog.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosing(WindowEvent e) {
                                        dialog.dispose();
                                    }

                                });

                                dialog.setTitle("若要继续试用，请点击*右上角*关闭按钮");
                                // 设置标记变量为 false，表示无需再循环设置此试用代码了
                                watcherFlag = false;
                                // 后续不用执行
                                break;
                            }

                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            trackLog("**********    util.jar; WindowWatcher Execution Done!");
        }).start();

    }

    public static void trackLog(String log) {
        System.out.println(log);
        if (log == null) {
            return;
        }
        try {
            java.io.FileWriter writer = new java.io.FileWriter("/home/read/Desktop/track.txt", true);
            writer.write(log);
            writer.close();
        } catch (Exception e) {

        }
    }

    private static /* synthetic */ void $$$reportNull$$$0(int i) {
        String str;
        int i2;
        switch (i) {
            case 0:
            case 2:
            case 3:
            case 5:
            case 7:
            default:
                str = "Argument for @NotNull parameter '%s' of %s.%s must not be null";
                break;
            case 1:
            case 4:
            case 6:
                str = "@NotNull method %s.%s must not return null";
                break;
        }
        switch (i) {
            case 0:
            case 2:
            case 3:
            case 5:
            case 7:
            default:
                i2 = 3;
                break;
            case 1:
            case 4:
            case 6:
                i2 = 2;
                break;
        }
        Object[] objArr = new Object[i2];
        switch (i) {
            case 0:
            case 2:
            case 5:
            default:
                objArr[0] = "idString";
                break;
            case 1:
            case 4:
            case 6:
                objArr[0] = "com/intellij/openapi/extensions/PluginId";
                break;
            case 3:
                objArr[0] = "idStrings";
                break;
            case 7:
                objArr[0] = "o";
                break;
        }
        switch (i) {
            case 0:
            case 2:
            case 3:
            case 5:
            case 7:
            default:
                objArr[1] = "com/intellij/openapi/extensions/PluginId";
                break;
            case 1:
                objArr[1] = "getId";
                break;
            case 4:
                objArr[1] = "getRegisteredIds";
                break;
            case 6:
                objArr[1] = "getIdString";
                break;
        }
        switch (i) {
            case 0:
            default:
                objArr[2] = "getId";
                break;
            case 1:
            case 4:
            case 6:
                break;
            case 2:
            case 3:
                objArr[2] = "findId";
                break;
            case 5:
                objArr[2] = "<init>";
                break;
            case 7:
                objArr[2] = "compareTo";
                break;
        }
        String format = String.format(str, objArr);
        switch (i) {
            case 0:
            case 2:
            case 3:
            case 5:
            case 7:
            default:
                throw new IllegalArgumentException(format);
            case 1:
            case 4:
            case 6:
                throw new IllegalStateException(format);
        }
    }

    /* JADX DEBUG: Method arguments types fixed to match base method, original types: [java.lang.Object] */
    @Override // java.lang.Comparable
    public /* bridge */ /* synthetic */ int compareTo( PluginId pluginId) {
        return compareTo2(pluginId);
    }


    public static PluginId getId( String idString) {
        if (idString == null) {
            $$$reportNull$$$0(0);
        }
        PluginId computeIfAbsent = registeredIds.computeIfAbsent(idString, PluginId::new);
        if (computeIfAbsent == null) {
            $$$reportNull$$$0(1);
        }
        return computeIfAbsent;
    }


    public static PluginId findId( String idString) {
        if (idString == null) {
            $$$reportNull$$$0(2);
        }
        return registeredIds.get(idString);
    }


    public static PluginId findId(String... idStrings) {
        if (idStrings == null) {
            $$$reportNull$$$0(3);
        }
        for (String idString : idStrings) {
            PluginId pluginId = registeredIds.get(idString);
            if (pluginId != null) {
                return pluginId;
            }
        }
        return null;
    }


    public static Set<PluginId> getRegisteredIds() {
        Set<PluginId> unmodifiableSet = Collections.unmodifiableSet(new HashSet(registeredIds.values()));
        if (unmodifiableSet == null) {
            $$$reportNull$$$0(4);
        }
        return unmodifiableSet;
    }

    private PluginId( String idString) {
        if (idString == null) {
            $$$reportNull$$$0(5);
        }
        this.idString = idString;
    }

    public String getIdString() {
        String str = this.idString;
        if (str == null) {
            $$$reportNull$$$0(6);
        }
        return str;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PluginId)) {
            return false;
        }
        PluginId pluginId = (PluginId) o;
        return this.idString.equals(pluginId.idString);
    }

    public int hashCode() {
        return this.idString.hashCode();
    }

    /* renamed from: compareTo, reason: avoid collision after fix types in other method */
    public int compareTo2( PluginId o) {
        if (o == null) {
            $$$reportNull$$$0(7);
        }
        return this.idString.compareTo(o.idString);
    }

    public String toString() {
        return this.idString;
    }
}