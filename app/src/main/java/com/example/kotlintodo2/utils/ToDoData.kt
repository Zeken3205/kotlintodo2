package com.example.kotlintodo2.utils

import com.google.firebase.Timestamp

//task id and task value from firebase
data class ToDoData(
    val taskid:String,
    var task:String,
    var date:String,
    var time:String,
    var completed:Boolean=false

)
