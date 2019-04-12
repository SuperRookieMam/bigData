package com.yhl.resourceServer.componet.featur;


import com.yhl.oauthCommon.entity.OAthUserDetailesDto;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Collections;
import java.util.Map;

// userdetail的map对象转换成为自定义的userdetail
public class UserDetailsDtoPrincipalExtractor implements PrincipalExtractor {
    private final String USERNAME= "userName";
    private final String PASSWORD= "passWord";
    private final String HEADIMAGE= "headImage";
    private final String AUTHORITIES = "authorities";
    private final String ISEXPIRED= "isExpired";
    private final String ISLOCK= "isLock";
    private final String CREDENTIALS= "credentials";
    private final String ISENABLED= "isEnabled";
    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        OAthUserDetailesDto userDetails = new OAthUserDetailesDto();
        userDetails.setAuthorities(Collections.emptyList());
        //因为没有用凭证这里放tokenId
        userDetails.setCredentials(map.get(CREDENTIALS).toString());
        userDetails.setLock((boolean)map.get(ISLOCK));
        userDetails.setEnabled((boolean)map.get(ISENABLED));
        userDetails.setExpired((boolean)map.get(ISEXPIRED));
        userDetails.setCredentials(map.get(HEADIMAGE).toString());
        userDetails.setUserName(map.get(USERNAME).toString());
        userDetails.setPassWord(map.get(PASSWORD).toString());
        return userDetails;
    }
}
