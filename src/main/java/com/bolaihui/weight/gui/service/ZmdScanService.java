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
public class ZmdScanService {

    private static final Logger logger = LoggerFactory.getLogger(ZmdScanService.class);

    private static WeightContext weightContext = WeightContext.getInstance();

    public static void generateLocation() {

        try {
            int yesCount = weightContext.getZmdScanYesSet().size();
            int noCount = weightContext.getZmdScanNoSet().size();

            java.util.List<String> allEmsNos = new ArrayList<>();
            allEmsNos.addAll(weightContext.getZmdScanYesSet());
            allEmsNos.addAll(weightContext.getZmdScanNoSet());

            String allEmsNosJson = BaseUtil.toJson(allEmsNos);

            Map result = ZmdClient.generateLocation(yesCount, noCount, allEmsNosJson);
            if (Boolean.valueOf(result.get("success").toString())) {
                SwingUtilities.invokeLater(() -> {
                    // done 生成位置号
                    weightContext.getWeightForm().getZmdScanEndBtn().setEnabled(false);
                    weightContext.getWeightForm().getZmdScanBeginBtn().setEnabled(true);
                    // 打扫数据
                    weightContext.getZmdScanYesSet().clear();
                    weightContext.getZmdScanNoSet().clear();
                    countRefresh();
                    BaseUtil.textAreaDialog("位置号生成", result.get("location").toString());
                });
            } else {
                BaseUtil.messageDialog(result.get("message").toString());
            }
            weightContext.getWeightForm().getZmdScanEmsNo().requestFocus();
        } catch (JsonProcessingException e) {
            logger.error(BaseUtil.getExceptionStackTrace(e));
            BaseUtil.messageDialog("Json序列化错误");
        } catch (Exception e) {
            logger.error(BaseUtil.getExceptionStackTrace(e));
            BaseUtil.messageDialog("网络错误");
        }
    }

    public static void doScan() {
        SwingUtilities.invokeLater(() -> {
            try {
                String emsNo = weightContext.getWeightForm().getZmdScanEmsNo().getText();
                String emsNoLength = weightContext.getWeightForm().getZmdScanEmsLength().getText();
                String operator = weightContext.getWeightForm().getLoginName().getText();
                if (StringUtils.equals(emsNo.length() + "", emsNoLength) &&
                        weightContext.getWeightForm().getZmdScanEndBtn().isEnabled()) {
                    // done 扫描请求
                    Map result = ZmdClient.zmdScan(emsNo, operator);
                    // 重复检查
                    if (result.get("dup") != null) {
                        BaseUtil.sound(Constants.DUP_SOUND);
                    }
                    if (Boolean.valueOf(result.get("success").toString())) {
                        BaseUtil.sound(Constants.SUCCESS_SOUND);
                        weightContext.getZmdScanYesSet().add(emsNo);
                        weightContext.getZmdScanNoSet().remove(emsNo);
                        weightContext.getWeightForm().getZmdScanEmsColor().setBackground(Constants.okColor);
                    } else {
                        BaseUtil.sound(Constants.FAILURE_SOUND);
                        if (result.get("error") == null) {
                            weightContext.getZmdScanNoSet().add(emsNo);
                            weightContext.getZmdScanYesSet().remove(emsNo);
                        }
                        weightContext.getWeightForm().getZmdScanEmsColor().setBackground(Color.RED);
                        String message = result.get("message").toString();
                        if (StringUtils.isNotBlank(message)) {
                            if (result.get("dup") != null) {
                                message += "\n\n";
                                message += "重复扫描：" + result.get("dup");
                            }
                            BaseUtil.textAreaDialog("请注意，这个订单有问题", message);
                        }
                    }
                    JLabel zmdScanEmsInfo = weightContext.getWeightForm().getZmdScanEmsInfo();
                    String emsInfo = emsNo + " | " + result.get("status").toString();
                    zmdScanEmsInfo.setText(emsInfo);
                    countRefresh();
                } else {
                    BaseUtil.sound(Constants.WARNING_SOUND);
                }
                weightContext.getWeightForm().getZmdScanEmsNo().setText("");
                weightContext.getWeightForm().getZmdScanEmsNo().requestFocus();
            } catch (Exception e) {
                logger.error(BaseUtil.getExceptionStackTrace(e));
                e.printStackTrace();
                BaseUtil.messageDialog("网络错误");
                weightContext.getWeightForm().getZmdScanEmsNo().setText("");
                weightContext.getWeightForm().getZmdScanEmsNo().requestFocus();
            }
        });
    }

