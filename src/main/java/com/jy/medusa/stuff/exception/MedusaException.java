package com.jy.medusa.stuff.exception;

/**
 * Created by neo on 2016/11/4.
 */
public class MedusaException extends RuntimeException{

    public MedusaException() {
    }

    public MedusaException(String message) {
        super(message);
    }
}
