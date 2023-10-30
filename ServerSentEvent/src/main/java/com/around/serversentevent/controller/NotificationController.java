package com.around.serversentevent.controller;

import com.around.serversentevent.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/notifications")
@RestController
public class NotificationController {
    //sink의 many는 flux를 가리키고 하나에서만 subscribe를 하기 때문에 unicast를 사용
    private final NotificationService notificationService;
    private static AtomicInteger lastEventId = new AtomicInteger(1);
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> getNotifications(){

        return notificationService.getMessageFromSink()
                .map(message -> {
                    String id = lastEventId.getAndIncrement() + "";
                    return ServerSentEvent
                        .builder(message)
                        .event("notification")
                        .id(id)
                        .comment("this is notification")
                        .build();
                    }
                );

    }
    @PostMapping()
    public Mono<String> addNotification(@RequestBody Event event){
        String notificationMessage = event.getType() + ": "+event.getMessage();
        notificationService.tryEmitNext(notificationMessage);
        return Mono.just("ok");
    }
}
