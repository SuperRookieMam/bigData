package com.yhl.securityCommon.access;

import com.yhl.securityCommon.provider.RequestAuthoritiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/*封装你想要给用户反回的安全元素*/
public class RequestAuthoritiesFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private RequestAuthoritiesService requestAuthoritiesService;

    private Map<RequestInfo, RequestMatcher> requestMatchMap = new ConcurrentHashMap<RequestInfo, RequestMatcher>();

    private final  String ANT_PATH ="path";
    private final  String REGEXP ="regexp";
    private final  String ACCESSTOKEN ="access_token";

    public RequestAuthoritiesFilterInvocationSecurityMetadataSource() {
        super();
    }

    public RequestAuthoritiesFilterInvocationSecurityMetadataSource(
            RequestAuthoritiesService requestAuthoritiesService) {
        super();
        this.requestAuthoritiesService = requestAuthoritiesService;
    }


    @Autowired
    public void setRequestAuthoritiesService(RequestAuthoritiesService requestAuthoritiesService) {
        this.requestAuthoritiesService = requestAuthoritiesService;
    }
    //TODO : 之一这里如果返回的是空的collection 就不会执行投票的操作，这个坑踩了两天，
    // TODO：但是我我在这里已经比较了，如果不满足的情况集合为空，所以当列表为空反悔一个可访问集合
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        String accesstoken =request.getHeader(ACCESSTOKEN)==null?request.getParameter(ACCESSTOKEN):request.getHeader(ACCESSTOKEN);
        if (StringUtils.isEmpty(accesstoken)){
            return Collections.emptyList();
        }
        //比如说属性菜单
        List<RequestAuthorityAttribute> allAttributes = requestAuthoritiesService.listAllAttributes(accesstoken);
        // 过滤掉不符合规格的请求，看看有没有符合这个请求的属性
        Collection<ConfigAttribute>  ret = allAttributes.stream().filter(attribute -> match(request, attribute)).collect(Collectors.toList());
        RequestAuthorityAttribute requestAuthorityAttribute =new RequestAuthorityAttribute();
        if (ret.isEmpty()){
            requestAuthorityAttribute.setAccessVisit(false);
        }else {
            requestAuthorityAttribute.setAccessVisit(true);
        }
        List<ConfigAttribute> list =new ArrayList<>();
        list.add(requestAuthorityAttribute);
        //过滤掉
        return  list;
    }


    /*If available, returns all of the {@code ConfigAttribute}s defined by the implementing class.*/
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return Collections.emptySet();
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    private boolean match(HttpServletRequest request, RequestAuthorityAttribute attribute) {

        boolean b =getRequestMatcher(attribute).matches(request);
        return b;
    }

    private RequestMatcher getRequestMatcher(RequestAuthorityAttribute attribute) {
        String apiUri  = attribute.getApiUri();
        HttpMethod method = attribute.getMethod();
        String matchType = attribute.getMactherType();
        RequestInfo requestInfo = new RequestInfo(apiUri, method, matchType);
        RequestMatcher matcher = requestMatchMap.get(requestInfo);
        if (Objects.isNull(matcher)) {
            if (ANT_PATH.equals(matchType)) {
                matcher = new AntPathRequestMatcher(apiUri, method.toString());
            } else if (REGEXP.equals(matchType)) {
                matcher = new RegexRequestMatcher(apiUri, method.toString());
            }
            requestMatchMap.put(requestInfo, matcher);
        }
        return matcher;
    }

}

// 请求信息
class RequestInfo {
    private String apiUri;
    private HttpMethod method;
    private String matchType;

    public RequestInfo(String apiUri, HttpMethod method, String matchType) {
        super();
        this.apiUri = apiUri;
        this.method = method;
        this.matchType = matchType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((matchType == null) ? 0 : matchType.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((apiUri == null) ? 0 : apiUri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RequestInfo other = (RequestInfo) obj;
        if (matchType == null) {
            if (other.matchType != null)
                return false;
        } else if (!matchType.equals(other.matchType))
            return false;
        if (method == null) {
            if (other.method != null)
                return false;
        } else if (!method.equals(other.method))
            return false;
        if (apiUri == null) {
            if (other.apiUri != null)
                return false;
        } else if (!apiUri.equals(other.apiUri))
            return false;
        return true;
    }

}
