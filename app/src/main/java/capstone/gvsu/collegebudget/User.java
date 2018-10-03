package capstone.gvsu.collegebudget;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

public class User implements Parcelable{
    private String id;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    public User(String id, FirebaseAuth mAuth, GoogleSignInClient mGoogleSignInClient){
        this.id = id;
        this.mAuth = mAuth;
        this.mGoogleSignInClient = mGoogleSignInClient;
    }

    public String getId(){
        return this.id;
    }

    protected User(Parcel in) {
        id = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
    }
}
