package capstone.gvsu.collegebudget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Transactions extends AppCompatActivity {

    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        categoryName = getIntent().getParcelableExtra("categoryName");
    }
}
