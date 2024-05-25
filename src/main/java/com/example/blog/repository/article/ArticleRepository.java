package com.example.blog.repository.article;

import com.example.blog.service.article.ArticleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper //このアノテーションでDIコンテナに登録される
public interface ArticleRepository {

    //アノテーションでセレクト文を入力し、簡素化
    @Select("""
            SELECT 
            id 
            , title 
            , body 
            , created_at
            , updated_at
            FROM articles 
            WHERE id = #{id}
            """)
    Optional<ArticleEntity> selectById(@Param("id") long id); //windows環境では＠Paramがないと動かないという挙動がまれにあり
}
