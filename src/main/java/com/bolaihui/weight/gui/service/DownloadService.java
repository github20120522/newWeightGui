package com.bolaihui.weight.gui.service;

import com.bolaihui.weight.gui.client.ZmdClient;
import com.bolaihui.weight.gui.context.WeightContext;
import com.bolaihui.weight.gui.util.AES128Util;
import com.bolaihui.weight.gui.util.BaseUtil;
import com.bolaihui.weight.gui.util.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class DownloadService {

    private static Logger logger = LoggerFactory.getLogger(DownloadService.class);

    private static WeightContext weightContext = WeightContext.getInstance();

    private static String aesKey;

    static {
        try (InputStream inputStream = ZmdClient.class.getClassLoader().getResourceAsStream("system.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            aesKey = properties.getProperty("aesKey");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void zmdExport() {
        try {
            String date = weightContext.getWeightForm().getExportDate().getText();
            Map result = ZmdClient.zmdExport(date);
            String sheetName = "自贸达核放单";
            String[] headers = new String[]{"运单号", "批次", "物流", "电商", "订单", "出区时间"};
            String[] keys = new String[]{"emsNo", "batchNumbers", "emsCom", "accountBook", "orderNumber", "exitedTime"};
            String folder = "自贸达核放单";
            buildExcel(date, result, sheetName, folder, headers, keys);
        } catch (Exception e) {
            String error = BaseUtil.getExceptionStackTrace(e);
            logger.error(error);
            BaseUtil.textAreaErrorDialog("错误", error);
        }
    }

    public static void cnExport() {
        try {
            String date = weightContext.getWeightForm().getExportDate().getText();
            Map result = ZmdClient.cnExport(date);
            String sheetName = "菜鸟核放单";
            String[] headers = new String[]{"运单号", "批次", "物流", "电商", "订单", "出区时间", "重量（kg）"};
            String[] keys = new String[]{"emsNo", "batchNumbers", "emsCom", "accountBook", "orderNumber", "exitedTime", "weight"};
            String folder = "菜鸟核放单";
            buildExcel(date, result, sheetName, folder, headers, keys);
        } catch (Exception e) {
            String error = BaseUtil.getExceptionStackTrace(e);
            logger.error(error);
            BaseUtil.textAreaErrorDialog("错误", error);
        }
    }

    public static void cnInterceptionExport() {
        try {
            String date = weightContext.getWeightForm().getExportDate().getText();
            Map result = ZmdClient.cnInterception(date);
            String sheetName = "菜鸟拦截";
            String[] headers = new String[]{"运单号", "菜鸟货号", "货号", "数量", "任务编号", "捡货位"};
            String[] keys = new String[]{"emsNo", "itemId", "articleNum", "orderCount", "batchNumbers", "repoNo"};
            String folder = "菜鸟拦截";
            buildExcel(date, result, sheetName, folder, headers, keys);
        } catch (Exception e) {
            String error = BaseUtil.getExceptionStackTrace(e);
            logger.error(error);
            BaseUtil.textAreaErrorDialog("错误", error);
        }
    }


    private static void buildExcel(String date, Map result, String sheetName, String folder, String[] headers, String[] keys) {

        String[] dates = date.split("-");
        FileOutputStream fos = null;
        try {
            if (Boolean.valueOf(result.get("success").toString())) {
                // 1.解密数据
                String enData = result.get("data").toString();
                String data = AES128Util.decrypt(aesKey, enData);
                // 2.创建excel表格
                HSSFWorkbook wb = new HSSFWorkbook();
                HSSFSheet sheet = wb.createSheet(sheetName);
                // 3.创建excel文件
                File zmdExportFile = new File("./export/" + folder + "/" + (dates[0] + dates[1]) + "/" + date + ".xls");
                FileUtils.touch(zmdExportFile);
                fos = new FileOutputStream(zmdExportFile);
                // 4.将数据填充到excel
                List<Map<String, Object>> list = BaseUtil.parseJson(data, List.class);
                BaseUtil.excelDataFill(sheet, headers, keys, list);
                // 5.将数据写入到文件中
                wb.write(fos);
                BaseUtil.sound(Constants.DOWNLOAD_SOUND);
                Desktop.getDesktop().open(zmdExportFile.getParentFile());
            } else {
                BaseUtil.messageDialog(result.get("message").toString());
            }
        } catch (Exception e) {
            String error = BaseUtil.getExceptionStackTrace(e);
            logger.error(error);
            BaseUtil.textAreaErrorDialog("错误", error);
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
}
