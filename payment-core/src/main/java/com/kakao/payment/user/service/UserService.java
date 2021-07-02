package com.kakao.payment.user.service;

import com.kakao.payment.user.domain.User;
import com.kakao.payment.user.domain.UserRepository;
import com.kakao.payment.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User save(String uid) {
        User user = User.builder()
            .uid(uid)
            .build();
        return userRepository.save(user);
    }

    public User findOrSave(String uid) {
        return userRepository.findByUid(uid).orElse(save(uid));
    }

    public User findOne(String uid) {
        return userRepository.findByUid(uid)
            .orElseThrow(() -> new UserNotFoundException("The user does not exist."));
    }
}
