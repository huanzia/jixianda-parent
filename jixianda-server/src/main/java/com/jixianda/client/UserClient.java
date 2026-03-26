package com.jixianda.client;

import com.jixianda.entity.AddressBook;
import com.jixianda.entity.User;
import com.jixianda.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@FeignClient("jixianda-user")
public interface UserClient {

    @GetMapping("/user/addressBook/{id}")
    Result<AddressBook> getAddressBookById(@PathVariable("id") Long id,
                                           @RequestHeader("userId") Long userId);

    @GetMapping("/user/user/{id}")
    Result<User> getUserById(@PathVariable("id") Long id);

    @GetMapping("/user/user/count")
    Result<Integer> countUserByTime(
            @RequestParam(value = "begin", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime begin,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end);
}
