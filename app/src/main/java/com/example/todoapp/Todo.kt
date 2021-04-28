package com.example.todoapp

import com.google.firebase.firestore.DocumentId
import java.util.*

data class Todo(var text: String, var date: Date?, var user_id: String) {
    @DocumentId
    var id: String = ""

    constructor() : this("", null, "")
}
