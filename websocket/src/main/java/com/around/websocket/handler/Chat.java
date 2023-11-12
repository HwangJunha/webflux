package com.around.websocket.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Chat {
    private final String message;
    private final String from;
}
