package com.rbc.petstore.rest;

import com.google.gson.Gson;
import com.rbc.petstore.dto.PetDTO;
import com.rbc.petstore.model.InventoryStatus;
import com.rbc.petstore.model.Pet;
import com.rbc.petstore.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class PetResourceIT {

    private static final String CNT_PET_NAME = "testValidPet";

    @Autowired
    private PetRepository repository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    /**
     * Initialize mocks
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * Test Pet creation REST call with valid parameters
     *
     * @throws IOException
     * @throws Exception
     * @see PetResource#create(PetDTO)
     */
    @Test
    void testCreate_valid() throws Exception {
        PetDTO pet = getValidPet();

        mockMvc.perform(
                post("/api/pet")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getPetJSON(pet)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(pet.getName()));
    }

    /**
     * Test Pet creation REST calls with invalid inputs that must fail at validation level
     *
     * @throws IOException
     * @throws Exception
     * @see PetResource#create(PetDTO)
     */
    @Test
    void testCreate_invalidName() throws Exception {
        PetDTO pet = getValidPet();
        pet.setName("");

        mockMvc.perform(
                post("/api/pet")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getPetJSON(pet)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreate_nullName() throws Exception {
        PetDTO pet = getValidPet();
        pet.setName(null);

        mockMvc.perform(
                post("/api/pet")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getPetJSON(pet)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreate_existingIdRejected() throws Exception {
        PetDTO pet = getValidPet();
        pet.setId(42L);

        mockMvc.perform(
                post("/api/pet")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getPetJSON(pet)))
            .andExpect(status().isBadRequest());
    }

    /**
     * Test get pet call with an existing id
     *
     * @throws IOException
     * @throws Exception
     * @see PetResource#getOne(Long)
     */
    @Test
    void testGet_valid() throws Exception {
        Pet firstPet = repository.findById(1L).get();

        mockMvc.perform(
                get("/api/pet/1")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(firstPet.getId().toString()))
            .andExpect(jsonPath("$.name").value(firstPet.getName()))
            .andExpect(jsonPath("$.status").value(firstPet.getStatus().toString()));
    }

    /**
     * Test get pet calls with negative value.
     *
     * @throws IOException
     * @throws Exception
     * @see PetResource#getOne(Long)
     */
    @Test
    void testGet_negativeValue() throws Exception {
        mockMvc.perform(
                get("/api/pet/-1")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    /**
     * Test get pet calls with invalid id
     *
     * @throws IOException
     * @throws Exception
     * @see PetResource#getOne(Long)
     */
    @Test
    void testGet_nonNumericValue() throws Exception {
        mockMvc.perform(
                get("/api/pet/aa")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    /**
     * Test delete call with an existing pet identifier
     *
     * @throws IOException
     * @throws Exception
     * @see PetResource#delete(Long)
     */
    @Test
    void testDelete_valid() throws Exception {

        mockMvc.perform(
                delete("/api/pet/1")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    /**
     * Test delete call with invalid value
     *
     * @throws IOException
     * @throws Exception
     * @see PetResource#delete(Long)
     */
    @Test
    void testDelete_invalidValue() throws Exception {
        mockMvc.perform(
                delete("/api/pet/-1")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    /**
     * Test delete call with invalid value
     *
     * @throws IOException
     * @throws Exception
     * @see PetResource#delete(Long)
     */
    @Test
    void testGet_petStatus() throws Exception {
        mockMvc.perform(
                get("/api/pet/status/SOLD")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    /**
     * Helper methods
     */

    /**
     * @return a dummy valid pet instance
     */
    private PetDTO getValidPet() {
        return new PetDTO(null, CNT_PET_NAME, InventoryStatus.AVAILABLE);
    }

    private String getPetJSON(PetDTO pet) {
        return new Gson().toJson(pet);
    }
}
