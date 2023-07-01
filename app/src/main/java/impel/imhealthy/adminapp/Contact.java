package impel.imhealthy.adminapp;


public class Contact implements android.os.Parcelable {

    public String id,name,phone,label;

    Contact(String id, String name, String phone, String label){
        this.id=id;
        this.name=name;
        this.phone=phone;
        this.label=label;
    }

    protected Contact(android.os.Parcel in) {
        id = in.readString();
        name = in.readString();
        phone = in.readString();
        label = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(android.os.Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public String toString()
    {
        return name+","+phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(label);
    }
}
