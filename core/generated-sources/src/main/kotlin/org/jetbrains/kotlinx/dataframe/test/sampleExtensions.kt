package samples

public fun String.stringExtension() {
    println("Hello, world!")
}

public fun List<Any?>.nullableAnyListExtension() {}

public fun List<Any>.anyListExtension() {}

public fun <T> List<T>.genericListExtension() {}

public fun <T> List<T?>.nullableGenericListExtension() {}

public fun <T : Any?> List<T>.boundGenericListExtension() {}

public fun <T : Any?> List<T?>.boundNullableGenericListExtension() {}
