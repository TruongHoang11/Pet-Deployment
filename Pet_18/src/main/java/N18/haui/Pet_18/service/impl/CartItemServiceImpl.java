package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.domain.dto.request.ReqAddCartItem;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateCartItem;
import N18.haui.Pet_18.domain.dto.response.CartItemDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.entity.*;
import N18.haui.Pet_18.domain.mapper.CartItemMapper;
import N18.haui.Pet_18.exception.BadRequestException;
import N18.haui.Pet_18.exception.ForbiddenException;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.*;
import N18.haui.Pet_18.service.CartItemService;
import N18.haui.Pet_18.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    private final CartRepository cartRepository;
    private final InventoryRepository inventoryRepository;

    private final CartItemMapper cartItemMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartItemDto addCartItem(ReqAddCartItem reqAddCartItem) {
        log.info("[CART] Thêm sản phẩm ID: {} vào giỏ hàng", reqAddCartItem.getProductId());


        User currentUser = userService.getUserLogin();
        String userId = currentUser.getId();

        Cart cart = cartRepository.findByUserId(currentUser.getId()).orElseGet(() -> {
            log.info("[CART] User ID: {} chưa có giỏ hàng, tạo mới", currentUser.getId());
           Cart newCart = new Cart();
           newCart.setUser(currentUser);
           currentUser.setCart(newCart);
           userRepository.save(currentUser);
           return cartRepository.save(newCart);
        });

        // kiểm tra product có tồn tại không
        Product product = productRepository.findById(reqAddCartItem.getProductId()).orElseThrow(
                () -> new NotFoundException("Product not found with ID: " + reqAddCartItem.getProductId())
        );

        // kiểm tra tồn kho
        Inventory inventory = inventoryRepository.findByProductId(reqAddCartItem.getProductId()).orElseThrow(
                () -> new NotFoundException("Inventory not found with Product ID: " + reqAddCartItem.getProductId())
        );

        if(inventory.getQuantity() < reqAddCartItem.getQuantity()){
            throw new BadRequestException("Inventory quantity is not enough: " + inventory.getQuantity());
        }

        // tìm cartItem đã tồn tại chưa
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);
        if(cartItem != null){
            Integer newQty = cartItem.getQuantity() + reqAddCartItem.getQuantity();

            // kiểm tra tồn kho sau khi cộng dồn
            if(inventory.getQuantity() < newQty){
                throw new BadRequestException("Inventory quantity is not enough: " + inventory.getQuantity());
            }
            log.info("[CART] Cộng dồn quantity {} → {}", cartItem.getQuantity(), newQty);
            cartItem.setQuantity(newQty);
        } else{
            log.info("[CART] Tạo mới CartItem cho Product ID: {}", reqAddCartItem.getProductId());
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(reqAddCartItem.getQuantity());
        }

        cartItemRepository.save(cartItem);
        log.info("[CART] Lưu CartItem thành công | Product ID: {} | Quantity: {}",
                reqAddCartItem.getProductId(), cartItem.getQuantity());

        CartItemDto dto = cartItemMapper.toDto(cartItem);
        return dto;
    }

    @Override
    @Transactional
    public CartItemDto updateCartItem(ReqUpdateCartItem req) {
        log.info("[CART] Cập nhật CartItem ID: {} | Quantity mới: {}", req.getItemId(), req.getQuantity());

        CartItem cartItem = cartItemRepository.findById(req.getItemId()).orElseThrow(
                () -> new NotFoundException("CartItem not found with ID: " + req.getItemId())
        );

        User currentUser = userService.getUserLogin();
        if(!cartItem.getCart().getUser().getId().equals(currentUser.getId()) ){
            throw new ForbiddenException("You do not have permission to update this cart item");
        }

        if(req.getQuantity() == 0){
            cartItemRepository.delete(cartItem);
            return null;
        }
        Inventory inventory = inventoryRepository.findByProductId(cartItem.getProduct().getId()).orElseThrow(
                () -> new NotFoundException("Inventory not found with Product ID: " + cartItem.getProduct().getId())
        );
        if(inventory.getQuantity() < req.getQuantity()){
            throw new BadRequestException("Inventory quantity is not enough: " + inventory.getQuantity());
        }
        cartItem.setQuantity(req.getQuantity());
        cartItemRepository.save(cartItem);
        log.info("[CART] Cập nhật thành công CartItem ID: {}", req.getItemId());
        return cartItemMapper.toDto(cartItem);


    }

    @Override
    @Transactional
    public CommonResponseDto deleteCartItem(Long itemId) {
        log.info("[CART] Xóa CartItem ID: {}", itemId);
        CartItem cartItem = cartItemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("CartItem not found with ID: " + itemId)
        );
        User currentUser = userService.getUserLogin();
        if(!cartItem.getCart().getUser().getId().equals(currentUser.getId())){
            throw new ForbiddenException("You do not have permission to delete this cart item");
        }
        cartItemRepository.delete(cartItem);
        log.info("[CART] Xóa thành công CartItem ID: {}", itemId);
        return new CommonResponseDto(true, "Delete cart item successfully");
    }
}
