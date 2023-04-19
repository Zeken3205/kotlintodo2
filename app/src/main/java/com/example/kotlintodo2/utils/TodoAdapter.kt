package com.example.kotlintodo2.utils

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlintodo2.databinding.EachTodoItemBinding

class TodoAdapter(private val list:MutableList<ToDoData>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){
    inner class TodoViewHolder(val binding: EachTodoItemBinding): RecyclerView.ViewHolder(binding.root){


    }
    private var listener:ToDoAdapterClicksInterface?=null
    private var checked:Boolean=false
    fun setListener(listener:ToDoAdapterClicksInterface){
        this.listener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding=EachTodoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TodoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.todoTask.text=this.task
                binding.todoTime.text=this.time
                binding.todoDate.text=this.date
                binding.checkbox.isChecked=this.completed
                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(this)
                }

                binding.editTask.setOnClickListener {
                    listener?.onEditTaskBtnClicked(this)
                }
                binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (binding.checkbox.isChecked) {
                        binding.todoTask.setTextColor(Color.GRAY)
                        checked = true
                    } else {
                        binding.todoTask.setTextColor(Color.BLACK)
                        checked = false
                    }
                }
            }
        }
    }
    interface ToDoAdapterClicksInterface{
        fun onDeleteTaskBtnClicked(toDoData: ToDoData)
        fun onEditTaskBtnClicked(toDoData: ToDoData)
    }
}