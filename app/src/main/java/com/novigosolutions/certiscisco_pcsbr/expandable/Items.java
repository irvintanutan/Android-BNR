package com.novigosolutions.certiscisco_pcsbr.expandable;

import android.os.Parcel;
import android.os.Parcelable;

public class Items implements Parcelable {
    public String head;
    public String summary;

    public Items(String head, String summary) {
        this.summary = summary;
        this.head = head;
    }

    public Items() {

    }

    protected Items(Parcel in) {
        head = in.readString();
        summary = in.readString();
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public static Creator<Items> getCREATOR() {
        return CREATOR;
    }


    public static final Creator<Items> CREATOR = new Creator<Items>() {
        @Override
        public Items createFromParcel(Parcel in) {
            return new Items(in);
        }

        @Override
        public Items[] newArray(int size) {
            return new Items[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(head);
        parcel.writeString(summary);
    }
}
