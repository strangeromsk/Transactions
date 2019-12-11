import com.github.javafaker.Faker;

import java.util.Map;

public class Main {
    private static final int processors = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        Map<String, Account> accounts = new Bank().getAccounts();

        for (int i = 0; i < Bank.clientsNumber; i++) {
            Account account = new Account();
            account.setAccNumber(i);
            account.setMoney((long) (Math.random() * 10000));
            accounts.put(randomIdentifier(), account);
        }
        for (int i = 0; i < processors; i++) {
            new Thread(new Threads()).start();
        }
    }

    private static String randomIdentifier() {
        Faker faker = new Faker();

        String name = faker.name().fullName(); // Miss Samanta Schmidt
        String firstName = faker.name().firstName(); // Emory
        String lastName = faker.name().lastName(); // Barton

        return name + " " + firstName + " " + lastName;
    }
}
