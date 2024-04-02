package src.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import src.config.annotation.ApiPrefixController;
import src.config.annotation.Authenticate;
import src.config.dto.PagedResultDto;

import src.model.Cart;
import src.service.Cart.CartService;
import src.service.Cart.Dto.CartDto;
import src.service.Cart.Dto.CartOrderDTO;
import src.service.Cart.Dto.CartUpdateDto;
import src.service.Course.Dto.CourseInfoDTO;


import java.util.List;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
@RestController
@ApiPrefixController(value = "/carts")
public class CartController {
    @Autowired
    private CartService cartService;
    @GetMapping()
    public CompletableFuture<List<CartDto>> findAll() {
        return cartService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<CartDto> getOne(@PathVariable int id) {
        return cartService.getOne(id);
    }
   @PatchMapping("/{cartId}")
   public ResponseEntity<Cart> updateCartField(
           @PathVariable int cartId,
           @RequestBody Map<String, Object> fieldsToUpdate) {
       Cart updatedCart = cartService.updateCart(cartId, fieldsToUpdate);
       if (updatedCart != null) {
           return ResponseEntity.ok(updatedCart);
       } else {
           return ResponseEntity.notFound().build();
       }
   }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return cartService.deleteByIdNew(id);
    }

    @DeleteMapping(value = "/{courseId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteByCourseIdAndUserId(@PathVariable int courseId, @PathVariable int userId) {
        return cartService.deleteByCourseIdAndUserId(courseId, userId);
    }
    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<?>> create(@RequestBody CartOrderDTO cartOrderDTO) {
        try {
            CartDto create = cartService.create(cartOrderDTO).get();
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.CREATED).body(create));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Thêm giỏ hàng thất bại"));
        }
    }
    @GetMapping("/user/{userId}")
    public CompletableFuture<ResponseEntity<List<CartOrderDTO>>> getCartByUserId(@PathVariable int userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<CartOrderDTO> cart = cartService.getCartByUserId(userId);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        });
    }

    @GetMapping("/check/{userId}/{courseId}")
    public CompletableFuture<String> checkCourseRegister(@PathVariable int userId, @PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> {
            if (cartService.isCartValid(userId, courseId)) {
                return "true";
            } else {
                return "false";
            }
        });
    }
}
