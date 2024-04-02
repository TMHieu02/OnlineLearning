package src.service.OrderItem;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import src.config.dto.PagedResultDto;
import src.config.dto.Pagination;
import src.config.exception.NotFoundException;
import src.config.utils.ApiQuery;
import src.model.OrderItem;
import src.repository.OrderItemRepository;
import src.service.OrderItem.Dto.OrderItemCreateDto;
import src.service.OrderItem.Dto.OrderItemDto;
import src.service.OrderItem.Dto.OrderItemUpdateDto;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ModelMapper toDto;

    @PersistenceContext
    EntityManager em;

    @Async
    public CompletableFuture<List<OrderItemDto>> getAll() {
        return CompletableFuture.completedFuture(
                orderItemRepository.findAll().stream().map(
                        x -> toDto.map(x, OrderItemDto.class)
                ).collect(Collectors.toList()));
    }

    @Async
    public CompletableFuture<OrderItemDto> getOne(int id) {
        OrderItem orderItem = orderItemRepository.findById(id).orElse(null);
        if (orderItem == null) {
            throw new NotFoundException("Không tìm thấy quyền với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(orderItem, OrderItemDto.class));
    }

    @Async
    public CompletableFuture<OrderItemDto> create(OrderItemCreateDto input) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCourseId(input.getCourseId());
        orderItem.setOrderId(input.getOrderId());

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return CompletableFuture.completedFuture(toDto.map(savedOrderItem, OrderItemDto.class));
    }

    @Async
    public CompletableFuture<OrderItemDto> update(int id, OrderItemUpdateDto orderItems) {
        OrderItem existingOrderItem = orderItemRepository.findById(id).orElse(null);
        if (existingOrderItem == null)
            throw new NotFoundException("Unable to find orderItem!");
        BeanUtils.copyProperties(orderItems, existingOrderItem);
        existingOrderItem.setUpdateAt(new Date(new java.util.Date().getTime()));
        return CompletableFuture.completedFuture(toDto.map(orderItemRepository.save(existingOrderItem), OrderItemDto.class));
    }

    @Async
    public CompletableFuture<PagedResultDto<OrderItemDto>> findAllPagination(HttpServletRequest request, Integer limit, Integer skip) {
        long total = orderItemRepository.count();
        Pagination pagination = Pagination.create(total, skip, limit);

        ApiQuery<OrderItem> features = new ApiQuery<>(request, em, OrderItem.class, pagination);
        return CompletableFuture.completedFuture(PagedResultDto.create(pagination,
                features.filter().orderBy().paginate().exec().stream().map(x -> toDto.map(x, OrderItemDto.class)).toList()));
    }

    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(id);
        if (!orderItemOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            OrderItem orderItem = orderItemOptional.get();
            orderItem.setIsDeleted(true);
            orderItem.setUpdateAt(new Date(new java.util.Date().getTime()));
            orderItemRepository.save(orderItem);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }
}
