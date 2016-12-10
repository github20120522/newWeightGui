package com.bolaihui.weight.gui.form;

import com.bolaihui.weight.gui.context.WeightContext;
import com.bolaihui.weight.gui.service.*;
import com.bolaihui.weight.gui.util.BaseUtil;
import com.bolaihui.weight.gui.util.Constants;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/25 0025.
 */
public class WeightForm {

    private WeightContext weightContext = WeightContext.getInstance();

    private JTextField userName;
    private JPasswordField passWord;
    private JButton connectBtn;
    private JButton disConnectBtn;
    private JButton zmdExportBtn;
    private JButton cnExportBtn;
    private JTextField exportDate;
    private JTabbedPane tabbedPane;
    private JButton zmdModelBtn;
    private JButton zmdWeightBeginBtn;
    private JTextField zmdWeightEmsNo;
    private JLabel loginStatus;
    private JLabel connectStatus;
    private JButton cnInterceptBtn;
    private JButton cnModelBtn;
    private JLabel zmdWeightModel;
    private JLabel zmdWeightYesCount;
    private JLabel zmdWeightNoCount;
    private JButton zmdWeightEndBtn;
    private JLabel zmdWeightValue;
    private JLabel zmdWeightEmsInfo;
    private JPanel zmdWeightEmsColor;
    private JTextField zmdWeightEmsLength;
    private JTextField zmdWeightBoxNo;
    private JButton zmdWeightBoxOpenBtn;
    private JButton zmdWeightBoxCloseBtn;
    private JLabel zmdScanYesCount;
    private JLabel zmdScanNoCount;
    private JButton zmdScanBeginBtn;
    private JButton zmdScanEndBtn;
    private JLabel zmdScanEmsInfo;
    private JPanel zmdScanEmsColor;
    private JTextField zmdScanEmsLength;
    private JTextField zmdScanEmsNo;
    private JLabel cnWeightModel;
    private JLabel cnWeightYesCount;
    private JLabel cnWeightNoCount;
    private JButton cnWeightBeginBtn;
    private JButton cnWeightEndBtn;
    private JLabel cnWeightValue;
    private JLabel cnWeightEmsInfo;
    private JPanel cnWeightEmsColor;
    private JTextField cnWeightEmsLength;
    private JTextField cnWeightEmsNo;
    private JTextField cnWeightBoxNo;
    private JLabel cnCheckYesCount;
    private JLabel cnCheckNoCount;
    private JLabel cnCheckEmsInfo;
    private JPanel cnCheckEmsColor;
    private JTextField cnCheckEmsLength;
    private JTextField cnCheckEmsNo;
    private JLabel loginName;
    private JPanel mainPanel;
    private JButton cnCheckBeginBtn;
    private JButton cnCheckEndBtn;
    private JTextField weightScopeValue;
    private JButton weightScopeBtn;
    private JLabel zmdLeftCount;
    private JLabel cnLeftCount;
    private JButton leftCountBtn;
    private JButton modelChangeBtn;
    private JList scopeList;
    private JTextField scopeBoxNo;

    public WeightForm() {
        init();
        initListeners();
    }

