package com.tecsup.petclinic.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * Pruebas unitarias para OwnerService
 */
@SpringBootTest
@Slf4j
@Transactional
public class OwnerServiceTest {

	@Autowired
	private OwnerService ownerService;
	
	private Owner testOwner;
	
	/**
	 * Método que se ejecuta antes de cada prueba para preparar datos de prueba
	 */
	@BeforeEach
	void setUp() {
		// Crear un owner para pruebas
		testOwner = new Owner();
		testOwner.setFirstName("John");
		testOwner.setLastName("Doe");
		testOwner.setAddress("123 Main St");
		testOwner.setCity("Springfield");
		testOwner.setTelephone("555-1234");
	}

	/**
	 * Prueba para verificar la creación exitosa de un owner
	 */
	@Test
	public void testCreateOwner() {
		// Crear un nuevo owner
		Owner newOwner = ownerService.create(testOwner);
		
		// Registrar información del owner creado
		log.info("OWNER CREATED: " + newOwner);
		
		// Verificar que se haya asignado un ID (persistencia exitosa)
		assertNotNull(newOwner.getId(), "El ID del owner no debería ser nulo después de crearlo");
		
		// Verificar que los datos se hayan guardado correctamente
		assertEquals(testOwner.getFirstName(), newOwner.getFirstName(), "El nombre debería coincidir");
		assertEquals(testOwner.getLastName(), newOwner.getLastName(), "El apellido debería coincidir");
		assertEquals(testOwner.getAddress(), newOwner.getAddress(), "La dirección debería coincidir");
		assertEquals(testOwner.getCity(), newOwner.getCity(), "La ciudad debería coincidir");
		assertEquals(testOwner.getTelephone(), newOwner.getTelephone(), "El teléfono debería coincidir");
	}

	/**
	 * Prueba para verificar la búsqueda de owner por ID
	 */
	@Test
	public void testFindOwnerById() {
		// Crear un owner para la prueba
		Owner createdOwner = ownerService.create(testOwner);
		Integer ownerId = createdOwner.getId();
		
		// Buscar el owner por ID
		Owner foundOwner = null;
		try {
			foundOwner = ownerService.findById(ownerId);
		} catch (OwnerNotFoundException e) {
			fail("No se debería lanzar excepción al buscar un owner existente: " + e.getMessage());
		}
		
		// Registrar información del owner encontrado
		log.info("OWNER FOUND: " + foundOwner);
		
		// Verificar que los datos coincidan con los del owner creado
		assertEquals(createdOwner.getId(), foundOwner.getId(), "El ID debería coincidir");
		assertEquals(createdOwner.getFirstName(), foundOwner.getFirstName(), "El nombre debería coincidir");
		assertEquals(createdOwner.getLastName(), foundOwner.getLastName(), "El apellido debería coincidir");
		assertEquals(createdOwner.getAddress(), foundOwner.getAddress(), "La dirección debería coincidir");
		assertEquals(createdOwner.getCity(), foundOwner.getCity(), "La ciudad debería coincidir");
		assertEquals(createdOwner.getTelephone(), foundOwner.getTelephone(), "El teléfono debería coincidir");
	}

	/**
	 * Prueba para verificar la búsqueda de owners por apellido
	 */
	@Test
	public void testFindOwnerByLastName() {
		// Crear un owner para la prueba
		Owner createdOwner = ownerService.create(testOwner);
		String lastName = createdOwner.getLastName();
		
		// Buscar owners por apellido
		List<Owner> owners = ownerService.findByLastName(lastName);
		
		// Registrar información de los owners encontrados
		log.info("OWNERS FOUND BY LAST NAME '" + lastName + "': " + owners.size());
		
		// Verificar que se haya encontrado al menos un owner
		assertTrue(owners.size() > 0, "Debería encontrarse al menos un owner con el apellido " + lastName);
		
		// Verificar que todos los owners encontrados tengan el apellido buscado
		boolean foundMatch = false;
		for (Owner owner : owners) {
			assertEquals(lastName, owner.getLastName(), "Todos los owners encontrados deberían tener el apellido " + lastName);
			
			// Verificar si alguno de los owners encontrados es el que creamos
			if (owner.getId().equals(createdOwner.getId())) {
				foundMatch = true;
				assertEquals(createdOwner.getFirstName(), owner.getFirstName(), "El nombre debería coincidir");
				assertEquals(createdOwner.getAddress(), owner.getAddress(), "La dirección debería coincidir");
				assertEquals(createdOwner.getCity(), owner.getCity(), "La ciudad debería coincidir");
				assertEquals(createdOwner.getTelephone(), owner.getTelephone(), "El teléfono debería coincidir");
			}
		}
		
		// Verificar que se haya encontrado el owner que creamos
		assertTrue(foundMatch, "El owner creado debería estar entre los resultados de la búsqueda");
	}
}
