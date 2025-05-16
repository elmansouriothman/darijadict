package com.example.darijadict.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.darijadict.data.*
import com.example.darijadict.model.GrammarSlide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

enum class SearchCategory(val displayName: String) {
    ALL("All Categories"),
    DARIJA("Darija"),
    ENGLISH("English"),
    ARABIC("Arabic")
}

@HiltViewModel
class EntryViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private val firestore = Firebase.firestore
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

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

    fun renameList(listId: Int, newName: String) {
        viewModelScope.launch {
            dao.renameList(listId, newName)
        }
    }

    fun getGrammarLessonSlides(lessonId: String): LiveData<List<GrammarSlide>> {
        val slidesLiveData = MutableLiveData<List<GrammarSlide>>()
        
        firestore.collection("grammar_lessons")
            .document(lessonId)
            .get()
            .addOnSuccessListener { document ->
                try {
                    val slides = document.get("slides") as? List<Map<String, Any>> ?: emptyList()
                    val parsedSlides = slides.mapNotNull { map ->
                        try {
                            GrammarSlide(
                                text = map["text"] as? String ?: "",
                                type = map["type"] as? String ?: "paragraph",
                                order = (map["order"] as? Long)?.toInt() ?: 0,
                                arabicScript = map["arabicScript"] as? String,
                                characterEquivalent = map["characterEquivalent"] as? String,
                                needsAudio = map["needsAudio"] as? Boolean ?: false
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }.sortedBy { it.order }
                    slidesLiveData.value = parsedSlides
                } catch (e: Exception) {
                    slidesLiveData.value = emptyList()
                }
            }
            .addOnFailureListener { 
                slidesLiveData.value = emptyList()
            }
        
        return slidesLiveData
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
