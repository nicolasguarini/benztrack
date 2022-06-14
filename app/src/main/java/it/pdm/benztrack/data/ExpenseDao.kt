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

    @Query("SELECT * FROM expenses WHERE carId == :carId AND expenseId == :expenseId")
    fun getExpenseFromId(carId: Long, expenseId: Long): Expense

    @Delete
    fun delete(expense: Expense)

    @Query("DELETE FROM expenses WHERE expenseId == :expenseId")
    fun deleteFromId(expenseId: Long)
}