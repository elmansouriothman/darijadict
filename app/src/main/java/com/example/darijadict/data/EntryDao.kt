package com.example.darijadict.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    @Transaction
    @Query("SELECT * FROM entries")
    fun getAllEntries(): Flow<List<Entry>>

    // ✅ Home screen entries sorted alphabetically by word
    @Query("SELECT * FROM entries ORDER BY word COLLATE NOCASE ASC")
    fun getAllEntriesSortedByWord(): LiveData<List<Entry>>

    // ✅ Entry Basic
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Entry)

    @Update
    suspend fun update(entry: Entry)

    @Query("SELECT * FROM entries WHERE id = :id")
    fun getEntryById(id: Int): LiveData<Entry>

    @Query("SELECT * FROM entries")
    suspend fun getRawList(): List<Entry>

    @Query("SELECT COUNT(*) FROM entries")
    suspend fun countEntries(): Int

    @Query("DELETE FROM entries WHERE word = :word")
    suspend fun deleteByWord(word: String)

    // ✅ Saved entries
    @Query("SELECT * FROM entries WHERE saved = 1")
    fun getSavedEntries(): LiveData<List<Entry>>

    @Query("UPDATE entries SET saved = 1 WHERE id = :id")
    suspend fun saveEntry(id: Int)

    @Query("UPDATE entries SET saved = 0 WHERE id = :id")
    suspend fun unsaveEntry(id: Int)

    // ✅ Search (fallback / optional)
    @Query("SELECT * FROM entries WHERE word LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%'")
    fun search(query: String): LiveData<List<Entry>>

    // ✅ Check Entry presence in list
    @Query("SELECT COUNT(*) FROM entry_list_join WHERE entryId = :entryId AND listId = :listId")
    suspend fun isEntryInList(entryId: Int, listId: Int): Int

    // ✅ Custom List management
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createList(customList: CustomList)

    @Query("SELECT * FROM custom_lists ORDER BY name ASC")
    fun getAllLists(): LiveData<List<CustomList>>

    @Query("SELECT name FROM custom_lists WHERE id = :id")
    fun getListName(id: Int): LiveData<String>

    @Query("SELECT * FROM custom_lists WHERE id = :listId")
    suspend fun getListById(listId: Int): CustomList?

    @Delete
    suspend fun deleteList(customList: CustomList)

    @Query("DELETE FROM custom_lists WHERE id = :listId")
    suspend fun deleteListById(listId: Int)

    @Query("UPDATE custom_lists SET name = :newName WHERE id = :listId")
    suspend fun renameList(listId: Int, newName: String)

    // ✅ Entry <-> List Join
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addEntryToList(entryListJoin: EntryListJoin)

    @Query("DELETE FROM entry_list_join WHERE entryId = :entryId AND listId = :listId")
    suspend fun removeEntryFromList(entryId: Int, listId: Int)

    @Query("DELETE FROM entry_list_join WHERE listId = :listId")
    suspend fun clearList(listId: Int)

    @Query("""
        SELECT entries.* FROM entries
        INNER JOIN entry_list_join ON entries.id = entry_list_join.entryId
        WHERE entry_list_join.listId = :listId
    """)
    fun getEntriesInList(listId: Int): LiveData<List<Entry>>

    @Query("""
        SELECT custom_lists.* FROM custom_lists
        INNER JOIN entry_list_join ON custom_lists.id = entry_list_join.listId
        WHERE entry_list_join.entryId = :entryId
    """)
    fun getListsForEntry(entryId: Int): LiveData<List<CustomList>>
}
