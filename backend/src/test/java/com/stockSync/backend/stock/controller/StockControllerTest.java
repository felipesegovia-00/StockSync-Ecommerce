package com.stockSync.backend.stock.controller;



import com.stockSync.backend.stock.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(StockController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockService stockService;

   /* @BeforeEach
    void setUp() {
        //Objeto simulado
        productSample = new ProductResponse();
        productSample.setId(1L);
        productSample.setName("Guantes de Nitrilo");
        productSample.setStock(100L);
        productSample.setCategoryName("Insumos Medicos");
    }
    */
    @Test
    void getStock_ShouldReturnOk() throws Exception {

        //Simulamos la ruta
        mockMvc.perform(get("/v1/stocks/product/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        }


    }