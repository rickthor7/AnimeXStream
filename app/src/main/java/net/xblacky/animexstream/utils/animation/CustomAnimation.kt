package net.xblacky.animexstream.utils.animation

import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.google.android.exoplayer2.ui.PlayerView
import timber.log.Timber

object CustomAnimation {

    fun zoomInByScale(scaleBy: Float, player: PlayerView) {
        val animation = ScaleAnimation(
            1f,
            scaleBy,
            1f,
            scaleBy,
            ScaleAnimation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animation.fillAfter = true
        animation.duration = 200
        player.videoSurfaceView.startAnimation(animation)
    }

    fun zoomOutByScale(scaleBy: Float, player: PlayerView) {

        val animation = ScaleAnimation(
            scaleBy, 1f, scaleBy, 1f, ScaleAnimation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animation.duration = 200
        player.videoSurfaceView.startAnimation(animation)
    }
}