package ch.tbz.mogbattle;

import ch.tbz.mogbattle.engine.MogEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MogBattleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MogBattleApplication.class, args);
    }

    // MogEngine bleibt reines Java ohne Spring-Abhängigkeit
    @Bean
    MogEngine mogEngine() {
        return new MogEngine();
    }
}
