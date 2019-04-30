import cs487.prototype.Database
import cs487.prototype.SendableClass
import cs487.prototype.User
import cs487.prototype.gson
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.receive
import io.ktor.request.receiveParameters
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

fun main(args: Array<String>)
{
    val databaseFile = File("database.db")
    Database.loadDB(databaseFile)
    if(Database.users.isEmpty())
        Database.users.add(User("Bob", "bob@email.com", "password", ""))
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("This is a temporary website for a school project, you won't find anything interesting here.", ContentType.Text.Plain)
            }
            post("/createUser") {
                val params = call.receiveParameters()
                val name = params["name"]
                val email = params["email"]
                val password = params["password"]
                val phoneNumber = params["phoneNumber"] ?: ""
                if(name == null || email == null || password == null || name.contains('"') || email.contains('"') || phoneNumber.contains('"'))
                {
                    call.respondText("{status: \"failed\"}")
                    return@post
                }
                val user = User(name, email, password, phoneNumber)
                if(user !in Database.users)
                {
                    Database.users.add(user)
                    call.respondText("{status: \"success\"}")
                }
                else
                {
                    call.respondText("{status: \"failed\"}")
                }
            }
            post("/login") {
                val params = call.receiveParameters()
                val email = params["email"]
                val password = params["password"]
                if(email == null || password == null)
                {
                    call.respondText("{status: \"failed\"}")
                    return@post
                }
                val user = Database.users.firstOrNull {it.email == email}
                if(user != null && user.passwordMatches(password))
                {
                    call.respondText(gson.toJson(user.toSendableUser()))
                }
                else
                {
                    call.respondText("{status: \"failed\"}")
                }
            }
            post("/getClasses") {
                val email = call.receiveParameters()["email"]
                val user = Database.users.firstOrNull {it.email == email}
                if(email == null || user == null)
                {
                    call.respondText("{status: \"failed\"}")
                    return@post
                }
                call.respondText(gson.toJson(GetClassesReturn(user.teachesClasses.map {it.name}, user.takesClasses.map {it.toSendableClass()})))
            }
        }
    }
    server.start()
    val scanner = Scanner(System.`in`)
    while(true)
    {
        val line = scanner.nextLine()
        if(line.equals("quit", true))
        {
            server.stop(0, 1, TimeUnit.SECONDS)
            Database.saveDB(databaseFile)
            break
        }
    }
}

private data class GetClassesReturn(val teaches: List<String>, val takes: List<SendableClass>)