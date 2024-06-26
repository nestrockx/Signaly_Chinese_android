package com.wegielek.signalychinese.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import com.wegielek.signalychinese.R
import com.wegielek.signalychinese.SignalyChineseApplication
import com.wegielek.signalychinese.database.Dictionary
import com.wegielek.signalychinese.database.History
import com.wegielek.signalychinese.views.MainActivity
import java.lang.reflect.InvocationTargetException


class Utils {
    companion object {
        fun containsChinese(input: String): Boolean {
            return input.matches(".*[\\u4e00-\\u9fff]+.*".toRegex())
        }

        fun getPaint(color: Int, brushSize: Float, dashed: Boolean): Paint {
            val paint = Paint()
            paint.isAntiAlias = true
            paint.isDither = true
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeCap = Paint.Cap.ROUND
            paint.xfermode = null
            paint.alpha = 0xff
            paint.color = color
            paint.strokeWidth = brushSize
            if (dashed) {
                paint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 5f)
            }
            return paint
        }

        fun getTextPaint(color: Int, textSize: Float): Paint {
            val paint = Paint()
            paint.textSize = textSize
            paint.textAlign = Paint.Align.CENTER
            paint.style = Paint.Style.FILL
            paint.color = color
            return paint
        }

        fun dpToPixels(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        fun getScreenWidth(activity: Activity): Int {
            val displayMetrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return activity.windowManager.currentWindowMetrics.bounds.width()
            }
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

        fun getScreenHeight(activity: Activity): Int {
            val displayMetrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return activity.windowManager.currentWindowMetrics.bounds.height()
            }
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

        fun isScreenRotated(context: Context): Boolean {
            return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }

        fun copyToClipboard(context: Context, text: String?): Boolean {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", text)
            clipboard.setPrimaryClip(clip)
            return true
        }

        fun showDefinitionPopup(
            v: View,
            tv: TextView,
            text: String,
            languageFrom: String,
            languageTo: String,
            highlightColor: Int
        ) {
            val popupMenu = PopupMenu(v.context, v)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.copy -> {
                        var tmpText = "" + text
                        if (text.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray().size > 1) {
                            tmpText =
                                text.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        }
                        if (copyToClipboard(v.context, tmpText)) {
                            Log.d("Utils", "Successfully copied")
                        }
                        return@setOnMenuItemClickListener true
                    }
                    R.id.search -> {
                        var tmpText = "" + text
                        if (text.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray().size > 1) {
                            tmpText =
                                text.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        }
                        val intent = Intent(v.context, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("searchWord", tmpText)
                        v.context.startActivity(intent)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.translate -> {
                        var tmpText = "" + text
                        if (text.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray().size > 1) {
                            tmpText =
                                text.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                        }
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data =
                            Uri.parse("https://translate.google.com/?sl=$languageFrom&op=translate&tl=$languageTo&text=$tmpText")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        v.context.startActivity(intent)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.speak -> {
                        var tmpText = "" + text
                        if (text.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray().size > 1) {
                            tmpText =
                                text.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        }
                        if (languageFrom == "pl") {
                            TextToSpeechManager.speakPL(tv, tmpText, highlightColor)
                        } else {
                            TextToSpeechManager.speakCH(tv, tmpText, highlightColor)
                        }
                    }
                }
                false
            }
            popupMenu.inflate(R.menu.popup_definition_menu)
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup[popupMenu]
                menu?.let {
                    it.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                        .invoke(it, true)
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
                Log.e("Popup", "Error:" + e.message)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                Log.e("Popup", "Error:" + e.message)
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
                Log.e("Popup", "Error:" + e.message)
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
                Log.e("Popup", "Error:" + e.message)
            } finally {
                popupMenu.show()
            }
        }

        fun showSentencePopup(
            v: View,
            tv: TextView,
            text: String,
            highlightColor: Int
        ) {
            val popupMenu = PopupMenu(v.context, v)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.speak_polish -> {
                        TextToSpeechManager.speakPL(tv, text, highlightColor)
                        return@setOnMenuItemClickListener true
                    }
                }
                false
            }
            popupMenu.inflate(R.menu.popup_sentence_menu)
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup[popupMenu]
                menu?.let {
                    it.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                        .invoke(it, true)
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
                Log.e("Popup", "Error:" + e.message)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                Log.e("Popup", "Error:" + e.message)
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
                Log.e("Popup", "Error:" + e.message)
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
                Log.e("Popup", "Error:" + e.message)
            } finally {
                popupMenu.show()
            }
        }

        fun showMicLanguagePopup(v: View) {
            val popupMenu = PopupMenu(v.context, v)
            popupMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.polish) {
                    Preferences.setMicLanguage("pl-PL")
                } else if (it.itemId == R.id.chinese) {
                    Preferences.setMicLanguage("zh-CN")
                }
                false
            }
            popupMenu.inflate(R.menu.popup_mic_menu)
            popupMenu.show()
        }

        fun showSearchModePopup(v: View) {
            val popupMenu = PopupMenu(v.context, v)
            popupMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.normal_search) {
                    Preferences.setSearchMode("normal")
                } else if (it.itemId == R.id.all_search) {
                    Preferences.setSearchMode("all")
                }
                false
            }
            popupMenu.inflate(R.menu.popup_search_menu)
            popupMenu.show()
        }

        fun showWritingCharacterTypePopup(v: View, callback:(string: String) -> Unit) {
            val popupMenu = PopupMenu(v.context, v)
            popupMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.simplified) {
                    callback.invoke("simplified")
                } else if (it.itemId == R.id.traditional) {
                    callback.invoke("traditional")
                }
                false
            }
            popupMenu.inflate(R.menu.popup_writing_character_type_menu)
            popupMenu.show()
        }

        fun showCustomPopup(v: View) {
            val array = IntArray(2)
            v.getLocationOnScreen(array)
        }

        fun historyToDictionary(history: History): Dictionary {
            val dictionary = Dictionary()
            dictionary.traditionalSign = history.traditionalSign
            dictionary.simplifiedSign = history.simplifiedSign
            dictionary.pronunciation = history.pronunciation
            dictionary.pronunciationPhonetic = history.pronunciationPhonetic
            dictionary.translation = history.translation
            return dictionary
        }

        fun hideKeyboard(context: Context, v: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }

        fun hasInternetConnection(): Boolean {
            val connectivityManager = SignalyChineseApplication.instance.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }

        fun setHighLightedText(tv: TextView, textToHighlight: String, color: Int) {
            val tvt = tv.text.toString()
            var ofe = tvt.indexOf(textToHighlight, 0)
            val wordToSpan: Spannable = SpannableString(tv.text)
            var ofs = 0
            while (ofs < tvt.length && ofe != -1) {
                ofe = tvt.indexOf(textToHighlight, ofs)
                if (ofe == -1) break else {
                    wordToSpan.setSpan(
                        ForegroundColorSpan(color),
                        ofe,
                        ofe + textToHighlight.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    tv.setText(wordToSpan, TextView.BufferType.SPANNABLE)
                }
                ofs = ofe + 1
            }
        }

        fun removeHighlights(tv: TextView) {
            tv.text = SpannableString(tv.text.toString())
        }

        fun String.addStringAtIndex(string: String, index: Int) =
            StringBuilder(this).apply { insert(index, string) }.toString()
    }

}