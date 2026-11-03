package ch.tbz.mogbattle.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integrationstest: echter Spring-Kontext, echte H2-Datenbank, Requests über MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ChadControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/chads liefert die Seed-Daten aus data.sql")
    void listSeededChads() throws Exception {
        mockMvc.perform(get("/api/chads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].name").value("Gigachad"));
    }

    @Test
    @DisplayName("POST /api/chads speichert einen neuen Chad")
    void createChad() throws Exception {
        String body = """
                {"name":"Rizz Ricardo","height":7,"jawline":6,"aura":10,"status":6,"frame":7}
                """;

        mockMvc.perform(post("/api/chads").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Rizz Ricardo"));

        mockMvc.perform(get("/api/chads"))
                .andExpect(jsonPath("$", hasSize(7)));
    }

    @Test
    @DisplayName("Stats ausserhalb von 1-10 ergeben 400")
    void rejectsInvalidStats() throws Exception {
        String body = """
                {"name":"Cheater Chad","height":11,"jawline":6,"aura":0,"status":6,"frame":7}
                """;

        mockMvc.perform(post("/api/chads").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Leerer Name ergibt 400")
    void rejectsBlankName() throws Exception {
        String body = """
                {"name":"","height":5,"jawline":5,"aura":5,"status":5,"frame":5}
                """;

        mockMvc.perform(post("/api/chads").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
