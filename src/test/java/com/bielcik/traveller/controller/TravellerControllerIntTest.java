package com.bielcik.traveller.controller;

import com.bielcik.traveller.TravellerApplication;
import com.bielcik.traveller.domain.DocumentType;
import com.bielcik.traveller.error.ExceptionTranslator;
import com.bielcik.traveller.repository.TravellerRepository;
import com.bielcik.traveller.service.dto.DocumentSearchDTO;
import com.bielcik.traveller.service.dto.TravellerDTO;
import com.bielcik.traveller.service.dto.TravellerDocumentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TravellerApplication.class)
@Testcontainers
class TravellerControllerIntTest {

    private MockMvc restPurchaseOrderMockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private TravellerController travellerController;

    @Autowired
    private TravellerRepository travellerRepository;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
//            .withExposedPorts(5432)
//            .withCreateContainerCmdModifier(cmd -> {
//                cmd.getHostConfig().withPortBindings(
//                        new PortBinding(Ports.Binding.bindPort(25432),
//                                new ExposedPort(5432)));
//            })
            ;

    @BeforeEach
    public void setup() {
        this.restPurchaseOrderMockMvc = MockMvcBuilders.standaloneSetup(travellerController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setMessageConverters(jacksonMessageConverter).build();
        objectMapper.registerModule(new JavaTimeModule());
        travellerRepository.deleteAll();
    }

    @Test
    void testCreate() throws Exception {
        TravellerDTO travellerDTO = getTravellerDTO();

        restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.travellerId").isNumber())
                .andExpect(jsonPath("$.firstName").value(travellerDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(travellerDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(travellerDTO.getEmail()))
                .andExpect(jsonPath("$.mobileNumber").value(travellerDTO.getMobileNumber()))
                .andExpect(jsonPath("$.dateOfBirth").value(travellerDTO.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.travellerDocuments[0].documentType").value(travellerDTO.getTravellerDocuments().get(0).getDocumentType().toString()))
                .andExpect(jsonPath("$.travellerDocuments[0].documentNumber").value(travellerDTO.getTravellerDocuments().get(0).getDocumentNumber()))
                .andExpect(jsonPath("$.travellerDocuments[0].documentIssuingCountry").value(travellerDTO.getTravellerDocuments().get(0).getDocumentIssuingCountry()))
                .andReturn();
    }

    @Test
    void testUpdateWithoutId() throws Exception {
        TravellerDTO travellerDTO = getTravellerDTO();

        restPurchaseOrderMockMvc.perform(post("/api/travellers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        restPurchaseOrderMockMvc.perform(put("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void testUpdate() throws Exception {
        TravellerDTO travellerDTO = getTravellerDTO();

        MvcResult result = restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TravellerDTO savedTravellerDTO = objectMapper.readValue(json, TravellerDTO.class);
        changeForUpdateTravellerDTO(savedTravellerDTO);
        restPurchaseOrderMockMvc.perform(put("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTravellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.travellerId").isNumber())
                .andExpect(jsonPath("$.firstName").value(savedTravellerDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(savedTravellerDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(savedTravellerDTO.getEmail()))
                .andExpect(jsonPath("$.mobileNumber").value(savedTravellerDTO.getMobileNumber()))
                .andExpect(jsonPath("$.dateOfBirth").value(savedTravellerDTO.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.travellerDocuments[0].documentType").value(savedTravellerDTO.getTravellerDocuments().get(0).getDocumentType().toString()))
                .andExpect(jsonPath("$.travellerDocuments[0].documentNumber").value(savedTravellerDTO.getTravellerDocuments().get(0).getDocumentNumber()))
                .andExpect(jsonPath("$.travellerDocuments[0].documentIssuingCountry").value(savedTravellerDTO.getTravellerDocuments().get(0).getDocumentIssuingCountry()))
                .andReturn();
    }

    @Test
    void testUpdateWithTwoSameDocuments() throws Exception {
        TravellerDTO travellerDTO = getTravellerDTO();

        MvcResult result = restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TravellerDTO savedTravellerDTO = objectMapper.readValue(json, TravellerDTO.class);
        savedTravellerDTO.getTravellerDocuments().add(savedTravellerDTO.getTravellerDocuments().get(0));
        changeForUpdateTravellerDTO(savedTravellerDTO);
        restPurchaseOrderMockMvc.perform(put("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTravellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict());
    }

    @Test
    void testFindByDocument() throws Exception {
        TravellerDTO travellerDTO = getTravellerDTO();

        MvcResult result = restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        DocumentSearchDTO documentSearchDTO = new DocumentSearchDTO();
        documentSearchDTO.setDocumentType(travellerDTO.getTravellerDocuments().get(0).getDocumentType());
        documentSearchDTO.setDocumentNumber(travellerDTO.getTravellerDocuments().get(0).getDocumentNumber());
        documentSearchDTO.setDocumentIssuingCountry(travellerDTO.getTravellerDocuments().get(0).getDocumentIssuingCountry());
        restPurchaseOrderMockMvc.perform(get("/api/travellers/document/?documentType={type}&documentNumber={number}&documentIssuingCountry={country}", documentSearchDTO.getDocumentType(), documentSearchDTO.getDocumentNumber(), documentSearchDTO.getDocumentIssuingCountry())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentSearchDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void createTwoTravelersWithSameDocument() throws Exception {
        TravellerDTO travellerDTO = getTravellerDTO();

        MvcResult result = restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        TravellerDTO secondTravellerDTO = getTravellerDTO();
        secondTravellerDTO.setEmail("secondTraveller@test.com");
        secondTravellerDTO.setMobileNumber("77777777");

        restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondTravellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    void createTwoTravelersWithSameEmail() throws Exception {
        TravellerDTO travellerDTO = getTravellerDTO();

        restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        TravellerDTO secondTravellerDTO = getTravellerDTO();

        secondTravellerDTO.setMobileNumber("77777777");
        secondTravellerDTO.setTravellerDocuments(new ArrayList<>());

        restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondTravellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    void createTwoTravelersWithSameMobilePhone() throws Exception {
        TravellerDTO travellerDTO = getTravellerDTO();

        restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        TravellerDTO secondTravellerDTO = getTravellerDTO();
        secondTravellerDTO.setEmail("email@email.com");
        secondTravellerDTO.setTravellerDocuments(new ArrayList<>());

        restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondTravellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    void createTravelerWithNotActiveDocument() throws Exception {
        TravellerDTO travellerDTO = getTravellerDTO();
        TravellerDocumentDTO documentDTO = new TravellerDocumentDTO();
        documentDTO.setDocumentType(DocumentType.ID_DOCUMENT);
        documentDTO.setDocumentNumber("23231");
        documentDTO.setDocumentIssuingCountry("SK");
        travellerDTO.getTravellerDocuments().add(documentDTO);

        restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void createTravelerWithId() throws Exception {
        TravellerDTO travellerDTO = getTravellerDTO();
        travellerDTO.setTravellerId(1L);

        restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void testCreateAndMarkAsDeleted_testGetByEmailAndMobilePhone() throws Exception {
        TravellerDTO travellerDTO = getTravellerDTO();

        MvcResult result = restPurchaseOrderMockMvc.perform(post("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.travellerId").isNumber())
                .andExpect(jsonPath("$.firstName").value(travellerDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(travellerDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(travellerDTO.getEmail()))
                .andExpect(jsonPath("$.mobileNumber").value(travellerDTO.getMobileNumber()))
                .andExpect(jsonPath("$.dateOfBirth").value(travellerDTO.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.travellerDocuments[0].documentType").value(travellerDTO.getTravellerDocuments().get(0).getDocumentType().toString()))
                .andExpect(jsonPath("$.travellerDocuments[0].documentNumber").value(travellerDTO.getTravellerDocuments().get(0).getDocumentNumber()))
                .andExpect(jsonPath("$.travellerDocuments[0].documentIssuingCountry").value(travellerDTO.getTravellerDocuments().get(0).getDocumentIssuingCountry()))
                .andReturn();
        String json = result.getResponse().getContentAsString();
        TravellerDTO savedTravellerDTO = objectMapper.readValue(json, TravellerDTO.class);

        assertThat(savedTravellerDTO.getTravellerId(), Is.is(Matchers.notNullValue(Long.class)));

        // mark as deleted
        restPurchaseOrderMockMvc.perform(delete("/api/travellers?travellerId={id}", savedTravellerDTO.getTravellerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        // deleted not found
        restPurchaseOrderMockMvc.perform(get("/api/travellers/email/{email}", savedTravellerDTO.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());

        // deleted not found
        restPurchaseOrderMockMvc.perform(get("/api/travellers/mobile-phone/{mobilePhone}", savedTravellerDTO.getMobileNumber())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());

        restPurchaseOrderMockMvc.perform(put("/api/travellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(travellerDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    private TravellerDTO getTravellerDTO() {
        TravellerDTO travellerDTO = new TravellerDTO();
        travellerDTO.setFirstName("firstName");
        travellerDTO.setLastName("lastName");
        travellerDTO.setEmail("email@email.com");
        travellerDTO.setMobileNumber("12345566");
        travellerDTO.setDateOfBirth(LocalDate.of(1980,2,2));
        TravellerDocumentDTO travellerDocumentDTO  = new TravellerDocumentDTO();
        travellerDocumentDTO.setDocumentType(DocumentType.PASSPORT);
        travellerDocumentDTO.setDocumentNumber("23334");
        travellerDocumentDTO.setDocumentIssuingCountry("SK");
        travellerDTO.getTravellerDocuments().add(travellerDocumentDTO);
        return travellerDTO;
    }

    private void changeForUpdateTravellerDTO(TravellerDTO travellerDTO) {
        travellerDTO.setFirstName("firstName2");
        travellerDTO.setLastName("lastName2");
        travellerDTO.setEmail("email@email2.com");
        travellerDTO.setMobileNumber("555555");
        travellerDTO.setDateOfBirth(LocalDate.of(1985,2,2));
        TravellerDocumentDTO travellerDocumentDTO  = new TravellerDocumentDTO();
        travellerDocumentDTO.setDocumentType(DocumentType.ID_DOCUMENT);
        travellerDocumentDTO.setDocumentNumber("434343");
        travellerDocumentDTO.setDocumentIssuingCountry("CZ");
        travellerDTO.getTravellerDocuments().add(travellerDocumentDTO);
    }
}