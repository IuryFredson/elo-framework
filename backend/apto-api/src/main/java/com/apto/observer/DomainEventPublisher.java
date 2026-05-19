package com.apto.observer;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DomainEventPublisher {

    private final List<DomainObserver<? extends DomainEvent>> observers;

    public DomainEventPublisher(List<DomainObserver<? extends DomainEvent>> observers) {
        this.observers = observers;
    }

    public void publish(DomainEvent event) {
        observers.stream()
                .filter(observer -> observer.eventType().isAssignableFrom(event.getClass()))
                .forEach(observer -> notifyObserver(observer, event));
    }

    @SuppressWarnings("unchecked")
    private <T extends DomainEvent> void notifyObserver(DomainObserver<T> observer, DomainEvent event) {
        observer.update((T) event);
    }
}
