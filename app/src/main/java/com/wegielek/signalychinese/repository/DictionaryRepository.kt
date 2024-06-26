package com.wegielek.signalychinese.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.google.common.util.concurrent.ListenableFuture
import com.wegielek.signalychinese.database.AppDictionaryDatabase.Companion.getInstance
import com.wegielek.signalychinese.database.Dictionary
import com.wegielek.signalychinese.database.DictionaryDao
import com.wegielek.signalychinese.database.FlashCards
import com.wegielek.signalychinese.database.FlashCardsDao
import com.wegielek.signalychinese.database.History
import com.wegielek.signalychinese.database.HistoryDao
import com.wegielek.signalychinese.database.Radicals
import com.wegielek.signalychinese.database.RadicalsDao
import com.wegielek.signalychinese.database.Sentences
import com.wegielek.signalychinese.database.SentencesDao

class DictionaryRepository(application: Application) {
    private val dictionaryDao: DictionaryDao = getInstance(application)?.dictionaryDao() ?: throw NullPointerException("Database instance not available")
    private val radicalsDao: RadicalsDao = getInstance(application)?.radicalDao() ?: throw NullPointerException("Database instance not available")
    private val historyDao: HistoryDao = getInstance(application)?.historyDao() ?: throw NullPointerException("Database instance not available")
    private val flashCardsDao: FlashCardsDao = getInstance(application)?.flashCardsDao() ?: throw NullPointerException("Database instance not available")
    private val sentencesDao: SentencesDao = getInstance(application)?.sentencesDao() ?: throw NullPointerException("Database instance not available")

    fun findAllSentences(simplifiedWord: String, traditionalWord: String): ListenableFuture<List<Sentences>> {
        return sentencesDao.findAllSentences(simplifiedWord, traditionalWord)
    }

    fun findSimplifiedSentences(word: String): ListenableFuture<List<Sentences>> {
        return sentencesDao.findSimplifiedSentences(word)
    }

    fun findTraditionalSentences(word: String): ListenableFuture<List<Sentences>> {
        return sentencesDao.findTraditionalSentences(word)
    }

    fun getFlashCardGroup(group: String): LiveData<List<FlashCards>> {
        return flashCardsDao.getFlashCardGroup(group)
    }

    fun addFlashCardGroup(vararg flashCards: FlashCards): ListenableFuture<Void?> {
        return flashCardsDao.addFlashCardGroup(*flashCards)
    }

    fun deleteFlashCardGroup(group: String): ListenableFuture<Void?> {
        return flashCardsDao.deleteFlashCardGroup(group)
    }

    fun isFlashCardExists(
        traditional: String,
        simplified: String,
        pronunciation: String
    ): ListenableFuture<Boolean> {
        return flashCardsDao.isFlashCardExists(traditional, simplified, pronunciation)
    }

    fun addFlashCardToGroup(flashCards: FlashCards): ListenableFuture<Void?> {
        return flashCardsDao.addFlashCardToGroup(flashCards)
    }

    fun deleteFlashCard(
        traditional: String,
        simplified: String,
        pronunciation: String
    ): ListenableFuture<Void?> {
        return flashCardsDao.deleteFlashCard(traditional, simplified, pronunciation)
    }

    fun deleteFlashCardFromGroup(
        group: String,
        traditional: String,
        simplified: String,
        pronunciation: String
    ): ListenableFuture<Void?> {
        return flashCardsDao.deleteFlashCardFromGroup(group, traditional, simplified, pronunciation)
    }

    fun getFlashCardsGroups(): LiveData<List<String>> {
        return flashCardsDao.getFlashCardsGroups()
    }

    fun getFlashCardsGroupsNonObserve(): ListenableFuture<List<String>> {
        return flashCardsDao.getFlashCardsGroupsNonObserve()
    }

    fun getFlashCardGroups(
        traditional: String,
        simplified: String,
        pronunciation: String
    ): ListenableFuture<List<String>> {
        return flashCardsDao.getFlashCardGroups(traditional, simplified, pronunciation)
    }

    fun searchSingleCH(searchQuery: String): ListenableFuture<List<Dictionary>> {
        return dictionaryDao.searchSingleCH(searchQuery)
    }

    fun searchByWordCH(searchQuery: String): ListenableFuture<List<Dictionary>> {
        return dictionaryDao.searchByWordCH(searchQuery)
    }

    fun searchByWordCHAll(searchQuery: String): ListenableFuture<List<Dictionary>> {
        return dictionaryDao.searchByWordCHAll(searchQuery)
    }

    fun searchByWordPL(searchQuery: String): ListenableFuture<List<Dictionary>> {
        return dictionaryDao.searchByWordPL(searchQuery)
    }

    fun searchByWordPLAll(searchQuery: String): ListenableFuture<List<Dictionary>> {
        return dictionaryDao.searchByWordPLAll(searchQuery)
    }

    fun getRadicalsSection(section: String): ListenableFuture<List<Radicals>> {
        return radicalsDao.getRadicalsSection(section)
    }

    val wholeHistory: LiveData<List<History>>
        get() = historyDao.wholeHistory

    fun addHistoryRecord(history: History): ListenableFuture<Void?> {
        return historyDao.addHistoryRecord(history)
    }

    fun deleteWholeHistory(): ListenableFuture<Void?> {
        return historyDao.deleteWholeHistory()
    }
}
