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
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            encodeDefaults = true
        }
    }

    // 2. CLIENTE HTTP (AQUÍ ESTÁ EL CAMBIO)
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                val jsonInstance = get<Json>()

                json(jsonInstance, contentType = ContentType.Application.Json)

                json(jsonInstance, contentType = ContentType.Text.Plain)
            }
        }
    }
    single<GameRemoteDataSource> { KtorGameRemoteDataSource(get()) }
    single { ScoreStorage(androidContext()) }

    single<GameRepository> {
        GameRepositoryImpl(
            context = androidContext(), // Inyecta el contexto de la App
            remoteDataSource = get(),   // Inyecta el DataSource
            json = get()                // Inyecta el Json definido arriba
        )
    }
    single<ScoreRepository> { ScoreRepositoryImpl(get()) }


    viewModelOf(::StartGameViewModel)
    viewModel { StartGameViewModel(get(), get(), get()) }
}