package com.orels.auth.di
import android.content.Context
import androidx.room.Room
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.orels.auth.data.dao.UserDao
import com.orels.auth.data.local.AuthDatabase
import com.orels.auth.data.local.UserInteractorImpl
import com.orels.auth.domain.interactor.UserInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    private const val DB_NAME = "Auth_DB"

    @Provides
    fun provideAuthRepository(): AuthRepository =
        AuthRepository()

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AuthDatabase =
        Room.databaseBuilder(
            context,
            AuthDatabase::class.java,
            DB_NAME
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

    @Provides
    fun provideUserDB(db: AuthDatabase): UserDao =
        db.userDao()

    @Provides
    fun provideUserInteractor(userInteractor: UserInteractorImpl): UserInteractor = userInteractor

    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("670361895848-0jildiu2ebiip55tqnkdtuhm1oq5mujc.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }
}