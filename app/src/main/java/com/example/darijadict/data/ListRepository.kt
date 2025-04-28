package com.example.darijadict.data

import androidx.lifecycle.LiveData

class ListRepository(private val entryDao: EntryDao) {
    suspend fun createList(name: String) {
        entryDao.createList(CustomList(name = name))
    }

    fun getAllLists(): LiveData<List<CustomList>> = entryDao.getAllLists()

    suspend fun deleteList(list: CustomList) {
        entryDao.deleteList(list)
    }

    suspend fun addEntryToList(entryId: Int, listId: Int) {
        entryDao.addEntryToList(EntryListJoin(entryId, listId))
    }

    suspend fun removeEntryFromList(entryId: Int, listId: Int) {
        entryDao.removeEntryFromList(entryId, listId)
    }

    fun getEntriesInList(listId: Int): LiveData<List<Entry>> {
        return entryDao.getEntriesInList(listId)
    }

    fun getListsForEntry(entryId: Int): LiveData<List<CustomList>> {
        return entryDao.getListsForEntry(entryId)
    }
}