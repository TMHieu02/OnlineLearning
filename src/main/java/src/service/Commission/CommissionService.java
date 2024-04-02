package src.service.Commission;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import src.config.exception.NotFoundException;

import src.model.Commission;
import src.repository.CommissionRepository;
import src.service.Commission.Dto.CommissionDto;
import src.service.Commission.Dto.CommissionCreateDto;
import src.service.Commission.Dto.CommissionUpdateDto;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Service
public class CommissionService {
    @Autowired
    private CommissionRepository commissionRepository;
    @Autowired
    private ModelMapper toDto;
    @PersistenceContext
    EntityManager em;
    @Async
    public CompletableFuture<List<CommissionDto>> getAll() {
        return CompletableFuture.completedFuture(
                commissionRepository.findAll().stream().map(
                        x -> toDto.map(x, CommissionDto.class)
                ).collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<CommissionDto> getOne(int id) {
        Commission commission = commissionRepository.findById(id).orElse(null);
        if (commission == null) {
            throw new NotFoundException("Không tìm thấy sản phẩm với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(commission, CommissionDto.class));
    }
    @Async
    public CompletableFuture<Commission> create(CommissionCreateDto input) {
        CompletableFuture<Commission> future = new CompletableFuture<>();
        Commission newCommission = new Commission();
        newCommission.setName(input.getName());
        newCommission.setDescription(input.getDescription());
        newCommission.setCost(input.getCost());
        Commission savedCommission = commissionRepository.save(newCommission);
        future.complete(savedCommission);
        return future;
    }

    @Async
    public CompletableFuture<CommissionDto> update(int id, CommissionUpdateDto commissions) {
        Commission existingCommission = commissionRepository.findById(id).orElse(null);
        if (existingCommission == null)
            throw new NotFoundException("Unable to find Commission!");
        BeanUtils.copyProperties(commissions, existingCommission);
        return CompletableFuture.completedFuture(toDto.map(commissionRepository.save(existingCommission), CommissionDto.class));
    }
    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<Commission> commissionOptional = commissionRepository.findById(id);
        if (!commissionOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            Commission commission = commissionOptional.get();
            commission.setIsDeleted(true);
            commission.setUpdateAt(new Date(new java.util.Date().getTime()));
            commissionRepository.save(commission);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }

}
