package com.zy.webmail.cart.Dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MemberWebDto {

    private Long userId;

    private String userKey;

    private Boolean tempUser = false;
}
