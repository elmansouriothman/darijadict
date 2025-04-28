package com.example.darijadict.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.darijadict.data.*
import kotlinx.coroutines.launch
import androidx.lifecycle.AndroidViewModel
import com.example.darijadict.data.CsvLoader

enum class SearchCategory(val displayName: String) {
    ALL("All Categories"),
    DARIJA("Darija"),
    ENGLISH("English"),
    ARABIC("Arabic")
}

class EntryViewModel(application: Application) : AndroidViewModel(application) {
    fun getAll(): LiveData<List<Entry>> = dao.getAllEntriesSortedByWord()


    private val dao = DictionaryDatabase.getDatabase(application).entryDao()

    val allLists: LiveData<List<CustomList>> = dao.getAllLists()

    fun getEntriesInList(listId: Int): LiveData<List<Entry>> = dao.getEntriesInList(listId)

    fun getListsForEntry(entryId: Int): LiveData<List<CustomList>> = dao.getListsForEntry(entryId)

    fun getListName(listId: Int): LiveData<String> = dao.getListName(listId)

    fun toggleEntryInList(entryId: Int, listId: Int) {
        viewModelScope.launch {
            val isInList = dao.isEntryInList(entryId, listId) > 0
            if (isInList) {
                dao.removeEntryFromList(entryId, listId)
            } else {
                dao.addEntryToList(EntryListJoin(entryId, listId))
            }
        }
    }

    fun createList(name: String) {
        viewModelScope.launch {
            dao.createList(CustomList(name = name))
        }
    }

    private val _searchQuery = MutableLiveData("")
    private val _category = MutableLiveData(SearchCategory.ALL)

    val savedWords: LiveData<List<Entry>> = dao.getSavedEntries()

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCategory(category: SearchCategory) {
        _category.value = category
    }

    fun getEntryById(id: Int): LiveData<Entry> = dao.getEntryById(id)

    fun toggleSave(id: Int, isSaved: Boolean) {
        viewModelScope.launch {
            if (isSaved) dao.unsaveEntry(id) else dao.saveEntry(id)
        }
    }

    fun clearList(listId: Int) {
        viewModelScope.launch {
            dao.clearList(listId)
        }
    }

    fun deleteList(listId: Int) {
        viewModelScope.launch {
            dao.getListById(listId)?.let { dao.deleteList(it) }
        }
    }

    init {
        preloadFromCsv()
    }

    private fun preloadFromCsv() {
        viewModelScope.launch {
            if (dao.countEntries() == 0) {
                CsvLoader.loadEntriesFromAssets(getApplication()).forEach { dao.insert(it) }
            }
        }
    }

}
