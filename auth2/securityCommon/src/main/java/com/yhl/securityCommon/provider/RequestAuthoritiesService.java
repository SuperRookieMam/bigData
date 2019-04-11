package com.yhl.securityCommon.provider;


import com.yhl.securityCommon.access.RequestAuthorityAttribute;

import java.util.List;

/**
 * 自定义的服务提供暴露接口
 * 这个主要是你哪来反悔你想要的东西的
 */
public interface RequestAuthoritiesService {
    List<RequestAuthorityAttribute> listAllAttributes(String token);
}
