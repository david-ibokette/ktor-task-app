package net.ibokette.model

import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object TaskRepository {
    private val tasks = mutableListOf(
        Task("cleaning", "Clean the house", Priority.Low),
        Task("gardening", "Mow the lawn", Priority.Medium),
        Task("shopping", "Buy the groceries", Priority.High),
        Task("painting", "Paint the fence", Priority.Medium)
    )

    fun allTasks(): List<Task> = tasks

    fun allTasksFromDB(): List<Task> {
        return transaction {
            return@transaction TaskEntity.select(TaskEntity.id, TaskEntity.isCompleted, TaskEntity.name,
                TaskEntity.description, TaskEntity.priority)
//                .groupBy(TaskEntity.isCompleted)
                .map {
                    return@map Task(
                        it[TaskEntity.name],
                        it[TaskEntity.description],
                        it[TaskEntity.priority],
                        it[TaskEntity.isCompleted]
                    )
                }
        }
    }

    fun tasksByPriority(priority: Priority) = tasks.filter {
        it.priority == priority
    }

    fun taskByName(name: String) = tasks.find {
        it.name.equals(name, ignoreCase = true)
    }

    fun addTask(task: Task) {
        if(taskByName(task.name) != null) {
            throw IllegalStateException("Cannot duplicate task names!")
        }
        tasks.add(task)
    }
}
