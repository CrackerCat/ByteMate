package com.thewind.bytecode.intellij.def;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class IdeaLicensePatcher {
    public static boolean watcherFlag = true;

    public static void load() {
        trackLog("IdeaLicensePatcher -> load");
    }


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


    public static void setTitle(String title) {
        if (title.equals("Licenses")) {
            return;
        }
    }
}



