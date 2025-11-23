package com.ezh.Inventory.utils.common.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Data
@Component
@RequestScope
public class UserContextInfo {
    @org.jetbrains.annotations.NotNull
    private String userUUID;

    @NotNull
    private String mobileNumber;

    private String roleUUID;

    @NotNull
    private String tenantUUID;

    private String franchiseUUID;

}
