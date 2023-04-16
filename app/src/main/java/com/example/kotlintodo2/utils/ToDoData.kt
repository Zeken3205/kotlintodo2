package com.example.kotlintodo2.utils

import java.util.Date
//task id and task value from firebase
data class ToDoData(
    val taskid:String,
    var task:String,
    var date:String,
    var time:String
)



