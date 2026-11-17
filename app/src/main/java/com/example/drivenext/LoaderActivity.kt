package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoaderActivity : AppCompatActivity() {

    private lateinit var loadingState: View
    private lateinit var errorState: View
    private lateinit var tvError: TextView
    private lateinit var btnRetry: View

    private var query: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        loadingState = findViewById(R.id.loadingState)
        errorState = findViewById(R.id.errorState)
        tvError = findViewById(R.id.tvError)
        btnRetry = findViewById(R.id.btnRetry)

        query = intent.getStringExtra(EXTRA_QUERY).orEmpty().trim()

        btnRetry.setOnClickListener { startSearch() }
        startSearch()
    }

    private fun startSearch() {
        if (query.isEmpty()) {
            showSearchError()
            return
        }

        showLoading()
        CarRepository.search(
            query = query,
            onSuccess = { cars ->
                if (isFinishing || isDestroyed) return@search
                openResults(cars)
            },
            onError = {
                if (isFinishing || isDestroyed) return@search
                showLoadError()
            }
        )
    }

    private fun openResults(cars: List<Car>) {
        val intent = Intent(this, SearchResultActivity::class.java).apply {
            putExtra(SearchResultActivity.EXTRA_QUERY, query)
            putExtra(SearchResultActivity.EXTRA_RESULTS, ArrayList(cars))
        }
        startActivity(intent)
        finish()
    }

    private fun showLoading() {
        loadingState.visibility = View.VISIBLE
        errorState.visibility = View.GONE
    }

    private fun showSearchError() {
        loadingState.visibility = View.GONE
        errorState.visibility = View.VISIBLE
        tvError.text = getString(R.string.search_error)
        btnRetry.visibility = View.GONE
    }

    private fun showLoadError() {
        loadingState.visibility = View.GONE
        errorState.visibility = View.VISIBLE
        tvError.text = getString(R.string.load_error)
        btnRetry.visibility = View.VISIBLE
    }

    companion object {
        const val EXTRA_QUERY = "query"
    }
}
