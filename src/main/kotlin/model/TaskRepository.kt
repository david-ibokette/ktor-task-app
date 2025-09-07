package net.ibokette.model

import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
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

    fun tasksByPriorityFromDB(priority: Priority): List<Task> {
        return transaction {
            addLogger(StdOutSqlLogger)

            return@transaction TaskEntity.selectAll().where { TaskEntity.priority eq priority }
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


    fun taskByName(name: String) = tasks.find {
        it.name.equals(name, ignoreCase = true)
    }

    fun taskByNameFromDB(name: String) = tasks.find {
        return transaction {
            addLogger(StdOutSqlLogger)

            return@transaction TaskEntity.selectAll().where { TaskEntity.name eq name }
                .map {
                    return@map Task(
                        it[TaskEntity.name],
                        it[TaskEntity.description],
                        it[TaskEntity.priority],
                        it[TaskEntity.isCompleted]
                    )
                }
            .firstOrNull()
        }
    }

    fun addTask(task: Task) {
        if(taskByName(task.name) != null) {
            throw IllegalStateException("Cannot duplicate task names!")
        }
        tasks.add(task)
    }

    fun addTaskToDB(task: Task) {
        if(taskByNameFromDB(task.name) != null) {
            throw IllegalStateException("Cannot duplicate task names!")
        }

        transaction {
            val taskId = TaskEntity.insert {
                it[name] = task.name
                it[description] = task.description
                it[priority] = task.priority
                it[isCompleted] = task.isCompleted
            } get TaskEntity.id

            println("Created new task with ids $taskId.")
        }

    }
}
