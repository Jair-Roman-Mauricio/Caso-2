package com.tecsup.petclinic.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.repositories.OwnerRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio de Owner
 */
@Service
@Slf4j
public class OwnerServiceImpl implements OwnerService {

    private OwnerRepository ownerRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Owner create(Owner owner) {
        return ownerRepository.save(owner);
    }

    @Override
    public Owner findById(Integer id) throws OwnerNotFoundException {
        Optional<Owner> owner = ownerRepository.findById(id);
        
        if (!owner.isPresent()) {
            throw new OwnerNotFoundException("Owner not found with id: " + id);
        }
        
        return owner.get();
    }

    @Override
    public List<Owner> findByLastName(String lastName) {
        List<Owner> owners = ownerRepository.findByLastName(lastName);
        owners.forEach(owner -> log.info("Owner: {}", owner));
        return owners;
    }

    @Override
    public Owner update(Owner owner) throws OwnerNotFoundException {
        // Verificar que el owner existe antes de actualizar
        Optional<Owner> existingOwner = ownerRepository.findById(owner.getId());
        
        if (!existingOwner.isPresent()) {
            throw new OwnerNotFoundException("Owner not found with id: " + owner.getId());
        }
        
        // Actualizar el owner
        return ownerRepository.save(owner);
    }
}