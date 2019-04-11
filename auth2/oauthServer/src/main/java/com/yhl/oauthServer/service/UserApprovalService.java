package com.yhl.oauthServer.service;

import com.yhl.base.service.BaseService;
import com.yhl.oauthServer.entity.UserApproval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;

public interface UserApprovalService extends BaseService<UserApproval, Long>, ApprovalStore {
}
