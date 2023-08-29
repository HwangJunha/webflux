package com.around.webflux.practices.image.entity.common;

import lombok.Data;

@Data
public class Image {
    private final String id;
    private final String name;
    private final String url;
}
