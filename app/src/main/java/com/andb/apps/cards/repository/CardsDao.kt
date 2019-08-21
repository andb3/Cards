package com.andb.apps.cards.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import com.andb.apps.cards.objects.BaseCard

@Dao
interface CardsDao {

    @Insert
    fun insertCard(card: BaseCard)

    @Update
    fun updateCard(card: BaseCard)

    @Delete
    fun deleteCard(card: BaseCard)

    @Query("SELECT * FROM BaseCard")
    fun getCards(): LiveData<List<BaseCard>>

}