package android.bootcamp.mapscompose.data

import android.bootcamp.mapscompose.R
import android.content.Context
import android.media.MediaPlayer
import dagger.hilt.android.qualifiers.ApplicationContext

class SoundManager(
    @param:ApplicationContext private val context: Context,
) {
    private var deleteMediaPlayer: MediaPlayer? = null
    private var startMediaPlayer: MediaPlayer? = null

    fun playDeleteSound() {
        try {
            deleteMediaPlayer?.release()

            deleteMediaPlayer = MediaPlayer.create(context, R.raw.delete)

            deleteMediaPlayer?.setVolume(1.0f, 1.0f)

            deleteMediaPlayer?.setOnCompletionListener { mp ->
                mp.release()
                deleteMediaPlayer = null
            }

            deleteMediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
            deleteMediaPlayer?.release()
            deleteMediaPlayer = null
        }
    }

    fun playStartSound() {
        try {
            startMediaPlayer?.release()

            startMediaPlayer = MediaPlayer.create(context, R.raw.start)

            startMediaPlayer?.setVolume(1.0f, 1.0f)

            startMediaPlayer?.setOnCompletionListener { mp ->
                mp.release()
                startMediaPlayer = null
            }

            startMediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
            startMediaPlayer?.release()
            startMediaPlayer = null
        }
    }
}