    private void init() {

        zmdModelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cnModelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        leftCountBtn.setBorder(null);
        leftCountBtn.setFocusPainted(false);
        leftCountBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        connectBtn.setBorder(null);
        connectBtn.setFocusPainted(false);
        connectBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        disConnectBtn.setBorder(null);
        disConnectBtn.setFocusPainted(false);
        disConnectBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        modelChangeBtn.setBorder(null);
        modelChangeBtn.setFocusPainted(false);
        modelChangeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        zmdWeightBeginBtn.setBorder(null);
        zmdWeightBeginBtn.setFocusPainted(false);
        zmdWeightBeginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        zmdWeightEndBtn.setBorder(null);
        zmdWeightEndBtn.setFocusPainted(false);
        zmdWeightEndBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        zmdWeightBoxOpenBtn.setBorder(null);
        zmdWeightBoxOpenBtn.setFocusPainted(false);
        zmdWeightBoxOpenBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        zmdWeightBoxCloseBtn.setBorder(null);
        zmdWeightBoxCloseBtn.setFocusPainted(false);
        zmdWeightBoxCloseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        zmdScanBeginBtn.setBorder(null);
        zmdScanBeginBtn.setFocusPainted(false);
        zmdScanBeginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        zmdScanEndBtn.setBorder(null);
        zmdScanEndBtn.setFocusPainted(false);
        zmdScanEndBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cnWeightBeginBtn.setBorder(null);
        cnWeightBeginBtn.setFocusPainted(false);
        cnWeightBeginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cnWeightEndBtn.setBorder(null);
        cnWeightEndBtn.setFocusPainted(false);
        cnWeightEndBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cnCheckBeginBtn.setBorder(null);
        cnCheckBeginBtn.setFocusPainted(false);
        cnCheckBeginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cnCheckEndBtn.setBorder(null);
        cnCheckEndBtn.setFocusPainted(false);
        cnCheckEndBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        weightContext.setWeightForm(this);
        exportDate.setText(BaseUtil.ymdDateFormat(new Date()));
        // done 数据恢复，如果有的话
        // 自贸达称重数据恢复
        ZmdWeightService.restoreData();
        // 自贸达扫描数据恢复
        ZmdScanService.restoreData();
        // 菜鸟称重数据恢复
        CnWeightService.restoreData();
        // 菜鸟清关检查数据恢复
        CnCheckService.restoreData();
    }

