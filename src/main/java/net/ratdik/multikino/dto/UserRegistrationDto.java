package net.ratdik.multikino.dto;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserRegistrationDto {
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String password;
}