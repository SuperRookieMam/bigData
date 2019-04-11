package com.yhl.securityCommon.provider;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * 如果资源服务器和Oaut服务器在一个项目就用这个来获取Token
 */
public interface TokenStoreResourceServerTokenServices extends ResourceServerTokenServices, InitializingBean {

}
