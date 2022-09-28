package com.orelzman.mymessages.data.local.dao

import androidx.room.*
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import java.util.*

@Dao
interface PhoneCallDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(phoneCall: PhoneCall)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(phoneCalls: List<PhoneCall>)

    @Update
    fun update(phoneCall: PhoneCall)

    @Query(
        """
        UPDATE PhoneCall
        SET uploadState = :uploadState
        WHERE id = :phoneCallId
    """
    )
    fun updateUploadState(phoneCallId: String, uploadState: String)

    @Delete
    fun delete(phoneCall: List<PhoneCall>)

    @Query(
        """
        SELECT *
        FROM PhoneCall
    """
    )
    fun getAll(): List<PhoneCall>

    @Query(
        """
        SELECT *
        FROM PhoneCall
        WHERE startDate > :fromDate
    """
    )
    fun getAllFromDate(fromDate: Date): List<PhoneCall>


    @Query(
        """
        SELECT * 
        FROM PhoneCall
        WHERE startDate == :startDate
    """
    )
    fun getByStartDate(startDate: Date): PhoneCall?

    @Query(
        """
        SELECT *
        FROM PhoneCall
        WHERE id = :id
    """
    )
    fun get(id: String): PhoneCall?

    @Query(
        """
        DELETE FROM PhoneCall
    """
    )
    fun clear()

    @Delete
    fun remove(phoneCalls: List<PhoneCall>)
}