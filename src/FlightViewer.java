import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FlightViewer {
    private static final String URL = "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";
    private static final String USER = "root";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter your MySQL root password: ");
        String password = scanner.nextLine();

        try (Scanner inputScanner = scanner;
             Connection conn = DriverManager.getConnection(URL, USER, password)) {
            System.out.println("Successfully connected to FlightDB!\n");

            // Prompt the user for a target Terminal number
            while (true) {
                System.out.print("Enter a target Terminal number (2, 3, 4, 5): ");
                String terminal = inputScanner.nextLine();

                if ("2".equals(terminal) || "3".equals(terminal) || "4".equals(terminal) || "5".equals(terminal)) {
                    // Execute the query
                    displayFlightsForTerminal(conn, terminal);
                    break;
                } else {
                    System.out.println("Invalid Terminal number. Please enter one of 2, 3, 4, or 5.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    public static void displayFlightsForTerminal(Connection conn, String terminal) throws SQLException {
        // Construct the SQL query
        String sql = "SELECT a.flight_number, fr.from_city, a.scheduled, a.delay_status FROM Arrivals a JOIN FlightRoutes fr ON a.flight_number = fr.flight_number WHERE a.terminal = ? ORDER BY a.scheduled ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Bind the terminal parameter
            pstmt.setString(1, terminal);

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                // Print the headers
                System.out.printf("%-15s %-20s %-20s %-20s%n", "Flight Number", "Origin", "Scheduled Time", "Delay Status");

                // Print each row of data
                boolean hasFlights = false;
                while (rs.next()) {
                    String flightNumber = rs.getString("flight_number");
                    String origin = rs.getString("from_city");
                    String scheduledTime = rs.getTimestamp("scheduled").toString();
                    String delayStatus = rs.getString("delay_status");

                    System.out.printf("%-15s %-20s %-20s %-20s%n", flightNumber, origin, scheduledTime, delayStatus);
                    hasFlights = true;
                }

                if (!hasFlights) {
                    System.out.println("No flights found for Terminal " + terminal);
                }
            }
        }
    }
}