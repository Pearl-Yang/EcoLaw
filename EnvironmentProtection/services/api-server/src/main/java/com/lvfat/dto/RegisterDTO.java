package com.lvfat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户注册DTO
 */
@Data
@Schema(description = "用户注册请求")
public class RegisterDTO {
    
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "邮箱")
    private String email;
}
