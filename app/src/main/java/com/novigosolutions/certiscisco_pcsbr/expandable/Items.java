package com.novigosolutions.certiscisco_pcsbr.expandable;

import android.os.Parcel;
import android.os.Parcelable;

public class Items implements Parcelable {
    public Long id;
    public String head;
    public String summary;
    public int qty;

    public Items(String head, String summary, Long id, int qty) {
        this.summary = summary;
        this.head = head;
        this.id = id;
        this.qty = qty;
    }

    public Items() {
    }

    protected Items(Parcel in) {
        head = in.readString();
        summary = in.readString();
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
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
