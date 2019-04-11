package com.yhl.oauthServer.entity;

import com.yhl.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.oauth2.provider.approval.Approval;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_aproval",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "client_id", "scope"})},
        indexes = {@Index(columnList = "user_id")})
public class UserApproval extends BaseEntity {

    private static final long serialVersionUID = 5194945293219450500L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", length = 50)
    private String userId;


    @Column(name = "client_id", length = 50)
    private String clientId;


    @Column(name = "scope", length = 100)
    private String scope;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Approval.ApprovalStatus status;

    @Column(name = "expires_at")
    private Date expiresAt;

    @Column(name = "last_update_at")
    private Date lastUpdatedAt = new Date();


    public static List<UserApproval> approvalToUserApproval(Collection<Approval> approvals) {
        List<UserApproval> list = new ArrayList<>();
        Iterator<Approval> iterator = approvals.iterator();
        while (iterator.hasNext()) {
            Approval approval = iterator.next();
            UserApproval userApproval = new UserApproval();
            userApproval.setClientId(approval.getClientId());
            userApproval.setExpiresAt(approval.getExpiresAt());
            userApproval.setLastUpdatedAt(approval.getLastUpdatedAt());
            userApproval.setScope(approval.getScope());
            userApproval.setUserId(approval.getUserId());
            list.add(userApproval);
        }
        return list;
    }

    public static List<Approval> userApprovalToApproval(Collection<UserApproval> userApprovals) {
        List<Approval> list = new ArrayList<>();
        Iterator<UserApproval> iterator = userApprovals.iterator();
        while (iterator.hasNext()) {
            UserApproval userApproval = iterator.next();
            Approval approval = new Approval(
                    userApproval.getUserId(),
                    userApproval.getClientId(),
                    userApproval.getScope(),
                    userApproval.getExpiresAt(),
                    userApproval.getStatus(),
                    userApproval.getLastUpdatedAt()
            );
            list.add(approval);
        }
        return list;
    }
}
