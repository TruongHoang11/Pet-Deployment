package N18.haui.Pet_18;

import N18.haui.Pet_18.constant.RoleConstant;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.entity.Permission;
import N18.haui.Pet_18.domain.entity.Role;
import N18.haui.Pet_18.domain.entity.User;
import N18.haui.Pet_18.repository.PermissionRepository;
import N18.haui.Pet_18.repository.RoleRepository;
import N18.haui.Pet_18.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@SpringBootApplication
public class Pet18Application {
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;

	public static void main(String[] args) {
		SpringApplication.run(Pet18Application.class, args);
	}

    @Bean
	CommandLineRunner init() {
        return args -> {
            long countPermission = permissionRepository.count();
            if(countPermission == 0){
                List<Permission> permissionList = new ArrayList<>();
                // User Module
                permissionList.add(new Permission("Get current user", "/api/v1" + UrlConstant.User.GET_CURRENT_USER, "GET", "USER"));
                permissionList.add(new Permission("Update profile", "/api/v1" + UrlConstant.User.UPDATE_PROFILE, "PUT", "USER"));
                permissionList.add(new Permission("Add avatar", "/api/v1" + UrlConstant.User.ADD_AVATAR, "POST", "USER"));
                permissionList.add(new Permission("Logout", "/api/v1" + UrlConstant.Auth.LOGOUT, "POST", "AUTH"));
                permissionList.add(new Permission("Get user profile", "/api/v1" + UrlConstant.User.GET_PROFILE, "GET", "USER"));

                // Order Module
                permissionList.add(new Permission("Create order from cart", "/api/v1" + UrlConstant.Order.CREATE_ORDER_FROM_CART, "POST", "ORDER"));
                permissionList.add(new Permission("Create order buy now", "/api/v1" + UrlConstant.Order.CREATE_ORDER_FROM_BUY_NOW, "POST", "ORDER"));
                permissionList.add(new Permission("Get my orders", "/api/v1" + UrlConstant.Order.GET_MY_ORDERS, "GET", "ORDER"));
                permissionList.add(new Permission("Get order detail", "/api/v1" + UrlConstant.Order.GET_ORDER_DETAIL, "GET", "ORDER"));
                permissionList.add(new Permission("Cancel order", "/api/v1" + UrlConstant.Order.CANCEL_ORDER, "PATCH", "ORDER"));

                // Pet Module
                permissionList.add(new Permission("Get my pets", "/api/v1" + UrlConstant.Pet.GET_MY_PETS, "GET", "PET"));
                permissionList.add(new Permission("Get pet detail", "/api/v1" + UrlConstant.Pet.GET_PET_DETAIL, "GET", "PET"));
                permissionList.add(new Permission("Create a pet", "/api/v1" + UrlConstant.Pet.CREATE_PET, "POST", "PET"));
                permissionList.add(new Permission("Update a pet", "/api/v1" + UrlConstant.Pet.UPDATE_PET, "PUT", "PET"));
                permissionList.add(new Permission("Delete a pet", "/api/v1" + UrlConstant.Pet.DELETE_PET, "DELETE", "PET"));

                 // Product Review Module
                permissionList.add(new Permission("Get reviews by product", "/api/v1" + UrlConstant.ProductReview.GET_REVIEWS_BY_PRODUCT, "GET", "REVIEW"));
                permissionList.add(new Permission("Create a review", "/api/v1" + UrlConstant.ProductReview.CREATE_REVIEW, "POST", "REVIEW"));
                permissionList.add(new Permission("Update a review", "/api/v1" + UrlConstant.ProductReview.UPDATE_REVIEW, "PUT", "REVIEW"));
                permissionList.add(new Permission("Delete a review", "/api/v1" + UrlConstant.ProductReview.DELETE_REVIEW, "DELETE", "REVIEW"));


                // Shipping Address Module
                permissionList.add(new Permission("Create a shipping address", "/api/v1" + UrlConstant.ShippingAddress.CREATE_SHIPPING_ADDRESS, "POST", "SHIPPING_ADDRESS"));
                permissionList.add(new Permission("Get all shipping addresses", "/api/v1" + UrlConstant.ShippingAddress.GET_SHIPPING_ADDRESSES, "GET", "SHIPPING_ADDRESS"));
                permissionList.add(new Permission("Set default shipping address", "/api/v1" + UrlConstant.ShippingAddress.SET_DEFAULT_ADDRESS, "PATCH", "SHIPPING_ADDRESS"));
                permissionList.add(new Permission("Update a shipping address", "/api/v1" + UrlConstant.ShippingAddress.UPDATE_SHIPPING_ADDRESS, "PUT", "SHIPPING_ADDRESS"));
                permissionList.add(new Permission("Delete a shipping address", "/api/v1" + UrlConstant.ShippingAddress.DELETE_SHIPPING_ADDRESS, "DELETE", "SHIPPING_ADDRESS"));

                // Cart Module
                permissionList.add(new Permission("Get cart", "/api/v1" + UrlConstant.Cart.GET_CART, "GET", "CART"));
                permissionList.add(new Permission("Delete cart", "/api/v1" + UrlConstant.Cart.DELETE_CART, "DELETE", "CART"));

                // Cart Item Module
                permissionList.add(new Permission("Add item to cart", "/api/v1" + UrlConstant.CartItem.ADD_CART_ITEM, "POST", "CART_ITEM"));
                permissionList.add(new Permission("Update cart item", "/api/v1" + UrlConstant.CartItem.UPDATE_CART_ITEM, "PUT", "CART_ITEM"));
                permissionList.add(new Permission("Delete cart item", "/api/v1" + UrlConstant.CartItem.DELETE_CART_ITEM, "DELETE", "CART_ITEM"));

                // Booking Module
                permissionList.add(new Permission("Create booking", "/api/v1" + UrlConstant.Booking.CREATE_BOOKING, "POST", "BOOKING"));
                permissionList.add(new Permission("Get my bookings", "/api/v1" + UrlConstant.Booking.GET_MY_BOOKINGS, "GET", "BOOKING"));
                permissionList.add(new Permission("Cancel booking", "/api/v1" + UrlConstant.Booking.CANCEL_BOOKING, "PATCH", "BOOKING"));
                permissionList.add(new Permission("Get a booking", "/api/v1" + UrlConstant.Booking.GET_BOOKING, "GET", "BOOKING"));
                permissionList.add(new Permission("Get booked times", "/api/v1" + UrlConstant.Booking.GET_BOOKED_TIMES, "GET", "BOOKING"));

                //Payment Module
                permissionList.add(new Permission("Create payment", "/api/v1" + UrlConstant.Payment.CREATE_PAYMENT, "POST", "PAYMENT"));
                permissionList.add(new Permission("Get payment status", "/api/v1" + UrlConstant.Payment.GET_PAYMENT_STATUS, "GET", "PAYMENT"));;
                //Pet service Module
                permissionList.add(new Permission("Get all services", "/api/v1" + UrlConstant.PetService.GET_ALL_SERVICES, "GET", "PET_SERVICE"));
                permissionList.add(new Permission("Get service detail", "/api/v1" + UrlConstant.PetService.GET_SERVICE, "GET", "PET_SERVICE"));
                permissionList.add(new Permission("Search services", "/api/v1" + UrlConstant.PetService.SEARCH_SERVICES, "GET", "PET_SERVICE"));
                permissionList.add(new Permission("Get services by category", "/api/v1" + UrlConstant.PetService.GET_SERVICES_BY_CATEGORY, "GET", "PET_SERVICE"));
                permissionList.add(new Permission("Get top services", "/api/v1" + UrlConstant.PetService.GET_TOP_SERVICES, "GET", "PET_SERVICE"));
                permissionList.add(new Permission("Get service recommendations", "/api/v1" + UrlConstant.PetService.GET_RECOMMENDATIONS, "GET", "PET_SERVICE"));


                // Pet Service Review Module
                permissionList.add(new Permission("Create a service review", "/api/v1" + UrlConstant.PetServiceReviews.CREATE_REVIEW, "POST", "SERVICE_REVIEW"));
                permissionList.add(new Permission("Delete a service review", "/api/v1" + UrlConstant.PetServiceReviews.DELETE_REVIEW, "DELETE", "SERVICE_REVIEW"));
                permissionList.add(new Permission("Get reviews by service", "/api/v1" + UrlConstant.PetServiceReviews.GET_SERVICE_REVIEWS, "GET", "SERVICE_REVIEW"));
                permissionList.add(new Permission("Get average rating", "/api/v1" + UrlConstant.PetServiceReviews.GET_AVERAGE_RATING, "GET", "SERVICE_REVIEW"));
                permissionList.add(new Permission("Get review count", "/api/v1" + UrlConstant.PetServiceReviews.GET_REVIEW_COUNT, "GET", "SERVICE_REVIEW"));

                // Category Module
                permissionList.add(new Permission("Get all categories", "/api/v1" + UrlConstant.Category.GET_CATEGORIES, "GET", "CATEGORY"));
                permissionList.add(new Permission("Get category detail", "/api/v1" + UrlConstant.Category.GET_CATEGORY, "GET", "CATEGORY"));


                // Menu Module
                permissionList.add(new Permission("Get all menus", "/api/v1" + UrlConstant.Menu.GET_ALL_MENUS, "GET", "MENU"));
                permissionList.add(new Permission("Search menus", "/api/v1" + UrlConstant.Menu.SEARCH_MENUS, "GET", "MENU"));
                permissionList.add(new Permission("Get menus by category", "/api/v1" + UrlConstant.Menu.GET_MENUS_BY_CATEGORY, "GET", "MENU"));
                permissionList.add(new Permission("Get menus tree", "/api/v1" + UrlConstant.Menu.GET_MENUS_TREE, "GET", "MENU"));
                permissionList.add(new Permission("Get active menus", "/api/v1" + UrlConstant.Menu.GET_ACTIVE_MENUS, "GET", "MENU"));
                permissionList.add(new Permission("Get menu detail", "/api/v1" + UrlConstant.Menu.GET_MENU, "GET", "MENU"));

                // Product Module
                permissionList.add(new Permission("Get all products", "/api/v1" + UrlConstant.Product.GET_PRODUCTS, "GET", "PRODUCT"));
                permissionList.add(new Permission("Get product detail", "/api/v1" + UrlConstant.Product.GET_PRODUCT, "GET", "PRODUCT"));
                permissionList.add(new Permission("Get product recommendations", "/api/v1" + UrlConstant.Product.GET_RECOMMENDATIONS, "GET", "PRODUCT"));

                // Pet Service Image Module
                permissionList.add(new Permission("Get service images", "/api/v1" + UrlConstant.PetServiceImages.GET_SERVICE_IMAGES, "GET", "PET_SERVICE_IMAGE"));

                // Product Image Module
                permissionList.add(new Permission("Get product images", "/api/v1" + UrlConstant.ProductImages.GET_IMAGES, "GET", "PRODUCT_IMAGE"));


                this.permissionRepository.saveAll(permissionList);
            }
            //init role
            if (roleRepository.count() == 0) {
                List<Permission> permissionList = permissionRepository.findAll();
                roleRepository.save(new Role(null, RoleConstant.ADMIN, "Role for admin"));
                roleRepository.save(new Role(RoleConstant.USER, "Role for user",true, permissionList));
                roleRepository.save(new Role(null, RoleConstant.STAFF, "Role for staff"));



            }
            //init admin
            if (userRepository.count() == 0) {
                Role role = roleRepository.findByNameAndDeleteFlagFalse(RoleConstant.ADMIN).orElse(null);
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode("admin@123"));
                admin.setRole(role);
                userRepository.save(admin);

                Role role1 = roleRepository.findByNameAndDeleteFlagFalse(RoleConstant.USER).orElse(null);
                User user = new User();
                user.setName("User");
                user.setEmail("user@gmail.com");
                user.setPassword(passwordEncoder.encode("user@123"));
                user.setRole(role1);
                userRepository.save(user);
            }
        };
    }
}
