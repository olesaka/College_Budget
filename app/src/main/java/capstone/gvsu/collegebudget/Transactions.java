package capstone.gvsu.collegebudget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Transactions extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private String categoryName;
    private User user;
    private Database database;
    private Button deleteButton;
    private Button backButton;
    private LinearLayout linLayout;
    private TextView budgeted;
    private TextView spent;
    private Button budgetedButton;
    private Switch lockSwitch;

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
        linLayout = findViewById(R.id.transLayout);
        budgeted = findViewById(R.id.budgetedAmountText);
        spent = findViewById(R.id.spentAmountText);
        budgetedButton = findViewById(R.id.budgetedButton);
        budgetedButton.setOnClickListener(this);
        lockSwitch = findViewById(R.id.lockSwitch);
        lockSwitch.setOnCheckedChangeListener(this);
    }

    public void deleteCategory(){
        database.getUserIdRef().child("Budget").child("Category").child(categoryName).removeValue();
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
            return;
        }
        if(v.getId()==R.id.budgetedButton){
            setCategoryBudget();
            return;
        }
        if(v.getId()==R.id.back){
            back();
            return;
        }
    }

    public void showTransactions(){
        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                budgeted.setText("$" + dataSnapshot.child("Budgeted").getValue().toString());
                dataSnapshot = dataSnapshot.child("Transactions");
                Double totalSpent = 0.0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String date = getFormattedDate(child.getKey());
                    for(DataSnapshot subChild : child.getChildren()){
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View rowView = inflater.inflate(R.layout.transaction_line, null);
                        linLayout.addView(rowView, linLayout.getChildCount() - 1);
                        TextView descriptionText = rowView.findViewById(R.id.description);
                        descriptionText.setText(subChild.getKey());
                        TextView dateText = rowView.findViewById(R.id.date);
                        totalSpent += Double.parseDouble(subChild.getValue().toString());
                        dateText.setText(date);
                        TextView amountText = rowView.findViewById(R.id.amount);
                        amountText.setText(getFormattedNumber(subChild.getValue().toString()));
                    }
                }
                spent.setText(getFormattedNumber(totalSpent.toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference transRef = database.getUserIdRef().child("Budget").child("Category").child(categoryName);
        transRef.addListenerForSingleValueEvent(eventListener);
    }

    public String getFormattedDate(String date){
        return date.substring(4, 6) + "/" + date.substring(6, 8) + "/" + date.substring(0, 4);
    }

    public void setCategoryBudget(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Category Budget Amount");
        final EditText inputOne = new EditText(this);
        inputOne.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputOne.setHint("$0.00");
        builder.setView(inputOne);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amountStr = inputOne.getText().toString();
                database.getUserIdRef().child("Budget").child("Category").child(categoryName).child("Budgeted").setValue(amountStr);
                budgeted.setText("$" + amountStr);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public String getFormattedNumber(double amount){
        String strDouble = "$" + String.format("%.2f", amount);
        if(strDouble.substring(strDouble.length()-2).equals("00")){
            return strDouble.substring(0, strDouble.length()-3);
        }
        return strDouble;
    }

    public String getFormattedNumber(String str){
        double amount = Double.parseDouble(str);
        return getFormattedNumber(amount);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId()==R.id.lockSwitch){
            if(isChecked){
                Toast.makeText(getApplicationContext(), "On", Toast.LENGTH_LONG).show();
                lockSwitch.setText("locked");
            }else{
                Toast.makeText(getApplicationContext(), "Off", Toast.LENGTH_LONG).show();
                lockSwitch.setText("unlocked");
            }
            database.setCategoryLock(categoryName, isChecked);
        }
    }
}
