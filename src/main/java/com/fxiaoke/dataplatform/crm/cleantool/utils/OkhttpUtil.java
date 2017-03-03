package com.fxiaoke.dataplatform.crm.cleantool.utils;

import com.squareup.okhttp.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by weilei on 2016/11/15.
 */
@Slf4j
public class OkhttpUtil {
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    static {
        mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);
    }
    public  static void sendPost(Integer eid,String token,String result,String callbackUrl){
        //申明给服务端传递一个json串
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        log.info("eid :{},token:{}, post 参数:{}",eid,token,result);
        JSONObject json = new JSONObject();
        json.put("Token",token);

        if(result!=null&&result.indexOf("error:")==0){
            json.put("Result","[]");
            json.put("Error",result.substring(result.indexOf("error:")+6,result.length()));
        }else{
            json.put("Result",result);
            json.put("Error","");
        }

        RequestBody body = RequestBody.create(JSON, json.toString());
        //创建一个请求对象
        Request request = new Request.Builder()
                .url(callbackUrl)
                .addHeader("x-fs-ei",String.valueOf(eid))
                .post(body)
                .build();
        //发送请求获取响应
        try {
            Response response=okHttpClient.newCall(request).execute();
            //判断请求是否成功
            if(response.isSuccessful()){
                //打印服务端返回结果
                log.info("crm 接收数据后的返回："+response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 不会开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }
}
