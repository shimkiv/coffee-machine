package cinema;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Scanner;

public class Cinema {
  public static final int SMALL_MEDIUM_ROOM_SIZE = 60;
  public static final int SMALL_MEDIUM_ROOM_SIZE_TICKET_PRICE = 10;
  public static final int FRONT_HALF_TICKET_PRICE = 10;
  public static final int BACK_HALF_TICKET_PRICE = 8;

  public static final String CURRENCY_SYMBOL = Currency.getInstance("USD").getSymbol();

  private final List<CinemaHall> cinemaHalls = new ArrayList<>();

  public static void main(String[] args) {
    Cinema cinema = new Cinema();
    Scanner scanner = new Scanner(System.in);

    System.out.println("Enter the number of rows:");
    int rows = scanner.nextInt();
    System.out.println("Enter the number of seats in each row:");
    int seatsInRow = scanner.nextInt();

    cinema.initializeCinemaHall(rows, seatsInRow);

    while (true) {
      Event event = cinema.getUserEvent(scanner);

      switch (event) {
        case PRINT_CINEMA_HALL_SCHEMA:
          cinema.getFirstCinemaHall().printCinemaHallSchema();

          break;
        case BUY_CINEMA_HALL_TICKET:
          cinema.getFirstCinemaHall().bookSeat(scanner);

          break;
        default:
          return;
      }
    }
  }

  public List<CinemaHall> getCinemaHalls() {
    return cinemaHalls;
  }

  public CinemaHall getFirstCinemaHall() {
    return getCinemaHalls().get(0);
  }

  public void initializeCinemaHall(int rows, int seatsInRow) {
    getCinemaHalls().add(new CinemaHall(rows, seatsInRow));
  }

  public Event getUserEvent(Scanner scanner) {
    System.out.println();
    System.out.println("1. Show the seats");
    System.out.println("2. Buy a ticket");
    System.out.println("3. Statistics");
    System.out.println("0. Exit");

    return Event.getInstance(scanner.nextInt());
  }
}

class CinemaHall {
  private final int rows;
  private final int seatsInRow;
  private final String[][] schema;

  CinemaHall(int rows, int seatsInRow) {
    this.rows = rows;
    this.seatsInRow = seatsInRow;
    this.schema = generateCinemaHallSchema();
  }

  public void printCinemaHallSchema() {
    System.out.println();
    System.out.println("Cinema:");
    print2dArray(getSchema());
  }

  public void printCinemaHallIncome() {
    System.out.println();
    System.out.println("Total income:");
    System.out.println(Cinema.CURRENCY_SYMBOL + calculateCinemaHallIncome());
  }

  public void printCinemaHallTicketPrice(int rowNumber) {
    System.out.println();
    System.out.printf(
        "Ticket price: %s%d%n", Cinema.CURRENCY_SYMBOL, calculateCinemaHallTicketPrice(rowNumber));
  }

  public void bookSeat(Scanner scanner) {
    System.out.println();
    System.out.println("Enter a row number:");
    int rowNumber = scanner.nextInt();
    System.out.println("Enter a seat number in that row:");
    int seatNumber = scanner.nextInt();

    printCinemaHallTicketPrice(rowNumber);

    getSchema()[rowNumber][seatNumber] = Availability.BOOKED.getCode();
  }

  private int getRows() {
    return rows;
  }

  private int getSeatsInRow() {
    return seatsInRow;
  }

  private String[][] getSchema() {
    return schema;
  }

  private int getTotalSeats() {
    return getRows() * getSeatsInRow();
  }

  private String[][] generateCinemaHallSchema() {
    String[][] array = new String[getRows() + 1][getSeatsInRow() + 1];

    for (int i = 0; i < array.length; i++) {
      for (int j = 0; j < array[i].length; j++) {
        if (i == 0 && j == 0) {
          array[i][j] = " ";
        } else if (i == 0) {
          array[i][j] = String.valueOf(j);
        } else if (j == 0) {
          array[i][j] = String.valueOf(i);
        } else {
          array[i][j] = Availability.FOR_SALE.getCode();
        }
      }
    }

    return array;
  }

  private int calculateCinemaHallTicketPrice(int rowNumber) {
    int totalSeats = getTotalSeats();

    if (totalSeats <= Cinema.SMALL_MEDIUM_ROOM_SIZE) {
      return Cinema.SMALL_MEDIUM_ROOM_SIZE_TICKET_PRICE;
    } else if (rowNumber <= getRows() / 2) {
      return Cinema.FRONT_HALF_TICKET_PRICE;
    } else {
      return Cinema.BACK_HALF_TICKET_PRICE;
    }
  }

  private int calculateCinemaHallIncome() {
    int totalSeats = getTotalSeats();

    if (totalSeats <= Cinema.SMALL_MEDIUM_ROOM_SIZE) {
      return totalSeats * Cinema.SMALL_MEDIUM_ROOM_SIZE_TICKET_PRICE;
    } else {
      int frontHalfRows = getRows() / 2;
      int frontHalfSeatsPrice = frontHalfRows * getSeatsInRow() * Cinema.FRONT_HALF_TICKET_PRICE;
      int backHalfSeatsPrice =
          (getRows() - frontHalfRows) * getSeatsInRow() * Cinema.BACK_HALF_TICKET_PRICE;

      return frontHalfSeatsPrice + backHalfSeatsPrice;
    }
  }

  private void print2dArray(String[][] array) {
    for (String[] strings : array) {
      for (String string : strings) {
        System.out.print(string + " ");
      }

      System.out.println();
    }
  }
}

enum Event {
  PRINT_CINEMA_HALL_SCHEMA(1),
  BUY_CINEMA_HALL_TICKET(2),
  PRINT_CINEMA_HALL_STATS(3),
  EXIT(0);

  private final int code;

  Event(int code) {
    this.code = code;
  }

  public static Event getInstance(int code) {
    for (Event event : Event.values()) {
      if (event.code == code) {
        return event;
      }
    }

    return null;
  }
}

enum Availability {
  BOOKED("B"),
  FOR_SALE("S");

  private final String code;

  Availability(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static Availability getInstance(String code) {
    for (Availability availability : Availability.values()) {
      if (availability.code.equalsIgnoreCase(code)) {
        return availability;
      }
    }

    return null;
  }
  
}