    public static void clear() {
        SwingUtilities.invokeLater(() -> {
            weightContext.getWeightForm().getZmdScanBeginBtn().setEnabled(false);
            weightContext.getWeightForm().getZmdScanEndBtn().setEnabled(true);
            weightContext.getZmdScanYesSet().clear();
            weightContext.getZmdScanNoSet().clear();
            weightContext.getWeightForm().getZmdScanEmsNo().setEnabled(true);
            weightContext.getWeightForm().getZmdScanEmsNo().requestFocus();
            countRefresh();
        });
    }

    @SuppressWarnings("unchecked")
    /* 启动程序时，如果有数据需要恢复则恢复数据 */
    public static void restoreData() {
        try {
            boolean restoreSuccess = false;
            File zmdScanYesFile = new File("./data/zmdScanYesData.data");
            if (zmdScanYesFile.exists()) {
                String zmdScanYesJson = FileUtils.readFileToString(zmdScanYesFile);
                if (StringUtils.isNotBlank(zmdScanYesJson)) {
                    Set zmdScanYesSet = BaseUtil.parseJson(zmdScanYesJson, Set.class);
                    weightContext.getZmdScanYesSet().addAll(zmdScanYesSet);
                    restoreSuccess = true;
                }
            }
            File zmdScanNoFile = new File("./data/zmdScanNoData.data");
            if (zmdScanNoFile.exists()) {
                String zmdScanNoJson = FileUtils.readFileToString(zmdScanNoFile);
                Set zmdScanNoSet = BaseUtil.parseJson(zmdScanNoJson, Set.class);
                weightContext.getZmdScanNoSet().addAll(zmdScanNoSet);
                restoreSuccess = true;
            }
            if (restoreSuccess) {
                SwingUtilities.invokeLater(() -> {
                    weightContext.getWeightForm().getZmdScanEndBtn().setEnabled(true);
                    weightContext.getWeightForm().getZmdScanBeginBtn().setEnabled(false);
                    countRefresh();
                });
            }
        } catch (Exception e) {
            // swallow
        }
    }

    private static void countRefresh() {
        SwingUtilities.invokeLater(() -> {
            // done 需要增加本地文件记录，以确保意外造成的关闭在下次启动时可以恢复
            try {
                File zmdScanYesFile = new File("./data/zmdScanYesData.data");
                if (!zmdScanYesFile.exists()) {
                    FileUtils.touch(zmdScanYesFile);
                }

                File zmdScanNoFile = new File("./data/zmdScanNoData.data");
                if (!zmdScanNoFile.exists()) {
                    FileUtils.touch(zmdScanNoFile);
                }

                // 每次刷新动作都全量更新文件中的数据，因为涉及到数据减少的情况，如果只使用追加文件的方式会不合适
                if (weightContext.getZmdScanYesSet() != null && weightContext.getZmdScanYesSet().size() > 0) {
                    FileUtils.writeStringToFile(zmdScanYesFile, BaseUtil.toJson(weightContext.getZmdScanYesSet()));
                } else {
                    FileUtils.writeStringToFile(zmdScanYesFile, "");
                }

                if (weightContext.getZmdScanNoSet() != null && weightContext.getZmdScanNoSet().size() > 0) {
                    FileUtils.writeStringToFile(zmdScanNoFile, BaseUtil.toJson(weightContext.getZmdScanNoSet()));
                } else {
                    FileUtils.writeStringToFile(zmdScanNoFile, "");
                }

                int zmdScanYesCount = weightContext.getZmdScanYesSet().size();
                int zmdScanNoCount = weightContext.getZmdScanNoSet().size();
                weightContext.getWeightForm().getZmdScanYesCount().setText(zmdScanYesCount + "");
                weightContext.getWeightForm().getZmdScanNoCount().setText(zmdScanNoCount + "");
            } catch (Exception e) {
                logger.error(BaseUtil.getExceptionStackTrace(e));
                BaseUtil.messageDialog("数据文件无法写入");
            }
        });
    }
}
