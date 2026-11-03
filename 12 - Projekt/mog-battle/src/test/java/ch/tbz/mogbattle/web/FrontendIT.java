package ch.tbz.mogbattle.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prüft, dass die Weboberfläche von Spring Boot ausgeliefert wird.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FrontendIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Startseite wird ausgeliefert")
    void indexIsServed() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("MogBattle")))
                .andExpect(content().string(containsString("app.js")));
    }

    @Test
    @DisplayName("JavaScript und CSS werden ausgeliefert")
    void assetsAreServed() throws Exception {
        mockMvc.perform(get("/app.js")).andExpect(status().isOk());
        mockMvc.perform(get("/style.css")).andExpect(status().isOk());
    }
}
