package com.andb.apps.cards.utils

import android.content.res.Resources
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.lang.reflect.AccessibleObject.setAccessible
import android.graphics.drawable.Drawable
import android.graphics.PorterDuff
import android.view.View
import androidx.core.content.ContextCompat
import android.widget.TextView
import androidx.annotation.ColorInt
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun dpToPx(dp: Int): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dp * scale).toInt()
}

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, observer: (T)->Unit){
    this.observe(lifecycleOwner, Observer {
        observer.invoke(it)
    })
}

fun newIoThread(block: suspend CoroutineScope.()->Unit){
    CoroutineScope(Dispatchers.IO).launch(block = block)
}

suspend fun mainThread(block: suspend CoroutineScope.() -> Unit){
    withContext(Dispatchers.Main, block)
}

suspend fun ioThread(block: suspend CoroutineScope.() -> Unit){
    withContext(Dispatchers.IO, block)
}