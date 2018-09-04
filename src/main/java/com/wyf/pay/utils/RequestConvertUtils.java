package com.wyf.pay.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuyifei on 2018/5/17.
 */
public class RequestConvertUtils {

    //request url参数解析
    public static Map convertParseUrl(HttpServletRequest request){
        Map<String, Object> param = new HashMap<String, Object>();
        Map<String, String[]> map = request.getParameterMap();
        for(String key : map.keySet()){
            String[] paramValues = request.getParameterValues(key);
            if (paramValues.length == 1) {
                String value = paramValues[0];
                param.put(key, value);
            }
        }
        return param;
    }


    //post body取值
    public static String convertBody(BufferedReader br){
        String inputLine;
        String strBody = "";
        try {
            while ((inputLine = br.readLine()) != null) {
                strBody += inputLine;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        try {
            strBody = URLDecoder.decode(strBody, "UTF-8");
        }catch (Exception e){
            strBody = null;
        }
        return strBody;
    }
}
