package src.service.Orders;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import src.config.exception.NotFoundException;
import src.model.Orders;
import src.repository.OrdersRepository;
import src.service.Orders.Dto.OrderCreateDto;
import src.service.Orders.Dto.OrderDto;
import src.service.Orders.Dto.OrderUpdateDto;


import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private ModelMapper toDto;
    @PersistenceContext
    EntityManager em;
    @Async
    public CompletableFuture<List<OrderDto>> getAll() {
        return CompletableFuture.completedFuture(
                ordersRepository.findAll().stream().map(
                        x -> toDto.map(x, OrderDto.class)
                ).collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<OrderDto> getOne(int id) {
        Orders order = ordersRepository.findById(id).orElse(null);
        if (order == null) {
            throw new NotFoundException("Không tìm thấy sản phẩm với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(order, OrderDto.class));
    }


    public CompletableFuture<Orders> createOrder(OrderCreateDto orderCreateDto) {
        CompletableFuture<Orders> future = new CompletableFuture<>();

        Orders newOrder = new Orders();
        newOrder.setFullname(orderCreateDto.getFullname());
        newOrder.setEmail(orderCreateDto.getEmail());
        newOrder.setPhone(orderCreateDto.getPhone());
        newOrder.setPayment_price(orderCreateDto.getPayment_price());
        newOrder.setStatus(orderCreateDto.getStatus());
        newOrder.setUserId(orderCreateDto.getUserId());

        Orders savedOrder = ordersRepository.save(newOrder);
        future.complete(savedOrder);

        return future;
    }

    @Async
    public CompletableFuture<OrderDto> update(int id, OrderUpdateDto orders) {
        Orders existingOrder = ordersRepository.findById(id).orElse(null);
        if (existingOrder == null)
            throw new NotFoundException("Unable to find Order!");
        BeanUtils.copyProperties(orders, existingOrder);
        return CompletableFuture.completedFuture(toDto.map(ordersRepository.save(existingOrder), OrderDto.class));
    }

    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<Orders> orderOptional = ordersRepository.findById(id);
        if (!orderOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            Orders order = orderOptional.get();
            order.setIsDeleted(true);
            order.setUpdateAt(new Date(new java.util.Date().getTime()));
            ordersRepository.save(order);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }
}
