package com.yhl.oauthServer.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhl.oauthCommon.entity.OAthGrantedAuthorityDto;
import com.yhl.oauthServer.dao.OAthGrantedAuthorityDao;
import com.yhl.oauthServer.dao.OAthGrantedAuthorityMapDao;
import com.yhl.oauthServer.entity.OAthGrantedAuthority;
import com.yhl.oauthServer.entity.OAthGrantedAuthorityMap;
import com.yhl.oauthServer.entity.OAuthAccessToken;
import com.yhl.oauthServer.entity.ResourceServer;
import com.yhl.oauthServer.service.OAthGrantedAuthorityMapService;
import com.yhl.oauthServer.service.OAuthAccessTokenService;
import com.yhl.oauthServer.service.ResourceServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("tokenGet")
public class TokenGetEndPoint {
    @Autowired
    private OAuthAccessTokenService oAuthAccessTokenService;
    @Autowired
    private ResourceServerService resourceServerService;
    @Autowired
    private OAthGrantedAuthorityMapService oAthGrantedAuthorityMapService;
    private  static final   String  ID ="id";
    private  static final   String  SECRET ="remark";
    private  static final   String  TOKENID ="tokenId";
    private  static final   String  RESOURCESECRET ="resourceSecret";
    private  static final   String  RESOURCEID ="resourceId";
    private  static final   String   CLIENTID="clientId";
    private  static final   String   ROLEINFO="roleInfo.id";

    @PostMapping("token")
    public String getAccessToken(@RequestBody Map<String,String> map) throws HttpRequestMethodNotSupportedException {
       TypedQuery typedQuery = resourceServerService.getWhereBuildUtil().beginAnSeclect().beginAnWhere()
                                                    .addEq(ID,map.get(RESOURCEID))
                                                    .and()
                                                    .addEq(SECRET,map.get(RESOURCESECRET))
                                                    .and()
                                                    .end().buildTypedQuery();
        List<ResourceServer> resourceServer =(List<ResourceServer>)resourceServerService.findbyTypeQuery(typedQuery).getData();
       if (resourceServer.isEmpty()){
            throw new HttpRequestMethodNotSupportedException("资源服务器不存在");
        }
        TypedQuery typedQuery1 = oAuthAccessTokenService.getWhereBuildUtil().beginAnSeclect().beginAnWhere().addEq(TOKENID,map.get(TOKENID)).and().end().buildTypedQuery();
        List<OAuthAccessToken> list =(List<OAuthAccessToken> ) oAuthAccessTokenService.findbyTypeQuery(typedQuery1).getData();
        if (list.isEmpty()){
            throw new HttpRequestMethodNotSupportedException("token不存在");
        }
        return JSONObject.toJSONString(list.get(0));
    }

    @PostMapping("getCanVisit")
    public String getCanVisit(@RequestBody Map<String,String> map) throws HttpRequestMethodNotSupportedException {
        TypedQuery typedQuery1 = oAuthAccessTokenService.getWhereBuildUtil().beginAnSeclect().beginAnWhere().addEq(TOKENID,map.get(TOKENID)).and().end().buildTypedQuery();
        List<OAuthAccessToken> list =(List<OAuthAccessToken> ) oAuthAccessTokenService.findbyTypeQuery(typedQuery1).getData();
        if (list.isEmpty()){
            throw new HttpRequestMethodNotSupportedException("token不存在");
        }
        OAuthAccessToken oAuthAccessToken =list.get(0);
        TypedQuery typedQuery =  oAthGrantedAuthorityMapService.getWhereBuildUtil().beginAnSeclect().beginAnWhere()
                                            .addEq(CLIENTID,oAuthAccessToken.getClientId())
                                            .and()
                                            .addEq(ROLEINFO,oAuthAccessToken.getRoleInfo().getId())
                                            .and().end().buildTypedQuery();
        List<OAthGrantedAuthorityMap> list1 =  (List<OAthGrantedAuthorityMap>) oAthGrantedAuthorityMapService.findbyTypeQuery(typedQuery).getData();
        JSONArray jsonArray  =new  JSONArray();
        list1.forEach(ele ->{
              JSONObject jsonObject =  new  JSONObject();
              jsonObject.put("apiUri",ele.getOAthGrantedAuthority().getApiUri());
              jsonObject.put("method",ele.getOAthGrantedAuthority().getMethod());
              jsonObject.put("mactherType",ele.getOAthGrantedAuthority().getMactherType());
              jsonArray.add(jsonObject);
        });
        return jsonArray.toJSONString();
    }
}
