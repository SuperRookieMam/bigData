package com.yhl.oauthServer.entity;

import com.yhl.base.entity.BaseEntity;
import com.yhl.oauthCommon.entity.OAthGrantedAuthorityDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "oath_granted_authority",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"client_id", "role_info", "api_uri"})},
        indexes = {@Index(columnList = "client_id")
                , @Index(columnList = "role_info")})
public class OAthGrantedAuthority extends BaseEntity {
    private static final long serialVersionUID = 4062924753193768577L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //客户端id
    @Column(name = "client_id")
    private String clientId;

    /*apiName*/
    @Column(name = "api_name")
    private String apiName;
    /*api描述*/
    @Column(name = "api_description")
    private String apiDescription;

    /*资源定位*/
    @Column(name = "api_uri")
    private String apiUri;

    @ManyToOne
    @JoinColumn(name = "role_info")
    private RoleInfo roleInfo;

    //对此接口的读写权限,如果多个
    @Column(name = "method")
    @Enumerated(EnumType.STRING)
    private HttpMethod method = HttpMethod.GET;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "macther_type")
    private String mactherType;


    public String getAuthority() {
        String jsonStr="{";
               jsonStr+="\"companyId\":\""+roleInfo.getCompanyId()+"\",";
               jsonStr+="\"rolName\":\""+roleInfo.getRolName()+"\",";
               jsonStr+="\"apiUri\":\""+apiUri+"\",";
               jsonStr+="\"method\":\""+method.name()+"\",";
               jsonStr+="\"mactherType\":\""+mactherType+"\"}";
        return jsonStr;
    }
    public static Set<OAthGrantedAuthorityDto> tooAthGrantedAuthorityDto(Collection<OAthGrantedAuthority> oAthGrantedAuthoritys){
        Iterator<OAthGrantedAuthority> iterator = oAthGrantedAuthoritys.iterator();
        Set<OAthGrantedAuthorityDto> set = new HashSet<>();
        while (iterator.hasNext()) {
            OAthGrantedAuthority oAthGrantedAuthority =iterator.next();
            OAthGrantedAuthorityDto oAthGrantedAuthorityDto = new OAthGrantedAuthorityDto();
            oAthGrantedAuthorityDto.setApiDescription(oAthGrantedAuthority.getApiDescription());
            oAthGrantedAuthorityDto.setApiName(oAthGrantedAuthority.getApiName());
            oAthGrantedAuthorityDto.setApiUri(oAthGrantedAuthority.getApiUri());
            oAthGrantedAuthorityDto.setClientId(oAthGrantedAuthority.getClientId());
            oAthGrantedAuthorityDto.setRoleInfo(RoleInfo.toRoleInfoDto(oAthGrantedAuthority.getRoleInfo()));
            oAthGrantedAuthorityDto.setMethod(oAthGrantedAuthority.getMethod());
            oAthGrantedAuthorityDto.setMactherType(oAthGrantedAuthority.getMactherType());
            oAthGrantedAuthorityDto.setCompanyId(oAthGrantedAuthority.getCompanyId());
            set.add(oAthGrantedAuthorityDto);
        }
        return set;
    }

    public static OAthGrantedAuthorityDto tooAthGrantedAuthorityDto(OAthGrantedAuthority oAthGrantedAuthority){
        OAthGrantedAuthorityDto oAthGrantedAuthorityDto = new OAthGrantedAuthorityDto();
        oAthGrantedAuthorityDto.setApiDescription(oAthGrantedAuthority.getApiDescription());
        oAthGrantedAuthorityDto.setApiName(oAthGrantedAuthority.getApiName());
        oAthGrantedAuthorityDto.setApiUri(oAthGrantedAuthority.getApiUri());
        oAthGrantedAuthorityDto.setClientId(oAthGrantedAuthority.getClientId());
        oAthGrantedAuthorityDto.setRoleInfo(RoleInfo.toRoleInfoDto(oAthGrantedAuthority.getRoleInfo()));
        oAthGrantedAuthorityDto.setMethod(oAthGrantedAuthority.getMethod());
        oAthGrantedAuthorityDto.setMactherType(oAthGrantedAuthority.getMactherType());
        oAthGrantedAuthorityDto.setCompanyId(oAthGrantedAuthority.getCompanyId());
        return oAthGrantedAuthorityDto;
    }
    public static OAthGrantedAuthority copyProperties(OAthGrantedAuthorityDto oAthGrantedAuthorityDto){
        OAthGrantedAuthority oAthGrantedAuthority = new OAthGrantedAuthority();
        oAthGrantedAuthority.setApiDescription(oAthGrantedAuthorityDto.getApiDescription());
        oAthGrantedAuthority.setApiName(oAthGrantedAuthorityDto.getApiName());
        oAthGrantedAuthority.setApiUri(oAthGrantedAuthorityDto.getApiUri());
        oAthGrantedAuthority.setClientId(oAthGrantedAuthorityDto.getClientId());
        oAthGrantedAuthority.setRoleInfo(RoleInfo.copyPropertis(oAthGrantedAuthorityDto.getRoleInfo()));
        oAthGrantedAuthority.setMethod(oAthGrantedAuthorityDto.getMethod());
        oAthGrantedAuthority.setMactherType(oAthGrantedAuthorityDto.getMactherType());
        oAthGrantedAuthority.setCompanyId(oAthGrantedAuthorityDto.getCompanyId());
        return oAthGrantedAuthority;
    }

}
