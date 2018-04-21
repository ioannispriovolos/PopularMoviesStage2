package com.example.android.popularmoviesstage2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

// https://developer.android.com/reference/android/os/Parcelable.html
public class Trailer implements Parcelable {
    @SerializedName("id")
    public String id;

    @SerializedName("key")
    public String key;

    @SerializedName("name")
    public String name;

    @SerializedName("size")
    public Integer size;

    @SerializedName("type")
    public String type;

    protected Trailer(Parcel in) {
        id = in.readString();
        key = in.readString();
        name = in.readString();
        size = in.readInt();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeInt((size == null) ? 0 : size);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public String toString() {
        return name;
    }
}
