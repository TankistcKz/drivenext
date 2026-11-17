package com.example.drivenext

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.drivenext.data.AppPreferences

class ProfileActivity : AppCompatActivity() {

    private val preferences by lazy { AppPreferences(this) }

    private lateinit var imgAvatar: ImageView
    private lateinit var avatarPlaceholder: View

    private val pickAvatar = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            preferences.avatarUri = uri.toString()
            applyAvatar(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imgAvatar = findViewById(R.id.imgAvatar)
        avatarPlaceholder = findViewById(R.id.imgAvatarPlaceholder)
        showSavedAvatar()

        val openPicker = View.OnClickListener {
            pickAvatar.launch(arrayOf("image/*"))
        }
        findViewById<View>(R.id.btnAvatar).setOnClickListener(openPicker)
        findViewById<View>(R.id.btnAvatarAdd).setOnClickListener(openPicker)

        findViewById<View>(R.id.rowChangePassword).setOnClickListener {
            Toast.makeText(this, getString(R.string.profile_password_value), Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.rowLogout).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        findViewById<View>(R.id.tabHome).setOnClickListener {
            startActivity(Intent(this, HomepageActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.tabBookmark).setOnClickListener {
            Toast.makeText(this, "Закладки", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.tabSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
    }

    private fun showSavedAvatar() {
        val saved = preferences.avatarUri ?: return
        applyAvatar(Uri.parse(saved))
    }

    private fun applyAvatar(uri: Uri) {
        imgAvatar.setImageURI(uri)
        if (imgAvatar.drawable != null) {
            avatarPlaceholder.visibility = View.GONE
        }
    }
}
