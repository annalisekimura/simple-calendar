import java.time.format.*;
import java.util.*;
import java.time.*;

// event class
class Event {
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


// manages a list of events
class CalendarManager {
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
            dayEvents.forEach(event -> System.out.println(event.getTitle()));
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
            remainingEvents.forEach(event -> System.out.println(event.getTitle()));
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

// runs calendar app
public class CalendarApp {
    private CalendarManager calendar;
    private Scanner scanner;

    // constructor to create new instance of CalendarManager and Scanner object
    public CalendarApp() {
        this.calendar = new CalendarManager();
        this.scanner = new Scanner(System.in);
    }

    // interface for calendar manager menu
    public void run() {
        System.out.println("=== Calendar Management System ===");

        while (true) {
            showMenu();
            int choice = getChoice();

            switch (choice) {
                case 1:
                    createEvent();
                    break;
                case 2:
                    listEventsForToday();
                    break;
                case 3:
                    listRemainingEventsForToday();
                    break;
                case 4:
                    listEventsForSpecificDay();
                    break;
                case 5:
                    findNextAvailableSlot();
                    break;
                case 6:
                    System.out.println("Thank you for using the Calendar Mangement System!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");

            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    // list options for users
    private void showMenu() {
        System.out.println("\n=== MENU ===");
        System.out.println("1. Create Event");
        System.out.println("2. List Events for Today");
        System.out.println("3. List Remaining Events for Today");
        System.out.println("4. List Events for Specific Day");
        System.out.println("5. Find Next Available Slot");
        System.out.println("6. Exit");
        System.out.println("Choose an option (1-6): ");
    }
    
    // read in the menu choice
    private int getChoice() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            return choice;
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }

    // choice 1: create an event
    private void createEvent() {
        System.out.println("\n===Create Event===");
        System.out.println("Enter event title: ");
        String title = scanner.nextLine();

        System.out.println("Enter start date (yyyy-MM-dd): ");
        CharSequence startDatestr = scanner.nextLine();

        System.out.println("Enter start time (HH:mm): ");
        CharSequence startTimeStr = scanner.nextLine();

        System.out.println("Enter end date (yyyy-MM-dd): ");
        CharSequence endDateStr = scanner.nextLine();

        System.out.println("Enter end time (HH:mm): ");
        CharSequence endTimeStr = scanner.nextLine();

        try {
            LocalDate startDate = LocalDate.parse(startDatestr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));

            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

            calendar.createEvent(title, startDateTime, endDateTime);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Please use yyyy-mm-dd for date and HH:mm for times.");

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // choice 2: list events for today
    private void listEventsForToday() {
        calendar.listEventsForToday(LocalDate.now());
    }

    // choice 3: list remaning events for today
    private void listRemainingEventsForToday() {
        calendar.listRemainingEventsForToday();
    }

    // choice 4: list events for a specific day
    private void listEventsForSpecificDay() {
        System.out.println("\n=== List Events for Specific Day===");
        System.out.println("Enter date (yyyy-MM-dd): ");
        CharSequence dateStr = scanner.nextLine();

        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            calendar.listEventsForToday(date);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    // choice 5: find the next available time slot
    private void findNextAvailableSlot() {
        System.out.println("\n=== Find Next Available Slot ===");
        System.out.println("Enter duration in minutes: ");

        try {
            int duration = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Enter date (yyyy-MM-dd) or press Enter for today: ");
            CharSequence dateStr = scanner.nextLine();

            LocalDate date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            calendar.findNextAvailableSlot(duration, date);
        } catch (NumberFormatException e) {
            System.out.println("Invalid duration. Please enter a number.");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    // run the app
    public static void main(String[] args) {
        CalendarApp app = new CalendarApp();
        app.run();
    }
}

