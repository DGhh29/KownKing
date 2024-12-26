package com.dg.schoolhelp.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private Integer id;

    private String userName;

    private String userOpenid;

    private String userEmail;

    private String userAvatar;
}
