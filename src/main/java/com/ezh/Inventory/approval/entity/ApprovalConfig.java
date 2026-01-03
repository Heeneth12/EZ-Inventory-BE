package com.ezh.Inventory.approval.entity;

import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "approval_config",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "approval_type"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalConfig extends CommonSerializable {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_type", length = 50, nullable = false)
    private ApprovalType approvalType;

    @Column(name = "is_enabled")
    @Builder.Default
    private boolean isEnabled = true;

    // --- The Rules ---
    // Use this for Invoice Value, PO Value, Stock Value
    @Column(name = "threshold_amount")
    private BigDecimal thresholdAmount;

    // Use this for Discounts, Tax variance
    @Column(name = "threshold_percentage")
    private Double thresholdPercentage;

    // --- Role Management ---
    // Who is allowed to approve this?
    // e.g., "MANAGER", "ADMIN", "SUPERVISOR"
    @Column(name = "approver_role")
    private String approverRole;
}