package assignment.samespace.musicplayer.di

import android.content.Context
import android.os.Vibrator
import assignment.samespace.musicplayer.MediaControllerHelper
import assignment.samespace.musicplayer.feature_music.data.data_source.SongsApi
import assignment.samespace.musicplayer.feature_music.data.repository.SongRepositoryImpl
import assignment.samespace.musicplayer.feature_music.domain.repository.SongRepository
import assignment.samespace.musicplayer.feature_music.domain.use_cases.GetAllSongsUseCase
import assignment.samespace.musicplayer.feature_music.domain.use_cases.MusicUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://cms.samespace.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): SongsApi {
        return retrofit.create(SongsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSongRepository(songsApi: SongsApi): SongRepository {
        return SongRepositoryImpl(
            songsApi = songsApi
        )
    }

    @Provides
    @Singleton
    fun provideMediaController(@ApplicationContext context: Context): Vibrator{
        return context.getSystemService(Vibrator::class.java)
    }


    @Provides
    @Singleton
    fun providesSongsUseCase(songRepository: SongRepository): MusicUseCases{
        return MusicUseCases(
            getAllSongsUseCase = GetAllSongsUseCase(songRepository)
        )
    }

}