package cs487.prototype

fun main(args: Array<String>)
{
    println("Running tests\n")
    for(test in Tester.tests)
    {
        println("Running ${test.name}")
        if(!test.test())
            println("Failed ${test.name}")
        else
            println("Passed ${test.name}")
        println()
    }
    println("Finished running tests")
}

object Tester
{
    // Add tests to run to this list
    val tests = mutableListOf(ByteArrayToStringTest)
}

interface Test
{
    val name: String
    
    fun test(): Boolean
}