package net.ibokette.model

import net.ibokette.model.TaskEntity.description
import net.ibokette.model.TaskEntity.isCompleted
import net.ibokette.model.TaskEntity.name
import net.ibokette.model.TaskEntity.priority
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

const val MAX_VARCHAR_LENGTH = 128

object TaskEntity : Table("task") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val description = varchar("description", MAX_VARCHAR_LENGTH)
//    val priority = enumeration<Priority>("priority")
    val priority: Column<Priority> = customEnumeration(
        name = "priority", // Name of the column in the database
        fromDb = { value -> Priority.valueOf(value as String) }, // Converts database value to enum
        toDb = { it.name } // Converts enum to database value (its name)
    )

    val isCompleted = bool("completed").default(false)
}

fun TaskEntity.taskAsRow() = """
    <tr>
        <td>$name</td><td>$description</td><td>$priority</td><td>Completed: $isCompleted</td>
    </tr>
    """.trimIndent()

fun taskEntityAsRow(taskEntity: TaskEntity) = taskEntity.taskAsRow()

fun List<TaskEntity>.tasksAsTable() = this.joinToString(
    prefix = "<table rules=\"all\">",
    postfix = "</table>",
    separator = "\n",
//    transform = TaskEntity::taskAsRow
    transform = ::taskEntityAsRow
)
