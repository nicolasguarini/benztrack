package it.pdm.benztrack.data

import androidx.room.*

@Dao
interface CarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCar(car: Car): Long

    @Query("SELECT * FROM cars")
    fun getAll(): List<Car>

    @Delete
    fun delete(car: Car)
}