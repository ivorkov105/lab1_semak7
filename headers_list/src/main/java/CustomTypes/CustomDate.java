package CustomTypes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class CustomDate implements Comparable<CustomDate> {

    private final LocalDate date;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private CustomDate(LocalDate date) {
        this.date = date;
    }

    public static CustomDate of(int day, int month, int year) {
        return new CustomDate(LocalDate.of(year, month, day));
    }

    public static CustomDate parse(String dateString) {
        return new CustomDate(LocalDate.parse(dateString, FORMATTER));
    }

    public int getDay() {
        return date.getDayOfMonth();
    }

    public int getMonth() {
        return date.getMonthValue();
    }

    public int getYear() {
        return date.getYear();
    }

    public CustomDate addDays(int days) {
        return new CustomDate(this.date.plusDays(days));
    }

    public CustomDate subtractDays(int days) {
        return new CustomDate(this.date.minusDays(days));
    }

    @Override
    public int compareTo(CustomDate other) {
        return this.date.compareTo(other.date);
    }

    @Override
    public String toString() {
        return date.format(FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomDate that = (CustomDate) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
