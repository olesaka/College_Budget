package capstone.gvsu.collegebudget;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Database {

    private DatabaseReference userIdRef;
    private DatabaseReference userCategory;
    private ArrayList<String> categories;

    public Database(String id) {
        this.userIdRef = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        this.categories = new ArrayList<>();
        //CheckForCategoryChild();
    }

    public Database(DatabaseReference userRef){
        this.userIdRef = userRef;
    }

    public DatabaseReference getUserIdRef() {
        return this.userIdRef;
    }

    public void CheckForCategoryChild(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    //create new user
                    userCategory = dataSnapshot.getRef();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference categoryRef = userIdRef.child("Category");
        categoryRef.addListenerForSingleValueEvent(eventListener);
    }

    public void addNewCategory(String categoryName){
        userIdRef.child("Category").child(categoryName).setValue("");
    }

    public void addCategoryBudget(String categoryName, int budgetAmount){
        userIdRef.child("Category").child(categoryName).child("BudgetAmount").setValue(budgetAmount);
    }

    public void addNewTransaction(String categoryName, Double amount){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        DatabaseReference transRef = userIdRef.child("Category").child(categoryName).child("Transactions").child(timeStamp);
        transRef.setValue(amount);
    }

    public void setIncome(Double income){
        DatabaseReference incomeRef = userIdRef.child("Income");
        incomeRef.setValue(income);
    }
}
