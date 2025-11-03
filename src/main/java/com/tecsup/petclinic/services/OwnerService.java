package com.tecsup.petclinic.services;

import java.util.List;

import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;

/**
 * Interfaz para el servicio de Owner
 */
public interface OwnerService {

    /**
     * Crear un nuevo owner
     * @param owner
     * @return owner creado
     */
    Owner create(Owner owner);

    /**
     * Buscar owner por ID
     * @param id
     * @return owner encontrado
     * @throws OwnerNotFoundException
     */
    Owner findById(Integer id) throws OwnerNotFoundException;

    /**
     * Buscar owners por apellido
     * @param lastName
     * @return lista de owners
     */
    List<Owner> findByLastName(String lastName);
}