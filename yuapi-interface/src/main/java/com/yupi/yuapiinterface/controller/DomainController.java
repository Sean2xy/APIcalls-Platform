package com.yupi.yuapiinterface.controller;

import cn.hutool.http.HttpUtil;
import com.yupi.yuapiclientsdk.model.Domain;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/domain")
public class DomainController {

    @PostMapping("/name")
    public String getDomainNameByPost(@RequestBody(required = false) Domain domainNameParams, HttpServletRequest request) throws Exception {
        String domainUrl = "http://api.btstu.cn/dmreg/api.php";
        HashMap<String, Object> paramMap = new HashMap<>();
        if (domainNameParams == null) {
            domainNameParams = new Domain();
        }

        if (domainNameParams.getDomain() == null || "".equals(domainNameParams.getDomain())) {
            return "{\"error\": \"参数不能为空\"}";
        } else {
            paramMap.put("domain", domainNameParams.getDomain());
        }
        String result = HttpUtil.get(domainUrl, paramMap);
        //StringBuffer stringBuffer = new StringBuffer();
        //stringBuffer.append("{\"text\": \"");
        //stringBuffer.append(result);
        //stringBuffer.append("\"}");
        //return stringBuffer.toString();
        return result;
    }
}