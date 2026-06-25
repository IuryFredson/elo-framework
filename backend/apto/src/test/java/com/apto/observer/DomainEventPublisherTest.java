package com.apto.observer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DomainEventPublisherTest {

    @Test
    void deveNotificarSomenteObserversDoEventoPublicado() {
        TestEvent event = new TestEvent();

        @SuppressWarnings("unchecked")
        DomainObserver<TestEvent> observerCompativel = mock(DomainObserver.class);
        @SuppressWarnings("unchecked")
        DomainObserver<OutroTestEvent> observerIncompativel = mock(DomainObserver.class);

        when(observerCompativel.eventType()).thenReturn(TestEvent.class);
        when(observerIncompativel.eventType()).thenReturn(OutroTestEvent.class);

        DomainEventPublisher publisher = new DomainEventPublisher(
                List.of(observerCompativel, observerIncompativel));

        publisher.publish(event);

        verify(observerCompativel).update(event);
        verify(observerIncompativel, never()).update(new OutroTestEvent());
    }

    private record TestEvent() implements DomainEvent {
    }

    private record OutroTestEvent() implements DomainEvent {
    }
}
