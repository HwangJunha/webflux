package com.around.webflux.practices.reactor;

import com.around.webflux.practices.reactor.common.EmptyImage;
import com.around.webflux.practices.reactor.common.Article;
import com.around.webflux.practices.reactor.common.Image;
import com.around.webflux.practices.reactor.common.User;
import com.around.webflux.practices.reactor.common.repository.UserEntity;
import com.around.webflux.practices.reactor.repository.ArticleReactorRepository;
import com.around.webflux.practices.reactor.repository.FollowReactorRepository;
import com.around.webflux.practices.reactor.repository.ImageReactorRepository;
import com.around.webflux.practices.reactor.repository.UserReactorRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UserReactorService {

    private final UserReactorRepository userRepository;
    private final ArticleReactorRepository articleRepository;
    private final ImageReactorRepository imageRepository;
    private final FollowReactorRepository followRepository;

    @SneakyThrows
    public Mono<User> getUserById(String id) {
        return userRepository.findById(id)
                .flatMap(this::getUser);

    }

    @SneakyThrows
    private Mono<User> getUser(UserEntity userEntity) {
        Context conext = Context.of("user", userEntity);

        var imageMono = imageRepository.findWithContext()
                        .map(imageEntity -> new Image(imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl()))
                        .onErrorReturn(new EmptyImage())
                        .contextWrite(conext);


        var articlesMono = articleRepository.findAllWithContext()
                            .skip(5)
                            .take(2)
                            .map(articleEntity -> new Article(articleEntity.getId(), articleEntity.getTitle(), articleEntity.getContent()))
                            .collectList()
                            .contextWrite(conext);

        var followCountMono = followRepository.countWithContext()
                            .contextWrite(conext);
        //return Flux.concat(imageMono, articlesMono, followCountMono)//순서를 보장한다 하지만 imageMono, articleMono, followCountMono 순서를 기다려서 상대적으로 느림
        //return Flux.merge(imageMono, articlesMono, followCountMono)//속도는 빠르지만 순서는 보장되지 않는다
        //return Flux.mergeSequential(imageMono, articlesMono, followCountMono)//순서를 보장하면서 속도가 빠르다
        return Mono.zip(imageMono, articlesMono, followCountMono)//순서를 보장한다
                .map(resultTuple ->{
                    //zip를 사용했을 경우 강제로 캐스팅을 할 필요가 없어진다
                    Image image = resultTuple.getT1();
                    List<Article> articleList = resultTuple.getT2();
                    Long followCount = resultTuple.getT3();
//                    Image image = (Image)resultList.get(0);
//                    List<Article> articleList = (List<Article>) resultList.get(1);
//                    Long followCount = (Long)resultList.get(2);

                    Optional<Image> imageOptional = Optional.empty();
                    if(!(image instanceof EmptyImage)){
                        imageOptional = Optional.of(image);
                    }
                    return new User(
                            userEntity.getId(),
                            userEntity.getName(),
                            userEntity.getAge(),
                            imageOptional,
                            articleList,
                            followCount
                    );
                });

    }

}
