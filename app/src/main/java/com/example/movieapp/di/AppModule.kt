package com.example.movieapp.di

import com.example.core.domain.usecase.MovieAppInteractor
import com.example.core.domain.usecase.MovieAppUseCase
import com.example.movieapp.detail.DetailViewModel
import com.example.movieapp.home.SearchViewModel
import com.example.movieapp.movies.MoviesViewModel
import com.example.movieapp.tvshows.TvShowsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val useCaseModule = module {
    factory<MovieAppUseCase> { MovieAppInteractor(get()) }
}

@ExperimentalCoroutinesApi
@FlowPreview
val viewModelModule = module {
    viewModel { MoviesViewModel(get()) }
    viewModel { TvShowsViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { SearchViewModel(get()) }
}