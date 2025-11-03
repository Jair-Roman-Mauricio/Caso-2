package com.tecsup.petclinic.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tecsup.petclinic.entities.Owner;

/**
 * Repositorio para Owner
 */
@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    // Buscar owners por apellido
    List<Owner> findByLastName(String lastName);
}