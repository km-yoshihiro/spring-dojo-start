package com.example.blog.web.controller.article;

import com.example.blog.service.article.ArticleEntity;

import java.time.LocalDateTime;
//表示する部位のみを変更したい場合はArticleDTOのみを変更すればよい
public record ArticleDTO(
        long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ArticleDTO from(ArticleEntity entity){
        return new ArticleDTO(
                entity.id(),
                entity.title(),
                entity.content(),
                entity.createdAt(),
                entity.updatedAt()
        );
    }
}
