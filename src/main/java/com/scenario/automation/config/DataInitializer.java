package com.scenario.automation.config;

import com.scenario.automation.model.User;
import com.scenario.automation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Criar usuário admin se não existir
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@scenario.com");
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("✅ Usuário ADMIN criado: admin/admin123");
        }

        // Criar usuário comum se não existir
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@scenario.com");
            user.setRole("USER");
            user.setEnabled(true);
            userRepository.save(user);
            System.out.println("✅ Usuário USER criado: user/user123");
        }

        // Criar usuário de demo se não existir
        if (!userRepository.existsByUsername("demo")) {
            User demo = new User();
            demo.setUsername("demo");
            demo.setPassword(passwordEncoder.encode("demo123"));
            demo.setEmail("demo@scenario.com");
            demo.setRole("USER");
            demo.setEnabled(true);
            userRepository.save(demo);
            System.out.println("✅ Usuário DEMO criado: demo/demo123");
        }

        System.out.println("🚀 Dados iniciais carregados com sucesso!");
        System.out.println("🔐 Credenciais disponíveis:");
        System.out.println("   - admin/admin123 (ADMIN)");
        System.out.println("   - user/user123 (USER)");
        System.out.println("   - demo/demo123 (USER)");
    }
}
