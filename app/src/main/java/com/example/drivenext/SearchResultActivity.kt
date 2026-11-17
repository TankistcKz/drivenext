package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        val cars = intent.getSerializableExtra(EXTRA_RESULTS) as? ArrayList<Car> ?: arrayListOf()

        val rv = findViewById<RecyclerView>(R.id.rvResults)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = CarAdapter(
            cars,
            onBook = { Toast.makeText(this, "Забронировать: ${it.model}", Toast.LENGTH_SHORT).show() },
            onDetails = { Toast.makeText(this, "Детали: ${it.model}", Toast.LENGTH_SHORT).show() }
        )

        findViewById<View>(R.id.tabHome).setOnClickListener {
            startActivity(Intent(this, HomepageActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.tabBookmark).setOnClickListener { Toast.makeText(this, "Закладки", Toast.LENGTH_SHORT).show() }
        findViewById<View>(R.id.tabSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
    }

    class CarAdapter(
        private val items: List<Car>,
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

    companion object {
        const val EXTRA_QUERY = "query"
        const val EXTRA_RESULTS = "results"
    }
}
