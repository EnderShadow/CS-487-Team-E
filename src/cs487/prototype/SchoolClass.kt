package cs487.prototype

class SchoolClass(var name: String, val teacher: User, students: Set<User> = emptySet(), assignments: Set<Assignment> = emptySet())
{
    val students = mutableSetOf<User>()
    val assignments = mutableSetOf<Assignment>()
    
    init
    {
        this.students.addAll(students)
        this.assignments.addAll(assignments)
    }
    
    fun toSendableClass() = SendableClass(name, teacher.toSendableUser())
}

data class SendableClass(val name: String, val teacher: SendableUser)