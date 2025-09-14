package moviebookingsystem;

import java.io.Serializable;
import java.util.Set;
import java.io.FileWriter;
import java.io.IOException;

class Booking implements Serializable {
    private String name;
    private String phone;
    private String email;
    private String movieName;
    private Set<String> bookedSeats;
    private int bookingId;

    public Booking(String name, String phone, String email, String movieName, Set<String> bookedSeats, int bookingId) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.movieName = movieName;
        this.bookedSeats = bookedSeats;
        this.bookingId = bookingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Set<String> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(Set<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", movieName='" + movieName + '\'' +
                ", bookedSeats=" + bookedSeats +
                ", bookingId=" + bookingId +
                '}';
    }

    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(",");
        sb.append(phone).append(",");
        sb.append(email).append(",");
        sb.append(movieName).append(",");
        sb.append(bookingId).append(",");
        for (String seat : bookedSeats) {
            sb.append(seat).append(",");
        }
        return sb.toString();
    }
}