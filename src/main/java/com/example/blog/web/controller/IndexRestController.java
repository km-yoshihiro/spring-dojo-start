package com.example.blog.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexRestController {

    //GET/ログインのリクエストを出す前に一旦CookieにCsrfトークンを受け取る　そのあとにPOSTする時にはCsrfトークンをヘッダーにつけかえて送る処理
    @GetMapping
    public ResponseEntity<Void> index(){ //ResponseEntity<Void>の意味はResponseEntityのbody中身が空っぽということ
        return ResponseEntity
                .noContent()
                .build();
    }
}
