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
import com.yhl.orm.componet.util.PredicateBuilder;
import com.yhl.orm.componet.util.WhereBuilder;
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
        WhereBuilder whereBuilder =resourceServerService.getWhereBuilder();
        PredicateBuilder predicateBuilder =whereBuilder.getPredicateBuilder();
        TypedQuery typedQuery = whereBuilder.where(
                             predicateBuilder.addEq(ID,map.get(RESOURCEID)) .and().addEq(SECRET,map.get(RESOURCESECRET)).end()
                            ).buildTypeQuery();
        List<ResourceServer> resourceServer =(List<ResourceServer>)resourceServerService.findbyTypeQuery(typedQuery).getData();
       if (resourceServer.isEmpty()){
            throw new HttpRequestMethodNotSupportedException("资源服务器不存在");
        }
        WhereBuilder whereBuilder1 =oAuthAccessTokenService.getWhereBuilder();
        PredicateBuilder predicateBuilder1 =whereBuilder1.getPredicateBuilder();

        TypedQuery typedQuery1 =whereBuilder1.where(
                 predicateBuilder1.addEq(TOKENID,map.get(TOKENID)).end()
                                    ).buildTypeQuery();
        List<OAuthAccessToken> list =(List<OAuthAccessToken> ) oAuthAccessTokenService.findbyTypeQuery(typedQuery1).getData();
        if (list.isEmpty()){
            throw new HttpRequestMethodNotSupportedException("token不存在");
        }
        return JSONObject.toJSONString(list.get(0));
    }

    @PostMapping("getCanVisit")
    public String getCanVisit(@RequestBody Map<String,String> map) throws HttpRequestMethodNotSupportedException {
        WhereBuilder whereBuilder =oAuthAccessTokenService.getWhereBuilder();
        PredicateBuilder predicateBuilder =whereBuilder.getPredicateBuilder();

        TypedQuery typedQuery1 = whereBuilder.where(
                predicateBuilder.addEq(TOKENID,map.get(TOKENID)).end()
                             ).buildTypeQuery();
        List<OAuthAccessToken> list =(List<OAuthAccessToken> ) oAuthAccessTokenService.findbyTypeQuery(typedQuery1).getData();
        if (list.isEmpty()){
            throw new HttpRequestMethodNotSupportedException("token不存在");
        }
        OAuthAccessToken oAuthAccessToken =list.get(0);

        WhereBuilder whereBuilder1 =oAthGrantedAuthorityMapService.getWhereBuilder();
        PredicateBuilder predicateBuilder1 =whereBuilder1.getPredicateBuilder();
        TypedQuery typedQuery = whereBuilder1.where(
                                predicateBuilder1.addEq(CLIENTID,oAuthAccessToken.getClientId())
                                                 .and()
                                                 .addEq(ROLEINFO,oAuthAccessToken.getRoleInfo().getId())
                                                 .end()

                            ).buildTypeQuery();
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