    private void initListeners() {
        // tab页切换
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = tabbedPane.getSelectedIndex();
                switch (index) {
                    case 1:
                        zmdWeightEmsNo.requestFocus();
                        break;
                    case 2:
                        zmdScanEmsNo.requestFocus();
                        break;
                    case 3:
                        cnWeightEmsNo.requestFocus();
                        break;
                    case 4:
                        cnCheckEmsNo.requestFocus();
                        break;
                    case 5:
                        WeightScopeService.getWeightScopeList();
                        break;
                }
            }
        });
        // 登陆
        passWord.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    LoginService.login();
                }
            }
        });
        // 模式选择
        zmdModelBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (zmdModelBtn.isEnabled()) {
                    ModelService.choseZmdModel();
                }
            }
        });
        cnModelBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (cnModelBtn.isEnabled()) {
                    ModelService.choseCnModel();
                }
            }
        });
        // 电子秤连接
        connectBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (connectBtn.isEnabled()) {
                    WeightService.weightStart();
                }
            }
        });
        disConnectBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (disConnectBtn.isEnabled()) {
                    WeightService.weightStop();
                }
            }
        });
        // 自贸达称重部分
        zmdWeightBoxOpenBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (zmdWeightBoxOpenBtn.isEnabled()) {
                    ZmdWeightService.boxOpen();
                }
            }
        });
        zmdWeightBoxCloseBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (zmdWeightBoxCloseBtn.isEnabled()) {
                    ZmdWeightService.boxClose();
                }
            }
        });
        zmdWeightBeginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (zmdWeightBeginBtn.isEnabled()) {
                    ZmdWeightService.clear();
                }
            }
        });
        zmdWeightEndBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (zmdWeightEndBtn.isEnabled()) {
                    ZmdWeightService.generateLocation();
                    StatusService.leftCount();
                }
            }
        });
        zmdWeightEmsNo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ZmdWeightService.doWeight();
                }
            }
        });
        zmdWeightBoxNo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ZmdWeightService.doWeight();
                }
            }
        });
        // 自贸达扫描部分
        zmdScanBeginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (zmdScanBeginBtn.isEnabled()) {
                    ZmdScanService.clear();
                }
            }
        });
        zmdScanEndBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (zmdScanEndBtn.isEnabled()) {
                    ZmdScanService.generateLocation();
                    StatusService.leftCount();
                }
            }
        });
        zmdScanEmsNo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ZmdScanService.doScan();
                }
            }
        });
        // 菜鸟称重部分
        cnWeightBeginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (cnWeightBeginBtn.isEnabled()) {
                    CnWeightService.clear();
                }
            }
        });
        cnWeightEndBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (cnWeightEndBtn.isEnabled()) {
                    CnWeightService.generateLocation();
                    StatusService.leftCount();
                }
            }
        });
        cnWeightEmsNo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    CnWeightService.doWeight();
                }
            }
        });
        cnWeightBoxNo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    CnWeightService.doWeight();
                }
            }
        });
        // 菜鸟清关检查部分
        cnCheckBeginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (cnCheckBeginBtn.isEnabled()) {
                    CnCheckService.clear();
                }
            }
        });
        cnCheckEndBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (cnCheckEndBtn.isEnabled()) {
                    CnCheckService.cnCheckExport();
                    StatusService.leftCount();
                }
            }
        });
        cnCheckEmsNo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    CnCheckService.doCheck();
                }
            }
        });
        // done 导出button
        zmdExportBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (zmdExportBtn.isEnabled()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            zmdExportBtn.setEnabled(false);
                        }
                    });
                    weightContext.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {
                            DownloadService.zmdExport();
                        }
                    });
                }
            }
        });
        cnExportBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (cnExportBtn.isEnabled()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            cnExportBtn.setEnabled(false);
                        }
                    });
                    weightContext.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {
                            DownloadService.cnExport();
                        }
                    });
                }
            }
        });
        cnInterceptBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (cnInterceptBtn.isEnabled()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            cnInterceptBtn.setEnabled(false);
                        }
                    });
                    weightContext.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {
                            DownloadService.cnInterceptionExport();
                        }
                    });
                }
            }
        });
        // 误差范围设置
        weightScopeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (weightScopeBtn.isEnabled()) {
                    WeightScopeService.setWeightScope();
                }
            }
        });
        // 刷新剩余数量
        leftCountBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (leftCountBtn.isEnabled()) {
                    StatusService.leftCount();
                }
            }
        });
        // 自贸达称重模式转变
        modelChangeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (modelChangeBtn.isEnabled()) {
                    if (zmdWeightBoxNo.isEnabled() && connectStatus.getText().equals(Constants.CONNECT_SUCCESS)) {
                        weightContext.getWeightForm().getZmdWeightModel().setText(Constants.WEIGHT_CHECK_MODEL);
                        weightContext.getWeightForm().getZmdWeightModel().setForeground(Constants.okColor);
                    } else {
                        BaseUtil.messageDialog("请连接电子秤并开启箱码扫描");
                    }
                }
            }
        });
        scopeList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                String boxScope = scopeList.getModel().getElementAt(scopeList.locationToIndex(e.getPoint())).toString();
                String[] data = boxScope.split("_");
                scopeBoxNo.setText(data[0]);
                weightScopeValue.setText(data[1]);
            }
        });
    }

    public JTextField getUserName() {
        return userName;
    }

    public JPasswordField getPassWord() {
        return passWord;
    }

    public JButton getConnectBtn() {
        return connectBtn;
    }

    public JButton getDisConnectBtn() {
        return disConnectBtn;
    }

    public JButton getZmdExportBtn() {
        return zmdExportBtn;
    }

    public JButton getCnExportBtn() {
        return cnExportBtn;
    }

    public JTextField getExportDate() {
        return exportDate;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public JButton getZmdModelBtn() {
        return zmdModelBtn;
    }

    public JButton getZmdWeightBeginBtn() {
        return zmdWeightBeginBtn;
    }

    public JTextField getZmdWeightEmsNo() {
        return zmdWeightEmsNo;
    }

    public JLabel getLoginStatus() {
        return loginStatus;
    }

    public JLabel getConnectStatus() {
        return connectStatus;
    }

    public JButton getCnInterceptBtn() {
        return cnInterceptBtn;
    }

    public JButton getCnModelBtn() {
        return cnModelBtn;
    }

    public JLabel getZmdWeightModel() {
        return zmdWeightModel;
    }

    public JLabel getZmdWeightYesCount() {
        return zmdWeightYesCount;
    }

    public JLabel getZmdWeightNoCount() {
        return zmdWeightNoCount;
    }

    public JButton getZmdWeightEndBtn() {
        return zmdWeightEndBtn;
    }

    public JLabel getZmdWeightValue() {
        return zmdWeightValue;
    }

    public JLabel getZmdWeightEmsInfo() {
        return zmdWeightEmsInfo;
    }

    public JPanel getZmdWeightEmsColor() {
        return zmdWeightEmsColor;
    }

    public JTextField getZmdWeightEmsLength() {
        return zmdWeightEmsLength;
    }

    public JTextField getZmdWeightBoxNo() {
        return zmdWeightBoxNo;
    }

    public JButton getZmdWeightBoxOpenBtn() {
        return zmdWeightBoxOpenBtn;
    }

    public JButton getZmdWeightBoxCloseBtn() {
        return zmdWeightBoxCloseBtn;
    }

    public JLabel getZmdScanYesCount() {
        return zmdScanYesCount;
    }

    public JLabel getZmdScanNoCount() {
        return zmdScanNoCount;
    }

    public JButton getZmdScanBeginBtn() {
        return zmdScanBeginBtn;
    }

    public JButton getZmdScanEndBtn() {
        return zmdScanEndBtn;
    }

    public JLabel getZmdScanEmsInfo() {
        return zmdScanEmsInfo;
    }

    public JPanel getZmdScanEmsColor() {
        return zmdScanEmsColor;
    }

    public JTextField getZmdScanEmsLength() {
        return zmdScanEmsLength;
    }

    public JTextField getZmdScanEmsNo() {
        return zmdScanEmsNo;
    }

    public JLabel getCnWeightModel() {
        return cnWeightModel;
    }

    public JLabel getCnWeightYesCount() {
        return cnWeightYesCount;
    }

    public JLabel getCnWeightNoCount() {
        return cnWeightNoCount;
    }

    public JButton getCnWeightBeginBtn() {
        return cnWeightBeginBtn;
    }

    public JButton getCnWeightEndBtn() {
        return cnWeightEndBtn;
    }

    public JLabel getCnWeightValue() {
        return cnWeightValue;
    }

    public JLabel getCnWeightEmsInfo() {
        return cnWeightEmsInfo;
    }

    public JPanel getCnWeightEmsColor() {
        return cnWeightEmsColor;
    }

    public JTextField getCnWeightEmsLength() {
        return cnWeightEmsLength;
    }

    public JTextField getCnWeightEmsNo() {
        return cnWeightEmsNo;
    }

    public JTextField getCnWeightBoxNo() {
        return cnWeightBoxNo;
    }

    public JLabel getCnCheckYesCount() {
        return cnCheckYesCount;
    }

    public JLabel getCnCheckNoCount() {
        return cnCheckNoCount;
    }

    public JLabel getCnCheckEmsInfo() {
        return cnCheckEmsInfo;
    }

    public JPanel getCnCheckEmsColor() {
        return cnCheckEmsColor;
    }

    public JTextField getCnCheckEmsLength() {
        return cnCheckEmsLength;
    }

    public JTextField getCnCheckEmsNo() {
        return cnCheckEmsNo;
    }

    public JLabel getLoginName() {
        return loginName;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JButton getCnCheckBeginBtn() {
        return cnCheckBeginBtn;
    }

    public JButton getCnCheckEndBtn() {
        return cnCheckEndBtn;
    }

    public JTextField getWeightScopeValue() {
        return weightScopeValue;
    }

    public JLabel getZmdLeftCount() {
        return zmdLeftCount;
    }

    public JLabel getCnLeftCount() {
        return cnLeftCount;
    }

    public JList getScopeList() {
        return scopeList;
    }

    public JTextField getScopeBoxNo() {
        return scopeBoxNo;
    }
}
