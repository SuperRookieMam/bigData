package com.yhl.oauthServer.service.impl;

import com.yhl.base.componet.dto.ResultDto;
import com.yhl.base.service.impl.BaseServiceImpl;
import com.yhl.oauthServer.entity.UserApproval;
import com.yhl.oauthServer.service.UserApprovalService;
import com.yhl.orm.componet.util.PredicateBuilder;
import com.yhl.orm.componet.util.WhereBuilder;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
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
        revokeApprovals(approvals);
        List<UserApproval> userApprovals = UserApproval.approvalToUserApproval(approvals);
        revokeApprovals(approvals);
        ResultDto resultDto = insertByList(userApprovals);
        boolean flage = ((int) resultDto.getData()) == approvals.size();
        return flage;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Exception.class)
    public boolean revokeApprovals(Collection<Approval> approvals) {
       Iterator<Approval> iterator = approvals.iterator();
       int flage =0;
        while (iterator.hasNext()){
            Approval ele =iterator.next();
            WhereBuilder whereBuilder =getWhereBuilder();
            PredicateBuilder predicateBuilder =whereBuilder.getPredicateBuilder();
            TypedQuery typedQuery = whereBuilder.where(
                        predicateBuilder.addEq(CLIENTID, ele.getClientId())
                                        .and()
                                        .addEq(USERID,ele.getUserId())
                                        .and()
                                        .addEq(SCOPE,ele.getScope())
                                        .end()
                         ).buildTypeQuery();
            deleteByTypeQuery(typedQuery);
            flage ++;
        }
        return  flage==approvals.size();
    }

    @Override
    public Collection<Approval> getApprovals(String userId, String clientId) {
        WhereBuilder whereBuilder =getWhereBuilder();
        PredicateBuilder predicateBuilder =whereBuilder.getPredicateBuilder();
        TypedQuery typedQuery =whereBuilder.where(
                                predicateBuilder
                                        .addEq(USERID,userId)
                                        .and()
                                        .addEq(CLIENTID,clientId)
                                        .end()
                                      ).buildTypeQuery();
        ResultDto resultDto = findbyTypeQuery(typedQuery);
        List<Approval> approvals = UserApproval.userApprovalToApproval((List<UserApproval>) resultDto.getData());
        return approvals;
    }
}
