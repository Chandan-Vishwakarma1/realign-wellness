package com.realignwellness.service;


import com.realignwellness.dto.UserDTO;
import com.realignwellness.entity.User;
import com.realignwellness.mapper.UserMapper;
import com.realignwellness.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Page<UserDTO> getAllUsers(int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(userMapper::toUserDto);
    }
}
