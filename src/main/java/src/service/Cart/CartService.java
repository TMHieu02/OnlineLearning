package src.service.Cart;

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
import src.config.exception.BadRequestException;
import src.config.exception.NotFoundException;
import src.config.utils.ApiQuery;
import src.model.*;
import src.model.Cart;
import src.repository.CartRepository;
import src.repository.OrderItemRepository;
import src.repository.OrdersRepository;
import src.service.Cart.Dto.CartDto;
import src.service.Cart.Dto.CartOrderDTO;
import src.service.Cart.Dto.CartUpdateDto;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ModelMapper toDto;
    @PersistenceContext
    EntityManager em;
    @Async
    public CompletableFuture<List<CartDto>> getAll() {
        return CompletableFuture.completedFuture(
                cartRepository.findAll().stream().map(
                        x -> toDto.map(x, CartDto.class)
                ).collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<CartDto> getOne(int id) {
        Cart cart = cartRepository.findById(id).orElse(null);
        if (cart == null) {
            throw new NotFoundException("Không tìm thấy quyền với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(cart, CartDto.class));
    }
    @Async
    public CompletableFuture<CartDto> create(CartOrderDTO cartOrderDTO) {
        try {
            List<Cart> existCart = cartRepository.findCart(
                    cartOrderDTO.getUser_id(), cartOrderDTO.getUser_id());
            List<Orders> existUser = ordersRepository.findUser(
                    cartOrderDTO.getUser_id());
            List<OrderItem> existCourse = orderItemRepository.findCourse(
                    cartOrderDTO.getCourse_id());
            if (!existCart.isEmpty()) {
                throw new BadRequestException("Giỏ hàng chỉ tồn tại 1 khóa học");
            }
            if (!existUser.isEmpty() && !existCourse.isEmpty()) {
                throw new BadRequestException("Khóa học đã được thanh toán");
            }
            Cart cart = new Cart();
            cart.setCourseId(cartOrderDTO.getCourse_id());
            cart.setUserId(cartOrderDTO.getUser_id());
            cartRepository.save(cart);
            CartDto cartDto = toDto.map(cart, CartDto.class);
            return CompletableFuture.completedFuture(cartDto);
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(new RuntimeException("Thêm giỏ hàng thất bại"));
        }
    }
    public Cart updateCart(int cartId, Map<String, Object> fieldsToUpdate) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            updateCartFields(cart, fieldsToUpdate);
            cart.setUpdateAt(new Date());
            cartRepository.save(cart);
            return cart;
        }
        return null;
    }
    private void updateCartFields(Cart cart, Map<String, Object> fieldsToUpdate) {
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            updateCartField(cart, fieldName, value);
        }
    }
    private void updateCartField(Cart cart, String fieldName, Object value) {
        switch (fieldName) {
            case "courseId":
                cart.setCourseId((int) value);
                break;
            case "userId":
                cart.setUserId((int) value);
                break;
            default:
                break;
        }
    }


    public CompletableFuture<String> deleteByIdNew(int id) {

        Optional<Cart> cartOptional = cartRepository.findById(id);
        if (!cartOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            cartRepository.deleteById(id);
            return CompletableFuture.completedFuture("Xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }

    public CompletableFuture<String> deleteByCourseIdAndUserId(int courseId, int userId) {

        Optional<Cart> cartOptional = cartRepository.findByCourseIdAndUserId(courseId, userId);
        if (cartOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            cartRepository.deleteByCourseIdAndUserId(courseId, userId);
            return CompletableFuture.completedFuture("Xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }

    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<Cart> cartOptional = cartRepository.findById(id);
        if (!cartOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            Cart cart = cartOptional.get();
            cart.setIsDeleted(true);
            cart.setUpdateAt(new Date(new java.util.Date().getTime()));
            cartRepository.save(cart);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }
    public List<CartOrderDTO> getCartByUserId(int userId) {
        List<CartOrderDTO> result = new ArrayList<>();
        List<Cart> carts = cartRepository.findByUserId(userId);
        for (Cart cart : carts) {
            CartOrderDTO dto = new CartOrderDTO();
            dto.setCart_id(cart.getId());
            dto.setCourse_id(cart.getCourseByCourseId().getId());
            dto.setTitle(cart.getCourseByCourseId().getTitle());
            dto.setCategory_id(cart.getCourseByCourseId().getCategoryId());
            dto.setCategory_name(cart.getCourseByCourseId().getCategoryByCategoryId().getName());
            dto.setUser_id(cart.getUserId());
            dto.setUser_name(cart.getCourseByCourseId().getUserByUserId().getFullname());
            dto.setPrice(cart.getCourseByCourseId().getPrice());
            dto.setPromotional_price(cart.getCourseByCourseId().getPromotional_price());
            dto.setSold(cart.getCourseByCourseId().getSold());
            dto.setDescription(cart.getCourseByCourseId().getDescription());
            dto.setImage(cart.getCourseByCourseId().getImage());
            dto.setActive(cart.getCourseByCourseId().getActive());
            dto.setCreated_at(cart.getCreateAt());
            dto.setUpdate_at(cart.getUpdateAt());
            dto.setRating(cart.getCourseByCourseId().getRating());
            dto.setCreateAt(cart.getCourseByCourseId().getCreateAt());
            dto.setUpdateAt(cart.getCourseByCourseId().getUpdateAt());
            dto.setIsDeleted(cart.getIsDeleted());
            result.add(dto);
        }
        return result;
    }

    public boolean isCartValid(int userId, int courseId) {
        Optional<Cart> optionalCourseRegister = cartRepository.findByUserIdAndCourseIdAndIsActiveAndIsDeletedNot(userId, courseId);
        return optionalCourseRegister.isPresent();
    }
}
