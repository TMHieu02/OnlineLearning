

package src.service.User;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import src.config.auth.JwtTokenUtil;
import src.config.dto.PagedResultDto;

import src.config.dto.Pagination;
import src.config.exception.NotFoundException;
import src.config.utils.ApiQuery;
import src.model.User;
import src.repository.RoleRepository;
import src.repository.UserRepository;
import src.service.User.Dto.UserCreateDto;
import src.service.User.Dto.UserDto;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private UserRepository userRepository;
    private ModelMapper toDto;
    private RoleRepository roleRepository;
    int roleId;
    @Autowired
    public UserService(UserRepository userRepository, ModelMapper toDto, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.toDto = toDto;
        this.roleRepository = roleRepository;
    }
    @Async
    public CompletableFuture<List<UserDto>> getAll() {
        return CompletableFuture.completedFuture(
                userRepository.findAll().stream().map(
                        x -> toDto.map(x, UserDto.class)
                ).collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<UserDto> getOne(int id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy người dùng với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(user, UserDto.class));
    }

   @Async
    public CompletableFuture<UserDto> create(UserCreateDto input) {
        roleId = roleRepository.findByName("user").orElse(null).getId();
        input.setPassword(JwtTokenUtil.hashPassword(input.getPassword()));
        input.setRoleId(roleId);
        User user = userRepository.save(toDto.map(input, User.class));
        toDto.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return CompletableFuture.completedFuture(toDto.map(user, UserDto.class));
    }
    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            User user = userOptional.get();
            user.setIsDeleted(true);
            userRepository.save(user);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }
    @Async
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        request.setAttribute("id", user.getId());
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        } else {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRoleByRoleId().getName()));
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
    }
    public User updateUser(int userId, Map<String, Object> fieldsToUpdate) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            updateUserFields(user, fieldsToUpdate);
            user.setUpdateAt(new Date());
            userRepository.save(user);
            return user;
        }
        return null;
    }
    private void updateUserFields(User user, Map<String, Object> fieldsToUpdate) {
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            updateUserField(user, fieldName, value);
        }
    }
    private void updateUserField(User user, String fieldName, Object value) {
        switch (fieldName) {
            case "fullname":
                user.setFullname((String) value);
                break;
            case "email":
                user.setEmail((String) value);
                break;
            case "phone":
                user.setPhone((String) value);
                break;
            case "avatar":
                user.setAvatar((String) value);
                break;
            case "description":
                user.setDescription((String) value);
                break;
            case "bank_name":
                user.setBank_name((String) value);
                break;
            case "account_number":
                user.setAccount_number((String) value);
                break;
            case "account_name":
                user.setAccount_name((String) value);
                break;
            case "password":
                user.setPassword(JwtTokenUtil.hashPassword((String) value));
                break;
            case "roleId":
                user.setRoleId((int) value);
                break;
            default:
                break;
        }
    }

    @Async
    public CompletableFuture<List<UserDto>> findByRoleId(int roleId) {
        return CompletableFuture.completedFuture(
                userRepository.findByRoleId(roleId).stream().map(
                        x -> toDto.map(x, UserDto.class)
                ).collect(Collectors.toList()));
    }

    public String KhoaTaiKhoan(int id) {
        Optional<User> userOptinal = userRepository.findById(id);
        if (userOptinal.isPresent()) {
            User u1 = userOptinal.get();
            u1.setIsDeleted(true);
            userRepository.save(u1);
        }
        return "User will be block";
    }
    public String MoKhoaTaiKhoan(int id) {
        Optional<User> userOptinal = userRepository.findById(id);
        if (userOptinal.isPresent()) {
            User u1 = userOptinal.get();
            u1.setIsDeleted(false);
            userRepository.save(u1);
        }
        return "User will be unblock";
    }
}

