package src.service.Cart;

import src.service.Cart.Dto.CartCreateDto;
import src.service.Cart.Dto.CartDto;
import src.service.Cart.Dto.CartUpdateDto;
import src.service.IService;

import java.util.concurrent.CompletableFuture;

public interface ICartService extends IService<CartDto, CartCreateDto, CartUpdateDto> {
    CompletableFuture<Boolean> removeFromCart(int courseId, int userId);
    void deleteByIdNew(int id);
}
