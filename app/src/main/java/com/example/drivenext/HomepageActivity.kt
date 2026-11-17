package com.example.drivenext

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomepageActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var adapter: CarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        etSearch = findViewById(R.id.etSearch)

        val rv = findViewById<RecyclerView>(R.id.rvAds)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = CarAdapter(mutableListOf(),
            onBook = { Toast.makeText(this, "Забронировать: ${it.model}", Toast.LENGTH_SHORT).show() },
            onDetails = { Toast.makeText(this, "Детали: ${it.model}", Toast.LENGTH_SHORT).show() }
        )
        rv.adapter = adapter

        loadCars()

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                startSearch()
                true
            } else {
                false
            }
        }

        findViewById<View>(R.id.tabBookmark).setOnClickListener {
            Toast.makeText(this, "Закладки", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.tabSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun loadCars() {
        CarRepository.loadCars(
            onSuccess = { adapter.submit(it) },
            onError = {
                Toast.makeText(this, getString(R.string.load_error), Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun startSearch() {
        val query = etSearch.text?.toString().orEmpty().trim()
        if (query.isEmpty()) {
            Toast.makeText(this, getString(R.string.search_error), Toast.LENGTH_SHORT).show()
            return
        }

        hideKeyboard()
        val intent = Intent(this, LoaderActivity::class.java)
            .putExtra(LoaderActivity.EXTRA_QUERY, query)
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(etSearch.windowToken, 0)
        etSearch.clearFocus()
    }

    class CarAdapter(
        private val items: MutableList<Car>,
        private val onBook: (Car) -> Unit,
        private val onDetails: (Car) -> Unit
    ) : RecyclerView.Adapter<CarAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val model: TextView = v.findViewById(R.id.carModel)
            val brand: TextView = v.findViewById(R.id.carBrand)
            val price: TextView = v.findViewById(R.id.carPrice)
            val transmission: TextView = v.findViewById(R.id.carTransmission)
            val fuel: TextView = v.findViewById(R.id.carFuel)
            val image: ImageView = v.findViewById(R.id.carImage)
            val btnBook: AppCompatButton = v.findViewById(R.id.btnBook)
            val btnDetails: AppCompatButton = v.findViewById(R.id.btnDetails)
        }

        fun submit(newItems: List<Car>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_car_card, parent, false)
            return VH(v)
        }
        override fun onBindViewHolder(holder: VH, position: Int) {
            val c = items[position]
            holder.model.text = c.model
            holder.brand.text = c.brand
            holder.price.text = c.price
            holder.transmission.text = c.transmission
            holder.fuel.text = c.fuel
            CarImageBinder.bind(holder.image, c)
            holder.btnBook.setOnClickListener { onBook(c) }
            holder.btnDetails.setOnClickListener { onDetails(c) }
        }
        override fun getItemCount() = items.size
    }
}
