package src.service.User.auth;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.config.auth.JwtTokenUtil;
import src.model.Role;
import src.model.User;
import src.repository.RoleRepository;
import src.repository.UserRepository;
import src.service.User.Dto.UserCreateDto;
import src.service.User.Dto.UserDto;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDto createUser(UserCreateDto userCreateDTO) {
        ModelMapper modelMapper = new ModelMapper();
        User user = modelMapper.map(userCreateDTO, User.class);

        user.setPassword(JwtTokenUtil.hashPassword(userCreateDTO.getPassword()));

        Optional<Role> userRoleOptional = roleRepository.findByName("user");

        if (userRoleOptional.isPresent()) {
            Role userRole = userRoleOptional.get();
            user.setRoleId(userRole.getId());

            User createdUser = userRepository.save(user);
            UserDto userDTO = modelMapper.map(createdUser, UserDto.class);

            return userDTO;
        } else {

            return null;
        }
    }


}
