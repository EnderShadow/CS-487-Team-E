package cs487.prototype

object ByteArrayToStringTest: Test
{
    override val name = "ByteArray String Conversion Test"
    
    override fun test(): Boolean
    {
        repeat(32) {
            val array = getRandomByteArray(16)
            val text = array.toHexString()
            val newArray = fromHexString(text)
            if(!array.contentEquals(newArray))
                return false
        }
        return true
    }
}