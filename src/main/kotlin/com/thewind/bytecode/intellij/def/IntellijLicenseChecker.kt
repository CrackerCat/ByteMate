package com.thewind.bytecode.intellij.def


internal const val IDEA_LICENSE_WATCHER = """
{

        System.out.println("**********    util.jar; Addition of WindowWatcher");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean finish = false;

                while (!finish) {
                    try {
                        System.out.println("进入 WindowWatcher 循环...");
                        // 遍历 Windows
                        for (java.awt.Window window : java.awt.Window.getWindows()) {
                            if (window instanceof javax.swing.JDialog dialog) {
                                if ("Licenses".equals(dialog.getTitle())) {

                                    for (java.awt.event.WindowListener windowListener : dialog.getWindowListeners()) {
                                        dialog.removeWindowListener(windowListener);
                                    }

                                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                                        @Override
                                        public void windowClosing(java.awt.event.WindowEvent e) {
                                            dialog.dispose();
                                        }

                                    });

                                    dialog.setTitle("若要继续试用，请点击*右上角*关闭按钮");

                                    // 设置标记变量为 false，表示无需再循环设置此试用代码了
                                    // 后续不用执行
                                    finish = true;
                                    break;
                                }

                            }
                        }
                        Thread.sleep(500L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("**********    util.jar; WindowWatcher Execution Done!");
            }
        };
        new Thread(runnable).start();


    }
            """