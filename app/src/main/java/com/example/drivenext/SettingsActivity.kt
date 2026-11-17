package com.example.drivenext

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drivenext.data.AppPreferences

class SettingsActivity : AppCompatActivity() {

    private val preferences by lazy { AppPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<View>(R.id.rowProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<View>(R.id.rowBookings).setOnClickListener { showStub(getString(R.string.settings_my_bookings)) }

        setupRow(R.id.rowTheme, R.drawable.ic_menu_theme, R.string.settings_theme)
        setupRow(R.id.rowNotifications, R.drawable.ic_menu_notifications, R.string.settings_notifications)
        setupRow(R.id.rowConnectCar, R.drawable.ic_menu_connect_car, R.string.settings_connect_car)
        setupRow(R.id.rowHelp, R.drawable.ic_menu_help, R.string.settings_help)
        setupRow(R.id.rowInvite, R.drawable.ic_menu_invite, R.string.settings_invite_friend)

        findViewById<View>(R.id.tabHome).setOnClickListener {
            startActivity(Intent(this, HomepageActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.tabBookmark).setOnClickListener { showStub("Закладки") }
        // tabSettings is current screen
    }

    private fun setupRow(rowId: Int, iconRes: Int, textRes: Int) {
        val row = findViewById<View>(rowId)
        row.findViewById<ImageView>(R.id.rowIcon).setImageResource(iconRes)
        row.findViewById<TextView>(R.id.rowText).setText(textRes)
        row.setOnClickListener { showStub(getString(textRes)) }
    }

    override fun onResume() {
        super.onResume()
        showAvatar()
    }

    private fun showAvatar() {
        val imgAvatar = findViewById<ImageView>(R.id.imgSettingsAvatar)
        val placeholder = findViewById<View>(R.id.imgSettingsAvatarPlaceholder)
        val saved = preferences.avatarUri
        if (saved == null) {
            imgAvatar.setImageDrawable(null)
            placeholder.visibility = View.VISIBLE
            return
        }
        imgAvatar.setImageURI(Uri.parse(saved))
        placeholder.visibility =
            if (imgAvatar.drawable != null) View.GONE else View.VISIBLE
    }

    private fun showStub(name: String) {
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
    }
}
