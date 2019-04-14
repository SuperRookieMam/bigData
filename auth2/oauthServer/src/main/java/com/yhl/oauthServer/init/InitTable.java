package com.yhl.oauthServer.init;

import com.yhl.oauthServer.dao.*;
import com.yhl.oauthServer.entity.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Conditional(InitCondition.class)
public class InitTable  implements InitializingBean {
    @Autowired
    AuthorizedGrantTypeDao authorizedGrantTypeDao;
    @Autowired
    ClientRegisteredRedirectUriDao clientRegisteredRedirectUriDao;
    @Autowired
    ClientScopeDao clientScopeDao;
    @Autowired
    CompanyDao companyDao;
    @Autowired
    DepartmentDao departmentDao;
    @Autowired
    OAthGrantedAuthorityDao oAthGrantedAuthorityDao;
    @Autowired
    OAthGrantedAuthorityMapDao oAthGrantedAuthorityMapDao;
    @Autowired
    OAthUserDetailesDao oAthUserDetailesDao;
    @Autowired
    OAuthAccessTokenDao oAuthAccessTokenDao;
    @Autowired
    OAuthClientDetailsDao oAuthClientDetailsDao;
    @Autowired
    OAuthCodeDao oAuthCodeDao;
    @Autowired
    OAuthRefreshTokenDao oAuthRefreshTokenDao;
    @Autowired
    ResourceServerClientDao resourceServerClientDao;
    @Autowired
    ResourceServerDao resourceServerDao;
    @Autowired
    RoleInfoDao roleInfoDao;
    @Autowired
    UserApprovalDao userApprovalDao;
    @Autowired
    UserRoleDao userRoleDao;

    @Override
    public void afterPropertiesSet() throws Exception {
    }
    @Transactional(value = "jpaTransactionManager")
    public  void  init(){
        Company company =new Company();
        company.setBusinessLicense("绝世好男人");
        company.setBusinessLicenseImage("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3715031121,1461812215&fm=26&gp=0.jpg");
        company.setCompanyName("绝世好男人");
        company.setLegalPersonId("1");
        company.setOrganizationCode("123456789");
        company.setSecret("123456");
        companyDao.insertByEntity(company);

        Department department =new Department();
        department.setCompanyId(1l);
        department.setCompanyName(company.getCompanyName());
        department.setDepartmentName("美男部");
        department.setPid("0");
        department.setPrincipalMan("绝世好男人");
        departmentDao.insertByEntity(department);

        RoleInfo roleInfo =new RoleInfo();
        roleInfo.setCompanyId(company.getId());
        roleInfo.setCompanyName(company.getCompanyName());
        roleInfo.setDepartmentId(department.getId());
        roleInfo.setDepartmentName(department.getDepartmentName());
        roleInfo.setRolName("世界最帅美男");
        roleInfoDao.insertByEntity(roleInfo);

        UserRole userRole =new UserRole();
        userRole.setCompanyId(company.getId());
        userRole.setRoleInfo(roleInfo);
        userRole.setUserName("绝世好男人");
        userRoleDao.insertByEntity(userRole);

        OAthUserDetailes user =new OAthUserDetailes();
        user.setUserName("绝世好男人");
        user.setPassWord("$2a$10$hjI6o5xdOxaxGnqNaFzwwOUnXEvUOAASQMKXPNZ5W9o18skQBwcS6");
        user.setCredentials("$2a$10$hjI6o5xdOxaxGnqNaFzwwOUnXEvUOAASQMKXPNZ5W9o18skQBwcS6");
        user.setEnabled(true);
        user.setLock(false);
        user.setExpired(false);
        user.setHeadImage("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3715031121,1461812215&fm=26&gp=0.jpg");
        oAthUserDetailesDao.insertByEntity(user);

        OAuthClientDetails clientDetails =new OAuthClientDetails();
        clientDetails.setAccessTokenValiditySeconds(60000);
        clientDetails.setArchived(1);
        clientDetails.setAutoApprove(true);
        clientDetails.setClientId("zuul");
        clientDetails.setCompanyId(company.getId());
        clientDetails.setClientSecret("$2a$10$hjI6o5xdOxaxGnqNaFzwwOUnXEvUOAASQMKXPNZ5W9o18skQBwcS6");
        clientDetails.setRefreshTokenValiditySeconds(60000);
        clientDetails.setTrusted(1);
        oAuthClientDetailsDao.insertByEntity(clientDetails);

        ResourceServer resourceServer =new ResourceServer();
        resourceServer.setCompanyId(company.getId());
        resourceServer.setIsUse(1);
        resourceServer.setName("zuul");
        resourceServer.setRemark("123456");
        resourceServer.setRegisterUrl("http://www.baidu.com");
        resourceServerDao.insertByEntity(resourceServer);

        ResourceServerClient resourceServerClient =new ResourceServerClient();
        resourceServerClient.setClientId("zuul");
        resourceServerClient.setCompanyId(company.getId());
        resourceServerClient.setResourceId(1l);
        resourceServerClientDao.insertByEntity(resourceServerClient);

        OAthGrantedAuthority oAthGrantedAuthority =new OAthGrantedAuthority();
        oAthGrantedAuthority.setApiDescription("这是什么");
        oAthGrantedAuthority.setApiName("张氏");
        oAthGrantedAuthority.setApiUri("/oauht/tokenGet");
        oAthGrantedAuthority.setMethod(HttpMethod.GET);
        oAthGrantedAuthority.setRoleInfo(roleInfo);
        oAthGrantedAuthority.setMactherType("url");
        oAthGrantedAuthority.setClientId("zuul");
        oAthGrantedAuthority.setCompanyId(company.getId());
        oAthGrantedAuthorityDao.insertByEntity(oAthGrantedAuthority);

        OAthGrantedAuthorityMap oAthGrantedAuthorityMap =new OAthGrantedAuthorityMap();
        oAthGrantedAuthorityMap.setOAthGrantedAuthority(oAthGrantedAuthority);
        oAthGrantedAuthorityMap.setClientId("zuul");
        oAthGrantedAuthorityMap.setRoleInfo(roleInfo);
        oAthGrantedAuthorityMapDao.insertByEntity(oAthGrantedAuthorityMap);

        AuthorizedGrantType authorizedGrantType =new AuthorizedGrantType();
        authorizedGrantType.setClientId("zuul");
        authorizedGrantType.setCompanyId(company.getId());
        authorizedGrantType.setGrantType("authorization_code");
        authorizedGrantTypeDao.insertByEntity(authorizedGrantType);

        ClientRegisteredRedirectUri uri =new ClientRegisteredRedirectUri();
        uri.setClientId("zuul");
        uri.setCompanyId(company.getId());
        uri.setRedirectUri("http://www.baidu.com");
        clientRegisteredRedirectUriDao.insertByEntity(uri);

        ClientScope clientScope =new ClientScope();
        clientScope.setClientId("zuul");
        clientScope.setCompanyId(company.getId());
        clientScope.setScope("read");
        clientScopeDao.insertByEntity(clientScope);


    }
}
