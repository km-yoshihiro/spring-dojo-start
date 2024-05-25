package com.example.blog.web.controller.article;

import com.example.blog.service.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController; //springwebを導入した時のもの
import org.springframework.web.server.ResponseStatusException;

//import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class ArticleRestController {

    private final ArticleService articleService;

    /*public ArticleRestController(ArticleService articleService){ //ボイラープレートコード
        this.articleService = articleService;                  //ほぼ変更なしで繰り返し使用されるコードのこと
    }*/
    //Get/Article/
    @GetMapping("/articles/{id}")
    public ArticleDTO showArticle(@PathVariable("id") long id){

        return articleService.findById(id) //Optional<ArticleEntity>からArticleDTOにかえるためmap()
                .map(ArticleDTO::from)
                    /*return new ArticleDTO(      //1センテンスの場合return削除できる
                            entity.id(),
                            entity.title(),
                            entity.content(),
                            entity.createdAt(),
                            entity.updatedAt()　//変換コードだが、ArticleDTOにfromメソッドを作り変換することで省略できる
                    )*/
                //)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                //オブジェクトの中身が存在しない場合は例外を投げる　Controllerクラスに実装
    }
}
