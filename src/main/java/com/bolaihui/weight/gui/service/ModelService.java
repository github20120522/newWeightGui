package com.bolaihui.weight.gui.service;

import com.bolaihui.weight.gui.context.WeightContext;

import javax.swing.*;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class ModelService {

    private static WeightContext weightContext = WeightContext.getInstance();

    public static void choseZmdModel() {
        SwingUtilities.invokeLater(() -> {
            JTabbedPane tabbedPane = weightContext.getWeightForm().getTabbedPane();
            tabbedPane.setEnabledAt(1, true);
            tabbedPane.setEnabledAt(2, true);
            tabbedPane.setEnabledAt(0, false);
            tabbedPane.setSelectedIndex(1);
            weightContext.getWeightForm().getConnectBtn().setEnabled(true);
            // 直接尝试一次电子秤连接
            WeightService.weightStart();
        });
    }

    public static void choseCnModel() {
        SwingUtilities.invokeLater(() -> {
            JTabbedPane tabbedPane = weightContext.getWeightForm().getTabbedPane();
            tabbedPane.setEnabledAt(3, true);
            tabbedPane.setEnabledAt(4, true);
            tabbedPane.setEnabledAt(0, false);
            tabbedPane.setSelectedIndex(3);
            SwingUtilities.invokeLater(() -> {
                weightContext.getWeightForm().getCnWeightEmsNo().requestFocus();
                weightContext.getWeightForm().getConnectBtn().setEnabled(true);
            });
        });
    }
}
