package com.yhl.oauthCommon.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.provider.approval.Approval;

import java.util.Date;

@Getter
@Setter
public class UserApprovalDto {

    private static final long serialVersionUID = 789452490908188301L;

    private String userId;

    private String clientId;


    private String scope;

    private Approval.ApprovalStatus status;

    private Date expiresAt;

    private Date lastUpdatedAt = new Date();


}
