package com.bolaihui.weight.gui.main;

import com.bolaihui.weight.gui.form.WeightForm;

import javax.swing.*;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class MainGui {

    public static void main(String[] args) {
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
}
