package com.around.webflux.practices.reactor.repository;

import com.around.webflux.practices.reactor.common.repository.UserEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class FollowReactorRepository {
    private Map<String, Long> userFollowCountMap;

    public FollowReactorRepository() {
        userFollowCountMap = Map.of("1234", 1000L);
    }

    @SneakyThrows
    public Mono<Long> countByUserId(String userId) {
        return Mono.create(sink ->{
            log.info("FollowRepository.countByUserId: {}", userId);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sink.success(userFollowCountMap.getOrDefault(userId, 0L));
        });

    }

    public Mono<Long> countWithContext(){
        return Mono.deferContextual(context -> {
            Optional<UserEntity> userEntityOptional = context.getOrEmpty("user");
            if(userEntityOptional.isEmpty())
                throw new RuntimeException("user not found");
            return Mono.just(userEntityOptional.get().getId());
        }).flatMap(this::countByUserId);
    }
}
