package com.around.webflux.practices.webflux.service;

import com.around.webflux.practices.reactor.common.User;
import com.around.webflux.practices.webflux.repository.UserReactorRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserReactorRepository userRepository = new UserReactorRepository();
    public Mono<User> findById(String userId){
        return userRepository.findById(userId)
                .map(userEntity -> new User(
                        userEntity.getId(),
                        userEntity.getName(),
                        userEntity.getAge(),
                        Optional.empty(),
                        List.of(),
                        0L));
    }
}
