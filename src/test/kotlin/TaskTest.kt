import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.testApplication
import net.ibokette.module
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class TaskTest {

    @Test
    fun testNewEndpoint() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("html", response.contentType()?.contentSubtype)
        assertContains(response.bodyAsText(), "gardening")
    }

    @Test
    fun `test by priority`() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks/byPriority/Medium")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("html", response.contentType()?.contentSubtype)
        assertContains(response.bodyAsText(), "gardening")
    }

    @Test
    fun invalidPriorityProduces400() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks/byPriority/Invalid")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun unusedPriorityProduces404() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks/byPriority/Vital")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `test ui`() = testApplication {
        application {
            module()
        }

        val response = client.get("/task-ui/task-form.html")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("html", response.contentType()?.contentSubtype)
        assertContains(response.bodyAsText(), "Adding a new task")
    }

    @Test
    fun newTasksCanBeAdded() = testApplication {
        application {
            module()
        }

        val response1 = client.post("/tasks") {
            header(
                HttpHeaders.ContentType,
                ContentType.Application.FormUrlEncoded.toString()
            )
            setBody(
                listOf(
                    "name" to "swimming",
                    "description" to "Go to the beach",
                    "priority" to "Low"
                ).formUrlEncode()
            )
        }

        assertEquals(HttpStatusCode.NoContent, response1.status)

        val response2 = client.get("/tasks")
        assertEquals(HttpStatusCode.OK, response2.status)
        val body = response2.bodyAsText()

        assertContains(body, "swimming")
        assertContains(body, "Go to the beach")
    }
}