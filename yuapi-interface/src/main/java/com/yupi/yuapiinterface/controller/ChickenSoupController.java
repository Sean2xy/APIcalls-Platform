package com.yupi.yuapiinterface.controller;

import cn.hutool.http.HttpUtil;
import com.yupi.yuapiclientsdk.model.ChickenSoup;
import com.yupi.yuapiclientsdk.model.QQ;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/chickenSoup")
public class ChickenSoupController {
    @PostMapping("/text")
    public String getChickenSoupByPost(@RequestBody(required = false) ChickenSoup chickenSoupParams, HttpServletRequest request) throws Exception {
        String chickenSoupUrl = "http://api.btstu.cn/yan/api.php";
        HashMap<String, Object> paramMap = new HashMap<>();
        if (chickenSoupParams == null) {
            chickenSoupParams = new ChickenSoup();
        }

        if (chickenSoupParams.getCharset() == null || "".equals(chickenSoupParams.getCharset())) {
            paramMap.put("charset", "utf-8");
        }else {
            paramMap.put("charset", chickenSoupParams.getCharset());
        }
        if (chickenSoupParams.getEncode() == null || "".equals(chickenSoupParams.getEncode())) {
            paramMap.put("encode", "text");
        }else {
            paramMap.put("charset", chickenSoupParams.getEncode());
        }
        String text = HttpUtil.get(chickenSoupUrl, paramMap);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{\"text\": \"");
        stringBuffer.append(text);
        stringBuffer.append("\"}");
        return stringBuffer.toString();
    }

}
