package com.dab.book_network.config;

import com.dab.book_network.entity.Role;
import com.dab.book_network.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.findByName("USER").isEmpty()) {
            roleRepository.save(Role.builder()
                    .name("USER")
                    .build());
        }
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(Role.builder()
                    .name("ADMIN")
                    .build());
        }
    }
}
