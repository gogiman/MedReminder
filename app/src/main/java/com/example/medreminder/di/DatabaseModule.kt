package com.example.medreminder.di

import android.content.Context
import androidx.room.Room
import com.example.medreminder.data.database.AppDatabase
import com.example.medreminder.data.database.dao.MedicationDao
import com.example.medreminder.data.database.dao.MedicationLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "medreminder.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideMedicationDao(db: AppDatabase): MedicationDao = db.medicationDao()

    @Provides
    fun provideMedicationLogDao(db: AppDatabase): MedicationLogDao = db.medicationLogDao()
}
