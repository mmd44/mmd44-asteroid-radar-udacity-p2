package com.udacity.asteroidradar.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.models.Asteroid
import java.util.*

@Entity(tableName = "asteroid_table")
data class DatabaseAsteroid(
    @PrimaryKey(autoGenerate = true)
    var id: Long,

    @ColumnInfo(name = "name")
    val codename: String,

    @ColumnInfo(name = "close_approach_date")
    val approachDate: Date,

    @ColumnInfo(name = "absolute_magnitude")
    val absoluteMagnitude: Double,

    @ColumnInfo(name = "estimated_diameter_max")
    var estimatedDiameterMax: Double,

    @ColumnInfo(name = "is_potentially_hazardous_asteroid")
    var isPotentiallyHazardous: Boolean,

    @ColumnInfo(name = "kilometers_per_second")
    var kilometersPerSecond: Double,

    @ColumnInfo(name = "astronomical")
    var astronomical: Double
)

fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid (
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.approachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameterMax,
            relativeVelocity = it.kilometersPerSecond,
            distanceFromEarth = it.astronomical,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

fun List<Asteroid>.asDatabaseModel(): Array<DatabaseAsteroid> {
    return this.map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            approachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameterMax = it.estimatedDiameter,
            kilometersPerSecond = it.relativeVelocity,
            astronomical = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}