package com.example.movieapp.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.core.domain.usecase.MovieAppUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class SearchViewModel(private val movieAppUseCase: MovieAppUseCase) : ViewModel() {

    @ObsoleteCoroutinesApi
    private val querySearch = ConflatedBroadcastChannel<String>()

    @ObsoleteCoroutinesApi
    fun setSearchQuery(search: String) {
        querySearch.trySend(search).isSuccess
    }

    @ObsoleteCoroutinesApi
    val movieResult = querySearch.asFlow()
        .debounce(300)
        .distinctUntilChanged()
        .filter {
            it.trim().isNotEmpty()
        }
        .flatMapLatest {
            movieAppUseCase.getSearchMovies(it)
        }.asLiveData()

    @ObsoleteCoroutinesApi
    val tvShowResult = querySearch.asFlow()
        .debounce(300)
        .distinctUntilChanged()
        .filter {
            it.trim().isNotEmpty()
        }
        .flatMapLatest {
            movieAppUseCase.getSearchTvShows(it)
        }.asLiveData()
}