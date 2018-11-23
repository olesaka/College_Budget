package capstone.gvsu.collegebudget;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private User user;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        user = (User) getIntent().getParcelableExtra("user");
        database = new Database(user.getId());
    }

    public void exportBudgetHistory(String budgetDate){
        ValueEventListener eventListener = new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String income = dataSnapshot.child("Income").toString();
                String totalBudgeted = dataSnapshot.child("TotalBudgeted").toString();
                String totalSpent = dataSnapshot.child("TotalSpent").toString();
                ArrayList<Category> categories = getCategoryData(dataSnapshot);
                /*
                 * Add whatever you need to using the data above
                 */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference categoryRef = database.getUserIdRef().child("History").child(budgetDate);
        categoryRef.addListenerForSingleValueEvent(eventListener);
    }

    public ArrayList<Category> getCategoryData(DataSnapshot dataSnapshot){
        ArrayList<Category> categories = new ArrayList<>();
        for(DataSnapshot child : dataSnapshot.child("Category").getChildren()){
            Category category = new Category(child.getKey(), Double.parseDouble(child.child("Spent").getValue().toString()), Double.parseDouble(child.child("Budgeted").getValue().toString()));
            for(DataSnapshot subChild : child.child("Transactions").getChildren()){
                for(DataSnapshot transaction : subChild.getChildren()){
                    category.addTransaction(transaction.getValue().toString());
                }
            }
            categories.add(category);
        }
        return categories;
    }













}
