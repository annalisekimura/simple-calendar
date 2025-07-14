import java.time.format.*;
import java.util.*;
import java.time.*;

// manages a list of events
public class CalendarManager {
    // stores all created events
    private List<Event> events;

    // constructor that initializes events array list
    public CalendarManager() {
        this.events = new ArrayList<>();
    }

    // create a new event and add it to the calendar
    public boolean createEvent(String title, LocalDateTime startTime, LocalDateTime endTime) {
        Event newEvent = new Event(title, startTime, endTime);

        // check that the new event does not overlap with existing events
        for (Event existingEvent : events) {
            if (newEvent.overlapsWith(existingEvent)) {
                System.out.println("Event overlaps with the event: " + existingEvent.getTitle());
                return false;
            }
        }

        events.add(newEvent);

        // sort the events by time
        events.sort((e1, e2) -> e1.getStartTime().compareTo(e2.getStartTime()));

        System.out.println("Event " + newEvent.getTitle() + " created.");
        return true;
    }

    // lists all events for today
    public void listEventsForToday(LocalDate date) {

        // gets all the events for today
        List<Event> dayEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.isOnDate(date)) {
                dayEvents.add(event);
            }
        }

        System.out.println("\nEvents for " + date + ":");

        if (dayEvents.isEmpty()) {
            System.out.println("No events scheduled for this day.");
        } else {
            dayEvents.forEach(event -> System.out.println(
                event.getTitle() + " from " + event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))));
        }
    }

    // gets all the events for the day that haven't passed
    public void listRemainingEventsForToday() {

        LocalDate today = LocalDate.now();

        // get all remaining events
        List<Event> remainingEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.isOnDate(today) && event.isUpcoming()) {
                remainingEvents.add(event);
            }
        }

        System.out.println("\nRemaining events for today: ");

        if (remainingEvents.isEmpty()) {
            System.out.println("No remaining events for today.");
        } else {
            remainingEvents.forEach(event -> System.out.println(
                event.getTitle() + " from " + event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))));
        }

    }
    
    // find the next available time slot for today or a specific day
    public void findNextAvailableSlot(int durationMinutes, LocalDate date) {
        LocalDateTime searchStart = date.atTime(0,0);
        LocalDateTime searchEnd = date.atTime(23,59);

        // get all events for the day specified in order of start time
        List<Event> dayEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.isOnDate(date)) {
                dayEvents.add(event);
            }
        }
        dayEvents.sort(Comparator.comparing(Event::getStartTime));

        // if the date specified is today, start the search time at the current time
        if (date.equals(LocalDate.now())) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(searchStart)) {
                searchStart = now;
            }
        }

        System.out.println("\nSearching for " + durationMinutes + " minute slot on " + date + ":");

        // if there are no scheduled events yet
        if (dayEvents.isEmpty()) {
            System.out.println("Available slot: " + searchStart.format(DateTimeFormatter.ofPattern("HH:mm"))
                 + " - " + searchStart.plusMinutes(durationMinutes).format(DateTimeFormatter.ofPattern("HH:mm")));

            return;
        }

        // check slots between start time and first event
        if (dayEvents.get(0).getStartTime().isAfter(searchStart.plusMinutes(durationMinutes))) {
            System.out.println("Available slot: " + searchStart.format(DateTimeFormatter.ofPattern("HH:mm"))
                 + " - " + searchStart.plusMinutes(durationMinutes).format(DateTimeFormatter.ofPattern("HH:mm")));

            return;
        }

        // check slots between events in order
        for (int i = 0; i < dayEvents.size() - 1; i++) {
            LocalDateTime slotStart = dayEvents.get(i).getEndTime();
            LocalDateTime slotEnd = dayEvents.get(i + 1).getStartTime();

            if (Duration.between(slotStart, slotEnd).toMinutes() >= durationMinutes) {
                System.out.println("Available slot: " + slotStart.format(DateTimeFormatter.ofPattern("HH:mm"))
                     + " - " + slotStart.plusMinutes(durationMinutes).format(DateTimeFormatter.ofPattern("HH:mm")));

                return;
            }
        }

        // check slot after last event
        LocalDateTime afterLastEvent = dayEvents.get(dayEvents.size() - 1).getEndTime();
        if (afterLastEvent.plusMinutes(durationMinutes).isBefore(searchEnd)) {
            System.out.println("Available slot: " + afterLastEvent.format(DateTimeFormatter.ofPattern("HH:mm")) 
                + " - " + afterLastEvent.plusMinutes(durationMinutes).format(DateTimeFormatter.ofPattern("HH:mm")));
            return;
        }

        System.out.println("No available slot found for " + durationMinutes + " minutes on " + date);
    }

}