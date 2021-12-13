package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

internal inline fun <reified T> KClass<*>.createTypeUsing() = typeOf<T>().projectTo(this)

internal fun KType.projectTo(targetClass: KClass<*>): KType {
    val currentClass = classifier as? KClass<*>
    return when {
        targetClass.typeParameters.isEmpty() || currentClass == null -> targetClass.createStarProjectedType(
            isMarkedNullable
        )
        currentClass == targetClass -> this
        targetClass.isSubclassOf(currentClass) -> projectDownTo(targetClass)
        targetClass.isSuperclassOf(currentClass) -> projectUpTo(targetClass)
        else -> targetClass.createStarProjectedType(isMarkedNullable)
    }
}

internal fun KType.projectUpTo(superClass: KClass<*>): KType {
    val chain = inheritanceChain(jvmErasure, superClass)
    var current = this
    chain.forEach { (clazz, declaredBaseType) ->
        val substitution = clazz.typeParameters.zip(current.arguments.map { it.type }).toMap()
        current = declaredBaseType.replace(substitution)
    }
    return current.withNullability(isMarkedNullable)
}

internal fun KType.replaceTypeParameters(): KType {
    var replaced = false
    val arguments = arguments.map {
        val type = it.type
        val newType = when {
            type == null -> typeOf<Any?>()
            type.classifier is KTypeParameter -> {
                replaced = true
                (type.classifier as KTypeParameter).upperBounds.firstOrNull() ?: typeOf<Any?>()
            }
            else -> type
        }
        KTypeProjection.invariant(newType)
    }
    return if (replaced) jvmErasure.createType(arguments, isMarkedNullable)
    else this
}

internal fun inheritanceChain(subClass: KClass<*>, superClass: KClass<*>): List<Pair<KClass<*>, KType>> {
    val inheritanceChain = mutableListOf<Pair<KClass<*>, KType>>()
    var current = subClass
    while (current != superClass) {
        val baseType = current.supertypes.first { (it.classifier as? KClass<*>)?.isSubclassOf(superClass) ?: false }
        inheritanceChain.add(current to baseType)
        current = baseType.classifier as KClass<*>
    }
    return inheritanceChain
}

internal fun KType.projectDownTo(subClass: KClass<*>): KType {
    val chain = inheritanceChain(subClass, classifier as KClass<*>)
    var type = this

    chain.reversed().forEach { (clazz, declaredBaseType) ->
        val substitution = resolve(type, declaredBaseType)
        val projection = clazz.typeParameters.map {
            substitution[it]?.let { KTypeProjection.invariant(it) } ?: KTypeProjection.STAR
        }
        type = clazz.createType(projection, isMarkedNullable)
    }
    return type
}

internal fun KType.replace(substitution: Map<KTypeParameter, KType?>): KType {
    return when (val clazz = classifier) {
        is KTypeParameter -> substitution[clazz] ?: this
        is KClass<*> -> clazz.createType(arguments.map { KTypeProjection(it.variance, it.type?.replace(substitution)) })
        else -> this
    }
}

internal fun resolve(actualType: KType, declaredType: KType): Map<KTypeParameter, KType?> {
    val map = mutableMapOf<KTypeParameter, KType?>()

    fun resolveRec(actualType: KType, declaredType: KType) {
        require(actualType.classifier == declaredType.classifier)
        actualType.arguments.zip(declaredType.arguments).forEach { (actualArgument, declaredArgument) ->
            val declared = declaredArgument.type
            val actual = actualArgument.type
            if (declared?.classifier is KTypeParameter) {
                val parameter = declared.classifier as KTypeParameter
                val currentResolution = map[parameter]
                if (currentResolution == null || (actual != null && actual.isSubtypeOf(currentResolution))) {
                    map[parameter] = actual
                }
            } else if (declared != null && actual != null) {
                val declaredClass = declared.classifier as? KClass<*>
                val actualClass = actual.classifier as? KClass<*>
                if (declaredClass != null && actualClass != null && declaredClass.isSubclassOf(actualClass)) {
                    val projected = actual.projectTo(declaredClass)
                    resolveRec(projected, declared)
                }
            }
        }
    }
    resolveRec(actualType, declaredType)
    return map
}

internal val numberTypeExtensions: Map<Pair<KClass<*>, KClass<*>>, KClass<*>> by lazy {
    val map = mutableMapOf<Pair<KClass<*>, KClass<*>>, KClass<*>>()
    fun add(from: KClass<*>, to: KClass<*>) {
        map[from to to] = to
        map[to to from] = to
    }
    val intTypes = listOf(Byte::class, Short::class, Int::class, Long::class)
    for (i in intTypes.indices) {
        for (j in i + 1 until intTypes.size)
            add(intTypes[i], intTypes[j])
        add(intTypes[i], Double::class)
    }
    add(Float::class, Double::class)
    map
}

internal fun getCommonNumberType(first: KClass<*>?, second: KClass<*>): KClass<*> =
    when {
        first == null -> second
        first == second -> first
        else -> numberTypeExtensions[first to second] ?: error("Can not find common number type for $first and $second")
    }

