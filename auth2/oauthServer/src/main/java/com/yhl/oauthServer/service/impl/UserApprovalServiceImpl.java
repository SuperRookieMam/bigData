package com.yhl.oauthServer.service.impl;

import com.yhl.base.componet.dto.ResultDto;
import com.yhl.base.service.impl.BaseServiceImpl;
import com.yhl.oauthServer.entity.UserApproval;
import com.yhl.oauthServer.service.UserApprovalService;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


@Service
public class UserApprovalServiceImpl extends BaseServiceImpl<UserApproval, Long> implements UserApprovalService {

    private final String USERID = "userId";
    private final String CLIENTID = "clientId";
    private final String SCOPE = "scope";

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public boolean addApprovals(Collection<Approval> approvals) {
        List<UserApproval> userApprovals = UserApproval.approvalToUserApproval(approvals);
        ResultDto resultDto = insertByList(userApprovals);
        boolean flage = ((int) resultDto.getData()) == approvals.size();
        return flage;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public boolean revokeApprovals(Collection<Approval> approvals) {
        Iterator<Approval> iterator = approvals.iterator();
        int m = 0;
        while (iterator.hasNext()) {
            Approval approval = iterator.next();
            Predicate predicate = getWhereBuildUtil().addEq(CLIENTID, approval.getClientId())
                               .and()
                               .addEq(USERID,approval.getUserId())
                               .and()
                               .addEq(SCOPE,approval.getScope())
                               .and()
                               .end();
            deleteByPredicate(predicate);
            m++;
        }
        return m == approvals.size();
    }

    @Override
    public Collection<Approval> getApprovals(String userId, String clientId) {
        Predicate predicate = getWhereBuildUtil().addEq(USERID,userId)
                                                 .and()
                                                 .addEq(CLIENTID,clientId)
                                                 .and()
                                                 .end();
        ResultDto resultDto = findbyPredicate(predicate);
        List<Approval> approvals = UserApproval.userApprovalToApproval((List<UserApproval>) resultDto.getData());
        return approvals;
    }
}
