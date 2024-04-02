package src.service.User;

import src.service.IService;
import src.service.User.Dto.UserCreateDto;
import src.service.User.Dto.UserDto;
import src.service.User.Dto.UserUpdateDto;

import java.util.UUID;

public interface IUserService extends IService<UserDto, UserCreateDto, UserUpdateDto> {

}
