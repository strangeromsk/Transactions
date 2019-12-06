import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Bank{

    private static Map<String, Account> accounts = new ConcurrentHashMap<>();

    public Map<String, Account> getAccounts() {
        return accounts;
    }
}
