package com.rbc.petstore.service;

import com.rbc.petstore.dto.PetDTO;
import com.rbc.petstore.model.Pet;
import com.rbc.petstore.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Pet Service
 *
 * @see PetDTO
 */
@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    /**
     * Get all Pets
     *
     * @return a pets
     */
    public Iterable<Pet> getAllPets() {
        return petRepository.findAll();
    }


    /**
     * Find pet by ID
     *
     * @param id identifier of the pet to find
     * @return a {@link Pet} instance if a match is found, null otherwise
     */
    public Pet getPet(Long id) {
        return petRepository.findById(id).orElse(null);
    }

    /**
     * Find pet by Status
     *
     * @param status status of the pet to find
     * @return a {@link Pet} instance if a match is found, null otherwise
     */
    public List<Pet> getPetStatus(String status) {

        List<Pet> statusPets = new ArrayList<Pet>();
       
        this.getAllPets().forEach(pet -> {
            if(pet.getStatus().toString().equals(status)) {
                statusPets.add(pet);
            }
        });

        return statusPets;
    }

    /**
     * Add a new pet to the store
     *
     * @param pet entity to create
     * @return the created {@link Pet}
     */
    public Pet createPet(Pet pet) {
        if (pet.getId() != null) {
            throw new InvalidParameterException("Cannot create an already existing pet");
        }
        return petRepository.save(pet);
    }

    /**
     * Deletes a pet
     *
     * @param id Pet id to delete
     */
    public void deletePet(Long id) {
        Optional<Pet> toDelete = petRepository.findById(id);
        if (toDelete.isEmpty()) {
            throw new InvalidParameterException("Pet not found");
        }

        petRepository.deleteById(id);
    }
}
