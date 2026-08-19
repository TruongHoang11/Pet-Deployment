package N18.haui.Pet_18.service.impl;


import N18.haui.Pet_18.domain.entity.User;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.UserRepository;
import N18.haui.Pet_18.security.UserPrincipal;
import N18.haui.Pet_18.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetails implements CustomUserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmailAndDeleteFlagFalse(email).orElseThrow(
                () ->  new UsernameNotFoundException("User not found with email: " + email)
        );


        return UserPrincipal.create(user);
    }

    @Override
    public UserDetails loadUserById(String id) {
        User user = this.userRepository.findById(id).orElseThrow(
                () ->  new NotFoundException("User not found with id: " + id)
        );
        return UserPrincipal.create(user);
    }
}
