package com.yhl.resourceServer.componet.featur;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhl.oauthCommon.entity.OAthGrantedAuthorityDto;
import com.yhl.securityCommon.access.RequestAuthorityAttribute;
import com.yhl.securityCommon.provider.RequestAuthoritiesService;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Setter
public class RequestAuthoritiesServiceImpl implements RequestAuthoritiesService, InitializingBean {

    private  String TOKENID ="tokenId";
    private  String url;
    private RestTemplate restTemplate;
    public RequestAuthoritiesServiceImpl(){
        restTemplate =new RestTemplate();
    }

    // 获取这个token能访问的这个对应的客户端的信息路径，以及请求方式信息
    @Override
    public List<RequestAuthorityAttribute> listAllAttributes(String token) {
        List<RequestAuthorityAttribute> list = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        JSONObject params =new JSONObject();
        params.put(TOKENID,token);
        HttpEntity<String> formEntity = new HttpEntity<>(params.toString(), headers);
        String resultStr = restTemplate.postForObject(url,formEntity , String.class);
        System.out.println(resultStr);
        JSONArray.parseArray(resultStr,OAthGrantedAuthorityDto.class).forEach(ele ->{
            list.add(new RequestAuthorityAttribute(ele.getApiUri(),ele.getMethod(),ele.getMactherType()));
        });
        return list;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
