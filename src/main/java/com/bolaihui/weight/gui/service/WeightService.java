package com.bolaihui.weight.gui.service;

import com.bolaihui.weight.gui.context.WeightContext;
import com.bolaihui.weight.gui.util.BaseUtil;
import com.bolaihui.weight.gui.util.Constants;
import gnu.io.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class WeightService implements SerialPortEventListener, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(WeightService.class);

    private BufferedInputStream inputStream;

    private SerialPort serialPort;

    private static WeightContext weightContext = WeightContext.getInstance();

    public static void weightStart() {
        WeightService weightService = new WeightService();
        weightContext.setWeightService(weightService);
        SwingUtilities.invokeLater(weightService);
    }

    public static void weightStop() {
        WeightService weightService = weightContext.getWeightService();
        if (weightService != null) {
            weightContext.setWeightService(null);
            SwingUtilities.invokeLater(() -> {
                weightService.disConnect();
            });
        }
    }

    @Override
    public void run() {
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BaseUtil.getExceptionStackTrace(e));
        }
    }

    private void connectTest() {
        okConnect();
    }

    private void disConnectTest() {
        noConnect();
    }

    private void connect() throws PortInUseException, IOException, TooManyListenersException {

        // 打开称重端口
        CommPortIdentifier commPortIdentifier;
        Enumeration en = CommPortIdentifier.getPortIdentifiers();
        boolean isOpened = false;

        while (en.hasMoreElements()) {
            commPortIdentifier = (CommPortIdentifier) en.nextElement();
            if (commPortIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                serialPort = (SerialPort) commPortIdentifier.open(WeightService.class.getName(), 2000);
                isOpened = true;
                break;
            }
        }

        if (isOpened) {
            serialPort.addEventListener(this);
            inputStream = new BufferedInputStream(serialPort.getInputStream());
            serialPort.notifyOnDataAvailable(true);
            okConnect();
        } else {
            logger.error("连接状态：连接失败，请重新连接");
            disConnect();
        }
    }

    private void disConnect() {
        if (serialPort != null) {
            serialPort.close();
        }
        noConnect();
    }

    private void okConnect() {
        // 连接部分
        weightContext.getWeightForm().getConnectStatus().setText(Constants.CONNECT_SUCCESS);
        weightContext.getWeightForm().getConnectStatus().setForeground(Constants.okColor);
        weightContext.getWeightForm().getConnectBtn().setEnabled(false);
        weightContext.getWeightForm().getDisConnectBtn().setEnabled(true);
        // 自贸达称重部分
        if (!weightContext.getWeightForm().getZmdWeightEndBtn().isEnabled()) {
            weightContext.getWeightForm().getZmdWeightBeginBtn().setEnabled(true);
        }
        weightContext.getWeightForm().getZmdWeightEmsNo().setEnabled(true);
        weightContext.getWeightForm().getZmdWeightBoxOpenBtn().setEnabled(true);
        // 菜鸟部分
        weightContext.getWeightForm().getCnWeightModel().setText(Constants.WEIGHT_CHECK_MODEL);
        weightContext.getWeightForm().getCnWeightModel().setForeground(Constants.okColor);
        // 运单号置焦
        if (weightContext.getWeightForm().getTabbedPane().getSelectedIndex() == 1) {
            weightContext.getWeightForm().getZmdWeightEmsNo().requestFocus();
        } else if (weightContext.getWeightForm().getTabbedPane().getSelectedIndex() == 3) {
            weightContext.getWeightForm().getCnWeightEmsNo().requestFocus();
        }
    }

    private void noConnect() {
        // 连接部分
        weightContext.getWeightForm().getConnectStatus().setText(Constants.CONNECT_FAILURE);
        weightContext.getWeightForm().getConnectStatus().setForeground(Color.RED);
        weightContext.getWeightForm().getConnectBtn().setEnabled(true);
        weightContext.getWeightForm().getDisConnectBtn().setEnabled(false);
        // 自贸达称重部分
        weightContext.getWeightForm().getZmdWeightBeginBtn().setEnabled(false);
        weightContext.getWeightForm().getZmdWeightEmsNo().setEnabled(false);
        weightContext.getWeightForm().getZmdWeightBoxNo().setEnabled(false);
        weightContext.getWeightForm().getZmdWeightBoxOpenBtn().setEnabled(false);
        weightContext.getWeightForm().getZmdWeightBoxCloseBtn().setEnabled(false);
        weightContext.getWeightForm().getZmdWeightModel().setText(Constants.WEIGHT_UN_CHECK_MODEL);
        weightContext.getWeightForm().getZmdWeightModel().setForeground(Color.BLUE);
        // 菜鸟部分
        weightContext.getWeightForm().getCnWeightModel().setText(Constants.WEIGHT_UN_CHECK_MODEL);
        weightContext.getWeightForm().getCnWeightModel().setForeground(Color.BLUE);
        // 运单号置焦
        if (weightContext.getWeightForm().getTabbedPane().getSelectedIndex() == 3) {
            weightContext.getWeightForm().getCnWeightEmsNo().requestFocus();
        }
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        switch (serialPortEvent.getEventType()) {
            case SerialPortEvent.BI:
                /* Break interrupt，通讯中断 */
                // done 结束线程，更新连接提示
                disConnect();
                logger.error("电脑与电子计重秤通信中断，请立即检查连接状况");
                break;
            case SerialPortEvent.OE:
                /* Overrun error，溢位错误 */
                break;
            case SerialPortEvent.FE:
                /* Framing error，传帧错误 */
                break;
            case SerialPortEvent.PE:
                /* Parity error，校验错误 */
                break;
            case SerialPortEvent.CD:
                /* Carrier detect，载波检测 */
                break;
            case SerialPortEvent.CTS:
                /* Clear to send，清除发送 */
                break;
            case SerialPortEvent.DSR:
                /* Data set ready，数据设备就绪 */
                break;
            case SerialPortEvent.RI:
                /* Ring indicator，响铃指示 */
                break;
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                /* Output buffer is empty，输出缓冲区清空 */
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                /* Data available at the serial port，端口有可用数据，读到缓冲数组，输出到终端 */
                try {
                    dataProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(BaseUtil.getExceptionStackTrace(e));
                    // done 关闭连接
                    disConnect();
                }
                break;
        }
    }

    /* 处理串口数据 */
    private void dataProcess() throws Exception {

        Thread.sleep(5);
        // 等待完整的称重数据
        byte[] readBuffer = new byte[256];
        if (inputStream.available() >= 18) {
            while (inputStream.available() > 0) {
                int c = inputStream.read(readBuffer);
                if (readBuffer.length >= 18) {
                    break;
                }
            }
        } else {
            return;
        }

        byte[] readBytes = new byte[18];
        System.arraycopy(readBuffer, 0, readBytes, 0, 18);
        byte[] weightBytes = new byte[8];
        byte[] unitBytes = new byte[2];

        if (readBytes.length == 18) {

            System.arraycopy(readBytes, 6, weightBytes, 0, 8);
            System.arraycopy(readBytes, 14, unitBytes, 0, 2);
            String weightStr = new String(weightBytes);
            String unitStr = new String(unitBytes);

            // done 显示称重重量
            showWeight(weightStr, unitStr);
        }

    }

    /* 显示称重信息 */
    private void showWeight(String weightValue, String unitStr) throws IOException {

        BigDecimal weight = new BigDecimal(weightValue.replace("+", "").replace("-", "").trim());
        if (StringUtils.equals("g", unitStr.trim())) {
            weight = weight.divide(new BigDecimal(1000), BigDecimal.ROUND_HALF_UP);
        }
        weight = weight.setScale(4, BigDecimal.ROUND_HALF_UP);
        // 自贸达称重部分
        weightContext.getWeightForm().getZmdWeightValue().setText(weight + "kg");
        // 菜鸟称重部分
        weightContext.getWeightForm().getCnWeightValue().setText(weight + "kg");
    }

}
