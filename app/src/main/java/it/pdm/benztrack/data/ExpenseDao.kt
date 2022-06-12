package it.pdm.benztrack.data

import androidx.room.*


@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses")
    fun getAll(): List<Expense>

    @Query("SELECT * FROM expenses WHERE carId == :carId")
    fun getExpensesFromCarId(carId: Long): List<Expense>

    @Delete
    fun delete(expense: Expense)
}