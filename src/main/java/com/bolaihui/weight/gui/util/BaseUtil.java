package com.bolaihui.weight.gui.util;

import com.bolaihui.weight.gui.context.WeightContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import sun.audio.AudioPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fz on 2015/12/30.
 */
public class BaseUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");

    private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public static <T> T parseJson(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    public static String getExceptionStackTrace(Exception e) {

        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw, true);
            e.printStackTrace(pw);
            return sw.toString();
        } finally {
            try {
                if (sw != null)
                    sw.close();
                if (pw != null)
                    pw.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void messageDialog(String message) {

        JOptionPane.showMessageDialog(null, message);
    }

    public static void textAreaDialog(String title, String content) {

        JTextArea textArea = new JTextArea(content);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        Font font = textArea.getFont();
        float size = font.getSize() + 12.0f;
        textArea.setFont(font.deriveFont(size));
        scrollPane.setPreferredSize(new Dimension(730, 430));
        String inputMsg = JOptionPane.showInputDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
        if (StringUtils.isNotBlank(inputMsg)) {
            SwingUtilities.invokeLater(() -> {
                // done 给出需要手动关闭的提示音
                InputStream warningSoundStream = BaseUtil.class.getClassLoader().getResourceAsStream("warning.wav");
                AudioPlayer.player.start(warningSoundStream);
                textAreaDialog(title, content);
            });
        }
    }

    public static void sound(String type) {
        WeightContext weightContext = WeightContext.getInstance();
        weightContext.getExecutorService().execute(() -> {
            InputStream soundStream = BaseUtil.class.getClassLoader().getResourceAsStream(type + ".wav");
            switch (type) {
                case Constants.WARNING_SOUND:
                    AudioPlayer.player.start(soundStream);
                    break;
                case Constants.DUP_SOUND:
                    AudioPlayer.player.start(soundStream);
                    break;
                case Constants.SUCCESS_SOUND:
                    AudioPlayer.player.start(soundStream);
                    break;
                case Constants.FAILURE_SOUND:
                    AudioPlayer.player.start(soundStream);
                    break;
                case Constants.DOWNLOAD_SOUND:
                    AudioPlayer.player.start(soundStream);
                    break;
            }
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            IOUtils.closeQuietly(soundStream);
        });
    }

    public static String ymdDateFormat(Date date) {

        return sdf.format(date);
    }

    public static String ymDateFormat(Date date) {

        return sdf2.format(date);
    }

    public static String ymdHmsDateFormat(Date date) {

        return sdf3.format(date);
    }

    public static void excelDataFill(HSSFSheet sheet, String[] headers, String[] keys, java.util.List<Map<String, Object>> list) {

        int rowIndex = 0;
        int cellIndex = 0;
        HSSFRow rowFirst = sheet.createRow(rowIndex++);
        for (String header : headers) {
            HSSFCell cell = rowFirst.createCell(cellIndex++);
            cell.setCellValue(header);
        }
        for (Map<String, Object> data : list) {
            HSSFRow row = sheet.createRow(rowIndex++);
            for (int i=0; i<keys.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(data.get(keys[i]) != null ? data.get(keys[i]).toString() : "");
            }
        }
    }

    public static java.util.List<Map<String, Object>> processExcel(HSSFSheet sheet, String[] keyHeader, int beginIndex) {

        java.util.List<Map<String, Object>> list = new ArrayList<>();
        for ( ; beginIndex <= sheet.getLastRowNum(); beginIndex++ ) {
            HSSFRow row = sheet.getRow(beginIndex);
            if (row == null) {
                break;
            }
            Map<String, Object> data = new HashMap<>();
            for (int i=0; i<keyHeader.length; i++) {
                HSSFCell cell = row.getCell(i);
                if (cell == null) {
                    data.put(keyHeader[i], "");
                } else {
                    data.put(keyHeader[i], cell.getStringCellValue());
                }
            }
            list.add(data);
        }
        return list;
    }
}
