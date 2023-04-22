package com.example.kotlintodo2.utils

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlintodo2.databinding.EachTodoItemBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.kotlintodo2.ui.AddTodoPopupFragment.Companion.TAG
import com.google.firebase.firestore.FirebaseFirestore

class TodoAdapter(private val list: MutableList<ToDoData>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    private var listener: ToDoAdapterClicksInterface? = null
    private var checked: Boolean = false
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val checkboxStates = mutableMapOf<Int, Boolean>()


    init {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    inner class TodoViewHolder(val binding: EachTodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

    fun setListener(listener: ToDoAdapterClicksInterface) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding =
            EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.todoTask.text = this.task
                binding.todoTime.text = this.time
                binding.todoDate.text = this.date
                binding.checkbox.isChecked=this.completed

                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(this)
                }

                binding.editTask.setOnClickListener {
                    listener?.onEditTaskBtnClicked(this)
                }
                val documentRef = db.collection("users").document(auth.currentUser?.uid.toString())
                    .collection("tasks").document(list[position].taskid)

                documentRef.get().addOnSuccessListener { document ->
                    if (document != null) {
                        val completed = document.getBoolean("completed") ?: false
                        if (completed) {
                            binding.todoTask.setTextColor(Color.GRAY)
                            binding.todoDate.setTextColor(Color.GRAY)
                            binding.todoTime.setTextColor(Color.GRAY)
                            binding.todoTask.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

                            binding.checkbox.isChecked=true
                        } else {
                            binding.todoTask.setTextColor(Color.BLACK)
                            binding.todoDate.setTextColor(Color.BLACK)
                            binding.todoTime.setTextColor(Color.BLACK)
                            binding.todoTask.paintFlags = binding.todoTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        }
                    } else {
                        // handle document not found case
                    }
                }.addOnFailureListener { exception ->
                    // handle exception
                }
                binding.checkbox.setOnClickListener {
                    val isChecked = binding.checkbox.isChecked
                    onCheckBoxClicked(position, isChecked)
                }


            }
        }
    }
    fun onCheckBoxClicked(position: Int, isChecked: Boolean) {
        val todo = list[position]
        val db = FirebaseFirestore.getInstance()
        val taskRef = db.collection("users").document(auth.currentUser?.uid.toString())
            .collection("tasks").document(todo.taskid)

        val updates = hashMapOf<String, Any>(
            "completed" to isChecked
        )

        taskRef.update(updates)
            .addOnSuccessListener {
                todo.completed = isChecked
                checkboxStates[position] = isChecked
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }


    interface ToDoAdapterClicksInterface {
        fun onDeleteTaskBtnClicked(toDoData: ToDoData)
        fun onEditTaskBtnClicked(toDoData: ToDoData)
    }
}