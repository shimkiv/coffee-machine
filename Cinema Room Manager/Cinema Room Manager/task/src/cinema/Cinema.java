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
          cinema.getFirstCinemaHall().bookCinemaHallSeat(scanner);

          break;
        case PRINT_CINEMA_HALL_STATS:
          cinema.getFirstCinemaHall().printCinemaHallStats();

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
  private int income;
  private final int rows;
  private final int seatsInRow;
  private int purchasedTickets;
  private final String[][] schema;

  CinemaHall(int rows, int seatsInRow) {
    this.income = 0;
    this.rows = rows;
    this.seatsInRow = seatsInRow;
    this.purchasedTickets = 0;
    this.schema = generateSchema();
  }

  public void printCinemaHallSchema() {
    System.out.println();
    System.out.println("Cinema:");
    print2dArray(getSchema());
  }

  public void bookCinemaHallSeat(Scanner scanner) {
    while (true) {
      try {
        System.out.println();
        System.out.println("Enter a row number:");
        int rowNumber = scanner.nextInt();
        System.out.println("Enter a seat number in that row:");
        int seatNumber = scanner.nextInt();

        if (rowNumber == 0 || seatNumber == 0) {
          throw new NullPointerException();
        }

        Availability seatAvailability =
            Availability.getInstance(getSchema()[rowNumber][seatNumber]);

        if (Availability.BOOKED.equals(seatAvailability)) {
          System.out.println();
          System.out.println("That ticket has already been purchased!");
        } else {
          int ticketPrice = calculateTicketPrice(rowNumber);

          System.out.println();
          System.out.printf("Ticket price: %s%d%n", Cinema.CURRENCY_SYMBOL, ticketPrice);

          incrementIncome(ticketPrice);
          incrementPurchasedTickets();
          getSchema()[rowNumber][seatNumber] = Availability.BOOKED.getCode();

          break;
        }
      } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
        System.out.println();
        System.out.println("Wrong input!");
      }
    }
  }

  public void printCinemaHallStats() {
    System.out.println();
    System.out.printf("Number of purchased tickets: %d%n", getPurchasedTickets());
    System.out.printf("Percentage: %.2f%s%n", calculateBookedSeatsToTotalSeatsPercentage(), "%");
    System.out.printf("Current income: %s%d%n", Cinema.CURRENCY_SYMBOL, getIncome());
    System.out.printf("Total income: %s%d%n", Cinema.CURRENCY_SYMBOL, calculateTotalIncome());
  }

  private int getRows() {
    return rows;
  }

  private int getSeatsInRow() {
    return seatsInRow;
  }

  private int getIncome() {
    return income;
  }

  private void incrementIncome(int ticketPrice) {
    this.income += ticketPrice;
  }

  private int getPurchasedTickets() {
    return purchasedTickets;
  }

  private void incrementPurchasedTickets() {
    this.purchasedTickets += 1;
  }

  private String[][] getSchema() {
    return schema;
  }

  private int getTotalSeats() {
    return getRows() * getSeatsInRow();
  }

  private String[][] generateSchema() {
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

  private int calculateTicketPrice(int rowNumber) {
    int totalSeats = getTotalSeats();

    if (totalSeats <= Cinema.SMALL_MEDIUM_ROOM_SIZE) {
      return Cinema.SMALL_MEDIUM_ROOM_SIZE_TICKET_PRICE;
    } else if (rowNumber <= getRows() / 2) {
      return Cinema.FRONT_HALF_TICKET_PRICE;
    } else {
      return Cinema.BACK_HALF_TICKET_PRICE;
    }
  }

  private int calculateTotalIncome() {
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

  private double calculateBookedSeatsToTotalSeatsPercentage() {
    return 100.0 * (getPurchasedTickets() / (double) getTotalSeats());
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
