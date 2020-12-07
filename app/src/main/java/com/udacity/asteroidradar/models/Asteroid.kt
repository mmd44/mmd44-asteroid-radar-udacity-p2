package com.udacity.asteroidradar.models

import android.os.Parcelable
import com.udacity.asteroidradar.utils.Constants.API_QUERY_DATE_FORMAT
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Asteroid(
    val id: Long, val codename: String, val closeApproachDate: Date,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable
