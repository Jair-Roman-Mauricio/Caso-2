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

	/**
	 * Prueba para verificar la ACTUALIZACIÓN COMPLETA de un owner.
	 * Se modifican todos los campos y se verifica que los cambios se persistan correctamente.
	 */
	@Test
	public void testUpdateOwner() {
		// Arrange: crear y persistir un owner inicial
		Owner created = ownerService.create(testOwner);
		Integer id = created.getId();
		assertNotNull(id, "El ID no debería ser nulo después de crear el owner");
		
		// Guardar valores originales para logging
		log.info("OWNER ORIGINAL: " + created);

		// Modificar todos los campos del owner
		created.setFirstName("Jane");
		created.setLastName("Smith");
		created.setAddress("742 Evergreen Terrace");
		created.setCity("Shelbyville");
		created.setTelephone("555-9876");

		// Act: actualizar el owner con los nuevos datos
		Owner updated = null;
		try {
			updated = ownerService.update(created);
			log.info("OWNER UPDATED: " + updated);
		} catch (OwnerNotFoundException e) {
			fail("No debería lanzarse OwnerNotFoundException al actualizar un owner existente: " + e.getMessage());
		}

		// Assert: recuperar desde la BD y comparar todos los campos
		try {
			Owner found = ownerService.findById(id);
			
			// Verificar que el ID se mantiene
			assertEquals(id, found.getId(), "El ID debería mantenerse tras la actualización");
			
			// Verificar que todos los campos se actualizaron correctamente
			assertEquals("Jane", found.getFirstName(), "El firstName debería actualizarse a 'Jane'");
			assertEquals("Smith", found.getLastName(), "El lastName debería actualizarse a 'Smith'");
			assertEquals("742 Evergreen Terrace", found.getAddress(), "El address debería actualizarse a '742 Evergreen Terrace'");
			assertEquals("Shelbyville", found.getCity(), "El city debería actualizarse a 'Shelbyville'");
			assertEquals("555-9876", found.getTelephone(), "El telephone debería actualizarse a '555-9876'");
		} catch (OwnerNotFoundException e) {
			fail("No debería lanzarse OwnerNotFoundException al recuperar un owner existente: " + e.getMessage());
		}
	}

	/**
	 * Prueba para verificar la ACTUALIZACIÓN PARCIAL de un owner.
	 * Solo se modifican algunos campos y se valida que los demás permanezcan iguales.
	 */
	@Test
	public void testUpdateOwnerPartialData() {
		// Arrange: crear y persistir un owner inicial
		Owner created = ownerService.create(testOwner);
		Integer id = created.getId();
		assertNotNull(id, "El ID no debería ser nulo después de crear el owner");

		// Guardar valores originales para comparar campos no modificados
		String originalFirstName = created.getFirstName();
		String originalLastName = created.getLastName();
		String originalCity = created.getCity();
		
		log.info("OWNER ANTES DE ACTUALIZACIÓN PARCIAL: " + created);

		// Modificar solo algunos campos (address y telephone)
		created.setAddress("Av. Los Próceres 123");
		created.setTelephone("999-111-222");

		// Act: actualizar el owner con cambios parciales
		try {
			ownerService.update(created);
		} catch (OwnerNotFoundException e) {
			fail("No debería lanzarse OwnerNotFoundException al actualizar un owner existente: " + e.getMessage());
		}

		// Assert: recuperar desde la BD y validar cambios parciales
		try {
			Owner found = ownerService.findById(id);
			log.info("OWNER DESPUÉS DE ACTUALIZACIÓN PARCIAL: " + found);
			
			// Verificar que los campos modificados se actualizaron
			assertEquals("Av. Los Próceres 123", found.getAddress(), "El address debería actualizarse a 'Av. Los Próceres 123'");
			assertEquals("999-111-222", found.getTelephone(), "El telephone debería actualizarse a '999-111-222'");
			
			// Verificar que los campos no modificados permanecen iguales
			assertEquals(originalFirstName, found.getFirstName(), "El firstName no debería cambiar");
			assertEquals(originalLastName, found.getLastName(), "El lastName no debería cambiar");
			assertEquals(originalCity, found.getCity(), "El city no debería cambiar");
		} catch (OwnerNotFoundException e) {
			fail("No debería lanzarse OwnerNotFoundException al recuperar un owner existente: " + e.getMessage());
		}
	}

	/**
	 * Pruebas de validaciones y casos edge en actualización.
	 * Incluye: actualizar owner inexistente, actualizar con datos nulos, etc.
	 */
	@Test
	public void testUpdateOwnerValidations() {
		// Arrange: crear un owner base para algunas pruebas
		Owner created = ownerService.create(testOwner);
		Integer id = created.getId();
		assertNotNull(id, "El ID no debería ser nulo después de crear el owner");

		// Caso 1: Actualizar con datos nulos en algunos campos
		// (El esquema actual permite nulos, por lo que debería funcionar)
		created.setAddress(null);
		created.setTelephone(null);

		// Act: actualizar con campos nulos
		try {
			ownerService.update(created);
			log.info("OWNER ACTUALIZADO CON CAMPOS NULOS: " + created);
		} catch (OwnerNotFoundException e) {
			fail("No debería lanzarse OwnerNotFoundException al actualizar un owner existente: " + e.getMessage());
		}

		// Assert: verificar que los campos se guardaron como nulos
		try {
			Owner found = ownerService.findById(id);
			assertEquals(null, found.getAddress(), "El address debería poder ser nulo según el esquema actual");
			assertEquals(null, found.getTelephone(), "El telephone debería poder ser nulo según el esquema actual");
		} catch (OwnerNotFoundException e) {
			fail("No debería lanzarse OwnerNotFoundException al recuperar un owner existente: " + e.getMessage());
		}

		// Caso 2: Intentar actualizar un owner inexistente (debería lanzar excepción)
		Owner nonExistentOwner = new Owner();
		nonExistentOwner.setId(999999); // ID que no existe en BD
		nonExistentOwner.setFirstName("Ghost");
		nonExistentOwner.setLastName("Owner");
		nonExistentOwner.setAddress("Unknown");
		nonExistentOwner.setCity("Nowhere");
		nonExistentOwner.setTelephone("000-0000");

		// Act & Assert: verificar que se lanza OwnerNotFoundException
		try {
			ownerService.update(nonExistentOwner);
			fail("Debería lanzarse OwnerNotFoundException al intentar actualizar un owner inexistente");
		} catch (OwnerNotFoundException e) {
			// Excepción esperada
			log.info("EXCEPCIÓN ESPERADA AL ACTUALIZAR OWNER INEXISTENTE: " + e.getMessage());
			assertNotNull(e.getMessage(), "El mensaje de la excepción no debería ser nulo");
			assertTrue(e.getMessage().contains("999999"), "El mensaje debería contener el ID del owner inexistente");
		}

		// Caso 3: Intentar actualizar un owner con ID nulo (debería lanzar excepción)
		Owner ownerWithNullId = new Owner();
		ownerWithNullId.setId(null);
		ownerWithNullId.setFirstName("Null");
		ownerWithNullId.setLastName("ID");
		ownerWithNullId.setAddress("Test Address");
		ownerWithNullId.setCity("Test City");
		ownerWithNullId.setTelephone("123-4567");

		// Act & Assert: verificar que se lanza NullPointerException o OwnerNotFoundException
		try {
			ownerService.update(ownerWithNullId);
			fail("Debería lanzarse una excepción al intentar actualizar un owner con ID nulo");
		} catch (OwnerNotFoundException e) {
			// Excepción esperada
			log.info("EXCEPCIÓN ESPERADA AL ACTUALIZAR OWNER CON ID NULO: " + e.getMessage());
			assertNotNull(e.getMessage(), "El mensaje de la excepción no debería ser nulo");
		} catch (NullPointerException e) {
			// También es aceptable en este caso
			log.info("NULLPOINTEREXCEPTION AL ACTUALIZAR OWNER CON ID NULO: " + e.getMessage());
		}
	}
}
