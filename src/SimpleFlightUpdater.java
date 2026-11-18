import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class SimpleFlightUpdater {
    private static final String URL = "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";
    private static final String USER = "root";

    /**
     * Main method to update the delay status of a flight.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter your MySQL root password: ");
        String password = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection(URL, USER, password)) {
            System.out.println("Successfully connected to FlightDB!\n");

            // Prompt the user for the new delay status
            System.out.print("Enter the new delay status for flight LX354: ");
            String newDelayStatus = scanner.nextLine();

            // Update the delay status for flight 'LX354'
            int rowsAffected = updateFlight(conn, newDelayStatus);

            // Print the number of affected rows
            System.out.println(rowsAffected + " row(s) updated.");
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
        }
    }

    /**
     * Updates the delay status of a specific flight using parameterized query to prevent SQL Injection.
     *
     * @param conn       Database connection
     * @param delayStatus New delay status to set for the flight
     * @return Number of rows affected by the update
     * @throws SQLException If an error occurs during the database operation
     */
    public static int updateFlight(Connection conn, String delayStatus) throws SQLException {
        // Construct the SQL statement using PreparedStatement with parameterized queries
        String sql = "UPDATE Arrivals SET delay_status = ? WHERE flight_number = 'LX354'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set the parameter for the delay status
            pstmt.setString(1, delayStatus);

            return pstmt.executeUpdate();
        }
    }
}