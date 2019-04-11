package com.yhl.oauthCommon.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
public class OAthGrantedAuthorityDto implements GrantedAuthority {
    private static final long serialVersionUID = 4062924753193768577L;

    private String clientId;


    private String apiName;


    private String apiDescription;


    private String apiUri;


    private RoleInfoDto roleInfo;

    //对此接口的读写权限,如果多个
    private HttpMethod method = HttpMethod.GET;

    private Long companyId;

    private String mactherType;


    public String getAuthority() {
        String jsonStr="{";
        jsonStr+="\"companyId\":\""+roleInfo.getCompanyId()+"\",";
        jsonStr+="\"rolName\":\""+roleInfo.getRolName()+"\",";
        jsonStr+="\"apiUri\":\""+apiUri+"\",";
        jsonStr+="\"method\":\""+method+"\",";
        jsonStr+="\"method\":\""+mactherType+"\"}";
        return jsonStr;
    }

}
