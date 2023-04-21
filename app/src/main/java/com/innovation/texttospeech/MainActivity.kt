package com.innovation.texttospeech

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.innovation.texttospeech.databinding.ActivityMainBinding
import com.innovation.texttospeech.translation_engine.ConversionCallback
import com.innovation.texttospeech.translation_engine.TranslatorFactory
import com.innovation.texttospeech.utils.TTSPlayPauseCallback

class MainActivity : BasePermissionActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    var ttsPlayPauseCallback: TTSPlayPauseCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun getActivityLayout(): Int {
        return R.layout.activity_main
    }

    override fun setUpView() {
        setSupportActionBar(binding.toolBar)
        //SPEECH TO TEXT DEMO
        binding.speechToText.setOnClickListener { view ->
            Snackbar.make(view, "Speak now, App is listening", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            TranslatorFactory
                .instance
                .with(
                    TranslatorFactory.TRANSLATORS.SPEECH_TO_TEXT,
                    object : ConversionCallback {
                        override fun onSuccess(result: String) {
                            binding.sttOutput.text = result
                        }

                        override fun onCompletion() {

                        }

                        override fun onErrorOccurred(errorMessage: String) {
                            binding.erroConsole.text = "Speech2Text Error: $errorMessage"
                        }

                    },ttsPlayPauseCallback).initialize("Speak Now !!", this@MainActivity)

        }

        //TEXT TO SPEECH DEMO
        binding.textToSpeech.setOnClickListener { view ->
            val stringToSpeak: String = binding.ttsInput.text.toString()
            if (null != stringToSpeak && stringToSpeak.isNotEmpty()) {
                TranslatorFactory
                    .instance
                    .with(
                        TranslatorFactory.TRANSLATORS.TEXT_TO_SPEECH,
                        object : ConversionCallback {
                            override fun onSuccess(result: String) {

                            }

                            override fun onCompletion() {

                            }

                            override fun onErrorOccurred(errorMessage: String) {
                                binding.erroConsole.text = "Text2Speech Error: $errorMessage"
                            }

                        },ttsPlayPauseCallback)
                    .initialize(stringToSpeak, this)

            } else {
                binding.ttsInput.setText("Invalid input")
                Snackbar.make(view, "Please enter some text to speak", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.tvStop.setOnClickListener {
            ttsPlayPauseCallback?.ttsPlayPauseCallback()
        }

    }

    fun findString(listOfPossibleMatches: ArrayList<String>?, stringToMatch: String): Boolean {

        if (null != listOfPossibleMatches) {

            for (transaltion in listOfPossibleMatches) {

                if (transaltion.contains(stringToMatch)) {

                    return true
                }
            }
        }
        return false
    }

    /**
     * Share on social media
     *
     * @param messageToShare message To Share
     * @param activity       context
     */
    fun share(messageToShare: String, activity: Activity) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, messageToShare)
        activity.startActivity(Intent.createChooser(shareIntent, "Share using"))
    }

}