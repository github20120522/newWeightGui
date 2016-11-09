package com.bolaihui.weight.gui.client;

import com.bolaihui.weight.gui.util.AES128Util;
import com.bolaihui.weight.gui.util.BaseUtil;
import com.bolaihui.weight.gui.util.Constants;
import com.bolaihui.weight.gui.util.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class ZmdClient {

    private static Logger logger = LoggerFactory.getLogger(ZmdClient.class);

    private static String aesKey;

    private static String loginUrl;

    private static String zmdWeightUrl;

    private static String zmdScanUrl;

    private static String cnWeightUrl;

    private static String cnCheckUrl;

    private static String cnCheckFinishUrl;

    private static String generateLocationUrl;

    private static String zmdExportUrl;

    private static String cnExportUrl;

    private static String cnInterceptionUrl;

    private static String weightScopeUrl;

    private static String leftCountUrl;

    static {
        try (InputStream inputStream = ZmdClient.class.getClassLoader().getResourceAsStream("system.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String basePath = properties.getProperty("basePath");
            aesKey = properties.getProperty("aesKey");
            loginUrl = basePath + properties.getProperty("loginUrl");
            zmdWeightUrl = basePath + properties.getProperty("zmdWeightUrl");
            zmdScanUrl = basePath + properties.getProperty("zmdScanUrl");
            cnWeightUrl = basePath + properties.getProperty("cnWeightUrl");
            cnCheckUrl = basePath + properties.getProperty("cnCheckUrl");
            cnCheckFinishUrl = basePath + properties.getProperty("cnCheckFinishUrl");
            generateLocationUrl = basePath + properties.getProperty("generateLocationUrl");
            zmdExportUrl = basePath + properties.getProperty("zmdExportUrl");
            cnExportUrl = basePath + properties.getProperty("cnExportUrl");
            cnInterceptionUrl = basePath + properties.getProperty("cnInterceptionUrl");
            weightScopeUrl = basePath + properties.getProperty("weightScopeUrl");
            leftCountUrl = basePath + properties.getProperty("leftCountUrl");
        } catch (IOException e) {
            String error = BaseUtil.getExceptionStackTrace(e);
            logger.error(error);
            BaseUtil.textAreaErrorDialog("错误", error);
        }
    }

    /**
     * request: date, sign, userName, password
     * response: success[, message]
     */
    public static Map login(String userName, String passWord) throws IOException {

        Map<String, Object> params = new HashMap<>();
        String today = BaseUtil.ymdDateFormat(new Date());
        params.put("date", today);
        params.put("sign", DigestUtils.sha1Hex(today + Constants.simpleKey));
        params.put("userName", AES128Util.encrypt(aesKey, userName));
        params.put("password", AES128Util.encrypt(aesKey, passWord));
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(loginUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

    /**
     * request: date, sign, emsNo, boxNo, weightValue, weightCheck
     * response: success, emsNo, status, weightValue[,error ,dup , message]
     * dup 标识重复
     * error 标识运单不存在，不做后续正常数异常数的增加
     * message为空 自贸达的订单并不清楚订单的放行状态，如未放行直接提示即可，在无错误出现的情况下不进行弹窗
     */
    public static Map zmdWeight(String emsNo, String boxNo, String weightValue, Boolean weightCheck, String operator) throws IOException {

        Map<String, Object> params = new HashMap<>();
        String today = BaseUtil.ymdDateFormat(new Date());
        params.put("date", today);
        params.put("sign", DigestUtils.sha1Hex(today + Constants.simpleKey));
        params.put("emsNo", AES128Util.encrypt(aesKey, emsNo));
        params.put("boxNo", AES128Util.encrypt(aesKey, boxNo));
        params.put("weightValue", AES128Util.encrypt(aesKey, weightValue));
        params.put("weightCheck", AES128Util.encrypt(aesKey, weightCheck.toString()));
        params.put("operator", AES128Util.encrypt(aesKey, operator));
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(zmdWeightUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

    /**
     * 该生成位置方法通用所有结束生成位置，故方法名统一为generateLocation
     * request: date, sign, yesCount, noCount, allEmsNosJson
     * response: success, location[, message]
     */
    public static Map generateLocation(int yesCount, int noCount, String allEmsNosJson) throws IOException {

        Map<String, Object> params = new HashMap<>();
        String today = BaseUtil.ymdDateFormat(new Date());
        params.put("date", today);
        params.put("sign", DigestUtils.sha1Hex(today + Constants.simpleKey));
        params.put("yesCount", AES128Util.encrypt(aesKey, yesCount + ""));
        params.put("noCount", AES128Util.encrypt(aesKey, noCount + ""));
        params.put("allEmsNosJson", AES128Util.encrypt(aesKey, allEmsNosJson));
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(generateLocationUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

    /**
     * request: date, sign, emsNo, operator
     * response: success, status[, message, error, dup]
     * dup 标识重复
     * error 标识运单不存在，不做后续正常数异常数的增加
     * message为空 自贸达的订单并不清楚订单的放行状态，如未放行直接提示即可，在无错误出现的情况下不进行弹窗
     */
    public static Map zmdScan(String emsNo, String operator) throws IOException {

        Map<String, Object> params = new HashMap<>();
        String today = BaseUtil.ymdDateFormat(new Date());
        params.put("date", today);
        params.put("sign", DigestUtils.sha1Hex(today + Constants.simpleKey));
        params.put("emsNo", AES128Util.encrypt(aesKey, emsNo));
        params.put("operator", AES128Util.encrypt(aesKey, operator));
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(zmdScanUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

    /**
     * request: date, sign, emsNo, boxNo, weightValue, weightCheck
     * response: success, emsNo, status, weightValue[,error ,dup , message]
     * dup 标识重复
     * error 标识运单不存在，不做后续正常数异常数的增加
     * message为空 自贸达的订单并不清楚订单的放行状态，如未放行直接提示即可，在无错误出现的情况下不进行弹窗
     */
    public static Map cnWeight(String emsNo, String boxNo, String weightValue, Boolean weightCheck, String operator) throws IOException {

        Map<String, Object> params = new HashMap<>();
        String today = BaseUtil.ymdDateFormat(new Date());
        params.put("date", today);
        params.put("sign", DigestUtils.sha1Hex(today + Constants.simpleKey));
        params.put("emsNo", AES128Util.encrypt(aesKey, emsNo));
        params.put("boxNo", AES128Util.encrypt(aesKey, boxNo));
        params.put("weightValue", AES128Util.encrypt(aesKey, weightValue));
        params.put("weightCheck", AES128Util.encrypt(aesKey, weightCheck.toString()));
        params.put("operator", AES128Util.encrypt(aesKey, operator));
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(cnWeightUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

    /**
     * request: date, sign, yesEmsNosJson
     * response: success, data
     */
    public static Map cnCheckExport(String yesEmsNosJson) throws IOException {

        Map<String, Object> params = new HashMap<>();
        String today = BaseUtil.ymdDateFormat(new Date());
        params.put("date", today);
        params.put("sign", DigestUtils.sha1Hex(today + Constants.simpleKey));
        params.put("yesEmsNosJson", AES128Util.encrypt(aesKey, yesEmsNosJson));
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(cnCheckFinishUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

    /**
     * request: date, sign, emsNo
     * response: success
     */
    public static Map cnCheck(String emsNo) throws IOException {

        Map<String, Object> params = new HashMap<>();
        String today = BaseUtil.ymdDateFormat(new Date());
        params.put("date", today);
        params.put("sign", DigestUtils.sha1Hex(today + Constants.simpleKey));
        params.put("emsNo", AES128Util.encrypt(aesKey, emsNo));
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(cnCheckUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

    /**
     * request: date, sign
     * response: success[, data]
     */
    public static Map zmdExport(String date) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);
        params.put("sign", DigestUtils.sha1Hex(date + Constants.simpleKey));
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(zmdExportUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

    /**
     * request: date, sign
     * response: success[, data]
     */
    public static Map cnExport(String date) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);
        params.put("sign", DigestUtils.sha1Hex(date + Constants.simpleKey));
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(cnExportUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

    /**
     * request: date, sign
     * response: success[, data]
     */
    public static Map cnInterception(String date) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);
        params.put("sign", DigestUtils.sha1Hex(date + Constants.simpleKey));
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(cnInterceptionUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

    public static String weightScope(String weightScope) throws IOException {

        Map<String, Object> params = new HashMap<>();
        Date today = new Date();
        String date = BaseUtil.ymdDateFormat(today);
        params.put("date", date);
        params.put("weightScope", weightScope);
        params.put("sign", DigestUtils.sha1Hex(date + Constants.simpleKey));
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        return HttpUtil.httpPost(weightScopeUrl, headers, params);
    }

    public static Map leftCount() throws IOException {

        Map<String, Object> params = new HashMap<>();
        Date today = new Date();
        String date = BaseUtil.ymdDateFormat(today);
        params.put("date", date);
        params.put("sign", DigestUtils.sha1Hex(date + Constants.simpleKey));
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(leftCountUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

}
