package com.example.libraryapplication.data.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "BorrowDetail")
data class BorrowDetail(
    @PrimaryKey
    val bookId:Int,
    val userId:Int,
    val borrowDate:String,
    val returnDate:String,
    val dueAmount:Int
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(bookId)
        parcel.writeInt(userId)
        parcel.writeString(borrowDate)
        parcel.writeString(returnDate)
        parcel.writeInt(dueAmount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BorrowDetail> {
        override fun createFromParcel(parcel: Parcel): BorrowDetail {
            return BorrowDetail(parcel)
        }

        override fun newArray(size: Int): Array<BorrowDetail?> {
            return arrayOfNulls(size)
        }
    }
}