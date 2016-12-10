package com.bolaihui.weight.gui.service;

import com.bolaihui.weight.gui.client.ZmdClient;
import com.bolaihui.weight.gui.context.WeightContext;
import com.bolaihui.weight.gui.util.BaseUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

/**
 * Created by Administrator on 2016/10/31 0031.
 */
@SuppressWarnings("unchecked")
public class WeightScopeService {

    private static Logger logger = LoggerFactory.getLogger(WeightScopeService.class);

    private static WeightContext weightContext = WeightContext.getInstance();

    public static void getWeightScopeList() {
        try {
            String weightScopeList = ZmdClient.weightScopeList("");
            if (StringUtils.isNotBlank(weightScopeList)) {
                Vector<String> scopeVector = BaseUtil.parseJson(weightScopeList, Vector.class);
                weightContext.getWeightForm().getScopeList().setListData(scopeVector);
            }
        } catch (Exception e) {
            String error = BaseUtil.getExceptionStackTrace(e);
            logger.error(error);
            BaseUtil.textAreaErrorDialog("错误", error);
        }
    }

    public static void setWeightScope() {
        try {
            String scopeBox = weightContext.getWeightForm().getScopeBoxNo().getText();
            String weightScope = weightContext.getWeightForm().getWeightScopeValue().getText();
            String backWeightScopeList = ZmdClient.weightScopeList(scopeBox + "_" + weightScope);
            if (StringUtils.isNotBlank(backWeightScopeList)) {
                Vector<String> scopeVector = BaseUtil.parseJson(backWeightScopeList, Vector.class);
                weightContext.getWeightForm().getScopeList().setListData(scopeVector);
                BaseUtil.messageDialog("设置成功");
            } else {
                BaseUtil.messageDialog("设置失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BaseUtil.getExceptionStackTrace(e));
        }
    }
}
