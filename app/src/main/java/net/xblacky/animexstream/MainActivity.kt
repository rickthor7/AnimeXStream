package net.xblacky.animexstream

import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import net.xblacky.animexstream.utils.preference.PreferenceHelper
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        updateRemoteConfig()
        toggleDayNight()
        setContentView(R.layout.main_activity)
    }

    private fun toggleDayNight() {
        when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                Timber.e("Night Mode")
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    } else {
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                    window.decorView.systemUiVisibility = flags
                }
                Timber.e("Day Mode")
            }
        }
    }

    private fun updateRemoteConfig() {
        val firebaseConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        firebaseConfig.setConfigSettingsAsync(configSettings)
        firebaseConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    val baseUrl = firebaseConfig.getString("BASE_URL")
                    val origin = firebaseConfig.getString("ORIGIN")
                    val ref = firebaseConfig.getString("REFERER")
                    Timber.e(baseUrl)
                    if (baseUrl.isNotEmpty()) {
                        PreferenceHelper.sharedPreference.setBaseUrl(baseUrl)
                    }
                    if (origin.isNotEmpty()) {
                        PreferenceHelper.sharedPreference.setOrigin(origin)
                    }
                    if (ref.isNotEmpty()) {
                        PreferenceHelper.sharedPreference.setReferrer(ref)
                    }
                }
            }
    }

}
