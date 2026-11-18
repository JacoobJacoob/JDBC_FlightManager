import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AdvancedFlightDispatcher {
    private static final String URL = "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";
    private static final String USER = "root";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter your MySQL root password: ");
        String password = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection(URL, USER, password)) {
            System.out.println("Successfully connected to FlightDB!\n");

            // Define the IDs and the new delay status
            List<Integer> arrivalIds = Arrays.asList(1, 3, 5);
            String newStatus = "Landed - Final";

            // Update the delay status for the given IDs
            updateMultipleDelays(conn, arrivalIds, newStatus);
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
        }
    }

    public static void updateMultipleDelays(Connection conn, List<Integer> arrivalIds, String newStatus) throws SQLException {
        // Construct the SQL statement
        String sql = "UPDATE Arrivals SET delay_status = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set the constant parameter outside the loop
            pstmt.setString(1, newStatus);

            int totalRowsAffected = 0;

            // Iterate over the list of IDs and set the 'id' parameter inside the loop
            for (int id : arrivalIds) {
                pstmt.setInt(2, id);
                pstmt.addBatch();
            }

            // Execute the batch
            int[] results = pstmt.executeBatch();

            // Compute total affected rows
            for (int result : results) {
                totalRowsAffected += result;
            }

            // Print a clear summary message
            System.out.println(totalRowsAffected + " row(s) updated.");
        }
    }
}