package SQLiteEx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Scanner;


public class library_management{

    public static void main(String[] args) {
        String url = "jdbc:sqlite:D:/Program_Code/MCA/JEAD_SQLite_DB/library.db";
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connected to the database");

                // Create Tables if they don't exist
                createTables(conn);

                while (true) {
                    // Display menu options
                    System.out.println("\nLibrary Management System");
                    System.out.println("1. Add Book");
                    System.out.println("2. Update Book");
                    System.out.println("3. Delete Book");
                    System.out.println("4. Add Member");
                    System.out.println("5. Borrow Book");
                    System.out.println("6. Return Book");
                    System.out.println("7. Query Books");
                    System.out.println("8. Query Transactions");
                    System.out.println("9. Exit");

                    System.out.print("Enter your choice: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();  // Consume newline character

                    switch (choice) {
                        case 1:
                            System.out.print("Enter book title: ");
                            String title = scanner.nextLine();
                            System.out.print("Enter author: ");
                            String author = scanner.nextLine();
                            System.out.print("Enter genre: ");
                            String genre = scanner.nextLine();
                            addBook(conn, title, author, genre);
                            break;

                        case 2:
                            System.out.print("Enter book ID to update: ");
                            int bookIdToUpdate = scanner.nextInt();
                            scanner.nextLine();  // Consume newline character
                            System.out.print("Enter new title: ");
                            String newTitle = scanner.nextLine();
                            System.out.print("Enter new author: ");
                            String newAuthor = scanner.nextLine();
                            System.out.print("Enter new genre: ");
                            String newGenre = scanner.nextLine();
                            updateBook(conn, bookIdToUpdate, newTitle, newAuthor, newGenre);
                            break;

                        case 3:
                            System.out.print("Enter book ID to delete: ");
                            int bookIdToDelete = scanner.nextInt();
                            deleteBook(conn, bookIdToDelete);
                            break;

                        case 4:
                            System.out.print("Enter member name: ");
                            String memberName = scanner.nextLine();
                            System.out.print("Enter member address: ");
                            String memberAddress = scanner.nextLine();
                            addMember(conn, memberName, memberAddress);
                            break;

                        case 5:
                            System.out.print("Enter book ID to borrow: ");
                            int bookIdToBorrow = scanner.nextInt();
                            System.out.print("Enter member ID: ");
                            int memberIdToBorrow = scanner.nextInt();
                            borrowBook(conn, bookIdToBorrow, memberIdToBorrow);
                            break;

                        case 6:
                            System.out.print("Enter book ID to return: ");
                            int bookIdToReturn = scanner.nextInt();
                            System.out.print("Enter member ID: ");
                            int memberIdToReturn = scanner.nextInt();
                            returnBook(conn, bookIdToReturn, memberIdToReturn);
                            break;

                        case 7:
                            queryBooks(conn);
                            break;

                        case 8:
                            queryTransactions(conn);
                            break;

                        case 9:
                            System.out.println("Exiting...");
                            return;

                        default:
                            System.out.println("Invalid choice. Please try again.");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws Exception {
        Statement stmt = conn.createStatement();

        // Create Books Table
        stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "author TEXT NOT NULL," +
                "genre TEXT)");

        // Create Members Table
        stmt.execute("CREATE TABLE IF NOT EXISTS members (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "address TEXT)");

        // Create Transactions Table (borrow/return records)
        stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                "transaction_id INTEGER PRIMARY KEY," +
                "book_id INTEGER," +
                "member_id INTEGER," +
                "transaction_type TEXT," +
                "FOREIGN KEY(book_id) REFERENCES books(id)," +
                "FOREIGN KEY(member_id) REFERENCES members(id))");
    }

    private static void addBook(Connection conn, String title, String author, String genre) throws Exception {
        String sql = "INSERT INTO books (title, author, genre) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, genre);
            pstmt.executeUpdate();
            System.out.println("Book added successfully.");
        }
    }

    private static void updateBook(Connection conn, int bookId, String title, String author, String genre) throws Exception {
        String sql = "UPDATE books SET title = ?, author = ?, genre = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, genre);
            pstmt.setInt(4, bookId);
            pstmt.executeUpdate();
            System.out.println("Book updated successfully.");
        }
    }

    private static void deleteBook(Connection conn, int bookId) throws Exception {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
            System.out.println("Book deleted successfully.");
        }
    }

    private static void addMember(Connection conn, String name, String address) throws Exception {
        String sql = "INSERT INTO members (name, address) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.executeUpdate();
            System.out.println("Member added successfully.");
        }
    }

    private static void borrowBook(Connection conn, int bookId, int memberId) throws Exception {
        String sql = "INSERT INTO transactions (book_id, member_id, transaction_type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, memberId);
            pstmt.setString(3, "borrow");
            pstmt.executeUpdate();
            System.out.println("Book borrowed successfully.");
        }
    }

    private static void returnBook(Connection conn, int bookId, int memberId) throws Exception {
        String sql = "INSERT INTO transactions (book_id, member_id, transaction_type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, memberId);
            pstmt.setString(3, "return");
            pstmt.executeUpdate();
            System.out.println("Book returned successfully.");
        }
    }

    private static void queryBooks(Connection conn) throws Exception {
        String sql = "SELECT * FROM books";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Book ID: " + rs.getInt("id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Author: " + rs.getString("author"));
                System.out.println("Genre: " + rs.getString("genre"));
                System.out.println("-----------------------------");
            }
        }
    }

    private static void queryTransactions(Connection conn) throws Exception {
        String sql = "SELECT t.transaction_id, b.title AS book_title, m.name AS member_name, t.transaction_type " +
                "FROM transactions t " +
                "JOIN books b ON t.book_id = b.id " +
                "JOIN members m ON t.member_id = m.id";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Transaction ID: " + rs.getInt("transaction_id"));
                System.out.println("Book: " + rs.getString("book_title"));
                System.out.println("Member: " + rs.getString("member_name"));
                System.out.println("Transaction Type: " + rs.getString("transaction_type"));
                   System.out.println("-----------------------------");
            }
        }
    }
}