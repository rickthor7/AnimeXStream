package net.xblacky.animexstream.ui.main.player

import android.app.AppOpsManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.fragment_video_player.*
import net.xblacky.animexstream.R
import net.xblacky.animexstream.utils.model.Content
import timber.log.Timber
import java.lang.Exception
import android.view.WindowInsetsController

import android.view.WindowInsets




class VideoPlayerActivity : AppCompatActivity(), VideoPlayerListener {

    private lateinit var viewModel: VideoPlayerViewModel
    private var episodeNumber: String? = ""
    private var animeName: String? = ""
    private lateinit var content: Content
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setupTransitions()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        viewModel = ViewModelProvider(this).get(VideoPlayerViewModel::class.java)
        getExtra(intent)
//        (playerFragment as VideoPlayerFragment).updateContent(Content(
//            url = url,
//            episodeNumber = "153"
//        ))
        setObserver()
        goFullScreen()
    }

    private fun setupTransitions() {
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.enterTransition = MaterialContainerTransform().apply {
            addTarget(R.id.playerActivityContainer)
            scrimColor = Color.TRANSPARENT
            fadeMode =  MaterialContainerTransform.FADE_MODE_THROUGH
            startContainerColor = ContextCompat.getColor(applicationContext, android.R.color.transparent)
            endContainerColor = ContextCompat.getColor(applicationContext, android.R.color.transparent)
            duration = 300L
        }
    }

    override fun onNewIntent(intent: Intent?) {
        (playerFragment as VideoPlayerFragment).playOrPausePlayer(
            playWhenReady = false,
            loseAudioFocus = false
        )
        (playerFragment as VideoPlayerFragment).saveWatchedDuration()
        getExtra(intent)
        super.onNewIntent(intent)

    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPipMode()
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            goFullScreen()
        }
    }

    private fun getExtra(intent: Intent?) {
        val url = intent?.extras?.getString("episodeUrl")
        episodeNumber = intent?.extras?.getString("episodeNumber")
        animeName = intent?.extras?.getString("animeName")
        viewModel.updateEpisodeContent(
            Content(
                animeName = animeName ?: "",
                episodeUrl = url,
                episodeName = "\"$episodeNumber\"",
                url = ""
            )
        )
        viewModel.fetchEpisodeMediaUrl()
    }

    @Suppress("DEPRECATION")
    private fun enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && packageManager
                .hasSystemFeature(
                    PackageManager.FEATURE_PICTURE_IN_PICTURE
                )
            && hasPipPermission()
            && (playerFragment as VideoPlayerFragment).isVideoPlaying()
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val params = PictureInPictureParams.Builder()
                this.enterPictureInPictureMode(params.build())
            } else {
                this.enterPictureInPictureMode()
            }
        }
    }

    override fun onStop() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
            && hasPipPermission()
        ) {
            finishAndRemoveTask()
        }
        super.onStop()
    }

    override fun finish() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        ) {
            finishAndRemoveTask()
        }
        super.finish()
    }

    fun enterPipModeOrExit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && packageManager
                .hasSystemFeature(
                    PackageManager.FEATURE_PICTURE_IN_PICTURE
                )
            && (playerFragment as VideoPlayerFragment).isVideoPlaying()
            && hasPipPermission()
        ) {
            try {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val params = PictureInPictureParams.Builder()
                    this.enterPictureInPictureMode(params.build())
                } else {
                    this.enterPictureInPictureMode()
                }
            } catch (ex: Exception) {
                Timber.e(ex.message)
            }

        } else {
            finish()

        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        exoPlayerView.useController = !isInPictureInPictureMode
    }

    private fun hasPipPermission(): Boolean {
        val appsOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                appsOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                    android.os.Process.myUid(),
                    packageName
                ) == AppOpsManager.MODE_ALLOWED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                appsOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                    android.os.Process.myUid(),
                    packageName
                ) == AppOpsManager.MODE_ALLOWED
            }
            else -> {
                false
            }
        }
    }

    private fun setObserver() {
        viewModel.liveContent.observe(this, Observer {
            this.content = it
            it?.let {
                if (!it.url.isNullOrEmpty()) {
                    (playerFragment as VideoPlayerFragment).updateContent(it)
                }
            }
        })
        viewModel.isLoading.observe(this, Observer {
            (playerFragment as VideoPlayerFragment).showLoading(it.isLoading)
        })
        viewModel.errorModel.observe(this, Observer {
            (playerFragment as VideoPlayerFragment).showErrorLayout(
                it.show,
                it.errorMsgId,
                it.errorCode
            )
        })
    }

    override fun onBackPressed() {
        enterPipModeOrExit()
    }

    private fun goFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }else{
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

    }

    override fun updateWatchedValue(content: Content) {
        viewModel.saveContent(content)
    }

    override fun playNextEpisode() {
        viewModel.updateEpisodeContent(
            Content(
                episodeUrl = content.nextEpisodeUrl,
                episodeName = "\"EP ${incrimentEpisodeNumber(content.episodeName!!)}\"",
                url = "",
                animeName = content.animeName
            )
        )
        viewModel.fetchEpisodeMediaUrl()

    }

    override fun playPreviousEpisode() {

        viewModel.updateEpisodeContent(
            Content(
                episodeUrl = content.previousEpisodeUrl,
                episodeName = "\"EP ${decrimentEpisodeNumber(content.episodeName!!)}\"",
                url = "",
                animeName = content.animeName
            )
        )
        viewModel.fetchEpisodeMediaUrl()
    }

    private fun incrimentEpisodeNumber(episodeName: String): String {
        return try {
            Timber.e("Episode Name $episodeName")
            val episodeString = episodeName.substring(
                episodeName.lastIndexOf(' ') + 1,
                episodeName.lastIndex
            )
            var episodeNumber = Integer.parseInt(episodeString)
            episodeNumber++
            episodeNumber.toString()

        } catch (obe: ArrayIndexOutOfBoundsException) {
            ""
        }
    }

    private fun decrimentEpisodeNumber(episodeName: String): String {
        return try {
            val episodeString = episodeName.substring(
                episodeName.lastIndexOf(' ') + 1,
                episodeName.lastIndex
            )
            var episodeNumber = Integer.parseInt(episodeString)
            episodeNumber--
            episodeNumber.toString()

        } catch (obe: ArrayIndexOutOfBoundsException) {
            ""
        }
    }

    fun refreshM3u8Url() {

        viewModel.fetchEpisodeMediaUrl(fetchFromDb = false)
    }

}