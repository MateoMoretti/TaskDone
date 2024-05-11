package my.life.tracker

import android.app.Application
import androidx.room.Room
import my.life.tracker.database.LifeTrackerDataBase


class App : Application() {

    lateinit var lifeTrackerDataBase: LifeTrackerDataBase

    override fun onCreate() {
        super.onCreate()
        lifeTrackerDataBase = Room
            .databaseBuilder(applicationContext, LifeTrackerDataBase::class.java, "life-tracker-room-database")
            .allowMainThreadQueries()
            .build()
    }
}