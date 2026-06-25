package com.apto.observer;

public interface DomainObserver<T extends DomainEvent> {

    Class<T> eventType();

    void update(T event);
}
