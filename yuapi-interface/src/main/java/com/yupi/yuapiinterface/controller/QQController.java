package com.yupi.yuapiinterface.controller;

import cn.hutool.http.HttpUtil;
import com.yupi.yuapiclientsdk.model.QQ;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/qq")
public class QQController {
    @PostMapping("/isOnline")
    public String getQQIsOnlineByPost(@RequestBody(required = false) QQ qqParams, HttpServletRequest request) throws Exception {
        String qqUrl = "http://api.btstu.cn/qqol/api.php";
        HashMap<String, Object> paramMap = new HashMap<>();
        if (qqParams == null) {
            qqParams = new QQ();
        }
        if (qqParams.getQq() == null || "".equals(qqParams.getQq())) {
            return "{\"error\": \"参数不能为空\"}";
        }else {
            paramMap.put("qq", qqParams.getQq());
        }
        String result = HttpUtil.get(qqUrl, paramMap);
        return result;
    }

    @PostMapping("/NameAndAvatar")
    public String getQQAvatarAndNameByPost(@RequestBody(required = false) QQ qqParams, HttpServletRequest request) throws Exception {
        String qqUrl = "http://api.btstu.cn/qqxt/api.php";
        HashMap<String, Object> paramMap = new HashMap<>();
        if (qqParams == null) {
            qqParams = new QQ();
        }
        if (qqParams.getQq() == null || "".equals(qqParams.getQq())) {
            return "{\"error\": \"参数不能为空\"}";
        }else {
            paramMap.put("qq", qqParams.getQq());
        }
        String result = HttpUtil.get(qqUrl, paramMap);
        return result;

    }

}
