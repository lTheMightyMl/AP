package sample.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Account {
    private static final String ACCOUNTS_PATH = "./accounts/";
    private static ArrayList<Account> accounts = new ArrayList<>();
    private String name;
    private int highscore = 0;

    public int getHighscore() {
        return highscore;
    }

    public String getName() {
        return name;
    }

    private Account(String name) {
        this.name = name;
        accounts.add(this);
    }

    private Account(Account account) {
        name = account.name;
        highscore = account.highscore;
        accounts.add(this);
    }

    public static Account getAccount(String name) {
        for (Account account : accounts)
            if (account.equals(name))
                return account;
        return new Account(name);
    }

    public static boolean exists(String name) {
        for (Account account : accounts)
            if (account.equals(name))
                return true;
        return false;
    }

    public static void load() {
        try {
            for (File file : new File(ACCOUNTS_PATH).listFiles()) {
                Gson Gson = new Gson();
                new Account(Gson.fromJson(new BufferedReader(new FileReader(file)), Account.class));
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name + ":" + highscore;
    }

    public void save() {
        try {
            FileWriter out = new FileWriter(ACCOUNTS_PATH + name + ".json", false);
            Gson yaGson = new GsonBuilder().setPrettyPrinting().create();
            out.write(yaGson.toJson(this, Account.class));
            out.flush();
            out.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String)
            return name.equalsIgnoreCase(((String) obj).trim());
        if (obj instanceof Account)
            return this == obj;
        return false;
    }

    public void changeUsername(String name) {
        for (File file : new File(ACCOUNTS_PATH).listFiles())
            if (this.equals(file.getName().split("\\.")[0])) {
                file.delete();
                this.name = name;
                save();
            }
    }

    public static ArrayList<Account> getAccounts() {
        ArrayList<Account> accounts = new ArrayList<>(Account.accounts);
        Collections.sort(accounts, Comparator.comparingInt(Account::getHighscore).reversed().thenComparing(Account::getName));
        return accounts;
    }

    public void setHighscore(int score) {
        highscore = Integer.max(highscore, score);
    }
}
