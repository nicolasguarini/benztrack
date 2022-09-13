package it.nicolasguarini.benztrack.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val expenseId: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "spent") val spent: Double,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "price_per_liter") val pricePerLiter: Double?,
    @ColumnInfo(name = "total_km") val totalKm: Int?,
    @ColumnInfo(name = "carId") val carId: Long
)