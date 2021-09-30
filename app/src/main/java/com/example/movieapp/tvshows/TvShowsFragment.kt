package com.example.movieapp.tvshows

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
import com.example.movieapp.databinding.FragmentTvShowsBinding
import com.example.movieapp.detail.DetailActivity
import com.example.movieapp.home.HomeActivity
import com.example.movieapp.home.SearchViewModel
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@FlowPreview
class TvShowsFragment : Fragment() {

    private var fragmentTvShowsBinding: FragmentTvShowsBinding? = null
    private val binding get() = fragmentTvShowsBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentTvShowsBinding = FragmentTvShowsBinding.inflate(inflater, container, false)
        val toolbar: Toolbar = activity?.findViewById<View>(R.id.t_toolbar) as Toolbar
        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        searchView = (activity as HomeActivity).findViewById(R.id.sv_search)
        return binding.root
    }

    private val viewModel: TvShowsViewModel by viewModel()
    private lateinit var tvShowsAdapter: MoviesAdapter
    private val searchViewModel: SearchViewModel by viewModel()
    private lateinit var searchView: MaterialSearchView
    private var sort = SortUtils.NEWEST

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvShowsAdapter = MoviesAdapter()
        setList(sort)
        observeSearchQuery()
        setSearchList()

        with(binding.rvTvShows) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = tvShowsAdapter
        }

        tvShowsAdapter.onItemClick = { selectedData ->
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_MOVIE, selectedData)
            startActivity(intent)
        }

        binding.fabTvShowsNewest.setOnClickListener {
            binding.famTvShowsSort.close(true)
            sort = SortUtils.NEWEST
            setList(sort)
        }
        binding.fabTvShowsPopularity.setOnClickListener {
            binding.famTvShowsSort.close(true)
            sort = SortUtils.POPULARITY
            setList(sort)
        }
        binding.fabTvShowsVote.setOnClickListener {
            binding.famTvShowsSort.close(true)
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
        viewModel.getTvShows(sort).observe(viewLifecycleOwner, tvShowsObserver)
    }

    private val tvShowsObserver = Observer<Resource<List<Movie>>> { tvShow ->
        if (tvShow != null) {
            when (tvShow) {
                is Resource.Loading -> {
                    binding.pbTvShowsLoading.visibility = View.VISIBLE
                    binding.lavTvShowsNotFound.visibility = View.GONE
                    binding.tvTvShowsNotFound.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.pbTvShowsLoading.visibility = View.GONE
                    binding.lavTvShowsNotFound.visibility = View.GONE
                    binding.tvTvShowsNotFound.visibility = View.GONE
                    tvShowsAdapter.setData(tvShow.data)
                }
                is Resource.Error -> {
                    binding.pbTvShowsLoading.visibility = View.GONE
                    binding.lavTvShowsNotFound.visibility = View.VISIBLE
                    binding.tvTvShowsNotFound.visibility = View.VISIBLE
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
        searchViewModel.tvShowResult.observe(viewLifecycleOwner, { tvShows ->
            if (tvShows.isNullOrEmpty()) {
                binding.pbTvShowsLoading.visibility = View.GONE
                binding.lavTvShowsNotFound.visibility = View.VISIBLE
                binding.tvTvShowsNotFound.visibility = View.VISIBLE
            } else {
                binding.pbTvShowsLoading.visibility = View.GONE
                binding.lavTvShowsNotFound.visibility = View.GONE
                binding.tvTvShowsNotFound.visibility = View.GONE
            }
            tvShowsAdapter.setData(tvShows)
        })
        searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener{
            override fun onSearchViewShown() {
                binding.pbTvShowsLoading.visibility = View.GONE
                binding.lavTvShowsNotFound.visibility = View.GONE
                binding.tvTvShowsNotFound.visibility = View.GONE
            }

            override fun onSearchViewClosed() {
                binding.pbTvShowsLoading.visibility = View.GONE
                binding.lavTvShowsNotFound.visibility = View.GONE
                binding.tvTvShowsNotFound.visibility = View.GONE
                setList(sort)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentTvShowsBinding = null
    }

}