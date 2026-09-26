package com.whalert.app.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.whalert.app.data.AppDatabase
import com.whalert.app.data.ConsoleDao
import com.whalert.app.data.PreferencesDao
import com.whalert.app.data.ReportDao
import com.whalert.app.data.UserDao
import com.whalert.app.repository.ConsoleRepository
import com.whalert.app.repository.ReportRepository
import com.whalert.app.repository.UserRepository
import com.whalert.app.service.ReportSyncService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing application dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Database
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideReportDao(database: AppDatabase): ReportDao {
        return database.reportDao()
    }

    @Provides
    @Singleton
    fun providePreferencesDao(database: AppDatabase): PreferencesDao {
        return database.preferencesDao()
    }

    @Provides
    @Singleton
    fun provideConsoleDao(database: AppDatabase): ConsoleDao {
        return database.consoleDao()
    }

    // Repositories
    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    fun provideReportRepository(reportDao: ReportDao, userRepository: UserRepository): ReportRepository {
        return ReportRepository(reportDao, userRepository)
    }

    @Provides
    @Singleton
    fun provideConsoleRepository(
        consoleDao: ConsoleDao,
        reportDao: ReportDao,
        userDao: UserDao
    ): ConsoleRepository {
        return ConsoleRepository(consoleDao, reportDao, userDao)
    }

    // Firebase
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    // Network
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://whalert-backend.firebaseapp.com/") // Firebase Hosting URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Services
    @Provides
    @Singleton
    fun provideReportSyncService(reportRepository: ReportRepository): ReportSyncService {
        return ReportSyncService(reportRepository)
    }
}
