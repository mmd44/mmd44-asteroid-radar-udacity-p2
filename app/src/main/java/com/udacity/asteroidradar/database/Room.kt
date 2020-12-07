package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.utils.Converters
import java.util.*

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroid_table WHERE close_approach_date >= :date ORDER BY  close_approach_date ASC")
    fun getAsteroidsStartingToday (date: Date): LiveData<List<DatabaseAsteroid>>

    @Query("select * from asteroid_table ORDER BY  close_approach_date ASC")
    suspend fun getSavedAsteroids(): List<DatabaseAsteroid>

    @Query("SELECT * FROM asteroid_table WHERE close_approach_date >= :date ORDER BY  close_approach_date ASC")
    suspend fun getAsteroidsAfterDate(date: Date): List<DatabaseAsteroid>

    @Query("SELECT * FROM asteroid_table WHERE close_approach_date >= :startDate AND close_approach_date < :endDate ORDER BY  close_approach_date ASC")
    suspend fun getAsteroidsBetweenDates (startDate: Date, endDate: Date): List<DatabaseAsteroid>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("DELETE FROM asteroid_table WHERE close_approach_date < :date")
    fun deleteBeforeDate(date: Date)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
@TypeConverters(Converters::class)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids").build()
        }
    }
    return INSTANCE
}