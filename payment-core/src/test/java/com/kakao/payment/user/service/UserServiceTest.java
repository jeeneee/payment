package com.kakao.payment.user.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.kakao.payment.user.domain.User;
import com.kakao.payment.user.domain.UserRepository;
import com.kakao.payment.user.exception.UserNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
        user = User.builder()
            .id(1L)
            .uid("test1")
            .build();
    }

    @DisplayName("유저 등록")
    @Test
    void save_UnsavedUser_Success() {
        // given
        given(userRepository.save(any())).willReturn(user);

        // when
        User saved = userService.save(user.getUid());

        // then
        assertAll(
            () -> assertEquals(user.getId(), saved.getId()),
            () -> assertEquals(user.getUid(), saved.getUid())
        );
    }

    @DisplayName("유저 조회")
    @Test
    void findOne_SavedUser_FindUserSuccess() {
        // given
        given(userRepository.findByUid(any())).willReturn(Optional.of(user));

        // when
        User found = userService.getUser(user.getUid());

        // then
        assertAll(
            () -> assertEquals(user.getId(), found.getId()),
            () -> assertEquals(user.getUid(), found.getUid())
        );
    }

    @DisplayName("유저 조회 - 유저가 등록되지 않은 경우 예외가 발생한다.")
    @Test
    void findOne_UnSavedUser_ExceptionThrown() {
        given(userRepository.findByUid(any())).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(user.getUid()));
    }
}