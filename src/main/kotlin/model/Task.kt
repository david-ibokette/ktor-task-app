package net.ibokette.model

enum class Priority {
    Low, Medium, High, Vital
}
data class Task(
    val name: String,
    val description: String,
    val priority: Priority,
    val isCompleted: Boolean = false,
    val id: Int? = null,
)

fun Task.taskAsRow() = """
    <tr>
        <td>$name</td><td>$description</td><td>$priority</td><td>Completed: $isCompleted</td>
    </tr>
    """.trimIndent()

fun List<Task>.tasksAsTable() = this.joinToString(
    prefix = "<table rules=\"all\">",
    postfix = "</table>",
    separator = "\n",
    transform = Task::taskAsRow
)

fun List<Task>.tasksAsMap() = this.associateBy { it.name }
