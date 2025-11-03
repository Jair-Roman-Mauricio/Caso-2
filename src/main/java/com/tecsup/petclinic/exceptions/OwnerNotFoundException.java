package com.tecsup.petclinic.exceptions;

/**
 * Excepción para cuando no se encuentra un Owner
 */
public class OwnerNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public OwnerNotFoundException(String message) {
        super(message);
    }
}