package com.bolaihui.weight.gui.main;

import com.bolaihui.weight.gui.form.WeightForm;
import com.bolaihui.weight.gui.util.BaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class MainGUI {

    private static final Logger logger = LoggerFactory.getLogger(MainGUI.class);

    public static void main(String[] args) throws IOException {

        runningCheck();

        JFrame frame = new JFrame();
        WeightForm weightForm = new WeightForm();
        frame.setContentPane(weightForm.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocation(200, 50);
        frame.pack();
        frame.setVisible(true);
        frame.setTitle("自贸达New称重程序");
        SwingUtilities.invokeLater(() -> {
            weightForm.getUserName().requestFocus();
            weightForm.getTabbedPane().setEnabledAt(1, false);
            weightForm.getTabbedPane().setEnabledAt(2, false);
            weightForm.getTabbedPane().setEnabledAt(3, false);
            weightForm.getTabbedPane().setEnabledAt(4, false);
            weightForm.getTabbedPane().setEnabledAt(5, false);
        });
    }

    private static void runningCheck() throws IOException {

        Process process = Runtime.getRuntime().exec("cmd.exe /c tasklist");
        try (InputStream inputStream = process.getInputStream()) {
            byte[] allBytes = new byte[0];
            byte[] buf = new byte[1024];
            while (true) {
                byte[] midBytes;
                int len = 0;
                if ((len = inputStream.read(buf)) != -1) {
                    midBytes = new byte[allBytes.length];
                    System.arraycopy(allBytes, 0, midBytes, 0, allBytes.length);
                    allBytes = new byte[midBytes.length + len];
                    System.arraycopy(midBytes, 0, allBytes, 0, midBytes.length);
                    System.arraycopy(buf, 0, allBytes, midBytes.length, len);
                } else {
                    break;
                }
            }
            String taskListStr = new String(allBytes, "gbk");
            String[] taskLines = taskListStr.split("\\n");
            int runningCount = 0;
            for (String task : taskLines) {
                if (task.toLowerCase().contains("weight") || task.contains("称重")) {
                    runningCount++;
                }
            }
            if (runningCount > 1) {
                BaseUtil.messageDialog("已有称重程序在运行，请先关闭其他称重程序");
                System.exit(0);
            }
        } catch (Exception e) {
            logger.error(BaseUtil.getExceptionStackTrace(e));
            e.printStackTrace();
        }
    }
}
