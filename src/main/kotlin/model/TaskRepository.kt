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
            return@transaction TaskTable.select(TaskTable.id, TaskTable.isCompleted, TaskTable.name,
                TaskTable.description, TaskTable.priority)
//                .groupBy(TaskEntity.isCompleted)
                .map {
                    return@map Task(
                        it[TaskTable.name],
                        it[TaskTable.description],
                        it[TaskTable.priority],
                        it[TaskTable.isCompleted],
                        it[TaskTable.id],
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

            return@transaction TaskTable.selectAll().where { TaskTable.priority eq priority }
                .map {
                    return@map Task(
                        it[TaskTable.name],
                        it[TaskTable.description],
                        it[TaskTable.priority],
                        it[TaskTable.isCompleted]
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

            return@transaction TaskTable.selectAll().where { TaskTable.name eq name }
                .map {
                    return@map Task(
                        it[TaskTable.name],
                        it[TaskTable.description],
                        it[TaskTable.priority],
                        it[TaskTable.isCompleted]
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
            val taskId = TaskTable.insert {
                it[name] = task.name
                it[description] = task.description
                it[priority] = task.priority
                it[isCompleted] = task.isCompleted
            } get TaskTable.id

            println("Created new task with ids $taskId.")
        }

    }
}
