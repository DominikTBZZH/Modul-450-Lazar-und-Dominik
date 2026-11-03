package ch.tbz.mogbattle.web;

import ch.tbz.mogbattle.oracle.AuraOracle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integrationstest über alle Schichten. Nur das zufällige AuraOracle wird gemockt.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BattleControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuraOracle auraOracle;

    @BeforeEach
    void noBonus() {
        when(auraOracle.auraBonus(any())).thenReturn(0);
    }

    @Test
    @DisplayName("Gigachad megamogt Average Andy")
    void gigachadMegamogsAndy() throws Exception {
        mockMvc.perform(post("/api/battles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chadAId\":1,\"chadBId\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winner").value("Gigachad"))
                .andExpect(jsonPath("$.loser").value("Average Andy"))
                .andExpect(jsonPath("$.margin").value(31))
                .andExpect(jsonPath("$.verdict").value("MEGAMOGGED"));
    }

    @Test
    @DisplayName("Unbekannte id ergibt 404")
    void unknownChadIs404() throws Exception {
        mockMvc.perform(post("/api/battles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chadAId\":1,\"chadBId\":999}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Chad mit id 999 existiert nicht"));
    }

    @Test
    @DisplayName("Selbst-Battle ergibt 400")
    void selfBattleIs400() throws Exception {
        mockMvc.perform(post("/api/battles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chadAId\":1,\"chadBId\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Fehlende id ergibt 400")
    void missingIdIs400() throws Exception {
        mockMvc.perform(post("/api/battles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chadAId\":1}"))
                .andExpect(status().isBadRequest());
    }
}
