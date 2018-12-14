package com.hangloose.ui.model

import android.os.Parcel
import android.os.Parcelable
import com.hangloose.model.BaseModel

class AdventuresDetails (
    val createdAt: String,
    val updatedAt: String,
    val id: String,
    var name: String,
    val image: String
) : Parcelable, BaseModel() {
    var checked: Boolean = false

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ActivitiesDetails> {
        override fun createFromParcel(parcel: Parcel): ActivitiesDetails {
            return ActivitiesDetails(parcel)
        }

        override fun newArray(size: Int): Array<ActivitiesDetails?> {
            return arrayOfNulls(size)
        }
    }
}