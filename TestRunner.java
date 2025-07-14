import java.time.*;
import java.time.format.DateTimeFormatter;

// test the calendar management system
public class TestRunner {
    private CalendarManager calendar;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public TestRunner() {
        this.calendar = new CalendarManager();
    }

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        runner.runComprehensiveTests();
    }

    // run tests
    public void runComprehensiveTests() {
        System.out.println("=== Calendar Management System Tests ===");

        // Test 1: Basic Event Creation
        testBasicEventCreation();
        
        // Test 2: Overlap Prevention
        testOverlapPrevention();
        
        // Test 3: Event Listing
        testEventListing();
        
        // Test 4: Time Slot Finding
        testTimeSlotFinding();
        
        System.out.println("\n=== All tests completed successfully! ===");
    }

    // test basic event creation
    private void testBasicEventCreation() {
        System.out.println("\n=== TEST 1: Basic Event Creation ===");
        
        LocalDate testDate = LocalDate.now().plusDays(1); // tomorrow
        createTestEvent("Really Early Meeting", testDate, "00:01", "08:30");
        createTestEvent("Morning Standup", testDate, "09:00", "09:30");
        createTestEvent("Client Meeting", testDate, "10:00", "11:00");
        createTestEvent("Lunch Break", testDate, "12:00", "13:00");
        createTestEvent("Code Review", testDate, "14:00", "15:00");
        createTestEvent("Team Retrospective", testDate, "16:00", "17:00");
        
        System.out.println("=== Basic event creation tests passed ===");
    }

    // test overlap prevention
    private void testOverlapPrevention() {
        System.out.println("\n=== TEST 2: Overlap Prevention ===");
        
        LocalDate testDate = LocalDate.now().plusDays(1);
        
        // These should fail due to overlaps
        System.out.println("\nTesting overlap detection: ");
        
        // Overlap with Morning Standup (09:00-09:30)
        createTestEvent("Conflicting Meeting 1", testDate, "09:15", "09:45");
        
        // Overlap with Client Meeting (10:00-11:00)
        createTestEvent("Conflicting Meeting 2", testDate, "10:30", "11:30");
        
        // Overlap with Lunch Break (12:00-13:00)
        createTestEvent("Extended Lunch", testDate, "11:30", "12:30");
        
        // Complete overlap
        createTestEvent("All Day Meeting", testDate, "00:01", "23:59");
        
        System.out.println("=== Overlap prevention tests passed ===");
    }

    // test event listing
    private void testEventListing() {
        System.out.println("\n=== TEST 3: Event Listing ===");
        
        LocalDate testDate = LocalDate.now().plusDays(1);
        
        // List events for tomorrow
        System.out.println("\nListing events for tomorrow:");
        calendar.listEventsForToday(testDate);
        
        // Test today's events (should be empty unless events exist)
        System.out.println("\nListing events for today:");
        calendar.listEventsForToday(LocalDate.now());
        
        // Test remaining events for today
        System.out.println("\nListing remaining events for today:");
        calendar.listRemainingEventsForToday();
        
        System.out.println("=== Event listing tests passed ===");
    }

    // test time slot finding functionality
    private void testTimeSlotFinding() {
        System.out.println("\n=== TEST 4: Time Slot Finding ===");
        
        LocalDate testDate = LocalDate.now().plusDays(1);
        
        // Test various slot durations
        int[] durations = {60, 120, 600};
        
        for (int duration : durations) {
            System.out.println("\nFinding " + duration + " minute slot:");
            calendar.findNextAvailableSlot(duration, testDate);
        }
        
        // Test slot finding for today (should consider current time)
        System.out.println("\nFinding 60 minute slot for today:");
        calendar.findNextAvailableSlot(60, LocalDate.now());
        
        System.out.println("=== Time slot finding tests passed ===");
    }


    // helper method to create test events
    private void createTestEvent(String title, LocalDate date, String startTime, String endTime) {
        try {
            LocalTime start = LocalTime.parse(startTime, TIME_FORMAT);
            LocalTime end = LocalTime.parse(endTime, TIME_FORMAT);
            
            LocalDateTime startDateTime = LocalDateTime.of(date, start);
            LocalDateTime endDateTime = LocalDateTime.of(date, end);
            
            boolean success = calendar.createEvent(title, startDateTime, endDateTime);
            
            if (success) {
                System.out.println("Created: " + title + " on " + date + " from " + startTime + " to " + endTime);
            } else {
                System.out.println("Failed to create: " + title + " (overlap detected)");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Error creating event: " + title + " - " + e.getMessage());
        }
    }

}