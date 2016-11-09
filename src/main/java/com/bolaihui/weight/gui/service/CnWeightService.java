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
public class CnWeightService {

    private static final Logger logger = LoggerFactory.getLogger(CnWeightService.class);

    private static WeightContext weightContext = WeightContext.getInstance();

    public static void clear() {
        SwingUtilities.invokeLater(() -> {
            weightContext.getWeightForm().getCnWeightBeginBtn().setEnabled(false);
            weightContext.getWeightForm().getCnWeightEndBtn().setEnabled(true);
            weightContext.getCnWeightYesSet().clear();
            weightContext.getCnWeightNoSet().clear();
            weightContext.getWeightForm().getCnWeightEmsNo().setEnabled(true);
            weightContext.getWeightForm().getCnWeightEmsNo().requestFocus();
            countRefresh();
        });
    }

    public static void generateLocation() {

        try {
            int yesCount = weightContext.getCnWeightYesSet().size();
            int noCount = weightContext.getCnWeightNoSet().size();

            java.util.List<String> allEmsNos = new ArrayList<>();
            allEmsNos.addAll(weightContext.getCnWeightYesSet());
            allEmsNos.addAll(weightContext.getCnWeightNoSet());

            String allEmsNosJson = BaseUtil.toJson(allEmsNos);

            Map result = ZmdClient.generateLocation(yesCount, noCount, allEmsNosJson);
            if (Boolean.valueOf(result.get("success").toString())) {
                SwingUtilities.invokeLater(() -> {
                    // done 生成位置号
                    weightContext.getWeightForm().getCnWeightEndBtn().setEnabled(false);
                    weightContext.getWeightForm().getCnWeightBeginBtn().setEnabled(true);
                    // 打扫数据
                    weightContext.getCnWeightYesSet().clear();
                    weightContext.getCnWeightNoSet().clear();
                    countRefresh();
                    BaseUtil.textAreaDialog("位置号生成", result.get("location").toString());
                });
            } else {
                BaseUtil.messageDialog(result.get("message").toString());
            }
            weightContext.getWeightForm().getCnWeightEmsNo().requestFocus();
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
                String emsNo = weightContext.getWeightForm().getCnWeightEmsNo().getText();
                String emsNoLength = weightContext.getWeightForm().getCnWeightEmsLength().getText();
                String weightValue = weightContext.getWeightForm().getCnWeightValue().getText().replace("kg", "");
                String weightModel = weightContext.getWeightForm().getCnWeightModel().getText();
                String operator = weightContext.getWeightForm().getLoginName().getText();
                String boxNo = weightContext.getWeightForm().getCnWeightBoxNo().getText();
                if (StringUtils.equals(emsNo.length() + "", emsNoLength) &&
                        weightContext.getWeightForm().getCnWeightEndBtn().isEnabled()) {
                    // done 称重
                    if (StringUtils.isBlank(boxNo)) {
                        weightContext.getWeightForm().getCnWeightBoxNo().requestFocus();
                        return;
                    }
                    // done cn称重
                    Map result = ZmdClient.cnWeight(emsNo, boxNo, weightValue, StringUtils.equals(weightModel, Constants.WEIGHT_CHECK_MODEL), operator);
                    // 重复检查
                    if (result.get("dup") != null) {
                        BaseUtil.sound(Constants.DUP_SOUND);
                    }
                    // 结果是否放行，需排除不存在的订单统计（不存在的订单使用error标记）
                    if (Boolean.valueOf(result.get("success").toString())) {
                        BaseUtil.sound(Constants.SUCCESS_SOUND);
                        weightContext.getCnWeightYesSet().add(emsNo);
                        weightContext.getCnWeightNoSet().remove(emsNo);
                        weightContext.getWeightForm().getCnWeightEmsColor().setBackground(Constants.okColor);
                    } else {
                        BaseUtil.sound(Constants.FAILURE_SOUND);
                        if (result.get("error") == null) {
                            weightContext.getCnWeightNoSet().add(emsNo);
                            weightContext.getCnWeightYesSet().remove(emsNo);
                        }
                        weightContext.getWeightForm().getCnWeightEmsColor().setBackground(Color.RED);
                        String message = result.get("message").toString();
                        if (StringUtils.isNotBlank(message)) {
                            if (result.get("dup") != null) {
                                message += "\n\n";
                                message += "重复扫描：" + result.get("dup");
                            }
                            BaseUtil.textAreaDialog("请注意，这个订单有问题", message);
                        }
                    }
                    JLabel cnWeightEmsInfo = weightContext.getWeightForm().getCnWeightEmsInfo();
                    String emsInfo = result.get("emsNo").toString() + " | " + result.get("weightValue").toString() + " | " + result.get("status").toString();
                    cnWeightEmsInfo.setText(emsInfo);
                    countRefresh();
                } else {
                    BaseUtil.sound(Constants.WARNING_SOUND);
                }
                weightContext.getWeightForm().getCnWeightEmsNo().setText("");
                weightContext.getWeightForm().getCnWeightBoxNo().setText("");
                weightContext.getWeightForm().getCnWeightEmsNo().requestFocus();
            } catch (Exception e) {
                String error = BaseUtil.getExceptionStackTrace(e);
                logger.error(error);
                BaseUtil.textAreaErrorDialog("错误", error);
                weightContext.getWeightForm().getCnWeightEmsNo().setText("");
                weightContext.getWeightForm().getCnWeightBoxNo().setText("");
                weightContext.getWeightForm().getCnWeightEmsNo().requestFocus();
            }
        });
    }

    @SuppressWarnings("unchecked")
    /* 启动程序时，如果有数据需要恢复则恢复数据 */
    public static void restoreData() {
        try {
            boolean restoreSuccess = false;
            File cnWeightYesFile = new File("./data/cnWeightYesData.data");
            if (cnWeightYesFile.exists()) {
                String cnWeightYesJson = FileUtils.readFileToString(cnWeightYesFile);
                if (StringUtils.isNotBlank(cnWeightYesJson)) {
                    Set cnWeightYesSet = BaseUtil.parseJson(cnWeightYesJson, Set.class);
                    weightContext.getCnWeightYesSet().addAll(cnWeightYesSet);
                    restoreSuccess = true;
                }
            }
            File cnWeightNoFile = new File("./data/cnWeightNoData.data");
            if (cnWeightNoFile.exists()) {
                String cnWeightNoJson = FileUtils.readFileToString(cnWeightNoFile);
                if (StringUtils.isNotBlank(cnWeightNoJson)) {
                    Set cnWeightNoSet = BaseUtil.parseJson(cnWeightNoJson, Set.class);
                    weightContext.getCnWeightNoSet().addAll(cnWeightNoSet);
                    restoreSuccess = true;
                }
            }
            if (restoreSuccess) {
                SwingUtilities.invokeLater(() -> {
                    weightContext.getWeightForm().getCnWeightEndBtn().setEnabled(true);
                    weightContext.getWeightForm().getCnWeightBeginBtn().setEnabled(false);
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
                File cnWeightYesFile = new File("./data/cnWeightYesData.data");
                if (!cnWeightYesFile.exists()) {
                    FileUtils.touch(cnWeightYesFile);
                }

                File cnWeightNoFile = new File("./data/cnWeightNoData.data");
                if (!cnWeightNoFile.exists()) {
                    FileUtils.touch(cnWeightNoFile);
                }

                // 每次刷新动作都全量更新文件中的数据，因为涉及到数据减少的情况，如果只使用追加文件的方式会不合适
                if (weightContext.getCnWeightYesSet() != null && weightContext.getCnWeightYesSet().size() > 0) {
                    FileUtils.writeStringToFile(cnWeightYesFile, BaseUtil.toJson(weightContext.getCnWeightYesSet()));
                } else {
                    FileUtils.writeStringToFile(cnWeightYesFile, "");
                }

                if (weightContext.getCnWeightNoSet() != null && weightContext.getCnWeightNoSet().size() > 0) {
                    FileUtils.writeStringToFile(cnWeightNoFile, BaseUtil.toJson(weightContext.getCnWeightNoSet()));
                } else {
                    FileUtils.writeStringToFile(cnWeightNoFile, "");
                }

                int cnWeightYesCount = weightContext.getCnWeightYesSet().size();
                int cnWeightNoCount = weightContext.getCnWeightNoSet().size();
                weightContext.getWeightForm().getCnWeightYesCount().setText(cnWeightYesCount + "");
                weightContext.getWeightForm().getCnWeightNoCount().setText(cnWeightNoCount + "");
            } catch (Exception e) {
                logger.error(BaseUtil.getExceptionStackTrace(e));
                BaseUtil.messageDialog("数据文件无法写入");
            }
        });
    }
}
