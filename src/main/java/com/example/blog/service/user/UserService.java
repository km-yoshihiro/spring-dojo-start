package com.example.blog.service.user;

import com.example.blog.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional //更新系には必ずTransactional付ける決まり このアノテーション付与でメソッド内でエラーが起きた場合、自動ロールバックされる
    public void register(String username , String rawPassword ){
        var encodedPassword = passwordEncoder.encode(rawPassword);
        userRepository.insert(username, encodedPassword, true);
    }
}
