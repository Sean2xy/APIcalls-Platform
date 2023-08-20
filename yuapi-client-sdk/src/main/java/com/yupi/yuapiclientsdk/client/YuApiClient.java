package com.yupi.yuapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.yupi.yuapiclientsdk.model.User;


import java.util.HashMap;
import java.util.Map;

import static com.yupi.yuapiclientsdk.utils.SignUtils.getSign;


public class YuApiClient {

    private String accessKey;
    private String secretKey;

//    private static String GATEWAY_HOST="http://localhost:8090";

    private static String GATEWAY_HOST="http://49.232.228.46:8090";
    public void setGATEWAY_HOST(String gateway_Host){
        this.GATEWAY_HOST=gateway_Host;
    }

    public YuApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name){
        HashMap<String, Object> paraMap = new HashMap<>();
        paraMap.put("name",name);
        String result = HttpUtil.get(GATEWAY_HOST+"/api/name/", paraMap);
        System.out.println(result);
        return result;
    }


    public String getNameByPost(String name){
        HashMap<String, Object> paraMap = new HashMap<>();
        paraMap.put("name",name);
        String result = HttpUtil.post(GATEWAY_HOST+"/api/name/", paraMap);
        System.out.println(result);
        return result;
    }

    private Map<String,String> getHeaders(String body){
        HashMap<String,String> map = new HashMap<>();
        map.put("accessKey",accessKey);
        //map.put("secretKey",secretKey);
        map.put("nonce", RandomUtil.randomNumbers(4));
        map.put("body",body);
        map.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
        map.put("sign",getSign(body,secretKey));

        return map;
    }

//    private String getSign(Map<String,String>map,String secretKey) {
//        Digester md5 = new Digester(DigestAlgorithm.SHA256);
//        String content = map.toString()+"."+secretKey;
//        return md5.digestHex(content);
//    }

//    public String getUsernameByPost(User user){
//        String json = JSONUtil.toJsonStr(user);
//        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST+"/api/name/user").addHeaders(getHeaders(json)).body(json).execute();
//        System.out.println(httpResponse.getStatus());
//        String result = httpResponse.body();
//        System.out.println(result);
//        return result;
//    }

    // be called
    public String onlineInvoke(String parameters, String url){
        HttpResponse execute = HttpRequest.post(GATEWAY_HOST + url).addHeaders(getHeaders(parameters)).body(parameters).execute();
        System.out.println(execute.getStatus());
        String result = execute.body();
        return result;
    }

}
