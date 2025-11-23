package com.ezh.Inventory.utils.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse{
    private String id;
    private String message;
    private Status status;
}
