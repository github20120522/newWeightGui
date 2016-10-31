package com.bolaihui.weight.gui.service;

import com.bolaihui.weight.gui.client.ZmdClient;
import com.bolaihui.weight.gui.context.WeightContext;
import com.bolaihui.weight.gui.util.BaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Administrator on 2016/10/31 0031.
 */
public class StatusService {

    private static Logger logger = LoggerFactory.getLogger(StatusService.class);

    private static WeightContext weightContext = WeightContext.getInstance();

    public static void leftCount() {
        try {
            Map result = ZmdClient.leftCount();
            weightContext.getWeightForm().getZmdLeftCount().setText(result.get("zmdLeftCount").toString());
            weightContext.getWeightForm().getCnLeftCount().setText(result.get("cnLeftCount").toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BaseUtil.getExceptionStackTrace(e));
        }
    }
}
