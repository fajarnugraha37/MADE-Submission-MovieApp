package com.example.movieapp.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.core.domain.model.Movie
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityDetailBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailMovie = intent.getParcelableExtra<Movie>(EXTRA_MOVIE)
        if (detailMovie != null)
            populateDetail(detailMovie)

        binding.ibDetailNavBack.setOnClickListener { onBackPressed() }
        binding.fabDetailShare.setOnClickListener { detailMovie?.let { it -> onShare(it) } }
    }

    private fun populateDetail(movie: Movie) {
        with(binding) {
            tvDetailTitle.text = movie.title
            tvDetailDate.text = movie.releaseDate
            tvDetailOverview.text = movie.overview
            tvDetailPopularity.text = getString(
                R.string.popularity_detail,
                movie.popularity.toString(),
                movie.voteCount.toString(),
                movie.voteAverage.toString()
            )
            tvDetailUserScore.text = "User Score: ${movie.voteAverage}"
            Glide.with(this@DetailActivity)
                .load(movie.poster)
                .into(ivDetailPoster)
            ivDetailPoster.tag = movie.posterPath

            var favoriteState = movie.favorite
            setFavoriteState(favoriteState)
            binding.fabDetailFavorite.setOnClickListener {
                favoriteState = !favoriteState
                viewModel.setFavoriteMovie(movie, favoriteState)
                setFavoriteState(favoriteState)


                val message = if (favoriteState) "ditambahkan ke" else "dekeluarkan dari"
                Snackbar
                    .make(binding.root, "${movie.title} berhasil $message favorite", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setFavoriteState(state: Boolean) {
        if (state) {
            binding
                .fabDetailFavorite
                .setImageDrawable(ContextCompat.getDrawable(applicationContext ,R.drawable.ic_favorite)
            )
        } else {
            binding
                .fabDetailFavorite
                .setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_favorite_border)
            )
        }
    }

    private fun onShare(movie: Movie) {
        val mimeType = "text/plain"
        ShareCompat.IntentBuilder(this)
            .apply {
                setType(mimeType)
                setChooserTitle(getString(R.string.share_title))
                setText("Tonton ${movie.title} yukk, ${getString(R.string.share_body)}")
                startChooser()
            }
    }

    companion object {
        const val EXTRA_MOVIE = "extra-movie"
    }
}