package my.life.tracker.agenda

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AgendaPreferencesModule {

    @Binds
    @Singleton
    abstract fun bindAgendaPreferences(agendaPreferencesImpl: AgendaPreferencesImpl): AgendaPreferences

}