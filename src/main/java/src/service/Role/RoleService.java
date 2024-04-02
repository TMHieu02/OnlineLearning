package src.service.Role;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import src.config.exception.NotFoundException;
import src.model.Role;
import src.repository.RoleRepository;
import src.service.Role.Dto.RoleDto;
import src.service.Role.Dto.RoleCreateDto;
import src.service.Role.Dto.RoleUpdateDto;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModelMapper toDto;
    @PersistenceContext
    EntityManager em;
    @Async
    public CompletableFuture<List<RoleDto>> getAll() {
        return CompletableFuture.completedFuture(
                roleRepository.findAll().stream().map(
                        x -> toDto.map(x, RoleDto.class)
                ).collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<RoleDto> getOne(int id) {
        Role role = roleRepository.findById(id).orElse(null);
        if (role == null) {
            throw new NotFoundException("Không tìm thấy quyền với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(role, RoleDto.class));
    }
    @Async
    public CompletableFuture<RoleDto> create(RoleCreateDto input) {
        Role role = roleRepository.save(toDto.map(input, Role.class));
        return CompletableFuture.completedFuture(toDto.map(role, RoleDto.class));
    }
    @Async
    public CompletableFuture<RoleDto> update(int id, RoleUpdateDto roles) {
        Role existingRole = roleRepository.findById(id).orElse(null);
        if (existingRole == null)
            throw new NotFoundException("Unable to find role!");
        BeanUtils.copyProperties(roles, existingRole);
        return CompletableFuture.completedFuture(toDto.map(roleRepository.save(existingRole), RoleDto.class));
    }
    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        if (!roleOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            Role role = roleOptional.get();
            role.setIsDeleted(true);
            role.setUpdateAt(new Date(new java.util.Date().getTime()));
            roleRepository.save(role);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }
}

