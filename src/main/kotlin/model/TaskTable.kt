package net.ibokette.model

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

const val MAX_VARCHAR_LENGTH = 128

object TaskTable : Table("task") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val description = varchar("description", MAX_VARCHAR_LENGTH)
    val priority: Column<Priority> = customEnumeration(
        name = "priority", // Name of the column in the database
        fromDb = { value -> Priority.valueOf(value as String) }, // Converts database value to enum
    toDb = { value -> PGEnum("priority", value) } // send PG enum, not varchar
)

    val isCompleted = bool("completed").default(false)
}

fun TaskTable.taskAsRow() = """
    <tr>
        <td>$name</td><td>$description</td><td>$priority</td><td>Completed: $isCompleted</td>
    </tr>
    """.trimIndent()

fun taskEntityAsRow(taskEntity: TaskTable) = taskEntity.taskAsRow()

fun List<TaskTable>.tasksAsTable() = this.joinToString(
    prefix = "<table rules=\"all\">",
    postfix = "</table>",
    separator = "\n",
//    transform = TaskEntity::taskAsRow
    transform = ::taskEntityAsRow
)
