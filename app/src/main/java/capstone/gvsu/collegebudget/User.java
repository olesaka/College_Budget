package capstone.gvsu.collegebudget;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
    private String id;

    public User(String id){
        this.id = id;
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
