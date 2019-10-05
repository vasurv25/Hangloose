package com.hangloose.database.dbmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hangloose.utils.LIKED_TABLE_RESTAURANT

@Entity(tableName = LIKED_TABLE_RESTAURANT)
data class LikedRestaurant(
    @ColumnInfo(name = "address")
    val address: String?,

    @ColumnInfo(name = "createdAt")
    val createdAt: String?,

    @ColumnInfo(name = "discount")
    val discount: String?,

    @ColumnInfo(name = "_id")
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "images")
    val images: List<String>?,

    @ColumnInfo(name = "latitude")
    val latitude: String?,

    @ColumnInfo(name = "longitude")
    val longitude: String?,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "offer")
    val offer: String?,

    @ColumnInfo(name = "priceFortwo")
    val priceFortwo: String?,

    @ColumnInfo(name = "ratings")
    val ratings: String?,

    @ColumnInfo(name = "restaurantType")
    val restaurantType: String?,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: String?,

    @ColumnInfo(name = "distanceFromLocation")
    val distanceFromLocation: Double?,

    @ColumnInfo(name = "about")
    val about: String?,

    @ColumnInfo(name = "tags")
    val tags: List<String>?,

    @ColumnInfo(name = "openCloseTime")
    val openCloseTime: String?,

    @ColumnInfo(name = "number")
    val number: String?,

    @ColumnInfo(name = "logo")
    val logo: String?,

    @ColumnInfo(name = "ambience")
    val ambience: List<String>?,

    @ColumnInfo(name = "menu")
    val menu: List<String>?,

    @ColumnInfo(name = "isLike")
    val isLike: Boolean,

    @ColumnInfo(name = "currentDate")
    val currentDate: String
)
