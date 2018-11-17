package capstone.gvsu.collegebudget;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class User implements Parcelable {
    private String id;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference userIdRef;
    private boolean newUser;

    public User(String id, FirebaseAuth mAuth, GoogleSignInClient mGoogleSignInClient, DatabaseReference userIdRef, boolean newUser){
        this.id = id;
        this.mAuth = mAuth;
        this.mGoogleSignInClient = mGoogleSignInClient;
        this.userIdRef = userIdRef;
        this.newUser = newUser;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public DatabaseReference GetDatabaseRef(){
        return userIdRef;
    }

    public String getId(){
        return this.id;
    }

    protected User(Parcel in) {
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
    }
}
