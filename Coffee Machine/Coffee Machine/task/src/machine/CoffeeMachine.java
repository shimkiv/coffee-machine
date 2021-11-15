package machine;

import java.util.Currency;
import java.util.Scanner;

public class CoffeeMachine {
  private static final String CURRENCY_SYMBOL = Currency.getInstance("USD").getSymbol();

  private int waterMlLeft;
  private int milkMlLeft;
  private int beansGrLeft;
  private int disposableCupsLeft;
  private int cash;

  public CoffeeMachine() {
    this.waterMlLeft = 400;
    this.milkMlLeft = 540;
    this.beansGrLeft = 120;
    this.disposableCupsLeft = 9;
    this.cash = 550;
  }

  public static void main(String[] args) {
    Action action;
    Scanner scanner = new Scanner(System.in);
    CoffeeMachine coffeeMachine = new CoffeeMachine();

    while (true) {
      try {
        action = coffeeMachine.askForAction(scanner);

        if (action != null) {
          switch (action) {
            case BUY:
              coffeeMachine.purchase(scanner);

              break;
            case FILL:
              coffeeMachine.maintenance(scanner);

              break;
            case TAKE:
              coffeeMachine.cashCollection();

              break;
            case REMAINING:
              coffeeMachine.printStatus();

              break;
            case EXIT:
              return;
            default:
              break;
          }
        } else {
          coffeeMachine.printWithLineSeparator("Wrong action!");
        }
      } catch (Exception e) {
        coffeeMachine.processException(e);
      }
    }
  }

  private Action askForAction(Scanner scanner) {
    printWithLineSeparator("Write action (buy, fill, take, remaining, exit):");

    return Action.getInstance(scanner.nextLine());
  }

  private void purchase(Scanner scanner) {
    CoffeeType coffeeType;

    try {
      printWithLineSeparator(
          "What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:");
      coffeeType = CoffeeType.getInstance(scanner.nextInt());

      if (coffeeType != null) {
        if (coffeeProductionPossible(coffeeType)) {
          System.out.println("I have enough resources, making you a coffee!");

          useDisposableCup();
          useWaterMl(coffeeType.getWaterMlRequired());
          useMilkMl(coffeeType.getMilkMlRequired());
          useBeansGr(coffeeType.getBeansGrRequired());
          depositCash(coffeeType.getPrice());
        } else {
          printCoffeeProductionFirstCause(coffeeType);
        }
      }
    } catch (Exception e) {
      processException(e);
    }
  }

  private void maintenance(Scanner scanner) {
    try {
      printWithLineSeparator("Write how many ml of water you want to add:");
      increaseWaterSupply(scanner.nextInt());
      System.out.println("Write how many ml of milk you want to add:");
      increaseMilkSupply(scanner.nextInt());
      System.out.println("Write how many grams of coffee beans you want to add:");
      increaseBeansSupply(scanner.nextInt());
      System.out.println("Write how many disposable cups of coffee you want to add:");
      increaseDisposableCupsSupply(scanner.nextInt());
    } catch (Exception e) {
      processException(e);
    }
  }

  private void cashCollection() {
    System.out.println("I gave you " + CURRENCY_SYMBOL + getCash());
    withdrawAllCash();
  }

  private boolean coffeeProductionPossible(CoffeeType coffeeType) {
    return calculateCoffeeCupsLeft(coffeeType) > 0 && getDisposableCupsLeft() > 0;
  }

  private void printCoffeeProductionFirstCause(CoffeeType coffeeType) {
    if (getWaterMlLeft() < coffeeType.getWaterMlRequired()) {
      System.out.println("Sorry, not enough water!");
    } else if (getMilkMlLeft() < coffeeType.getMilkMlRequired()) {
      System.out.println("Sorry, not enough milk!");
    } else if (getBeansGrLeft() < coffeeType.getBeansGrRequired()) {
      System.out.println("Sorry, not enough beans!");
    } else if (getDisposableCupsLeft() < 1) {
      System.out.println("Sorry, not enough disposable cups!");
    }
  }

