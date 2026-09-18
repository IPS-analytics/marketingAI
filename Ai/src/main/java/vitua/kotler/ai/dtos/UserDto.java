package vitua.kotler.ai.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String userName;

    private String userSurname;

    private String email;

    private String password;
}