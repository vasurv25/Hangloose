package com.hangloose.ui.model

import android.os.Parcel
import android.os.Parcelable
import com.hangloose.model.BaseModel

data class RestaurantData(
    val address: String?,
    val createdAt: String?,
    val discount: String?,
    val id: String?,
    val images: List<String>?,
    val latitude: String?,
    val longitude: String?,
    val name: String?,
    val offer: String?,
    val priceFortwo: String?,
    val ratings: String?,
    val restaurantType: String?,
    val updatedAt: String?,
    val distanceFromLocation: Double?,
    val about: String?,
    val tags: List<String>?,
    val openCloseTime: String?
) : Parcelable, BaseModel() {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(createdAt)
        parcel.writeString(discount)
        parcel.writeString(id)
        parcel.writeStringList(images)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(name)
        parcel.writeString(offer)
        parcel.writeString(priceFortwo)
        parcel.writeString(ratings)
        parcel.writeString(restaurantType)
        parcel.writeString(updatedAt)
        parcel.writeDouble(distanceFromLocation!!)
        parcel.writeString(about)
        parcel.writeStringList(tags)
        parcel.writeString(openCloseTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RestaurantData> {
        override fun createFromParcel(parcel: Parcel): RestaurantData {
            return RestaurantData(parcel)
        }

        override fun newArray(size: Int): Array<RestaurantData?> {
            return arrayOfNulls(size)
        }
    }
}