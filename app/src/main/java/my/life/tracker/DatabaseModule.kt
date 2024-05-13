package my.life.tracker

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import my.life.tracker.agenda.AgendaPreferences
import my.life.tracker.agenda.interfaces.ActividadDao
import my.life.tracker.database.LifeTrackerDataBase
import my.life.tracker.finanzas.interfaces.CurrencyDao
import my.life.tracker.finanzas.interfaces.RecordDao
import my.life.tracker.finanzas.interfaces.TagDao
import my.life.tracker.finanzas.interfaces.TagRecordDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWorkoutDatabase(@ApplicationContext context: Context): LifeTrackerDataBase =
        Room.databaseBuilder(context, LifeTrackerDataBase::class.java, "workout-db")
            .allowMainThreadQueries()
            .build()

    @Provides
    fun provideActividad(db : LifeTrackerDataBase): ActividadDao = db.actividadDao()
    @Provides
    fun provideTag(db : LifeTrackerDataBase): TagDao = db.tagDao()
    @Provides
    fun provideTagRecord(db : LifeTrackerDataBase): TagRecordDao = db.tagRecordDao()
    @Provides
    fun provideRecord(db : LifeTrackerDataBase): RecordDao = db.recordDao()
    @Provides
    fun provideCurrency(db : LifeTrackerDataBase): CurrencyDao = db.currencyDao()


}