package com.yupi.yuapiinterface.controller;

import com.yupi.yuapiclientsdk.model.User;
import com.yupi.yuapiclientsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/name")
public class NameController{
    @GetMapping("/get")
    public String getNameByGet(String name){

        return "GET 你的名字是"+name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name){
        return "POST 你的名字是"+name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request){
//        String accessKey = request.getHeader("accessKey");
//        String nonce = request.getHeader("nonce");
//        String timestamp = request.getHeader("timestamp");
//        String sign = request.getHeader("sign");
//        String body = request.getHeader("body");
//        //String secretKey = request.getHeader("secretKey");
//        if(!accessKey.equals("yupi")){
//            throw new RuntimeException("无权限");
//        }
//        if(Long.parseLong(nonce)>10000){
//            throw new RuntimeException("无权限");
//        }
//
//
//
//
//        String serverSign = SignUtils.getSign(body, "abcdefgh");
//        if (!sign.equals(serverSign)){
//            throw new RuntimeException("无权限");
//        }
        if (user.getUsername() == null || "".equals(user.getUsername())) {
            return "{\"error\": \"参数不能为空\"}";
        }
        //String result =  "POST 用户名字是" + userParams.getUsername();
        //return result;
        return "{\"username\": \"" + user.getUsername() + "\"}";
    }
}
