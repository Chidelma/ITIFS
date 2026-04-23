package com.rbc.petstore.rest;

import com.rbc.petstore.dto.PetDTO;
import com.rbc.petstore.model.Pet;
import com.rbc.petstore.service.PetService;
import com.rbc.petstore.util.PetConvertor;

import java.security.InvalidParameterException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST operations for {@link PetDTO} resource
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/pet")
public class PetResource {

    private final PetService petService;

    @Autowired
    public PetResource(PetService petService) {
        this.petService = petService;
    }

    /**
     * Get all pets
     *
     * @return a {@link HttpStatus#OK} on the status and a PetDTO instance in the response body if the id exists, {@link HttpStatus#BAD_REQUEST} on status otherwise
     */
    @GetMapping("")
    public ResponseEntity<Iterable<PetDTO>> getAll() {
        Iterable<Pet> pets = petService.getAllPets();
        return new ResponseEntity<>(PetConvertor.toDtoList(pets), HttpStatus.OK);
    }

    /**
     * Get one pet by id.
     *
     * @param petId pet identifier
     * @return a {@link HttpStatus#OK} on the status and a PetDTO instance in the response body if the id exists, {@link HttpStatus#BAD_REQUEST} on status otherwise
     */
    @GetMapping("/{petId}")
    public ResponseEntity<PetDTO> getOne(@PathVariable(name = "petId") Long petId) {
        Pet pet = petService.getPet(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(PetConvertor.toDto(pet), HttpStatus.OK);
    }

     /**
     * Get one pet by status.
     *
     * @param petStatus pet status
     * @return a {@link HttpStatus#OK} on the status and a PetDTO instance in the response body if the id exists, {@link HttpStatus#BAD_REQUEST} on status otherwise
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Iterable<PetDTO>> getByStatus(@PathVariable(name = "status") String petStatus) {

        List<Pet> pets = petService.getPetStatus(petStatus);

        return new ResponseEntity<>(PetConvertor.toDtoList(pets), HttpStatus.OK);
    }

    /**
     * Add a new pet to the store
     *
     * @param petDto to create
     * @return if successfully it returns {@link HttpStatus#CREATED} on status and the created {@link PetDTO}, {@link HttpStatus#BAD_REQUEST} on status otherwise
     */
    @PostMapping("")
    public ResponseEntity<PetDTO> create(@Valid @RequestBody PetDTO petDto) {
        if (petDto.getId() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Pet createdPet = petService.createPet(PetConvertor.fromDto(petDto));

            return new ResponseEntity<>(PetConvertor.toDto(createdPet), HttpStatus.CREATED);
        } catch (InvalidParameterException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes a pet
     *
     * @param petId pet identifier
     * @return if successfully a {@link HttpStatus#OK} on the status,  {@link HttpStatus#BAD_REQUEST} otherwise
     */
    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> delete(@PathVariable(name = "petId") Long petId) {
        try {
            petService.deletePet(petId);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
