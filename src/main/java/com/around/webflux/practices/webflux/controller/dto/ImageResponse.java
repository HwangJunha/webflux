package com.around.webflux.practices.webflux.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageResponse {
    private String id;
    private String name;
    private String url;
}
