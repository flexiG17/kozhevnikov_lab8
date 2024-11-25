import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

abstract class AbstractPhysicalCalculation<T extends Number> implements Comparable<AbstractPhysicalCalculation<T>>, Cloneable {
    private final T result;

    public AbstractPhysicalCalculation(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }

    public abstract T calculate();

    @Override
    public String toString() {
        return "Результат: " + calculate();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        AbstractPhysicalCalculation<Double> currentType = (AbstractPhysicalCalculation<Double>) object;
        return Double.compare(result.doubleValue(), currentType.result.doubleValue()) == 0;
    }

    @Override
    public int compareTo(AbstractPhysicalCalculation<T> other) {
        return Double.compare(this.result.doubleValue(), other.result.doubleValue());
    }

    @Override
    public AbstractPhysicalCalculation<T> clone() {
        try {
            return (AbstractPhysicalCalculation<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Клонирование невозможно");
        }
    }
}

class InvalidCalculationException extends Exception {
    public InvalidCalculationException(String message) {
        super(message);
    }
}

class CentripetalAcceleration extends AbstractPhysicalCalculation<Double> {
    private final double speed;
    private final double radius;

    public CentripetalAcceleration(double speed, double radius) throws InvalidCalculationException {
        super(Math.pow(speed, 2) / radius);
        if (radius <= 0) {
            throw new InvalidCalculationException("Радиус должен быть положительным числом!");
        }
        this.speed = speed;
        this.radius = radius;
    }

    @Override
    public Double calculate() {
        return Math.pow(speed, 2) / radius;
    }

    @Override
    public String toString() {
        return String.format("Центростремительное ускорение a = %.3f м/с^2", calculate());
    }
}

class CircularVelocity extends AbstractPhysicalCalculation<Double> {
    private final double radius;
    private final double period;

    public CircularVelocity(double radius, double period) throws InvalidCalculationException {
        super((2 * Math.PI * radius) / period);
        if (period <= 0) {
            throw new InvalidCalculationException("Период должен быть положительным числом!");
        }
        this.radius = radius;
        this.period = period;
    }

    @Override
    public Double calculate() {
        return (2 * Math.PI * radius) / period;
    }

    @Override
    public String toString() {
        return String.format("Круговая скорость V = %.3f м/с", calculate());
    }
}

class CalculationUtil {
    public static <T extends Number> double calculateAverage(List<AbstractPhysicalCalculation<T>> calculations) {
        if (calculations == null || calculations.isEmpty()) {
            throw new IllegalArgumentException("Список расчетов не может быть пустым!");
        }
        double sum = 0;
        for (AbstractPhysicalCalculation<T> calculation : calculations) {
            sum += calculation.calculate().doubleValue();
        }
        return sum / calculations.size();
    }
}

public class MyProgram {
    private static void printProgramDescription() {
        System.out.println("Данная программа поможет вам посчитать значение центростремительного ускорения или круговой скорости с точностью до тысячных");
    }

    private static void printProgrammerDescription() {
        System.out.println("Кожевников Арсений\nСтудент магистратуры ИРИТ-РТФ\nАкадем группа: РИМ-140970");
    }

    private static int getNumberOfCalculations(Scanner scanner) {
        System.out.print("Сколько расчетов вы хотите выполнить? ");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Введите целое число!");
            scanner.nextLine();
            return getNumberOfCalculations(scanner);
        }
    }

    private static AbstractPhysicalCalculation<Double> getCalculation(Scanner scanner, int index) {
        System.out.println("Выберите тип расчета для " + (index + 1) + " варианта\n1 - Центростремительное ускорение,\n2 - Круговая скорость");
        try {
            int type = scanner.nextInt();
            double radius;
            switch (type) {
                case 1:
                    System.out.print("Введите скорость V(м/с): ");
                    double speed = scanner.nextDouble();
                    System.out.print("Введите радиус R(м): ");
                    radius = scanner.nextDouble();
                    return new CentripetalAcceleration(speed, radius);
                case 2:
                    System.out.print("Введите радиус R(м): ");
                    radius = scanner.nextDouble();
                    System.out.print("Введите период T(с): ");
                    double period = scanner.nextDouble();
                    return new CircularVelocity(radius, period);
                default:
                    System.out.println("Некорректный выбор типа расчета!");
                    return null;
            }
        } catch (InputMismatchException e) {
            System.out.println("Введите число!");
            scanner.nextLine();
        } catch (InvalidCalculationException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static void performCalculations() {
        Scanner scanner = new Scanner(System.in);
        int count = getNumberOfCalculations(scanner);
        ArrayList<AbstractPhysicalCalculation<Double>> calculations = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            AbstractPhysicalCalculation<Double> calculation = getCalculation(scanner, i);
            if (calculation != null) {
                calculations.add(calculation);
            }
        }

        if (!calculations.isEmpty()) {
            System.out.println("\nРезультаты расчетов:");
            for (AbstractPhysicalCalculation<Double> calculation : calculations) {
                System.out.println(calculation);
            }

            Collections.sort(calculations);
            System.out.println("\nРезультаты после сортировки:");
            for (AbstractPhysicalCalculation<Double> calculation : calculations) {
                System.out.println(calculation);
            }

            try {
                AbstractPhysicalCalculation<Double> clonedCalculation = calculations.get(0).clone();
                System.out.println("\nКлонированный результат расчета: " + clonedCalculation);
            } catch (Exception e) {
                System.out.println("Ошибка клонирования: " + e.getMessage());
            }

            double average = CalculationUtil.calculateAverage(calculations);
            System.out.println("\nСреднее значение расчетов: " + average);

        } else {
            System.out.println("А нужно было вводить правильные данные!!!");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Привет юный физик или просто преподаватель, проверяющий дз!");

        String programText = """
                
                Введите 1, чтобы выполнить расчет
                Введите 2, чтобы получить информацию о программе
                Введите 3, чтобы получить информацию о разработчике
                Введите 4, чтобы выйти из программы.
                """;

        System.out.println(programText);
        int programCode;

        while (true) {
            System.out.print("Для продолжения введите цифру от 1 до 4: ");
            try {
                programCode = scanner.nextInt();
                if (programCode < 1 || programCode > 4) {
                    System.out.println("Введённое значение не может быть меньше 1 и больше 4!");
                    continue;
                }
                switch (programCode) {
                    case 1:
                        performCalculations();
                        break;
                    case 2:
                        printProgramDescription();
                        break;
                    case 3:
                        printProgrammerDescription();
                        break;
                    case 4:
                        System.out.println("До свидания!");
                        return;
                }
                System.out.println(programText);
            } catch (InputMismatchException e) {
                System.out.println("Введите целое число!");
                scanner.nextLine();
            }
        }
    }
}
