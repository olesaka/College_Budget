package capstone.gvsu.collegebudget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Transactions extends AppCompatActivity {

    private String categoryName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        categoryName = getIntent().getParcelableExtra("categoryName");
        userId = getIntent().getParcelableExtra("id");
        TextView textView = findViewById(R.id.categoryName);
        textView.setText(categoryName);
    }
}
