package com.bolaihui.weight.gui.service;

import com.bolaihui.weight.gui.client.ZmdClient;
import com.bolaihui.weight.gui.context.WeightContext;
import com.bolaihui.weight.gui.util.BaseUtil;
import com.bolaihui.weight.gui.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class ZmdWeightService {

    private static final Logger logger = LoggerFactory.getLogger(ZmdWeightService.class);

    private static WeightContext weightContext = WeightContext.getInstance();

    public static void boxOpen() {
        SwingUtilities.invokeLater(() -> {
            // 重量检测手动开启，不通过开启箱码扫描开启
            // weightContext.getWeightForm().getZmdWeightModel().setText(Constants.WEIGHT_CHECK_MODEL);
            // weightContext.getWeightForm().getZmdWeightModel().setForeground(Constants.okColor);
            weightContext.getWeightForm().getZmdWeightBoxNo().setEnabled(true);
            weightContext.getWeightForm().getZmdWeightBoxOpenBtn().setEnabled(false);
            weightContext.getWeightForm().getZmdWeightBoxCloseBtn().setEnabled(true);
            weightContext.getWeightForm().getZmdWeightEmsNo().requestFocus();
        });
    }

    public static void boxClose() {
        SwingUtilities.invokeLater(() -> {
            weightContext.getWeightForm().getZmdWeightModel().setText(Constants.WEIGHT_UN_CHECK_MODEL);
            weightContext.getWeightForm().getZmdWeightModel().setForeground(Color.BLUE);
            weightContext.getWeightForm().getZmdWeightBoxNo().setEnabled(false);
            weightContext.getWeightForm().getZmdWeightBoxOpenBtn().setEnabled(true);
            weightContext.getWeightForm().getZmdWeightBoxCloseBtn().setEnabled(false);
            weightContext.getWeightForm().getZmdWeightEmsNo().requestFocus();
        });
    }

    public static void clear() {
        SwingUtilities.invokeLater(() -> {
            weightContext.getWeightForm().getZmdWeightBeginBtn().setEnabled(false);
            weightContext.getWeightForm().getZmdWeightEndBtn().setEnabled(true);
            weightContext.getZmdWeightYesSet().clear();
            weightContext.getZmdWeightNoSet().clear();
            weightContext.getWeightForm().getZmdWeightEmsNo().setEnabled(true);
            weightContext.getWeightForm().getZmdWeightBoxOpenBtn().setEnabled(true);
            weightContext.getWeightForm().getZmdWeightEmsNo().requestFocus();
            countRefresh();
        });
    }

    public static void generateLocation() {

        try {
            int yesCount = weightContext.getZmdWeightYesSet().size();
            int noCount = weightContext.getZmdWeightNoSet().size();

            java.util.List<String> allEmsNos = new ArrayList<>();
            allEmsNos.addAll(weightContext.getZmdWeightYesSet());
            allEmsNos.addAll(weightContext.getZmdWeightNoSet());

            String allEmsNosJson = BaseUtil.toJson(allEmsNos);

            Map result = ZmdClient.generateLocation(yesCount, noCount, allEmsNosJson);
            if (Boolean.valueOf(result.get("success").toString())) {
                SwingUtilities.invokeLater(() -> {
                    // done 生成位置号
                    weightContext.getWeightForm().getZmdWeightEndBtn().setEnabled(false);
                    // 只有当称重连接成功时才可以将开始点亮
                    if (StringUtils.equals(weightContext.getWeightForm().getConnectStatus().getText(), Constants.CONNECT_SUCCESS)) {
                        weightContext.getWeightForm().getZmdWeightBeginBtn().setEnabled(true);
                    }
                    // 打扫数据
                    weightContext.getZmdWeightYesSet().clear();
                    weightContext.getZmdWeightNoSet().clear();
                    countRefresh();
                    BaseUtil.textAreaDialog("位置号生成", result.get("location").toString());
                });
            } else {
                BaseUtil.messageDialog(result.get("message").toString());
            }
            weightContext.getWeightForm().getZmdWeightEmsNo().requestFocus();
        } catch (JsonProcessingException e) {
            String error = BaseUtil.getExceptionStackTrace(e);
            logger.error(error);
            BaseUtil.textAreaErrorDialog("错误", error);
        } catch (Exception e) {
            String error = BaseUtil.getExceptionStackTrace(e);
            logger.error(error);
            BaseUtil.textAreaErrorDialog("错误", error);
        }
    }

    public static void doWeight() {
        SwingUtilities.invokeLater(() -> {
            try {
                String emsNo = weightContext.getWeightForm().getZmdWeightEmsNo().getText();
                String emsNoLength = weightContext.getWeightForm().getZmdWeightEmsLength().getText();
                String weightValue = weightContext.getWeightForm().getZmdWeightValue().getText().replace("kg", "");
                String weightModel = weightContext.getWeightForm().getZmdWeightModel().getText();
                String operator = weightContext.getWeightForm().getLoginName().getText();
                String boxNo = "0";
                if (StringUtils.equals(emsNo.length() + "", emsNoLength) &&
                        weightContext.getWeightForm().getZmdWeightEndBtn().isEnabled()) {
                    // done 称重
                    if (weightContext.getWeightForm().getZmdWeightBoxNo().isEnabled()) {
                        if (StringUtils.isBlank(weightContext.getWeightForm().getZmdWeightBoxNo().getText())) {
                            weightContext.getWeightForm().getZmdWeightBoxNo().requestFocus();
                            return;
                        } else {
                            boxNo = weightContext.getWeightForm().getZmdWeightBoxNo().getText();
                        }
                    }
                    // done zmd称重
                    String finalBoxNo = boxNo;
                    Map result = ZmdClient.zmdWeight(emsNo, finalBoxNo, weightValue, StringUtils.equals(weightModel, Constants.WEIGHT_CHECK_MODEL), operator);
                    // 重复检查
                    if (result.get("dup") != null) {
                        BaseUtil.sound(Constants.DUP_SOUND);
                    }
                    // 结果是否放行，需排除不存在的订单统计（不存在的订单使用error标记）
                    if (Boolean.valueOf(result.get("success").toString())) {
                        BaseUtil.sound(Constants.SUCCESS_SOUND);
                        weightContext.getZmdWeightYesSet().add(emsNo);
                        weightContext.getZmdWeightNoSet().remove(emsNo);
                        weightContext.getWeightForm().getZmdWeightEmsColor().setBackground(Constants.okColor);
                    } else {
                        BaseUtil.sound(Constants.FAILURE_SOUND);
                        if (result.get("error") == null) {
                            weightContext.getZmdWeightNoSet().add(emsNo);
                            weightContext.getZmdWeightYesSet().remove(emsNo);
                        }
                        weightContext.getWeightForm().getZmdWeightEmsColor().setBackground(Color.RED);
                    }
                    if (result.get("message") != null) {
                        String message = result.get("message").toString();
                        if (result.get("dup") != null) {
                            message += "\n\n";
                            message += "重复扫描：" + result.get("dup");
                        }
                        BaseUtil.textAreaDialog("请注意，这个订单有问题", message);
                    }
                    JLabel zmdWeightEmsInfo = weightContext.getWeightForm().getZmdWeightEmsInfo();
                    String emsInfo = result.get("emsNo").toString() + " | " + result.get("weightValue").toString() + " | " + result.get("status").toString();
                    zmdWeightEmsInfo.setText(emsInfo);
                    countRefresh();
                } else {
                    BaseUtil.sound(Constants.WARNING_SOUND);
                }
                weightContext.getWeightForm().getZmdWeightEmsNo().setText("");
                weightContext.getWeightForm().getZmdWeightBoxNo().setText("");
                weightContext.getWeightForm().getZmdWeightEmsNo().requestFocus();
            } catch (Exception e) {
                String error = BaseUtil.getExceptionStackTrace(e);
                logger.error(error);
                BaseUtil.textAreaErrorDialog("错误", error);
                weightContext.getWeightForm().getZmdWeightEmsNo().setText("");
                weightContext.getWeightForm().getZmdWeightBoxNo().setText("");
                weightContext.getWeightForm().getZmdWeightEmsNo().requestFocus();
            }
        });
    }

    @SuppressWarnings("unchecked")
    /* 启动程序时，如果有数据需要恢复则恢复数据 */
    public static void restoreData() {
        try {
            boolean restoreSuccess = false;
            File zmdWeightYesFile = new File("./data/zmdWeightYesData.data");
            if (zmdWeightYesFile.exists()) {
                String zmdWeightYesJson = FileUtils.readFileToString(zmdWeightYesFile);
                if (StringUtils.isNotBlank(zmdWeightYesJson)) {
                    Set zmdWeightYesSet = BaseUtil.parseJson(zmdWeightYesJson, Set.class);
                    weightContext.getZmdWeightYesSet().addAll(zmdWeightYesSet);
                    restoreSuccess = true;
                }
            }
            File zmdWeightNoFile = new File("./data/zmdWeightNoData.data");
            if (zmdWeightNoFile.exists()) {
                String zmdWeightNoJson = FileUtils.readFileToString(zmdWeightNoFile);
                if (StringUtils.isNotBlank(zmdWeightNoJson)) {
                    Set zmdWeightNoSet = BaseUtil.parseJson(zmdWeightNoJson, Set.class);
                    weightContext.getZmdWeightNoSet().addAll(zmdWeightNoSet);
                    restoreSuccess = true;
                }
            }
            if (restoreSuccess) {
                SwingUtilities.invokeLater(() -> {
                    weightContext.getWeightForm().getZmdWeightEndBtn().setEnabled(true);
                    countRefresh();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BaseUtil.getExceptionStackTrace(e));
        }
    }

    private static void countRefresh() {
        SwingUtilities.invokeLater(() -> {
            // done 需要增加本地文件记录，以确保意外造成的关闭在下次启动时可以恢复
            try {
                File zmdWeightYesFile = new File("./data/zmdWeightYesData.data");
                if (!zmdWeightYesFile.exists()) {
                    FileUtils.touch(zmdWeightYesFile);
                }

                File zmdWeightNoFile = new File("./data/zmdWeightNoData.data");
                if (!zmdWeightNoFile.exists()) {
                    FileUtils.touch(zmdWeightNoFile);
                }

                // 每次刷新动作都全量更新文件中的数据，因为涉及到数据减少的情况，如果只使用追加文件的方式会不合适
                if (weightContext.getZmdWeightYesSet() != null && weightContext.getZmdWeightYesSet().size() > 0) {
                    FileUtils.writeStringToFile(zmdWeightYesFile, BaseUtil.toJson(weightContext.getZmdWeightYesSet()));
                } else {
                    FileUtils.writeStringToFile(zmdWeightYesFile, "");
                }

                if (weightContext.getZmdWeightNoSet() != null && weightContext.getZmdWeightNoSet().size() > 0) {
                    FileUtils.writeStringToFile(zmdWeightNoFile, BaseUtil.toJson(weightContext.getZmdWeightNoSet()));
                } else {
                    FileUtils.writeStringToFile(zmdWeightNoFile, "");
                }

                int zmdWeightYesCount = weightContext.getZmdWeightYesSet().size();
                int zmdWeightNoCount = weightContext.getZmdWeightNoSet().size();
                weightContext.getWeightForm().getZmdWeightYesCount().setText(zmdWeightYesCount + "");
                weightContext.getWeightForm().getZmdWeightNoCount().setText(zmdWeightNoCount + "");
            } catch (Exception e) {
                logger.error(BaseUtil.getExceptionStackTrace(e));
                BaseUtil.messageDialog("数据文件无法写入");
            }
        });
    }
}
