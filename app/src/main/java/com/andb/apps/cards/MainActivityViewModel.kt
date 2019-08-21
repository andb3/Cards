package com.andb.apps.cards

import android.os.Bundle
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

}

fun <T> listOfGeneric(size: Int, apply: (Int)->T): List<T>{
    val start = mutableListOf<T>()
    for (i in 0 until size){
        start.add(apply.invoke(i))
    }
    return start
}