package com.yhl.oauthServer.controller;


import com.alibaba.fastjson.JSONObject;
import com.yhl.oauthServer.entity.OAuthAccessToken;
import com.yhl.oauthServer.entity.ResourceServer;
import com.yhl.oauthServer.service.OAuthAccessTokenService;
import com.yhl.oauthServer.service.ResourceServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("tokenGet")
public class TokenGetEndPoint {
    @Autowired
    private OAuthAccessTokenService oAuthAccessTokenService;
    @Autowired
    private ResourceServerService resourceServerService;
    private  static final   String  ID ="id";
    private  static final   String  SECRET ="remark";
    private  static final   String  TOKENID ="tokenId";
    private  static final   String  RESOURCESECRET ="resourceSecret";
    private  static final   String  RESOURCEID ="resourceId";
    @PostMapping("token")
    public String getAccessToken(@RequestBody Map<String,String> map) throws HttpRequestMethodNotSupportedException {
       Predicate predicate = resourceServerService.getWhereBuildUtil().addEq(ID,map.get(RESOURCEID))
                                                    .and()
                                                    .addEq(SECRET,map.get(RESOURCESECRET))
                                                    .and()
                                                    .end();
        List<ResourceServer> resourceServer =(List<ResourceServer>)resourceServerService.findbyPredicate(predicate).getData();
       if (resourceServer.isEmpty()){
            throw new HttpRequestMethodNotSupportedException("资源服务器不存在");
        }
        Predicate predicate1 =  oAuthAccessTokenService.getWhereBuildUtil().addEq(TOKENID,map.get(TOKENID)).and().end();
        List<OAuthAccessToken> list =(List<OAuthAccessToken> ) oAuthAccessTokenService.findbyPredicate(predicate1).getData();
        if (list.isEmpty()){
            throw new HttpRequestMethodNotSupportedException("token不存在");
        }
        return JSONObject.toJSONString(list.get(0));
    }

}
