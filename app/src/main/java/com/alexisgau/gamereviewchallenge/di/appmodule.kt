package com.alexisgau.gamereviewchallenge.di

import com.alexisgau.gamereviewchallenge.data.remote.source.GameRemoteDataSource
import com.alexisgau.gamereviewchallenge.data.remote.source.KtorGameRemoteDataSource
import com.alexisgau.gamereviewchallenge.data.repository.GameRepositoryImpl
import com.alexisgau.gamereviewchallenge.domain.repository.GameRepository
import com.alexisgau.gamereviewchallenge.ui.startGame.StartGameViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.alexisgau.gamereviewchallenge.BuildConfig
import com.alexisgau.gamereviewchallenge.data.local.ScoreStorage
import com.alexisgau.gamereviewchallenge.data.repository.ScoreRepositoryImpl
import com.alexisgau.gamereviewchallenge.domain.repository.ScoreRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {


    single {
        HttpClient(OkHttp) {

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }

            defaultRequest {
                url("http://10.0.2.2:8080/")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header("X-API-KEY", BuildConfig.API_KEY)
            }
        }
    }

    single<GameRemoteDataSource> { KtorGameRemoteDataSource(get()) }
    single { ScoreStorage(androidContext()) }

    single<GameRepository> { GameRepositoryImpl(get()) }
    single<ScoreRepository> { ScoreRepositoryImpl(get()) }


    viewModelOf(::StartGameViewModel)
    viewModel { StartGameViewModel(get(), get(), get()) }
}