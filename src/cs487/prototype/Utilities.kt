package cs487.prototype

import com.google.gson.GsonBuilder
import java.security.SecureRandom

private val random = SecureRandom()
val gson = GsonBuilder().setPrettyPrinting().create()

fun getRandomByteArray(size: Int): ByteArray
{
    val array = ByteArray(size)
    random.nextBytes(array)
    return array
}

private const val hexCharacters = "0123456789ABCDEF"

/**
 * converts a ByteArray into a big endian hex string
 */
fun ByteArray.toHexString(): String
{
    val sb = StringBuilder(size * 2)
    for(i in this.indices.reversed())
    {
        sb.append(hexCharacters[this[i].toInt().ushr(4).and(0xF)])
        sb.append(hexCharacters[this[i].toInt().and(0xF)])
    }
    return sb.toString()
}

/**
 * converts a big endian hex string into a byte array
 */
fun fromHexString(hexString: String): ByteArray
{
    if(!hexString.all {hexCharacters.contains(it, true)})
        throw IllegalArgumentException("Unable to parse hex string: $hexString")
    @Suppress("NAME_SHADOWING")
    var hexString = hexString
    if(hexString.length and 1 == 1)
        hexString = "0$hexString"
    
    val array = ByteArray(hexString.length / 2)
    val iterator = hexString.iterator()
    for(i in array.indices.reversed())
        array[i] = (toNibble(iterator.nextChar()).shl(4) + toNibble(iterator.nextChar())).toByte()
    return array
}


private fun toNibble(character: Char): Int
{
    return when(character.toUpperCase())
    {
        in '0'..'9' -> character - '0'
        in 'A'..'F' -> character - 'A' + 10
        else -> throw IllegalArgumentException("Cannot turn non-hex character into a nibble")
    }
}