package com.yhl.oauthServer.service.impl;

import com.yhl.base.componet.dto.ResultDto;
import com.yhl.base.service.impl.BaseServiceImpl;
import com.yhl.oauthServer.entity.UserApproval;
import com.yhl.oauthServer.service.UserApprovalService;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


@Service
public class UserApprovalServiceImpl extends BaseServiceImpl<UserApproval, Long> implements UserApprovalService {

    private final String USERID = "userId";
    private final String CLIENTID = "clientId";
    private final String SCOPE = "scope";

    @Override
    @Transactional(value = "transactionManagerPrimary", rollbackFor = Exception.class)
    public boolean addApprovals(Collection<Approval> approvals) {
        List<UserApproval> userApprovals = UserApproval.approvalToUserApproval(approvals);
        ResultDto resultDto = insertByList(userApprovals);
        boolean flage = ((int) resultDto.getData()) == approvals.size();
        return flage;
    }

    @Override
    @Transactional(value = "transactionManagerPrimary", rollbackFor = Exception.class)
    public boolean revokeApprovals(Collection<Approval> approvals) {
        Iterator<Approval> iterator = approvals.iterator();
        int m = 0;
        while (iterator.hasNext()) {
            Approval approval = iterator.next();
            WhereCondition whereCondition = new WhereCondition();
            whereCondition.and().addEq(CLIENTID, approval.getClientId())
                    .addEq(USERID, approval.getUserId())
                    .addEq(SCOPE, approval.getScope());
            deleteByWhereCondition(whereCondition);
            m++;
        }
        return m == approvals.size();
    }

    @Override
    public Collection<Approval> getApprovals(String userId, String clientId) {
        WhereCondition whereCondition = new WhereCondition();
        whereCondition.and().addEq(USERID, userId).addEq(CLIENTID, clientId);
        ResultDto resultDto = findByParams(whereCondition);
        List<Approval> approvals = UserApproval.userApprovalToApproval((List<UserApproval>) resultDto.getData());
        return approvals;
    }
}
