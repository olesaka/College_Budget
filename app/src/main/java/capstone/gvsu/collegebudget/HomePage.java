package capstone.gvsu.collegebudget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
    private ArrayList<String> categories;

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

    @Override
    public void onClick(View v) {
        if(v.getId() == 0){
            addCategory();
            return;
        }
        for(int i=1; i<categories.size(); i++){
            if(v.getId()==i){
                moveToTransactionsActivity(categories.get(i-1));
            }
        }
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
                Button categoryButton = new Button(HomePage.this);
                categoryButton.setOnClickListener(HomePage.this);
                categoryButton.setText(categoryName);
                linLayout.removeViewAt(linLayout.getChildCount()-1);
                linLayout.addView(categoryButton);
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

    public void addTransaction(String categoryName, int amount){
        database.addNewTransaction(categoryName, amount);
    }

    public void refreshHomePage() {
        ValueEventListener eventListener = new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Button categoryButton = new Button(HomePage.this);
                    categoryButton.setId(i+1);
                    categories.add(child.getKey());
                    categoryButton.setText(child.getKey());
                    linLayout.addView(categoryButton);
                    //Dynamically create a new panel/container for each category to
                    //appear on the home screen
                }
                linLayout.addView(addCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference categoryRef = database.getUserIdRef().child("Category");
        categoryRef.addListenerForSingleValueEvent(eventListener);
    }

    public void showTransactions(String categoryName){
        Intent intent = new Intent(HomePage.this, Transactions.class);
        intent.putExtra("categoryName", categoryName);
        startActivity(intent);
    }

    public void moveToTransactionsActivity(String categoryName){

        Intent intent = new Intent(HomePage.this, Transactions.class);
        intent.putExtra("id", user.getId());
        intent.putExtra("categoryName", categoryName);
        startActivity(intent);
    }
}
