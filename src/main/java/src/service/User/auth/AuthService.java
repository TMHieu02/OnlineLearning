package src.service.User.auth;

import src.service.User.Dto.UserCreateDto;
import src.service.User.Dto.UserDto;

public interface AuthService {
    UserDto createUser(UserCreateDto userCreateDTO);

}