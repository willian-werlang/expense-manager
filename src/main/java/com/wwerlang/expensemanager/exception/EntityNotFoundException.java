package com.wwerlang.expensemanager.exception;

public class EntityNotFoundException extends RuntimeException {

    private final String entity;
    private final long id;

    public EntityNotFoundException(String entity, long id) {
        super();
        this.entity = entity;
        this.id = id;
    }

    @Override
    public String getMessage() {
        return entity + " not found with ID " + id + ".";
    }
}
