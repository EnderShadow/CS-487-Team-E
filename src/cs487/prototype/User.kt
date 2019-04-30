package cs487.prototype

import java.security.MessageDigest

private const val hashAlgorithm = "SHA-256"
private val hashLength = MessageDigest.getInstance(hashAlgorithm).digestLength

class User(var name: String, val email: String, private val passwordSalt: ByteArray, var passwordHash: ByteArray, var phoneNumber: String)
{
    val teachesClasses = mutableListOf<SchoolClass>()
    val takesClasses = mutableListOf<SchoolClass>()
    
    constructor(name: String, email: String, password: String, phoneNumber: String): this(name, email, getRandomByteArray(hashLength), ByteArray(0), phoneNumber)
    {
        val md = MessageDigest.getInstance(hashAlgorithm)
        md.update(password.toByteArray())
        passwordHash = md.digest(passwordSalt)
    }
    
    fun changePassword(oldPassword: String, newPassword: String): Boolean
    {
        if(!hashPassword(oldPassword).contentEquals(passwordHash))
            return false
        passwordHash = hashPassword(newPassword)
        return true
    }
    
    fun passwordMatches(password: String): Boolean
    {
        return hashPassword(password).contentEquals(passwordHash)
    }
    
    fun registerForClass(clazz: SchoolClass)
    {
        clazz.students.add(this)
        takesClasses.add(clazz)
    }
    
    fun dropClass(clazz: SchoolClass)
    {
        clazz.students.remove(this)
        takesClasses.remove(clazz)
    }
    
    fun createClass(name: String): Boolean
    {
        if(Database.classes.any {it.name == name})
            return false
        
        val clazz = SchoolClass(name, this)
        Database.classes.add(clazz)
        teachesClasses.add(clazz)
        return true
    }
    
    fun deleteClass(clazz: SchoolClass): Boolean
    {
        if(clazz.teacher != this)
            return false
        
        clazz.students.toList().forEach {it.dropClass(clazz)}
        teachesClasses.remove(clazz)
        Database.classes.remove(clazz)
        return true
    }
    
    private fun hashPassword(password: String): ByteArray
    {
        val md = MessageDigest.getInstance(hashAlgorithm)
        md.update(password.toByteArray())
        return md.digest(passwordSalt)
    }
    
    fun toSendableUser() = SendableUser(name, email, phoneNumber)
    
    override fun equals(other: Any?) = other is User && other.email == email
    override fun hashCode() = email.hashCode()
}

data class SendableUser(val name: String, val email: String, val phoneNumber: String)