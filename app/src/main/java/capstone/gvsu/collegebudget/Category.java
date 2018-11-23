package capstone.gvsu.collegebudget;


import android.widget.Button;
import java.util.ArrayList;

public class Category {
    private double spent;
    private double budgeted;
    private String name;
    private boolean locked;
    private ArrayList<String> transactions;

    public Category(){
        this.spent = 0;
        this.budgeted = 0;
        this.name = "";
        this.locked = false;
        this.transactions = new ArrayList<>();
    }

    public Category(String name, double spent, double budgeted) {
        this.spent = spent;
        this.budgeted = budgeted;
        this.name = name;
        this.transactions = new ArrayList<>();
        this.locked = false;
    }

    public void addTransaction(String transaction){
        transactions.add(transaction);
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSpent(double spent) {
        this.spent = spent;
    }
    public void setBudgeted(double budgeted) {
        this.budgeted = budgeted;
    }
    public void setLocked(boolean locked){this.locked = locked;}

    public double getSpent() {
        return this.spent;
    }
    public double getBudgeted() {
        return budgeted;
    }
    public String getName() {
        return name;
    }
    public boolean getLocked(){
        return locked;
    }
    public void addToSpent(double amount){
        spent += amount;
    }
}