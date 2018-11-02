package capstone.gvsu.collegebudget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private User user;
    private Database database;
    private String categoryName;
    private LinearLayout linLayout;
    private Button addCategory;
    private ArrayList<Category> categories;
    private TextView incomeText;
    private TextView budgetedText;
    private TextView spentText;
    private TextView leftAmount;
    private View lineView;
    private Button incomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user = (User) getIntent().getParcelableExtra("user");
        database = new Database(user.getId());
        refreshHomePage();
        addCategory = new Button(HomePage.this);
        addCategory.setText("Category +");
        addCategory.setId(0);
        addCategory.setOnClickListener(this);
        linLayout = findViewById(R.id.linLayout);
        categories = new ArrayList<>();
        incomeText = findViewById(R.id.incomeAmount);
        budgetedText = findViewById(R.id.budgetAmount);
        spentText = findViewById(R.id.spentAmount);
        leftAmount = findViewById(R.id.leftAmount);
        incomeButton = findViewById(R.id.incomeButton);
        incomeButton.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.incomeButton){
            setIncome();
            return;
        }
        if(v.getId() == 0){
            addCategory();
            return;
        }
        Button btn = (Button)v;
        if(btn.getText()!=""){
            moveToTransactionsActivity(btn.getText().toString());
            return;
        }
        categoryName = btn.getTag().toString();
        addTransaction();
    }

    public void setIncome(){

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        // Add another TextView here for the "Amount" label
        final EditText amountBox = new EditText(this);
        amountBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers
        amountBox.setHint("$0.00");
        layout.addView(amountBox); // Another add method

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("set Income");
        builder.setView(layout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    String amountStr = amountBox.getText().toString();
                    incomeText.setText(amountStr);
                    database.setIncome(Double.parseDouble(amountStr));
                }catch(NumberFormatException e){
                    // let the user know that it was a wrong number
                }
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

    public void addCategory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Category Name");
        final EditText inputOne = new EditText(this);
        inputOne.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputOne);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoryName = inputOne.getText().toString();
                database.addNewCategory(categoryName);
                //Button categoryButton = new Button(HomePage.this);
                //categoryButton.setText(categoryName);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.budget_line, null);
                linLayout.removeViewAt(linLayout.getChildCount()-1);
                linLayout.addView(rowView);
                Button catButton = rowView.findViewById(R.id.categoryName);
                catButton.getBackground().setColorFilter(0xFF0000FF, PorterDuff.Mode.MULTIPLY);
                catButton.setOnClickListener(HomePage.this);
                catButton.setText(categoryName);
                catButton.setId(linLayout.getChildCount()-1);
                Button addTran = rowView.findViewById(R.id.addTransaction);
                addTran.setOnClickListener(HomePage.this);
                //i++;
                //linLayout.addView(categoryButton);
                linLayout.addView(addCategory);
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

    public void addTransaction(){

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Add a TextView here for the "Description" label
        final EditText descriptionBox = new EditText(this);
        descriptionBox.setHint("Description");
        layout.addView(descriptionBox);

        // Add another TextView here for the "Amount" label
        final EditText amountBox = new EditText(this);
        amountBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers
        amountBox.setHint("$0.00");
        layout.addView(amountBox); // Another add method

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Transaction");
        builder.setView(layout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    String amountStr = amountBox.getText().toString();
                    String descStr = descriptionBox.getText().toString();
                    double amount = Double.parseDouble(amountStr);
                    database.addNewTransaction(categoryName, amount);
                    updateSpentAndLeft(amount);
                    lineView = getView();
                    updateCategorySpent();
                }catch(NumberFormatException e){
                    // let the user know that it was a wrong number
                }
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

    public View getView(){
        for(int i=0; i<linLayout.getChildCount(); i++){
            Button btn = linLayout.getChildAt(i).findViewById(R.id.categoryName);
            String text = btn.getText().toString();
            if(text.equals(categoryName)){
                return linLayout.getChildAt(i);
            }
        }
        return null;
    }

    public void updateCategorySpent(){
        ValueEventListener eventListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double total = 0.0;
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    total += Double.parseDouble(child.getValue().toString());
                }
                TextView textView = lineView.findViewById(R.id.spent);
                textView.setText(Double.toString(total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        DatabaseReference categoryRef = database.getUserIdRef().child("Category").child(categoryName).child("Transactions");
        categoryRef.addListenerForSingleValueEvent(eventListener);
    }

    public void updateSpentAndLeft(double amount){
        double totalSpent = Double.parseDouble(spentText.getText().toString());
        spentText.setText(Double.toString(totalSpent + amount));
        double left = Double.parseDouble(leftAmount.getText().toString());
        leftAmount.setText(Double.toString(left-amount));
    }

    public void refreshHomePage() {
        ValueEventListener eventListener = new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                incomeText.setText(dataSnapshot.child("Income").getValue().toString());
                dataSnapshot = dataSnapshot.child("Category");
                double totalSpent = setCategoryInformation(dataSnapshot);
                spentText.setText(Double.toString(totalSpent));
                double budgeted = Double.parseDouble((String) budgetedText.getText());
                leftAmount.setText(Double.toString(budgeted - totalSpent));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference categoryRef = database.getUserIdRef();
        categoryRef.addListenerForSingleValueEvent(eventListener);
    }

    public double setCategoryInformation(DataSnapshot dataSnapshot){
        double totalSpent = 0.0;
        double totalBudgeted = 0.0;
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.budget_line, null);
            linLayout.addView(rowView, linLayout.getChildCount() - 1);
            setCategoryButton(rowView, child);
            totalBudgeted += setBudgetedSection(rowView, child);
            totalSpent += setSpentSection(rowView, child);
            setTransactionButton(rowView, child);
        }
        linLayout.addView(addCategory);
        budgetedText.setText(Double.toString(totalBudgeted));
        return totalSpent;
    }

    public void setCategoryButton(View rowView, DataSnapshot child){
        Button catButton = rowView.findViewById(R.id.categoryName);
        catButton.setOnClickListener(HomePage.this);
        catButton.getBackground().setColorFilter(0xFF0000FF, PorterDuff.Mode.MULTIPLY);
        catButton.setText(child.getKey());
    }

    public double setBudgetedSection(View rowView, DataSnapshot child){
        TextView budgetText = rowView.findViewById(R.id.budgeted);
        double budget = Double.parseDouble(child.child("Budgeted").getValue().toString());
        budgetText.setText(Double.toString(budget));
        return budget;
    }

    public double setSpentSection(View rowView, DataSnapshot child){
        TextView spentText = rowView.findViewById(R.id.spent);
        child = child.child("Transactions");
        double totalSpent = 0.0;
        for(DataSnapshot transChild : child.getChildren()){
            totalSpent += Double.parseDouble(transChild.getValue().toString());
        }
        spentText.setText(Double.toString(totalSpent));
        return totalSpent;
    }

    public void setTransactionButton(View rowView, DataSnapshot child){
        Button addTran = rowView.findViewById(R.id.addTransaction);
        addTran.setTag(child.getKey());
        addTran.setOnClickListener(HomePage.this);
    }

    public void moveToTransactionsActivity(String categoryName){
        Intent intent = new Intent(HomePage.this, Transactions.class);
        intent.putExtra("categoryName", categoryName);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
