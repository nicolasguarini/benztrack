package it.pdm.benztrack.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ExpenseDao {
    @Insert
    fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses")
    fun getAll(): List<Expense>

    @Delete
    fun delete(expense: Expense)
}