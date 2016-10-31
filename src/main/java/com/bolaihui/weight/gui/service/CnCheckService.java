package com.bolaihui.weight.gui.service;

import com.bolaihui.weight.gui.client.ZmdClient;
import com.bolaihui.weight.gui.context.WeightContext;
import com.bolaihui.weight.gui.util.BaseUtil;
import com.bolaihui.weight.gui.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class CnCheckService {

    private static WeightContext weightContext = WeightContext.getInstance();

    private static Logger logger = LoggerFactory.getLogger(CnCheckService.class);

    public static void clear() {
        SwingUtilities.invokeLater(() -> {
            weightContext.getWeightForm().getCnCheckBeginBtn().setEnabled(false);
            weightContext.getWeightForm().getCnCheckEndBtn().setEnabled(true);
            weightContext.getCnCheckYesSet().clear();
            weightContext.getCnCheckNoSet().clear();
            weightContext.getWeightForm().getCnCheckEmsNo().setEnabled(true);
            weightContext.getWeightForm().getCnCheckEmsNo().requestFocus();
            countRefresh();
        });
    }

    public static void doCheck() {
        SwingUtilities.invokeLater(() -> {
            try {
                String emsNo = weightContext.getWeightForm().getCnCheckEmsNo().getText();
                String emsNoLength = weightContext.getWeightForm().getCnCheckEmsLength().getText();
                if (StringUtils.equals(emsNo.length() + "", emsNoLength) &&
                        weightContext.getWeightForm().getCnCheckEndBtn().isEnabled()) {
                    Map result = ZmdClient.cnCheck(emsNo);
                    if (Boolean.valueOf(result.get("success").toString())) {
                        BaseUtil.sound(Constants.SUCCESS_SOUND);
                        weightContext.getCnCheckYesSet().add(emsNo);
                        weightContext.getCnCheckNoSet().remove(emsNo);
                        weightContext.getWeightForm().getCnCheckEmsColor().setBackground(Constants.okColor);
                        weightContext.getWeightForm().getCnCheckEmsInfo().setText(emsNo + " | " + "已清关");
                    } else {
                        BaseUtil.sound(Constants.FAILURE_SOUND);
                        weightContext.getCnCheckNoSet().add(emsNo);
                        weightContext.getCnCheckYesSet().remove(emsNo);
                        weightContext.getWeightForm().getCnCheckEmsColor().setBackground(Color.RED);
                        weightContext.getWeightForm().getCnCheckEmsInfo().setText(emsNo + " | " + "未清关");
                    }
                    countRefresh();
                } else {
                    BaseUtil.sound(Constants.WARNING_SOUND);
                }
                weightContext.getWeightForm().getCnCheckEmsNo().setText("");
                weightContext.getWeightForm().getCnCheckEmsNo().requestFocus();
            } catch (Exception e) {
                logger.error(BaseUtil.getExceptionStackTrace(e));
                e.printStackTrace();
                BaseUtil.messageDialog("网络错误");
            }
        });
    }

    public static void cnCheckExport() {

        try {
            String yesEmsNosJson = BaseUtil.toJson(weightContext.getCnCheckYesSet());
            Map result = ZmdClient.cnCheckExport(yesEmsNosJson);
            if (Boolean.valueOf(result.get("success").toString())) {
                // done 获取返回数据生成excel
                String data = result.get("data").toString();
                if (StringUtils.isNotBlank(data)) {
                    java.util.List<Map<String, Object>> list = BaseUtil.parseJson(data, java.util.List.class);
                    FileOutputStream fos = null;
                    try {
                        Date today = new Date();
                        File cnCheckExportFile = new File("./export/菜鸟清关检查/" + BaseUtil.ymDateFormat(today) + "/" + BaseUtil.ymdDateFormat(today) + ".xls");
                        FileUtils.touch(cnCheckExportFile);
                        HSSFWorkbook wb = new HSSFWorkbook();
                        HSSFSheet sheet = wb.createSheet("菜鸟清关数据");
                        String[] headers = new String[]{"运单号", "订单号", "物流公司", "报关批次", "账册"};
                        String[] keys = new String[]{"emsNo", "orderNumber", "emsCom", "batchNumbers", "accountBook"};
                        BaseUtil.excelDataFill(sheet, headers, keys, list);
                        fos = new FileOutputStream(cnCheckExportFile);
                        wb.write(fos);
                        BaseUtil.sound(Constants.DOWNLOAD_SOUND);
                        BaseUtil.messageDialog("菜鸟清关数据！已下载到：" + cnCheckExportFile.getAbsolutePath());
                    } catch (Exception e) {
                        logger.error(BaseUtil.getExceptionStackTrace(e));
                        BaseUtil.messageDialog(e.toString());
                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                weightContext.getCnCheckYesSet().clear();
                weightContext.getCnCheckNoSet().clear();
                weightContext.getWeightForm().getCnCheckBeginBtn().setEnabled(true);
                weightContext.getWeightForm().getCnCheckEndBtn().setEnabled(false);
                countRefresh();
            } else {
                BaseUtil.messageDialog(result.get("message").toString());
            }
            weightContext.getWeightForm().getCnCheckEmsNo().requestFocus();
        } catch (JsonProcessingException e) {
            logger.error(BaseUtil.getExceptionStackTrace(e));
            BaseUtil.messageDialog("Json序列化错误");
        } catch (Exception e) {
            logger.error(BaseUtil.getExceptionStackTrace(e));
            BaseUtil.messageDialog("网络错误");
        }
    }

    @SuppressWarnings("unchecked")
    /* 启动程序时，如果有数据需要恢复则恢复数据 */
    public static void restoreData() {
        try {
            boolean restoreSuccess = false;
            File cnCheckYesFile = new File("./data/cnCheckYesData.data");
            if (cnCheckYesFile.exists()) {
                String cnCheckYesJson = FileUtils.readFileToString(cnCheckYesFile);
                if (StringUtils.isNotBlank(cnCheckYesJson)) {
                    Set cnCheckYesSet = BaseUtil.parseJson(cnCheckYesJson, Set.class);
                    weightContext.getCnCheckYesSet().addAll(cnCheckYesSet);
                    restoreSuccess = true;
                }
            }
            File cnCheckNoFile = new File("./data/cnCheckNoData.data");
            if (cnCheckNoFile.exists()) {
                String cnCheckNoJson = FileUtils.readFileToString(cnCheckNoFile);
                Set cnCheckNoSet = BaseUtil.parseJson(cnCheckNoJson, Set.class);
                weightContext.getCnCheckNoSet().addAll(cnCheckNoSet);
                restoreSuccess = true;
            }
            if (restoreSuccess) {
                SwingUtilities.invokeLater(() -> {
                    weightContext.getWeightForm().getCnCheckEndBtn().setEnabled(true);
                    weightContext.getWeightForm().getCnCheckBeginBtn().setEnabled(false);
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
                File cnCheckYesFile = new File("./data/cnCheckYesData.data");
                if (!cnCheckYesFile.exists()) {
                    FileUtils.touch(cnCheckYesFile);
                }

                File cnCheckNoFile = new File("./data/cnCheckNoData.data");
                if (!cnCheckNoFile.exists()) {
                    FileUtils.touch(cnCheckNoFile);
                }

                // 每次刷新动作都全量更新文件中的数据，因为涉及到数据减少的情况，如果只使用追加文件的方式会不合适
                if (weightContext.getCnCheckYesSet() != null && weightContext.getCnCheckYesSet().size() > 0) {
                    FileUtils.writeStringToFile(cnCheckYesFile, BaseUtil.toJson(weightContext.getCnCheckYesSet()));
                } else {
                    FileUtils.writeStringToFile(cnCheckYesFile, "");
                }

                if (weightContext.getCnCheckNoSet() != null && weightContext.getCnCheckNoSet().size() > 0) {
                    FileUtils.writeStringToFile(cnCheckNoFile, BaseUtil.toJson(weightContext.getCnCheckNoSet()));
                } else {
                    FileUtils.writeStringToFile(cnCheckNoFile, "");
                }

                int cnCheckYesCount = weightContext.getCnCheckYesSet().size();
                int cnCheckNoCount = weightContext.getCnCheckNoSet().size();
                weightContext.getWeightForm().getCnCheckYesCount().setText(cnCheckYesCount + "");
                weightContext.getWeightForm().getCnCheckNoCount().setText(cnCheckNoCount + "");
            } catch (Exception e) {
                logger.error(BaseUtil.getExceptionStackTrace(e));
                BaseUtil.messageDialog("数据文件无法写入");
            }
        });
    }
}
