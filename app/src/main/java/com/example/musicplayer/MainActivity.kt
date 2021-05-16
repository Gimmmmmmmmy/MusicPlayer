package com.example.musicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var mp: MediaPlayer
    private lateinit var mp2: MediaPlayer
    private var totalTime: Int = 0
    var muteChek = false
    var currentPlaying = "drowning"
    var song = listOf<Int>()
    var songCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        song += R.raw.drowning
        song += R.raw.might_not_give_up

        mp = MediaPlayer.create(this, song[0])
        mp2 = MediaPlayer.create(this, song[1])
        mp.isLooping = true
        mp.setVolume(0.5f, 0.5f)
        totalTime = mp.duration
        var duration = mp.duration.toString()
        binding.remainingTimeLabel.text = "$duration"


        // Volume Bar
        binding.volumeBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            var volumeNum: Float = progress / 100.0f
                            mp.setVolume(volumeNum, volumeNum)
                        }
                    }
                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }
                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }
                }
        )

        // Position Bar
        binding.positionBar.max = totalTime
        binding.positionBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            mp.seekTo(progress)
                        }
                    }
                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }
                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }
                }
        )

        // Thread
        Thread(Runnable {
            while (mp != null) {
                try {
                    var msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what

            // Update positionBar
            binding.positionBar.progress = currentPosition

            // Update Labels
            var elapsedTime = createTimeLabel(currentPosition)
            binding.elapsedTimeLabel.text = elapsedTime

            var remainingTime = createTimeLabel(totalTime - currentPosition)
            binding.remainingTimeLabel.text = "-$remainingTime"
        }
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    fun playBtnClick(v: View) {

        if (mp.isPlaying) {
            // Stop
            mp.pause()
            binding.playBtn.setBackgroundResource(R.drawable.play)

        } else {
            // Start
            mp.start()
            binding.playBtn.setBackgroundResource(R.drawable.stop)
        }
    }
    fun prevBtnClick(v: View) {

        if (binding.positionBar.progress<5) {
            // Stop
            mp.seekTo(0)

        } else {
            // Start
            mp.seekTo(0)
        }
    }
    fun nextBtnClick(v: View) {

        if (mp.isPlaying) {
            mp.setNextMediaPlayer(mp2)
            binding.positionBar.progress = 100
        } else {
            mp2.setNextMediaPlayer(mp)
            binding.positionBar.progress = 100
        }
    }
    fun muteBtnClick(v: View) {

        var volumeNum = binding.volumeBar.progress

        if (muteChek) {
            binding.muteBtn.setBackgroundResource(R.drawable.volume_mute)
            mp.setVolume(volumeNum/100.0f,volumeNum/100.0f)
            muteChek = false

        } else {
            binding.muteBtn.setBackgroundResource(R.drawable.volume_off)
            mp.setVolume(0.0F,0.0f)
            muteChek = true
        }
    }

}