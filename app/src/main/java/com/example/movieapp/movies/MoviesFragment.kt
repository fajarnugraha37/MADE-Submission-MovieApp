package com.example.movieapp.movies

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.data.Resource
import com.example.core.domain.model.Movie
import com.example.core.ui.MoviesAdapter
import com.example.core.utils.SortUtils
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentMoviesBinding
import com.example.movieapp.detail.DetailActivity
import com.example.movieapp.home.HomeActivity
import com.example.movieapp.home.SearchViewModel
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.ext.android.viewModel


@ExperimentalCoroutinesApi
@FlowPreview
class MoviesFragment : Fragment() {

    private var _fragmentMoviesBinding: FragmentMoviesBinding? = null
    private val binding get() = _fragmentMoviesBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentMoviesBinding = FragmentMoviesBinding.inflate(inflater, container, false)
        val toolbar: Toolbar = activity?.findViewById<View>(R.id.t_toolbar) as Toolbar
        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        searchView = (activity as HomeActivity).findViewById(R.id.sv_search)
        return binding.root
    }

    private val viewModel: MoviesViewModel by viewModel()
    private lateinit var moviesAdapter: MoviesAdapter
    private val searchViewModel: SearchViewModel by viewModel()
    private lateinit var searchView: MaterialSearchView
    private var sort = SortUtils.NEWEST

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        moviesAdapter = MoviesAdapter()
        setList(sort)
        observeSearchQuery()
        setSearchList()

        with(binding.rvMovies) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = moviesAdapter
        }

        moviesAdapter.onItemClick = { selectedData ->
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_MOVIE, selectedData)
            startActivity(intent)
        }
        binding.fabMoviesNewest.setOnClickListener {
            binding.famMoviesSort.close(true)
            sort = SortUtils.NEWEST
            setList(sort)
        }
        binding.fabMoviesPopularity.setOnClickListener {
            binding.famMoviesSort.close(true)
            sort = SortUtils.POPULARITY
            setList(sort)
        }
        binding.fabMoviesVote.setOnClickListener {
            binding.famMoviesSort.close(true)
            sort = SortUtils.VOTE
            setList(sort)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val item = menu.findItem(R.id.action_search)
        searchView.setMenuItem(item)
    }

    private fun setList(sort: String) {
        viewModel.getMovies(sort).observe(viewLifecycleOwner, moviesObserver)
    }

    private val moviesObserver = Observer<Resource<List<Movie>>> { movies ->
        if (movies != null) {
            when (movies) {
                is Resource.Loading -> {
                    binding.pbMoviesLoading.visibility = View.VISIBLE
                    binding.lavMoviesNotFound.visibility = View.GONE
                    binding.tvMoviesNotFound.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.pbMoviesLoading.visibility = View.GONE
                    binding.lavMoviesNotFound.visibility = View.GONE
                    binding.tvMoviesNotFound.visibility = View.GONE
                    moviesAdapter.setData(movies.data)
                }
                is Resource.Error -> {
                    binding.pbMoviesLoading.visibility = View.GONE
                    binding.lavMoviesNotFound.visibility = View.VISIBLE
                    binding.tvMoviesNotFound.visibility = View.VISIBLE
                    Toast.makeText(context, getString(R.string.resource_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeSearchQuery() {
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchViewModel.setSearchQuery(it)
                }
                return true
            }
        })
    }

    private fun setSearchList() {
        searchViewModel.movieResult.observe(viewLifecycleOwner, { movies ->
            if (movies.isNullOrEmpty()){
                binding.pbMoviesLoading.visibility = View.GONE
                binding.lavMoviesNotFound.visibility = View.VISIBLE
                binding.tvMoviesNotFound.visibility = View.VISIBLE
            } else {
                binding.pbMoviesLoading.visibility = View.GONE
                binding.lavMoviesNotFound.visibility = View.GONE
                binding.tvMoviesNotFound.visibility = View.GONE
            }
            moviesAdapter.setData(movies)
        })
        searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener{
            override fun onSearchViewShown() {
                binding.pbMoviesLoading.visibility = View.GONE
                binding.lavMoviesNotFound.visibility = View.GONE
                binding.tvMoviesNotFound.visibility = View.GONE
            }

            override fun onSearchViewClosed() {
                binding.pbMoviesLoading.visibility = View.GONE
                binding.lavMoviesNotFound.visibility = View.GONE
                binding.tvMoviesNotFound.visibility = View.GONE
                setList(sort)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentMoviesBinding = null
    }

}