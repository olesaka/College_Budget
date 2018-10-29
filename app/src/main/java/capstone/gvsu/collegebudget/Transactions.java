package capstone.gvsu.collegebudget;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Transactions extends AppCompatActivity implements View.OnClickListener{

    private String categoryName;
    private User user;
    private Database database;
    private Button deleteButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Bundle extras = getIntent().getExtras();
        categoryName = extras.getString("categoryName");
        user = getIntent().getParcelableExtra("user");
        database = new Database(user.getId());
        showTransactions();
        TextView textView = findViewById(R.id.categoryName);
        textView.setText(categoryName);
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);
        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(this);
    }

    public void deleteCategory(){
        database.getUserIdRef().child("Category").child(categoryName).removeValue();
        Intent intent = new Intent(Transactions.this, HomePage.class);
        intent.putExtra("user", (Parcelable)user);
        startActivity(intent);
    }

    public void back(){
        Intent intent = new Intent(Transactions.this, HomePage.class);
        intent.putExtra("user", (Parcelable)user);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.deleteButton){
            deleteCategory();
        }
        if(v.getId()==R.id.back){
            back();
        }
    }

    public void showTransactions(){
        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference transRef = database.getUserIdRef().child("Category").child(categoryName).child("Transactions");
        transRef.addListenerForSingleValueEvent(eventListener);
    }
}
