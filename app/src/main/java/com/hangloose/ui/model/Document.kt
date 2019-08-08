package com.hangloose.ui.model

import android.os.Parcel
import android.os.Parcelable
import com.hangloose.model.BaseModel


data class Document(
    var createdAt: String?,
    var updatedAt: String?,
    var id: String?,
    var location: String?,
    var ownerId: String?,
    var documentType: String?
) : BaseModel(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(id)
        parcel.writeString(location)
        parcel.writeString(ownerId)
        parcel.writeString(documentType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Document> {
        override fun createFromParcel(parcel: Parcel): Document {
            return Document(parcel)
        }

        override fun newArray(size: Int): Array<Document?> {
            return arrayOfNulls(size)
        }
    }
}