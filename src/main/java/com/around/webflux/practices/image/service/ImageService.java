package com.around.webflux.practices.image.service;


import com.around.webflux.practices.image.entity.common.Image;
import com.around.webflux.practices.image.repository.ImageReactorRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ImageService {
    private ImageReactorRepository imageReactorRepository = new ImageReactorRepository();

    public Mono<Image> getImageById(String imageId){
        return imageReactorRepository.findById(imageId)
                .map(imageEntity -> new Image(imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl()));
    }

}
