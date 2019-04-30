package cs487.prototype

import com.google.gson.reflect.TypeToken
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object Database
{
    val classes = mutableSetOf<SchoolClass>()
    val users = mutableSetOf<User>()
    
    fun loadDB(dbFile: File)
    {
        if(!dbFile.exists())
        {
            println("Database does not exist, ignoring call to loadDB")
            return
        }
        if(!dbFile.isFile)
            throw IllegalArgumentException("The database file is not a file")

        val database: Pair<Set<User>, Set<SchoolClass>> = gson.fromJson(dbFile.readText(), object: TypeToken<Pair<Set<User>, Set<SchoolClass>>>() {}.type)
        
        // load users
        users.addAll(database.first)
        
        // load classes (MUST be done after loading users)
        classes.addAll(database.second)
        
        // update each user with the classes they are taking/teaching
        for(clazz in classes)
        {
            clazz.teacher.teachesClasses.add(clazz)
            for(student in clazz.students)
                student.takesClasses.add(clazz)
        }
    }
    
    fun saveDB(dbFile: File)
    {
        val data = gson.toJson(Pair(users, classes))
        
        val newDBFile = File(dbFile.parent, "${dbFile.name}.new")
        
        // prevents losing the database due to a crash
        Files.write(newDBFile.toPath(), data.toByteArray())
        Files.move(newDBFile.toPath(), dbFile.toPath(), StandardCopyOption.ATOMIC_MOVE)
    }
}