  private int calculateCoffeeCupsLeft(CoffeeType coffeeType) {
    int cupsLeft = this.waterMlLeft / coffeeType.getWaterMlRequired();
    int leftByBeans = this.beansGrLeft / coffeeType.getBeansGrRequired();

    if (coffeeType.getMilkMlRequired() > 0) {
      int leftByMilk = this.milkMlLeft / coffeeType.getMilkMlRequired();

      if (leftByMilk < cupsLeft) {
        cupsLeft = leftByMilk;
      }
    }

    if (leftByBeans < cupsLeft) {
      cupsLeft = leftByBeans;
    }

    return cupsLeft;
  }

  private int getWaterMlLeft() {
    return waterMlLeft;
  }

  private void increaseWaterSupply(int waterMlToAdd) {
    this.waterMlLeft += waterMlToAdd;
  }

  private void useWaterMl(int waterMlToUse) {
    this.waterMlLeft -= waterMlToUse;
  }

  private int getMilkMlLeft() {
    return milkMlLeft;
  }

  private void increaseMilkSupply(int milkMlToAdd) {
    this.milkMlLeft += milkMlToAdd;
  }

  private void useMilkMl(int milkMlToUse) {
    this.milkMlLeft -= milkMlToUse;
  }

  private int getBeansGrLeft() {
    return beansGrLeft;
  }

  private void increaseBeansSupply(int beansGrToAdd) {
    this.beansGrLeft += beansGrToAdd;
  }

  private void useBeansGr(int beansGrToUse) {
    this.beansGrLeft -= beansGrToUse;
  }

  private int getDisposableCupsLeft() {
    return disposableCupsLeft;
  }

  private void increaseDisposableCupsSupply(int disposableCupsToAdd) {
    this.disposableCupsLeft += disposableCupsToAdd;
  }

  private void useDisposableCup() {
    this.disposableCupsLeft -= 1;
  }

  private int getCash() {
    return cash;
  }

  private void depositCash(int cash) {
    this.cash += cash;
  }

  private void withdrawAllCash() {
    this.cash = 0;
  }

  private void printWithLineSeparator(String message) {
    System.out.println(System.lineSeparator() + message);
  }

  private void printStatus() {
    printWithLineSeparator(this.toString());
  }

  private void processException(Exception e) {
    System.out.println("Something went wrong!");
    e.printStackTrace();
  }

  @Override
  public String toString() {
    return System.lineSeparator()
        + "The coffee machine has:"
        + System.lineSeparator()
        + getWaterMlLeft()
        + " ml of water"
        + System.lineSeparator()
        + getMilkMlLeft()
        + " ml of milk"
        + System.lineSeparator()
        + getBeansGrLeft()
        + " g of coffee beans"
        + System.lineSeparator()
        + getDisposableCupsLeft()
        + " disposable cups"
        + System.lineSeparator()
        + CURRENCY_SYMBOL
        + getCash()
        + " of money"
        + System.lineSeparator();
  }
}

enum CoffeeType {
  ESPRESSO(1, 250, 0, 16, 4),
  LATTE(2, 350, 75, 20, 7),
  CAPPUCCINO(3, 200, 100, 12, 6);

  private final int type;
  private final int waterMlRequired;
  private final int milkMlRequired;
  private final int beansGrRequired;
  private final int price;

  CoffeeType(int type, int waterMlRequired, int milkMlRequired, int beansGrRequired, int price) {
    this.type = type;
    this.waterMlRequired = waterMlRequired;
    this.milkMlRequired = milkMlRequired;
    this.beansGrRequired = beansGrRequired;
    this.price = price;
  }

  public static CoffeeType getInstance(int type) {
    for (CoffeeType coffeeType : CoffeeType.values()) {
      if (coffeeType.getType() == type) {
        return coffeeType;
      }
    }

    return null;
  }

  public int getType() {
    return type;
  }

  public int getWaterMlRequired() {
    return waterMlRequired;
  }

  public int getMilkMlRequired() {
    return milkMlRequired;
  }

  public int getBeansGrRequired() {
    return beansGrRequired;
  }

  public int getPrice() {
    return price;
  }
}

enum Action {
  BUY("buy"),
  FILL("fill"),
  TAKE("take"),
  REMAINING("remaining"),
  EXIT("exit");

  private final String type;

  Action(String type) {
    this.type = type;
  }

  public static Action getInstance(String type) {
    for (Action action : Action.values()) {
      if (action.getType().equalsIgnoreCase(type)) {
        return action;
      }
    }

    return null;
  }

  public String getType() {
    return type;
  }
}
