package capstone.gvsu.collegebudget;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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

    @SuppressLint("ResourceType")
    //@Override
    public void onClick(View v) {
        if(v.getId()== R.id.textView_export){
            chooseBudgetMonth();
            return;
        }
    }

    public void chooseBudgetMonth() {
        // layout for export dialogue
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // populate the spinner array
        final ArrayList<String> spinnerArrayRaw = getMonthsFromHistory();           // <----------- This line successfully(?) executes as far as I can tell
        final ArrayList<String> spinnerArrayBeau = new ArrayList<String>();

        for(int x = 0; x < spinnerArrayRaw.size(); x++){                            // <----------- however, spinnerArrayRaw.size() returns 0
            spinnerArrayBeau.add(beautifyMonth(spinnerArrayRaw.get(x)));            // <----------- which means this line never executes
        }

        if(spinnerArrayRaw.isEmpty()){                                              // <----------- and this line is always registering as true, which means spinnerArrayBeau contains only the string "empty"
            spinnerArrayBeau.add("empty");
        }                                                                           // at this point you're thinking, "so obviously getMonthsFromHistory() didn't work
                                                                                    // on line 53." But that's the weird part in all this.
        // create a spinner to hold available months
        final Spinner spinner = new Spinner(this);                                                                                  // if you change spinnerArrayBeau
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArrayBeau); // <----- right here
        spinner.setAdapter(spinnerArrayAdapter);                                                                                                            // to be spinnerArrayRaw, the raw month strings
        layout.addView(spinner);                                                                                                                            // from the database will populate the spinner in the app, as if spinnerArrayRaw had data in it
                                                                                                                                            // but you cannot use spinnerArrayRaw in any other way, because it behaves as if it is empty
        // create the builder                                                                                                                  including the above .size(), and .isEmpty(), you also cannot use .get() or the app will crash with an
        AlertDialog.Builder builder = new AlertDialog.Builder(this);                                                                 // OutOfBoundsException because the list registers as empty. I cannot figure out how to get access to this raw data
        builder.setTitle("Select Month to Export");                                                                                         // and I need it in order to pull the proper monthly budget from the database once the user has chosen a month.
        builder.setView(layout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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

    public ArrayList<String> getMonthsFromHistory(){
        final ArrayList<String> returnList = new ArrayList<String>();
        ValueEventListener eventListener = new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    returnList.add(child.getKey().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference categoryRef = database.getUserIdRef().child("History");
        categoryRef.addListenerForSingleValueEvent(eventListener);

        return returnList;
    }

    public String beautifyMonth(String rawStr){
        String beauStr = new String();
        String str1 = rawStr.substring(0,4);
        String str2 = rawStr.substring(4);

        if(str2.equals("01")) {
            beauStr = "January " + str1;
        }
        else if(str2.equals("02")) {
            beauStr = "February " + str1;
        }
        else if(str2.equals("03")) {
            beauStr = "March " + str1;
        }
        else if(str2.equals("04")) {
            beauStr = "April " + str1;
        }
        else if(str2.equals("05")) {
            beauStr = "May " + str1;
        }
        else if(str2.equals("06")) {
            beauStr = "June " + str1;
        }
        else if(str2.equals("07")) {
            beauStr = "July " + str1;
        }
        else if(str2.equals("08")) {
            beauStr = "August " + str1;
        }
        else if(str2.equals("09")) {
            beauStr = "September " + str1;
        }
        else if(str2.equals("10")) {
            beauStr = "October " + str1;
        }
        else if(str2.equals("11")) {
            beauStr = "November " + str1;
        }
        else if(str2.equals("12")) {
            beauStr = "December " + str1;
        }

        return beauStr;
    }

    public Cat exportBudgetHistory(String budgetDate){
        final Cat cat = new Cat();
        ValueEventListener eventListener = new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cat.income = dataSnapshot.child("Income").toString();
                cat.totalBudgeted = dataSnapshot.child("TotalBudgeted").toString();
                cat.totalSpent = dataSnapshot.child("TotalSpent").toString();
                cat.categories = getCategoryData(dataSnapshot);
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

        return cat;
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

    /*public void exportTxt(String text){
        if(Environment.getExternalStorageState().equalsIgnoreCase("mounted"))//Check if Device Storage is present
        {
            try {
                File root = new File(Environment.getExternalStorageDirectory(), "MyAppFolder");//You might want to change this to the name of your app. (This is a folder that will be created to store all of your txt files)
                if (!root.exists()) {
                    root.mkdirs();
                }
                File myTxt = new File(root, "filename.txt"); //You might want to change the filename
                FileWriter writer = new FileWriter(myTxt);
                writer.append(text);//Writing the text
                writer.flush();
                writer.close();
                Toast.makeText(this, "File exported", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "Can't access device storage!", Toast.LENGTH_SHORT).show();
    }*/




    private class Cat {
        ArrayList<Category> categories;
        String totalSpent;
        String totalBudgeted;
        String income;
    }


}