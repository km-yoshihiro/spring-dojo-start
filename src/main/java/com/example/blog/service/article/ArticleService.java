package com.example.blog.service.article;

import com.example.blog.repository.article.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service  //このアノテーションにより、spring起動時にArticleServiceを生成しようとして、ArticleRepositoryが必要であると引数から判断し、参照しにいく。
@RequiredArgsConstructor  //finalがついているフィールドを初期化する
public class ArticleService {

    //finalでなければnullpointerException発生確率が上がる　値が代入されることを確約する意味
    private final ArticleRepository articleRepository;

    //ドメイン層由来のService
    public Optional<ArticleEntity> findById(long id){
        return articleRepository.selectById(id); //idというレコードをarticleRepositoryからとってくるメソッド

                /*Optional.of (new ArticleEntity(
                id,
                "title",
                "content",
                LocalDateTime.now(),
                LocalDateTime.now()
        ));*/
    }
}
