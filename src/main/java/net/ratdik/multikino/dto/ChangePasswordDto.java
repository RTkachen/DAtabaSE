// ChangePasswordDto.java
package net.ratdik.multikino.dto;
import lombok.Data;

@Data
public class ChangePasswordDto {
    private String oldPassword;
    private String newPassword;
    private String newPasswordConfirm;
}