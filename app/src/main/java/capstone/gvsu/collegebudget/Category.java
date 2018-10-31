package capstone.gvsu.collegebudget;


import android.widget.Button;
import java.util.ArrayList;

public class Category {
    private double spent;
    private double budgeted;
    private String name;
    private Button editButton;
    private ArrayList<Transactions> transactionHistory;

    public Category(String name, double spent, double budgeted, Button editButton) {
        this.spent = spent;
        this.budgeted = budgeted;
        this.name = name;
        this.editButton = editButton;
        this.transactionHistory = new ArrayList<Transactions>();
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
    public void setTransactionHistory(ArrayList<Transactions> th){
        this.transactionHistory = th;
    }

    public double getSpent() {
        return this.spent;
    }
    public double getBudgeted() {
        return budgeted;
    }
    public String getName() {
        return name;
    }
    public ArrayList getTransactionHistory(){
        return this.transactionHistory;
    }
}