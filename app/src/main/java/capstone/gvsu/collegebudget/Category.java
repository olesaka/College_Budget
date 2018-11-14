package capstone.gvsu.collegebudget;


import android.widget.Button;
import java.util.ArrayList;

public class Category {
    private String spent;
    private String budgeted;
    private double leftOver;
    private String name;
    private ArrayList<String> transactions;

    public Category(String name, String spent, String budgeted) {
        this.spent = spent;
        this.budgeted = budgeted;
        this.name = name;
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(String transaction){
        transactions.add(transaction);
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSpent(String spent) {
        this.spent = spent;
    }
    public void setBudgeted(String budgeted) {
        this.budgeted = budgeted;
    }

    public String getSpent() {
        return this.spent;
    }
    public String getBudgeted() {
        return budgeted;
    }
    public String getName() {
        return name;
    }
}