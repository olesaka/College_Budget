package capstone.gvsu.collegebudget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int UPDATE_CODE = 3;

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
    private TextView month;
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
        addCategory.getBackground().setColorFilter(0xFF00E600, PorterDuff.Mode.MULTIPLY);
        addCategory.setOnClickListener(this);
        linLayout = findViewById(R.id.linLayout);
        categories = new ArrayList<>();
        incomeText = findViewById(R.id.incomeAmount);
        budgetedText = findViewById(R.id.budgetAmount);
        spentText = findViewById(R.id.spentAmount);
        leftAmount = findViewById(R.id.leftAmount);
        incomeButton = findViewById(R.id.incomeButton);
        incomeButton.setOnClickListener(this);
        month = findViewById(R.id.budgetMonth);
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        Calendar calendar = Calendar.getInstance();
        month.setText(months[calendar.get(Calendar.MONTH)]);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // if the drawer is open, simply close drawer
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        // otherwise we handle as normal
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
        // if sign out is tapped, sign the user out
        if (id == R.id.action_sign_out) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    a simple method that ends current home page activity
    and returns a result to the main activity that the
    variable signOut is true. this lets the mainActivity know
    that the user wishes to be signed out.
    */
    private void signOut() {
        Intent intent = new Intent();
        intent.putExtra("signOut", true);
        setResult(RESULT_OK, intent);

        finish();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if we are returning from the Transaction page, we have to update
        // the information on the Home Screen
        if (requestCode == UPDATE_CODE){
            if (resultCode == RESULT_OK){
                // if there is data that needs to be updated, we have to update
                // the screen
                Boolean update = data.getExtras().getBoolean("update");
                if(update){
                    // update the screen
                   updateHomePage();
                }
            }
        }
    }

    public void updateHomePage(){
        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int num = linLayout.getChildCount();
                incomeText.setText("$" + dataSnapshot.child("Income").getValue().toString());
                dataSnapshot = dataSnapshot.child("Category");
                double totalSpent = refreshCategoryInformation(dataSnapshot);
                spentText.setText(getFormattedNumber(totalSpent));
                double budgeted = getDoubleFromDollar(budgetedText.getText().toString());
                leftAmount.setText(getFormattedNumber(budgeted - totalSpent));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference categoryRef = database.getUserIdRef().child("Budget");
        categoryRef.addListenerForSingleValueEvent(eventListener);
    }

    public double refreshCategoryInformation(DataSnapshot dataSnapshot){
        double totalSpent = 0.0;
        double totalBudgeted = 0.0;
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            int num = linLayout.getChildCount();
            View rowView = getLinearView(child.getKey());
            Category category = getCategoryByName(child.getKey());
            double budgetedAmnt = setBudgetedSection(rowView, child, category);
            totalBudgeted += budgetedAmnt;
            double spentAmnt = setSpentSection(rowView, child, category);
            totalSpent += spentAmnt;
            if (budgetedAmnt >= spentAmnt) {
                rowView.setBackgroundColor(Color.argb(40, 0, 255, 0));
            } else if (budgetedAmnt < spentAmnt){
                rowView.setBackgroundColor(Color.argb(40, 255, 0, 0));
            }
            category.setLocked(Boolean.parseBoolean(child.child("Locked").getValue().toString()));
            setTransactionButton(rowView, child);
        }
        budgetedText.setText(getFormattedNumber(totalBudgeted));
        return totalSpent;
    }

    public View getLinearView(String name){
        int count = linLayout.getChildCount();
        for(int i=0; i<count; i++) {
            View view = linLayout.getChildAt(i);
            Button v = view.findViewById(R.id.categoryName);
            if(v!=null) {
                String catName = v.getText().toString();
                if (catName.equals(name)) {
                    return linLayout.getChildAt(i);
                }
            }
        }
        return null;
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
                    incomeText.setText(getFormattedNumber(amountStr));
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
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.budget_line, null);
                int num = linLayout.getChildCount();
                linLayout.removeViewAt(linLayout.getChildCount()-1);
                num = linLayout.getChildCount();
                linLayout.addView(rowView);
                Button catButton = linLayout.getChildAt(4).findViewById(R.id.categoryName);
                Category category = new Category();
                category.setName(categoryName);
                category.setBudgeted(0.0);
                category.setSpent(0.0);
                categories.add(category);
                //Button catButton = rowView.findViewById(R.id.categoryName);
                catButton.getBackground().setColorFilter(0xFF0000FF, PorterDuff.Mode.MULTIPLY);
                catButton.setOnClickListener(HomePage.this);
                catButton.setText(categoryName);
                category.setName(categoryName);
                //catButton.setId(linLayout.getChildCount()-1);
                Button addTran = rowView.findViewById(R.id.addTransaction);
                addTran.setTag(categoryName);
                addTran.setOnClickListener(HomePage.this);
                TextView budgeted = rowView.findViewById(R.id.budgeted);
                budgeted.setText(getFormattedNumber(0.0));
                category.setBudgeted(0.0);
                TextView spent = rowView.findViewById(R.id.spent);
                spent.setText(getFormattedNumber(0.0));
                category.setSpent(0.0);
                rowView.setBackgroundColor(Color.argb(40, 0, 255, 0));
                linLayout.addView(addCategory);
                Button v = linLayout.getChildAt(4).findViewById(R.id.categoryName);
                num = linLayout.getChildCount();
                View vi = getLinearView("car");
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

    /*
    This main function that handles the addition of a transaction to a category.
    It prompts the user for all necessary information, and handles scenarios where
    a user is about to go over budget in said category.
    */
    public void addTransaction(){
        // create layout to be used to populate the dialog builder
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Add a EditView here for the "Description" label
        final EditText descriptionBox = new EditText(this);
        descriptionBox.setHint("Description");
        layout.addView(descriptionBox); // add view to the layout

        // Add another EditView here for the "Amount" label
        final EditText amountBox = new EditText(this);
        amountBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers
        amountBox.setHint("$0.00");
        layout.addView(amountBox); // Another add method

        // create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Transaction");
        builder.setView(layout); // add our layout to the builder

        // this is where we handle button pushes. Our first button is OK, the second
        // is Cancel.
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    // pull information from the dialog box
                    String amountStr = amountBox.getText().toString();
                    String descStr = descriptionBox.getText().toString();
                    // handle a blank message input
                    if(descStr.equals("")) descStr="No Message";
                    double amount = Double.parseDouble(amountStr);
                    // check amount vs the total budget amount
                    if(amountIsMoreThanBudgeted(amount)){
                        // Ask user if they want to pull funds from available categories
                        warnUserOfBudgetOverflow(amount, descStr);

                    // if amount is cleared, add the transaction
                    }else{
                        database.addNewTransaction(categoryName, amount, descStr);
                        updateSpentAndLeft(amount);
                        lineView = getView();
                        updateCategorySpent();
                        Category category = getCategoryByName(categoryName);
                        category.addToSpent(amount);
                    }
                }catch(NumberFormatException e){
                    // let the user know that it was a wrong number
                }
            }
        });
        // if the user decides to cancel their input, simply exit the dialog box
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // display the builder
        builder.show();
    }

    /*
    this is a simple function used above in addTransaction() to
    make sure that the user can afford to add a transaction of specified
    amount to the category
    */
    public boolean amountIsMoreThanBudgeted(double amount){
        for(Category category : categories){
            if(categoryName.equals(category.getName())) {
                // if amount is larger than remaining budget in category return true
                if (amount > (category.getBudgeted() - category.getSpent())) {
                    return true;
                }
            }
        }
        // if we're in the clear, return false
        return false;
    }

    /*
    this function is used above in addTransaction() if a user is attempting to add
    a transaction that will result in a category going over budget. This function
    creates another dialog box that prompts the user to either pull funds from a
    different unlocked budget, or instead simply allow the budget to overflow.
    */
    public void warnUserOfBudgetOverflow(final double amount, final String descStr) {
        // layout to populate the builder
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Add a TextView here for the "Warning" label
        final TextView warningBox = new EditText(this);
        warningBox.setText("Transaction will go over budget. Would you like to pull funds from an unlocked Category?");
        warningBox.setFocusable(false);
        warningBox.setClickable(false);
        layout.addView(warningBox); // add to layout

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setView(layout); // add layout to builder for proper display

        // two choices to either pull funds from another category, or allow transaction
        // to exceed the budgeted amount
        builder.setPositiveButton("Pull Funds", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // pull excess funds from a different category(ies)
                takeMoneyFromAnotherUnlockedCategory(amount, descStr);
            }
        });
        // if we exceed the budgeted amount, we simply add transaction as normal
        // which results in a category overflow
        builder.setNegativeButton("Exceed Budgeted", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // place transaction
                database.addNewTransaction(categoryName, amount, descStr);
                updateSpentAndLeft(amount);
                lineView = getView();
                updateCategorySpent();
                View rowView = getLinearView(categoryName);
                rowView.setBackgroundColor(Color.argb(40, 255, 0, 0));
                Category category = getCategoryByName(categoryName);
                category.addToSpent(amount);
            }
        });
        builder.show();
    }

    public void takeMoneyFromAnotherUnlockedCategory(final double amount, final String descStr){
        // get proper overflow amount
        double trueAmount = 0.0;
        for(Category category : categories){
            if (categoryName.equals(category.getName())){
                trueAmount = amount - (category.getBudgeted() - category.getSpent());
            }
        }
        // make it final in order to pass to onClick later
        final double trueAmt = trueAmount;

        // set pop up with options to take money from unlocked categories here
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // populate the spinner array that the spinner will use to display all options
        final ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("All");
        for(Category category : categories) {
            // make sure that the category is unlocked
            // and can also handle the amount to be pulled
            if(category.getLocked() == false
                    && !category.getName().equals(categoryName)
                    && trueAmt < (category.getBudgeted() - category.getSpent())) {
                        spinnerArray.add(category.getName());
            }
        }

        // create a spinner to hold available Categories
        final Spinner spinner = new Spinner(this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(spinnerArrayAdapter);
        layout.addView(spinner); // add spinner to layout

        // create the builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Category to Pull From");
        builder.setView(layout); // populate with layout

        // If user clicks Ok, pull from chosen spinner item
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String option = spinner.getSelectedItem().toString();
                // if user chooses all, divide overflow amount evenly among all unlocked categories
                if (option.equals("All")) {
                    // find amount to be taken from each category
                    double amt = trueAmt / (spinnerArray.size() - 1);

                    // add transaction to all unlocked categories
                    // make sure the category can handle the transaction without going over budget itself
                    for (Category category : categories) {
                        if (category.getLocked() == false
                                && category.getName() != categoryName
                                && amt < (category.getBudgeted() - category.getSpent())) {

                                    database.addNewTransaction(category.getName(), amt, ("Overflow transaction from " + categoryName));
                                    updateSpentAndLeft(amt);
                                    lineView = getView();
                                    updateCategorySpent();
                        }
                    }
                }
                // if user chooses single Category, take only from specified category
                else {
                    for (Category category : categories) {
                        if (category.getName().equals(option)) {
                            database.setCategoryBudget(category.getName(), category.getBudgeted()-trueAmt);
                            Category toCategory = getCategoryByName(categoryName);
                            database.setCategoryBudget(categoryName, (toCategory.getBudgeted()+trueAmt));
                            database.addNewTransaction(categoryName, amount, descStr);
                            updateSpentAndLeft(amount);
                            lineView = getView();
                            updateCategorySpent();
                            updateCategoryBudget(lineView,toCategory.getBudgeted()+trueAmt);
                            Category cat = getCategoryByName(categoryName);
                            cat.addToSpent(trueAmt);
                            View budgetLine = getView(option);
                            updateCategoryBudget(budgetLine, category.getBudgeted()-trueAmt);
                        }
                    }
                }
            }
        });

        // if user cancels, simply cancel the transaction and return to Home Screen
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // display the dialog
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

    public View getView(String categoryName){
        for(int i=0; i<linLayout.getChildCount(); i++){
            Button btn = linLayout.getChildAt(i).findViewById(R.id.categoryName);
            String text = btn.getText().toString();
            if(text.equals(categoryName)){
                return linLayout.getChildAt(i);
            }
        }
        return null;
    }

    public Category getCategoryByName(String categoryName){
        for(Category category: categories){
            if(category.getName().equals(categoryName)) {
                return category;
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
                    for(DataSnapshot subChild : child.getChildren()){
                        total += Double.parseDouble(subChild.getValue().toString());
                    }
                }
                TextView textView = lineView.findViewById(R.id.spent);
                textView.setText(getFormattedNumber(total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        DatabaseReference categoryRef = database.getUserIdRef().child("Budget").child("Category").child(categoryName).child("Transactions");
        categoryRef.addListenerForSingleValueEvent(eventListener);
    }

    public void updateCategoryBudget(View budgetLine, double amount){
        TextView textView = budgetLine.findViewById(R.id.budgeted);
        textView.setText(getFormattedNumber(amount));
    }

    public void updateSpentAndLeft(double amount){
        double totalSpent = getDoubleFromDollar(spentText.getText().toString());
        spentText.setText(getFormattedNumber(totalSpent + amount));
        double left = getDoubleFromDollar(leftAmount.getText().toString());
        leftAmount.setText(getFormattedNumber(left-amount));
    }

    public void refreshHomePage() {
        ValueEventListener eventListener = new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot historyRef = dataSnapshot.child("History");
                checkToSaveBudget(historyRef);
                dataSnapshot = dataSnapshot.child("Budget");
                incomeText.setText("$" + dataSnapshot.child("Income").getValue().toString());
                dataSnapshot = dataSnapshot.child("Category");
                double totalSpent = setCategoryInformation(dataSnapshot);
                spentText.setText(getFormattedNumber(totalSpent));
                double budgeted = getDoubleFromDollar(budgetedText.getText().toString());
                leftAmount.setText(getFormattedNumber(budgeted - totalSpent));
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
            int num = linLayout.getChildCount();
            linLayout.addView(rowView);
            num = linLayout.getChildCount();
            Category category = new Category();
            setCategoryButton(rowView, child, category);
            double budgetedAmnt = setBudgetedSection(rowView, child, category);
            totalBudgeted += budgetedAmnt;
            double spentAmnt = setSpentSection(rowView, child, category);
            totalSpent += spentAmnt;
            if (budgetedAmnt >= spentAmnt) {
                rowView.setBackgroundColor(Color.argb(40, 0, 255, 0));
            } else if (budgetedAmnt < spentAmnt){
                rowView.setBackgroundColor(Color.argb(40, 255, 0, 0));
            }
            category.setLocked(Boolean.parseBoolean(child.child("Locked").getValue().toString()));
            categories.add(category);
            setTransactionButton(rowView, child);
        }
        linLayout.addView(addCategory);
        int num = linLayout.getChildCount();
        budgetedText.setText(getFormattedNumber(totalBudgeted));
        return totalSpent;
    }

    public void setCategoryButton(View rowView, DataSnapshot child, Category category){
        Button catButton = rowView.findViewById(R.id.categoryName);
        catButton.setOnClickListener(HomePage.this);
        catButton.getBackground().setColorFilter(0xFF0000FF, PorterDuff.Mode.MULTIPLY);
        catButton.setText(child.getKey());
        category.setName(child.getKey());
    }

    public double setBudgetedSection(View rowView, DataSnapshot child, Category category){
        TextView budgetText = rowView.findViewById(R.id.budgeted);
        double budget = Double.parseDouble(child.child("Budgeted").getValue().toString());
        budgetText.setText(getFormattedNumber(budget));
        category.setBudgeted(budget);
        return budget;
    }

    public double setSpentSection(View rowView, DataSnapshot child, Category category){
        TextView spentText = rowView.findViewById(R.id.spent);
        child = child.child("Transactions");
        double totalSpent = 0.0;
        for(DataSnapshot transChild : child.getChildren()){
            for(DataSnapshot subChild : transChild.getChildren()){
                totalSpent += Double.parseDouble(subChild.getValue().toString());
            }
        }
        spentText.setText(getFormattedNumber(totalSpent));
        category.setSpent(totalSpent);
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
        intent.putExtra("isLocked", getCategoryLocked(categoryName));
        startActivityForResult(intent, UPDATE_CODE);
    }

    public boolean getCategoryLocked(String categoryName){
        for(Category category : categories){
            if(category.getName().equals(categoryName)){
                return category.getLocked();
            }
        }
        return false;
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

    public double getDoubleFromDollar(String dollarAmount){
        return Double.parseDouble(dollarAmount.substring(1, dollarAmount.length()));
    }

    public void checkToSaveBudget(DataSnapshot dataSnapshot){
        Calendar time = Calendar.getInstance();
        time.add(Calendar.MONTH, -1);
        String strDate = new SimpleDateFormat("yyyyMM").format(time.getTime());
        if(!dataSnapshot.hasChildren()){
            DatabaseReference firstHistoryRef = database.getUserIdRef().child("History").child(new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime()));
            firstHistoryRef.setValue("");
        }else if(dataSnapshot.getChildrenCount() !=1 && !dataSnapshot.hasChild(strDate)){
            saveBudgetToHistory();
        }
    }

    public void saveBudgetToHistory(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Calendar time = Calendar.getInstance();
                time.add(Calendar.MONTH, -1);
                String strDate = new SimpleDateFormat("yyyyMM").format(time.getTime());
                //strdate = 201811
                DatabaseReference toRef = database.getUserIdRef().child("History").child(strDate);
                toRef.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener(){

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                setHistory(dataSnapshot);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        };
                        databaseReference = databaseReference.child("Category");
                        databaseReference.addListenerForSingleValueEvent(eventListener);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference categoryRef = database.getUserIdRef().child("Budget");
        categoryRef.addListenerForSingleValueEvent(eventListener);
    }

    public void setHistory(DataSnapshot dataSnapshot){
        Calendar time = Calendar.getInstance();
        time.add(Calendar.MONTH, -1);
        String strDate = new SimpleDateFormat("yyyyMM").format(time.getTime());
        double totalBudget = 0.0;
        double totalSpent = 0.0;
        for(DataSnapshot category : dataSnapshot.getChildren()){
            double categorySpent = 0.0;
            totalBudget += Double.parseDouble(category.child("Budgeted").getValue().toString());
            for(DataSnapshot transDate : category.child("Transactions").getChildren()){
                for(DataSnapshot trans : transDate.getChildren()){
                    double temp = Double.parseDouble(trans.getValue().toString());
                    totalSpent += temp;
                    categorySpent += temp;
                }
            }
            DatabaseReference spentRef = database.getUserIdRef().child("History").child(strDate).child("Category").child(category.getKey()).child("Spent");
            spentRef.setValue(categorySpent);
        }
        DatabaseReference ref = database.getUserIdRef().child("History").child(strDate);
        ref.child("TotalBudgeted").setValue(totalBudget);
        ref.child("TotalSpent").setValue(totalSpent);
    }

}
