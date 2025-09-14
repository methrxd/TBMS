package moviebookingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MovieBookingSystem {
    // Constants for ticket prices
    private static final int PREMIUM_2D_TICKET_PRICE = 350;
    private static final int REGULAR_2D_TICKET_PRICE = 250;

    private JFrame frame;
    private JPanel panel;
    private JPanel moviePanel;
    private JTextArea outputArea;
    private JLabel selectedSeatsLabel;
    private JLabel totalCostLabel;
    private Set<String> selectedSeats;
    private int totalCost;
    private Map<String, Movie> movies;
    private List<Booking> bookings;
    private enum Page {
        MAIN, MOVIE_LIST, BOOKING, ADMIN_LOGIN, ADMIN_PANEL, VIEW_BOOKINGS
    }
    private Page currentPage;

    // Admin credentials
    private static final String ADMIN_USERNAME = "venkat";
    private static final String ADMIN_PASSWORD = "1234";

    public MovieBookingSystem() {
        initialize();
        loadBookings(); // Load bookings when the program starts
        movies = new HashMap<>();

        // Adding sample movies
        movies.put("Deadpool", new Movie("Deadpool", 100, generateScreeningTime(), "English", "2D", "English", "deadpool_poster.jpg"));
        movies.put("Fall Guy", new Movie("Fall Guy", 120, generateScreeningTime(), "English", "2D", "Hindi", "fall_guy_poster.jpg"));
        movies.put("Marvel: Avengers End Game", new Movie("Marvel: Avengers End Game", 150, generateScreeningTime(), "English", "2D", "English", "avengers_poster.jpg"));
        movies.put("The Matrix", new Movie("The Matrix", 130, generateScreeningTime(), "English", "2D", "English", "matrix_poster.jpg"));
        movies.put("Inception", new Movie("Inception", 140, generateScreeningTime(), "English", "2D", "English", "inception_poster.jpg"));
        movies.put("The Shawshank Redemption", new Movie("The Shawshank Redemption", 142, generateScreeningTime(), "English", "2D", "English", "shawshank_poster.jpg"));
        movies.put("The Godfather", new Movie("The Godfather", 175, generateScreeningTime(), "English", "2D", "English", "godfather_poster.jpg"));
        movies.put("Pulp Fiction", new Movie("Pulp Fiction", 154, generateScreeningTime(), "English", "2D", "English", "pulp_fiction_poster.jpg"));
        movies.put("The Dark Knight", new Movie("The Dark Knight", 152, generateScreeningTime(), "English", "2D", "English", "dark_knight_poster.jpg"));
        selectedSeats = new HashSet<>();
    }

    private void initialize() {
        frame = new JFrame("TBMS");
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel screenLabel = new JLabel("SCREEN THIS WAY");
        topPanel.add(screenLabel);
        panel.add(topPanel, BorderLayout.NORTH);
        outputArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        showMainPage();
        frame.getContentPane().add(panel);
        frame.setVisible(true);

        // Save bookings when the program exits
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveBookings));
    }

    // Method to save bookings to a file
    private void saveBookings() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("bookings.dat"))) {
            oos.writeObject(bookings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load bookings from a file
    @SuppressWarnings("unchecked")
    private void loadBookings() {
        File file = new File("bookings.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                bookings = (List<Booking>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            bookings = new ArrayList<>();
        }
    }

    private void showMainPage() {
        currentPage = Page.MAIN;
        panel.removeAll();
        JButton bookButton = new JButton("Book Tickets");
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMovieList();
            }
        });
        JButton adminLoginButton = new JButton("Admin Login");
        adminLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAdminLogin();
            }
        });
        JButton cancelTicketsButton = new JButton("Cancel Tickets");
        cancelTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCancelTickets();
            }
        });
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProgramState();
                closeProgram();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1));
        buttonPanel.add(bookButton);
        buttonPanel.add(adminLoginButton);
        buttonPanel.add(cancelTicketsButton);
        buttonPanel.add(exitButton);
        panel.add(buttonPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void closeProgram() {
        frame.dispose();
    }

    private void saveProgramState() {
        saveBookings();
    }

    private void showMovieList() {
        currentPage = Page.MOVIE_LIST;
        panel.removeAll();
        moviePanel = new JPanel();
        moviePanel.setLayout(new GridLayout(3, 3, 10, 10)); // 3x3 grid with 10px spacing
        for (Movie movie : movies.values()) {
            JPanel movieButtonPanel = new JPanel(new BorderLayout());
            JButton movieButton = new JButton(movie.getName());
            movieButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showBooking(movie);
                }
            });
            // Load the image
            ImageIcon icon = new ImageIcon(movie.getPoster());
            // Get the scaled instance of the image to fit the desired dimensions
            Image scaledImage = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
            // Create a new ImageIcon with the scaled image
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            JLabel posterLabel = new JLabel(scaledIcon);
            movieButtonPanel.add(posterLabel, BorderLayout.CENTER);
            movieButtonPanel.add(movieButton, BorderLayout.SOUTH);
            moviePanel.add(movieButtonPanel);
        }
        panel.add(new JScrollPane(moviePanel), BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void showBooking(Movie movie) {
        currentPage = Page.BOOKING;
        panel.removeAll();
        panel.setLayout(new BorderLayout());
        JPanel bookingPanel = new JPanel();
        bookingPanel.setLayout(new BorderLayout());
        JPanel movieDetailsPanel = new JPanel();
        movieDetailsPanel.setLayout(new GridLayout(4, 1));
        JLabel titleLabel = new JLabel("Booking Details for " + movie.getName());
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        movieDetailsPanel.add(titleLabel);
        JLabel languageLabel = new JLabel("Language: " + movie.getLanguage());
        movieDetailsPanel.add(languageLabel);
        JLabel formatLabel = new JLabel("Format: " + movie.getFormat());
        movieDetailsPanel.add(formatLabel);
        JLabel timeLabel = new JLabel("Screening Time: " + movie.getScreeningTime());
        movieDetailsPanel.add(timeLabel);
        JPanel screenPanel = new JPanel();
        screenPanel.setLayout(new BorderLayout());
        JPanel seatPanel = new JPanel();
        seatPanel.setLayout(new GridLayout(11, 10, 5, 5)); // Added one row for column labels
        // Add column labels
        seatPanel.add(new JLabel()); // Empty cell for alignment
        for (int col = 1; col <= 9; col++) {
            JLabel colLabel = new JLabel(String.valueOf(col));
            colLabel.setHorizontalAlignment(SwingConstants.CENTER);
            seatPanel.add(colLabel);
        }
        for (char row = 'A'; row <= 'I'; row++) {
            JLabel rowLabel = new JLabel(String.valueOf(row));
            rowLabel.setHorizontalAlignment(SwingConstants.CENTER);
            seatPanel.add(rowLabel); // Add row label
            for (int col = 1; col <= 9; col++) {
                String seat = "Seat " + row + col;
                JButton seatButton = new JButton();
                seatButton.setPreferredSize(new Dimension(10, 10));
                // Set the seat image as the background of the button
                ImageIcon seatIcon = new ImageIcon("seat_icon.png");
                seatButton.setIcon(seatIcon);
                seatButton.setContentAreaFilled(false);
                seatButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!selectedSeats.contains(seat)) {
                            selectedSeats.add(seat);
                            seatButton.setBackground(Color.BLUE); // Selected seat color
                            updateSelectedSeatsLabel();
                        } else {
                            selectedSeats.remove(seat);
                            seatButton.setBackground(Color.GRAY); // Default color
                            updateSelectedSeatsLabel();
                        }
                    }
                });
                seatButton.setBackground(Color.GRAY); // Regular seat
                if (row == 'A' || row == 'B') {
                    seatButton.setBackground(Color.ORANGE); // Premium seat
                }
                seatPanel.add(seatButton);
            }
        }
        screenPanel.add(seatPanel, BorderLayout.CENTER);
        bookingPanel.add(movieDetailsPanel, BorderLayout.NORTH);
        bookingPanel.add(screenPanel, BorderLayout.CENTER);
        JPanel bookingControlsPanel = new JPanel();
        bookingControlsPanel.setLayout(new FlowLayout());
        JButton viewBookingButton = new JButton("View Booking");
        viewBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showViewBookingDialog(movie, selectedSeats);
            }
        });
        bookingControlsPanel.add(viewBookingButton);
        selectedSeatsLabel = new JLabel("Selected Seats: ");
        bookingControlsPanel.add(selectedSeatsLabel);
        JButton bookButton = new JButton("Book");
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBookingDetails(movie);
            }
        });
        bookingControlsPanel.add(bookButton);
        totalCostLabel = new JLabel("Total Cost: ");
        bookingControlsPanel.add(totalCostLabel);
        JButton backButton = new JButton("Back to Movie List");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMovieList();
            }
        });
        bookingControlsPanel.add(backButton);
        JButton mainPageButton = new JButton("Back to Main Page");
        mainPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainPage();
            }
        });
        bookingControlsPanel.add(mainPageButton);
        bookingPanel.add(bookingControlsPanel, BorderLayout.SOUTH);
        panel.add(bookingPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void updateSelectedSeatsLabel() {
        StringBuilder sb = new StringBuilder("Selected Seats: ");
        for (String seat : selectedSeats) {
            sb.append(seat).append(", ");
        }
        selectedSeatsLabel.setText(sb.toString());
        totalCost = selectedSeats.size() <= 8 ? selectedSeats.size() * PREMIUM_2D_TICKET_PRICE : selectedSeats.size() * REGULAR_2D_TICKET_PRICE;
        totalCostLabel.setText("Total Cost: " + totalCost);
    }

    private void showBookingDetails(Movie movie) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(5, 2));
        JTextField nameField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        detailsPanel.add(new JLabel("Name:"));
        detailsPanel.add(nameField);
        detailsPanel.add(new JLabel("Phone Number:"));
        detailsPanel.add(phoneField);
        detailsPanel.add(new JLabel("Email ID:"));
        detailsPanel.add(emailField);
        int result = JOptionPane.showConfirmDialog(frame, detailsPanel, "Enter Your Details To Continue Booking", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all details", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!isValidPhoneNumber(phone)) {
                JOptionPane.showMessageDialog(frame, "Invalid phone number", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(frame, "Invalid email address", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!bookSeats(movie.getName(), selectedSeats)) {
                JOptionPane.showMessageDialog(frame, "Some of the selected seats are already booked. Please select different seats.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Random random = new Random();
                int bookingId = 10000000 + random.nextInt(90000000);
                Booking booking = new Booking(name, phone, email, movie.getName(), new HashSet<>(selectedSeats), bookingId);
                bookings.add(booking);
                // Writing booking information to a file
                try (FileWriter writer = new FileWriter("bookings.txt", true)) {
                    writer.write(booking.toFileString() + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                JOptionPane.showMessageDialog(frame, "Booking successful!\n\n" + "Booking ID: " + bookingId + "\n" + "Movie: " + movie.getName() + "\n" + "Screening Time: " + movie.getScreeningTime() + "\n" + "Selected Seats: " + selectedSeats.toString() + "\n" + "Total Cost: " + totalCost);
                selectedSeats.clear();
                showMainPage();
            }
        }
    }

    private boolean bookSeats(String movieName, Set<String> selectedSeats) {
        for (Booking booking : bookings) {
            if (booking.getMovieName().equals(movieName) && booking.getBookedSeats().stream().anyMatch(selectedSeats::contains)) {
                return false; // Some seats are already booked
            }
        }
        return true; // All selected seats are available
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10}");
    }

    private boolean isValidEmail(String email) {
        return email.matches("\\b[A-Za-z0-9._%+-]+@(?:gmail|outlook|hotmail|icloud)\\.com\\b");
    }

    private void showAdminLogin() {
        currentPage = Page.ADMIN_LOGIN;
        String username = JOptionPane.showInputDialog(frame, "Enter Admin Username:");
        String password = JOptionPane.showInputDialog(frame, "Enter Admin Password:");
        if (username != null && password != null && username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            showAdminPage();
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid Admin Credentials");
        }
    }

    private void showAdminPage() {
        currentPage = Page.ADMIN_PANEL;
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new GridLayout(4, 1));
        JButton addButton = new JButton("Add Movie");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField movieNameField = new JTextField(20);
                JTextField posterPathField = new JTextField(20);
                String[] languages = {"English", "Hindi", "Telugu"};
                JComboBox<String> languageComboBox = new JComboBox<>(languages);
                JPanel addMoviePanel = new JPanel(new GridLayout(0, 2));
                addMoviePanel.add(new JLabel("Movie Name:"));
                addMoviePanel.add(movieNameField);
                addMoviePanel.add(new JLabel("Language:"));
                addMoviePanel.add(languageComboBox);
                addMoviePanel.add(new JLabel("Poster Path:"));
                addMoviePanel.add(posterPathField);
                int result = JOptionPane.showConfirmDialog(frame, addMoviePanel, "Add Movie", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String movieName = movieNameField.getText();
                    String selectedLanguage = (String) languageComboBox.getSelectedItem();
                    String posterPath = posterPathField.getText();
                    if (!movieName.isEmpty() && !posterPath.isEmpty()) {
                        String screeningTime = generateScreeningTime();
                        movies.put(movieName, new Movie(movieName, 100, screeningTime, selectedLanguage, screeningTime, screeningTime, posterPath));
                        outputArea.append("Movie Added: " + movieName + "\n\n");
                        outputArea.append("Language: " + selectedLanguage + "\n\n");
                        outputArea.append("Screening Time: " + screeningTime + "\n\n");
                        outputArea.append("Poster Path: " + posterPath + "\n\n");
                        outputArea.append("Total Movies: " + movies.size() + "\n\n");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        adminPanel.add(addButton);
        JButton deleteButton = new JButton("Delete Movie");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> movieComboBox = new JComboBox<>(movies.keySet().toArray(new String[0]));
                int result = JOptionPane.showConfirmDialog(null, movieComboBox, "Select Movie to Delete", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String selectedMovie = (String) movieComboBox.getSelectedItem();
                    if (movies.containsKey(selectedMovie)) {
                        movies.remove(selectedMovie);
                        outputArea.append("Movie Deleted: " + selectedMovie + "\n\n");
                        outputArea.append("Total Movies: " + movies.size() + "\n\n");
                        JOptionPane.showMessageDialog(frame, "Movie " + selectedMovie + " deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        outputArea.append("Movie not found: " + selectedMovie + "\n\n");
                    }
                }
            }
        });
        adminPanel.add(deleteButton);
        JButton resetButton = new JButton("Reset Bookings");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBookings();
            }
        });
        adminPanel.add(resetButton);
        JButton logoutButton = new JButton("Log Out");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainPage();
            }
        });
        adminPanel.add(logoutButton);
        panel.removeAll();
        panel.add(adminPanel, BorderLayout.CENTER);
        outputArea.setText("Admin Page\n");
        outputArea.append("Total Movies: " + movies.size() + "\n");
        for (Movie movie : movies.values()) {
            outputArea.append(movie.toString() + "\n");
        }
        frame.revalidate();
        frame.repaint();
    }

    private void resetBookings() {
        bookings.clear();
        JOptionPane.showMessageDialog(frame, "All bookings have been reset.", "Reset Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showCancelTickets() {
        String bookingIdInput = JOptionPane.showInputDialog(frame, "Enter Booking ID to cancel:");
        if (bookingIdInput != null) {
            try {
                int bookingId = Integer.parseInt(bookingIdInput);
                boolean found = false;
                for (Booking booking : bookings) {
                    if (booking.getBookingId() == bookingId) {
                        found = true;
                        bookings.remove(booking);
                        JOptionPane.showMessageDialog(frame, "Booking ID " + bookingId + " cancelled successfully.");
                        break;
                    }
                }
                if (!found) {
                    JOptionPane.showMessageDialog(frame, "Invalid Booking ID", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid Booking ID", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generateScreeningTime() {
        Random rand = new Random();
        int hour = rand.nextInt(24);
        int minute = rand.nextInt(60);
        return String.format("%02d:%02d", hour, minute);
    }

    private void goBack() {
        switch (currentPage) {
            case MOVIE_LIST:
                showMainPage();
                break;
            case BOOKING:
                showMovieList();
                break;
            case ADMIN_LOGIN:
                showMainPage();
                break;
            case ADMIN_PANEL:
                showMainPage();
                break;
            case VIEW_BOOKINGS:
                showAdminPage();
                break;
            default:
                break;
        }
    }

    private void showViewBookingDialog(Movie movie, Set<String> selectedSeats) {
        JPanel viewBookingPanel = new JPanel();
        JComboBox<String> bookedSeatsComboBox = new JComboBox<>(selectedSeats.toArray(new String[0]));
        viewBookingPanel.add(bookedSeatsComboBox);
        int result = JOptionPane.showConfirmDialog(frame, viewBookingPanel, "View Booking", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String selectedSeat = (String) bookedSeatsComboBox.getSelectedItem();
            if (selectedSeat != null) {
                String bookingInfo = getBookingInfo(movie.getName(), selectedSeat);
                if (bookingInfo != null) {
                    JOptionPane.showMessageDialog(frame, bookingInfo, "Booking Details", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "No booking found for the selected seat.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private String getBookingInfo(String movieName, String selectedSeat) {
        for (Booking booking : bookings) {
            if (booking.getMovieName().equals(movieName) && booking.getBookedSeats().contains(selectedSeat)) {
                return "Booking ID: " + booking.getBookingId() + "\n" + "Name: " + booking.getName() + "\n" + "Phone: " + booking.getPhone() + "\n" + "Email: " + booking.getEmail() + "\n" + "Movie: " + booking.getMovieName() + "\n" + "Seat: " + selectedSeat;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        new MovieBookingSystem();
    }
}