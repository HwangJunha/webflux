package com.around.webflux.practices.image.entity.common.repository;

import lombok.Data;

@Data
public class ImageEntity {
    private final String id;
    private final String name;
    private final String url;
}
