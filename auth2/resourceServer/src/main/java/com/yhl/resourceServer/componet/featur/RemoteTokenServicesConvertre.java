package com.yhl.resourceServer.componet.featur;

import com.alibaba.fastjson.JSONObject;
import com.yhl.oauthCommon.entity.OAuthAccessTokenDto;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * 重写这个类的原因就是因为我吧权限存在AccessTken中的
 * 但是默认实现不允许获取token,所以复写这个类,
 * 本来数据库查也可以但是，以后有可能分开部署所以还是远程获取
 * 所有源码均参考源码，值做一个方法的实现
 * */
@Setter
public class RemoteTokenServicesConvertre extends RemoteTokenServices implements  InitializingBean {
    protected final Log logger = LogFactory.getLog(getClass());

    private RestTemplate template ;


    private String resourceId;

    private String resourceSecret;

    private String url;

    private  String REOURCEID ="resourceId";
    private  String RESOURCESECRET ="resourceSecret";
    private  String TOKENID ="tokenId";

    public RemoteTokenServicesConvertre() {
        super();
        template =new RestTemplate();
    }

    // 复写这个然后远程获取token
    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        JSONObject params =new JSONObject();
        params.put(REOURCEID,resourceId);
        params.put(RESOURCESECRET,resourceSecret);
        params.put(TOKENID,accessToken);
        HttpEntity<String> formEntity = new HttpEntity<>(params.toString(), headers);
        String result = template.postForObject(url,formEntity , String.class);
        OAuthAccessTokenDto oAuthAccessTokenDto =JSONObject.parseObject(result, OAuthAccessTokenDto.class);
        return  oAuthAccessTokenDto;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(resourceId, "tokenStore must be set");
        Assert.notNull(resourceSecret, "resourceSecret must be set");
        Assert.notNull(url, "url must be set");

    }
}
