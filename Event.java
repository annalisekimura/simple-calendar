import java.time.*;

// event class
public class Event {
    private String title;  // name of event
    private LocalDateTime startTime;  // start time
    private LocalDateTime endTime;  // end time

    // event creation constructor
    public Event(String title, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // get title
    public String getTitle() {
        return title;
    }

    // get start time of event
    public LocalDateTime getStartTime() {
        return startTime;
    }

    // get end time of event
    public LocalDateTime getEndTime() {
        return endTime;
    }

    // check if an event overlaps with another event
    public boolean overlapsWith(Event other) {
        return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
    }

    // check the events on a certain date
    public boolean isOnDate(LocalDate date) {
        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    // check the events after a certain date time
    public boolean isUpcoming() {
        return this.endTime.isAfter(LocalDateTime.now());
    }
}