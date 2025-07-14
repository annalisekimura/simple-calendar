import java.time.format.*;
import java.util.*;
import java.time.*;


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

