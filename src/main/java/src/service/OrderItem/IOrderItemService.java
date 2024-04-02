package src.service.OrderItem;

import src.service.IService;
import src.service.OrderItem.Dto.OrderItemCreateDto;
import src.service.OrderItem.Dto.OrderItemDto;
import src.service.OrderItem.Dto.OrderItemUpdateDto;

public interface IOrderItemService extends IService<OrderItemDto, OrderItemCreateDto, OrderItemUpdateDto> {
}
