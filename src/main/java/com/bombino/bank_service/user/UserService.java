package com.bombino.bank_service.user;

import com.bombino.bank_service.model.dto.CardDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDto registration(LoginDto loginDto) {

        String name = loginDto.name();
        String password = loginDto.password();
        userRepository.findByName(name).ifPresent(user -> {
            throw new EntityNotFoundException("Пользователь с таким именем уже существует!");
        });
        User user = User.builder()
                .name(name)
                .password(password)
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
    public UserDto login(LoginDto loginDto) {
        User user = userRepository.findByNameAndPassword(loginDto.name(), loginDto.password()).orElseThrow(
                ()->new EntityNotFoundException("Юзер не найден ")
        );
        return userMapper.toDto(user);
    }
    public UserDto findUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Юзера с id "+id + "не найдено"));
        return userMapper.toDto(user);
    }
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toDto(users);
    }
}
