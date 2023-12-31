package com.wegielek.signalychinese.interfaces

import com.google.mlkit.vision.digitalink.RecognitionCandidate

interface CanvasViewListener {
    fun onResults(recognitionCandidatesList: List<RecognitionCandidate>)
    fun onModelDownloaded(noInternet: Boolean)
    fun onInput()
}