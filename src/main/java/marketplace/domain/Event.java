package marketplace.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Event {
    private final EventType eventType;
    private final LocalDateTime timeStamp;

    private Event(final EventType eventType, final LocalDateTime timeStamp) {
        this.eventType = eventType;
        this.timeStamp = timeStamp;
    }

    public EventType getEventType() {
        return eventType;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventType=" + eventType +
                ", timeStamp=" + timeStamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eventType == event.eventType &&
                Objects.equals(timeStamp, event.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, timeStamp);
    }


    public static final class EventBuilder {
        private EventType eventType;
        private LocalDateTime timeStamp;

        private EventBuilder() {
        }

        public static EventBuilder anEvent() {
            return new EventBuilder();
        }

        public EventBuilder withEventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public EventBuilder withTimeStamp(LocalDateTime timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Event build() {
            return new Event(eventType, timeStamp);
        }
    }
}
