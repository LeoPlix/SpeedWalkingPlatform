package com.speedwalking.config;

import com.speedwalking.model.*;
import com.speedwalking.repository.AthleteRepository;
import com.speedwalking.repository.CompetitionRepository;
import com.speedwalking.repository.InfractionRepository;
import com.speedwalking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CompetitionRepository competitionRepository;
    private final AthleteRepository athleteRepository;
    private final InfractionRepository infractionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CompetitionRepository competitionRepository,
                           AthleteRepository athleteRepository,
                           InfractionRepository infractionRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.competitionRepository = competitionRepository;
        this.athleteRepository = athleteRepository;
        this.infractionRepository = infractionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Data already seeded
        }

        System.out.println(">>> Seeding initial data for Race Walking System...");

        // 1. Judges & Users
        String defaultPass = passwordEncoder.encode("pass123");
        String adminPass = passwordEncoder.encode("admin123");

        User admin = new User("admin", adminPass, "Juiz Secretariado Chefe", Role.ROLE_ADMIN, "JC");
        User j1 = new User("juiz1", defaultPass, "António Silva", Role.ROLE_JUDGE, "J01");
        User j2 = new User("juiz2", defaultPass, "Beatriz Costa", Role.ROLE_JUDGE, "J02");
        User j3 = new User("juiz3", defaultPass, "Carlos Ferreira", Role.ROLE_JUDGE, "J03");
        User j4 = new User("juiz4", defaultPass, "Diana Rodrigues", Role.ROLE_JUDGE, "J04");

        userRepository.saveAll(Arrays.asList(admin, j1, j2, j3, j4));

        // 2. Competition
        Competition comp = new Competition(
                "Campeonato Nacional de Marcha 20km - Rio Maior 2026",
                "Rio Maior, Portugal",
                LocalDate.now(),
                "ACTIVE",
                false // Traditional 3 Red Cards = DQ rule
        );
        comp = competitionRepository.save(comp);

        // 3. Athletes (Athletes from Portugal)
        List<Athlete> athletes = Arrays.asList(
                new Athlete("101", "Pedro Isidro", "SL Benfica", "20km Masculino", comp),
                new Athlete("102", "João Vieira", "Sporting CP", "20km Masculino", comp),
                new Athlete("103", "Ana Cabecinha", "CO Pechão", "20km Feminino", comp),
                new Athlete("104", "Vitória Oliveira", "SL Benfica", "20km Feminino", comp),
                new Athlete("105", "Rui Coelho", "SL Benfica", "20km Masculino", comp),
                new Athlete("106", "Inês Henriques", "CN Rio Maior", "20km Feminino", comp),
                new Athlete("107", "Hélder Santos", "GD Estreito", "20km Masculino", comp),
                new Athlete("108", "Edna Barros", "CO Pechão", "20km Feminino", comp),
                new Athlete("109", "Tiago Ramos", "Sporting CP", "20km Masculino", comp),
                new Athlete("110", "Carolina Costa", "Sporting CP", "20km Feminino", comp)
        );
        athleteRepository.saveAll(athletes);

        // 4. Sample Infractions for demo
        Infraction inf1 = new Infraction(
                comp, j1, "104", "Vitória Oliveira", "09:15:30",
                InfractionType.CONTACTO, CardCategory.YP,
                LocalDateTime.now().minusMinutes(40), "Perda de contacto na curva"
        );

        Infraction inf2 = new Infraction(
                comp, j2, "104", "Vitória Oliveira", "09:22:15",
                InfractionType.CONTACTO, CardCategory.RC,
                LocalDateTime.now().minusMinutes(33), "Contacto contínuo visível"
        );

        Infraction inf3 = new Infraction(
                comp, j1, "102", "João Vieira", "09:28:40",
                InfractionType.FLEXAO, CardCategory.YP,
                LocalDateTime.now().minusMinutes(27), "Joelho flexionado no apoio"
        );

        Infraction inf4 = new Infraction(
                comp, j3, "104", "Vitória Oliveira", "09:35:10",
                InfractionType.FLEXAO, CardCategory.RC,
                LocalDateTime.now().minusMinutes(20), "Flexão repetida"
        );

        infractionRepository.saveAll(Arrays.asList(inf1, inf2, inf3, inf4));

        System.out.println(">>> Race Walking initial data seeded successfully!");
    }
}
