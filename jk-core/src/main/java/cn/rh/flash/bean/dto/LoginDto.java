package cn.rh.flash.bean.dto;

import lombok.Data;

/**
 * DESCRIPT
 */
@Data
public class LoginDto {
    private String username;
    private String password;
    private Long ggcode;
}
