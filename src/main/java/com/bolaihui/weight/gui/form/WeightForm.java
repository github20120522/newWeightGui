package com.bolaihui.weight.gui.form;

import com.bolaihui.weight.gui.context.WeightContext;
import com.bolaihui.weight.gui.service.*;
import com.bolaihui.weight.gui.util.BaseUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

    public WeightForm() {
        init();
        initListeners();
    }

    private void init() {
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
                        WeightScopeService.getWeightScope();
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
                    DownloadService.zmdExport();
                }
            }
        });
        cnExportBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (cnExportBtn.isEnabled()) {
                    DownloadService.cnExport();
                }
            }
        });
        cnInterceptBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (cnInterceptBtn.isEnabled()) {
                    DownloadService.cnInterceptionExport();
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

}