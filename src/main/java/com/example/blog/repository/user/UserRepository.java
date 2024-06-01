package com.example.blog.repository.user;

import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface UserRepository {

    @Select("""
            SELECT u.username , 
            u.password ,
            u.enabled
            FROM users u
            WHERE u.username = #{username}
            """)
    Optional<UserRecord> selectByUsername(@Param("username") String username);

    @Insert("""
            INSERT INTO users (username , password , enabled)
            VALUES (#{username} , #{password} , #{enabled})
            """)
    void insert(
            @Param("username") String username ,
            @Param("password") String password ,
            @Param("enabled") boolean enabled
    );
    //ユーザーのカラムが増えてくると取得内容が変わってくることがあるため、明確に取得カラムを指定する

    @Delete("""
            DELETE FROM users u WHERE u.username = #{username}
            """)
    void deleteByUsername(@Param("username")String username);
    //paramで指定されたusername(String usernameが#{username}に挿入される
    //windows環境では@Paramをつけないと動作しないことが確認されている。
}