internal fun Iterable<KClass<*>>.commonNumberClass(): KClass<*> = fold(null as KClass<*>?, ::getCommonNumberType) ?: Number::class
internal fun commonParent(classes: Iterable<KClass<*>>): KClass<*>? = commonParents(classes).withMostSuperclasses()
internal fun commonParent(vararg classes: KClass<*>): KClass<*>? = commonParent(classes.toList())
internal fun Iterable<KClass<*>>.withMostSuperclasses(): KClass<*>? = maxByOrNull { it.allSuperclasses.size }
internal fun Iterable<KClass<*>>.createType(nullable: Boolean, upperBound: KType? = null): KType =
    if (upperBound == null) (withMostSuperclasses() ?: Any::class).createStarProjectedType(nullable)
    else {
        val upperClass = upperBound.classifier as KClass<*>
        val baseClass = filter { it.isSubclassOf(upperClass) }.withMostSuperclasses() ?: withMostSuperclasses()
        if (baseClass == null) upperBound.withNullability(nullable)
        else upperBound.projectTo(baseClass).withNullability(nullable)
    }

internal fun commonParents(vararg classes: KClass<*>): List<KClass<*>> = commonParents(classes.toList())
internal fun commonParents(classes: Iterable<KClass<*>>): List<KClass<*>> =
    when {
        !classes.any() -> emptyList()
        else -> {
            classes.distinct().let {
                when {
                    it.size == 1 && it[0].visibility == KVisibility.PUBLIC -> { // if there is only one class - return it
                        listOf(it[0])
                    }
                    else -> it.fold(null as (Set<KClass<*>>?)) { set, clazz ->
                        // collect a set of all common superclasses from original classes
                        val superclasses =
                            (clazz.allSuperclasses + clazz).filter { it.visibility == KVisibility.PUBLIC }
                        set?.intersect(superclasses) ?: superclasses.toSet()
                    }!!.let {
                        it - it.flatMap { it.superclasses } // leave only 'leaf' classes, that are not super to some other class in a set
                    }.toList()
                }
            }
        }
    }

internal fun baseType(types: Set<KType>): KType {
    val nullable = types.any { it.isMarkedNullable }
    return when (types.size) {
        0 -> typeOf<Unit>()
        1 -> types.single()
        else -> {
            val classes = types.map { it.jvmErasure }.distinct()
            when {
                classes.size == 1 -> {
                    val typeProjections = classes[0].typeParameters.mapIndexed { index, parameter ->
                        val arguments = types.map { it.arguments[index].type }.toSet()
                        if (arguments.contains(null)) KTypeProjection.STAR
                        else {
                            val type = baseType(arguments as Set<KType>)
                            KTypeProjection(parameter.variance, type)
                        }
                    }
                    classes[0].createType(typeProjections, nullable)
                }
                classes.any { it == List::class } && classes.all { it == List::class || !it.isSubclassOf(Collection::class) } -> {
                    val listTypes =
                        types.map { if (it.classifier == List::class) it.arguments[0].type else it }.toMutableSet()
                    if (listTypes.contains(null)) List::class.createStarProjectedType(nullable)
                    else {
                        val type = baseType(listTypes as Set<KType>)
                        List::class.createType(listOf(KTypeProjection.invariant(type)), nullable)
                    }
                }
                else -> {
                    val commonClass = commonParent(classes) ?: Any::class
                    commonClass.createStarProjectedType(nullable)
                }
            }
        }
    }
}

internal fun KClass<*>.createTypeWithArgument(argument: KType? = null, nullable: Boolean = false): KType {
    require(typeParameters.size == 1)
    return if (argument != null) createType(listOf(KTypeProjection.invariant(argument)), nullable)
    else createStarProjectedType(nullable)
}

internal inline fun <reified T> createTypeWithArgument(typeArgument: KType? = null) =
    T::class.createTypeWithArgument(typeArgument)

internal fun guessValueType(values: Sequence<Any?>, upperBound: KType? = null): KType {
    val classes = mutableSetOf<KClass<*>>()
    var hasNulls = false
    var hasFrames = false
    var hasRows = false
    var hasList = false
    val classesInList = mutableSetOf<KClass<*>>()
    var nullsInList = false
    values.forEach {
        when (it) {
            null -> hasNulls = true
            is AnyRow -> hasRows = true
            is AnyFrame -> hasFrames = true
            is List<*> -> {
                hasList = true
                it.forEach {
                    if (it == null) nullsInList = true
                    else classesInList.add(it.javaClass.kotlin)
                }
            }
            else -> classes.add(it.javaClass.kotlin)
        }
    }
    val allListsWithRows = classesInList.isNotEmpty() && classesInList.all { it.isSubclassOf(DataRow::class) } && !nullsInList
    return when {
        classes.isNotEmpty() -> {
            if (hasRows) classes.add(DataRow::class)
            if (hasFrames) classes.add(DataFrame::class)
            if (hasList) {
                if (classesInList.isNotEmpty()) {
                    val typeInLists = classesInList.commonType(nullsInList, upperBound)
                    val typeOfOthers = classes.commonType(nullsInList, upperBound)
                    if (typeInLists == typeOfOthers) {
                        return List::class.createTypeWithArgument(typeInLists, false)
                    }
                }
                classes.add(List::class)
            }
            return classes.commonType(hasNulls, upperBound)
        }
        (hasFrames && (!hasList || allListsWithRows)) || (!hasFrames && allListsWithRows) -> DataFrame::class.createStarProjectedType(hasNulls)
        hasRows && !hasFrames && !hasList -> DataRow::class.createStarProjectedType(false)
        hasList && !hasFrames && !hasRows -> {
            val elementType = upperBound?.let { if (it.jvmErasure == List::class) it.arguments[0].type else null }
            List::class.createTypeWithArgument(classesInList.commonType(nullsInList, elementType)).withNullability(hasNulls)
        }
        else -> {
            if (hasRows) classes.add(DataRow::class)
            if (hasFrames) classes.add(DataFrame::class)
            if (hasList) classes.add(List::class)
            return classes.commonType(hasNulls, upperBound)
        }
    }
}
