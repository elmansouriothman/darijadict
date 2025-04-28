package com.example.darijadict.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "entry_list_join",
    primaryKeys = ["entryId", "listId"],
    foreignKeys = [
        ForeignKey(
            entity = Entry::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CustomList::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EntryListJoin(
    val entryId: Int,
    val listId: Int
)