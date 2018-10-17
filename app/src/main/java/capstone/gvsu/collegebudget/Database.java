package capstone.gvsu.collegebudget;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Database {

    private DatabaseReference userIdRef;
    private DatabaseReference userCategory;
    private ArrayList<String> categories;

    public Database(String id){
        this.userIdRef = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        this.categories = new ArrayList<String>();
        CheckForCategoryChild();
        GetAllCategories();
    }

    public void CheckForCategoryChild(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    //create new user
                    userCategory = dataSnapshot.getRef();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference categoryRef = userIdRef.child("Category");
        categoryRef.addListenerForSingleValueEvent(eventListener);
    }


    public void GetAllCategories(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        categories.add(child.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference categoryRef = userIdRef.child("Category");
        categoryRef.addListenerForSingleValueEvent(eventListener);
    }
}
