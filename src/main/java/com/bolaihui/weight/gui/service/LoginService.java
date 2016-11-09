package com.bolaihui.weight.gui.service;

import com.bolaihui.weight.gui.client.ZmdClient;
import com.bolaihui.weight.gui.context.WeightContext;
import com.bolaihui.weight.gui.util.BaseUtil;
import com.bolaihui.weight.gui.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class LoginService {

    private static Logger logger = LoggerFactory.getLogger(LoginService.class);

    private static WeightContext weightContext = WeightContext.getInstance();

    public static void login() {
        SwingUtilities.invokeLater(() -> {
            try {
                String userName = weightContext.getWeightForm().getUserName().getText();
                String passWord = new String(weightContext.getWeightForm().getPassWord().getPassword());
                if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(passWord)) {
                    Map result = ZmdClient.login(userName, passWord);
                    if (Boolean.valueOf(result.get("success").toString())) {
                        // 登陆部分
                        weightContext.getWeightForm().getLoginStatus().setText(Constants.LOGIN_SUCCESS);
                        weightContext.getWeightForm().getLoginStatus().setForeground(Constants.okColor);
                        weightContext.getWeightForm().getLoginName().setText(result.get("realName").toString());
                        weightContext.getWeightForm().getUserName().setEnabled(false);
                        weightContext.getWeightForm().getPassWord().setEnabled(false);
                        // 功能栏部分
                        weightContext.getWeightForm().getExportDate().setEnabled(true);
                        weightContext.getWeightForm().getZmdExportBtn().setEnabled(true);
                        weightContext.getWeightForm().getCnExportBtn().setEnabled(true);
                        weightContext.getWeightForm().getCnInterceptBtn().setEnabled(true);
                        // 模式选择开启
                        weightContext.getWeightForm().getZmdModelBtn().setEnabled(true);
                        weightContext.getWeightForm().getCnModelBtn().setEnabled(true);
                        String roleName = result.get("roleName").toString();
                        // 管理员可以设置误差范围
                        if (StringUtils.contains(roleName, "管理")) {
                            weightContext.getWeightForm().getTabbedPane().setEnabledAt(5, true);
                        }
                        SwingUtilities.invokeLater(() -> {
                            StatusService.leftCount();
                        });
                    } else {
                        // 登陆部分
                        weightContext.getWeightForm().getLoginStatus().setText(Constants.LOGIN_FAILURE);
                        weightContext.getWeightForm().getLoginStatus().setForeground(Color.RED);
                        // 提示
                        BaseUtil.messageDialog("账号或密码错误");
                    }
                }
            } catch (Exception e) {
                String error = BaseUtil.getExceptionStackTrace(e);
                logger.error(error);
                BaseUtil.textAreaErrorDialog("错误", error);
            }
        });
    }
}
