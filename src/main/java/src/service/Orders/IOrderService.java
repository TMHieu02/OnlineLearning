package src.service.Orders;

import src.service.IService;
import src.service.Orders.Dto.OrderCreateDto;
import src.service.Orders.Dto.OrderDto;
import src.service.Orders.Dto.OrderUpdateDto;

public interface IOrderService extends IService<OrderDto, OrderCreateDto, OrderUpdateDto> {
}
