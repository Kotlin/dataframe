package foo.bar

fun box(): String {
    val result = MyClass().foo()
    return if (result == "Hello world") { "OK" } else { "Fail: $result" }
}
