package capstone.gvsu.collegebudget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

public class Transactions extends AppCompatActivity {

    private String categoryName;
    private User user;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Bundle extras = getIntent().getExtras();
        categoryName = extras.getString("categoryName");
        user = getIntent().getParcelableExtra("user");
        database = new Database(user.getId());
        TextView textView = findViewById(R.id.categoryName);
        textView.setText(categoryName);
        deleteCategory();
    }

        public void deleteCategory(){
        database.getUserIdRef().child("Category").child(categoryName).removeValue();
    }
}
