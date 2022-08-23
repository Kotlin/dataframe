import org.junit.jupiter.api.Test

class Add {
    @Test
    fun generateDfFunctionTestStub() {
        val schemaName = "Add0"
        val expression = "dataFrameOf(\"a\")(1)"
        val modify = "add(\"\") { 42 }"
        val id = "add0"
        generateDfFunctionTestStub(expression, schemaName, modify, id, "add.kt")
    }
}
