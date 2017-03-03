package com.fxiaoke.dataplatform.crm.cleantool.utils;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

/**
 * Created by chenlong on 2016/3/17.
 */
public class ControllerHelper {

    /**
     * 处理响应信息
     *
     * @param res
     * @return
     */
    public static Map returnResponseVal(int res, String msg) {
        String result;
        if (res > 0) {
            result = "数据" + msg + "成功!";
            return ImmutableMap.of("code", 200, "info", result);
        } else {
            result = "数据" + msg + "失败！";
            return ImmutableMap.of("code", -1, "info", result);
        }
    }

    /**
     * 处理响应信息
     *
     * @param res
     * @return
     */
    public static Map returnResponseValue(int res, String msg) {
        String result;
        if (res > 0) {
            result = "数据操作成功!";
            return ImmutableMap.of("code", 200, "info", result);
        } else {
            result = "数据操作失败！失败原因：" + msg;
            return ImmutableMap.of("code", -1, "info", result);
        }
    }

    /**
     * 验证数据重复性
     *
     * @param res
     * @return
     */
    public static Map returnResponseMsg(int res) {
        if (res == 0) {
            return ImmutableMap.of("code", 200, "info", "该命名可以使用");
        } else {
            return ImmutableMap.of("code", -1, "info", "该命名已存在，请重新输入");
        }
    }

    /**
     * 转换为JSONArray
     *
     * @param list
     * @return
     */
    public static JSONArray convertToJSON(List list) {
        JSONArray outerJSON = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONArray innerJSON = ReflectionUtil.convertToJSONArray(list.get(i));
            outerJSON.add(innerJSON);
        }
        return outerJSON;
    }

}
