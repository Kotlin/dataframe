@file:OptIn(ExperimentalUnsignedTypes::class)

package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.Infer
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.KVariance.IN
import kotlin.reflect.KVariance.INVARIANT
import kotlin.reflect.KVariance.OUT
import kotlin.reflect.KVisibility
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

internal inline fun <reified T> KClass<*>.createTypeUsing() = typeOf<T>().projectTo(this)

internal fun KType.projectTo(targetClass: KClass<*>): KType {
    val currentClass = classifier as? KClass<*>
    return when {
        targetClass.typeParameters.isEmpty() || currentClass == null -> targetClass.createStarProjectedType(
            isMarkedNullable,
        )

        currentClass == targetClass -> this

        targetClass.isSubclassOf(currentClass) -> projectDownTo(targetClass)

        targetClass.isSuperclassOf(currentClass) -> projectUpTo(targetClass)

        else -> targetClass.createStarProjectedType(isMarkedNullable)
    }
}

internal fun KType.projectUpTo(superClass: KClass<*>): KType {
    if (this == nothingType(false)) return superClass.createStarProjectedType(false)
    if (this == nothingType(true)) return superClass.createStarProjectedType(true)

    val chain = inheritanceChain(jvmErasure, superClass)
    var current = this
    chain.forEach { (clazz, declaredBaseType) ->
        val substitution = clazz.typeParameters.zip(current.arguments.map { it.type }).toMap()
        current = declaredBaseType.replace(substitution)
    }
    return current.withNullability(isMarkedNullable)
}

/**
 * Changes generic type parameters to its upperbound, like `List<T> -> List<Any?>` and
 * `Comparable<T> -> Comparable<Nothing>`.
 * Works recursively as well.
 */
@PublishedApi
internal fun KType.replaceGenericTypeParametersWithUpperbound(): KType {
    fun KType.replaceRecursively(): Pair<Boolean, KType> {
        var replacedAnyArgument = false
        val arguments = arguments.mapIndexed { i, it ->
            val oldType = it.type ?: return@mapIndexed it // is * if null, we'll leave those be

            val type = when {
                oldType.classifier is KTypeParameter -> { // e.g. T
                    replacedAnyArgument = true

                    // resolve the variance (`in`/`out`/``) of the argument in the original class
                    val varianceInClass = (classifier as? KClass<*>)?.typeParameters?.getOrNull(i)?.variance
                    when (varianceInClass) {
                        INVARIANT, OUT, null ->
                            (oldType.classifier as KTypeParameter).upperBounds.firstOrNull() ?: typeOf<Any?>()

                        // Type<in T> cannot be replaced with Type<Any?>, instead it should be replaced with Type<Nothing>
                        // TODO: issue #471
                        IN -> nothingType(false)
                    }
                }

                else -> oldType
            }
            val (replacedDownwards, newType) = type.replaceRecursively()
            if (replacedDownwards) replacedAnyArgument = true

            KTypeProjection.invariant(newType)
        }
        return Pair(
            first = replacedAnyArgument,
            second = if (replacedAnyArgument) jvmErasure.createType(arguments, isMarkedNullable) else this,
        )
    }

    return replaceRecursively().second
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

internal fun KType.replace(substitution: Map<KTypeParameter, KType?>): KType = when (val clazz = classifier) {
    is KTypeParameter -> substitution[clazz] ?: this
    is KClass<*> -> clazz.createType(arguments.map { KTypeProjection(it.variance, it.type?.replace(substitution)) })
    else -> this
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
        for (j in i + 1 until intTypes.size) {
            add(intTypes[i], intTypes[j])
        }
        add(intTypes[i], Double::class)
    }
    add(Float::class, Double::class)
    map
}

internal fun getCommonNumberType(first: KClass<*>?, second: KClass<*>): KClass<*> = when {
    first == null -> second
    first == second -> first
    else -> numberTypeExtensions[first to second] ?: error("Can not find common number type for $first and $second")
}

internal fun Iterable<KClass<*>>.commonNumberClass(): KClass<*> =
    fold(null as KClass<*>?, ::getCommonNumberType) ?: Number::class

internal fun commonParent(classes: Iterable<KClass<*>>): KClass<*>? = commonParents(classes).withMostSuperclasses()
internal fun commonParent(vararg classes: KClass<*>): KClass<*>? = commonParent(classes.toList())
internal fun Iterable<KClass<*>>.withMostSuperclasses(): KClass<*>? = maxByOrNull { it.allSuperclasses.size }
internal fun Iterable<KClass<*>>.createType(nullable: Boolean, upperBound: KType? = null): KType =
    if (upperBound == null) {
        (withMostSuperclasses() ?: Any::class).createStarProjectedType(nullable)
    } else {
        val upperClass = upperBound.classifier as KClass<*>
        val baseClass = filter { it.isSubclassOf(upperClass) }.withMostSuperclasses() ?: withMostSuperclasses()
        if (baseClass == null) {
            upperBound.withNullability(nullable)
        } else {
            upperBound.projectTo(baseClass).withNullability(nullable)
        }
    }

internal fun commonParents(vararg classes: KClass<*>): List<KClass<*>> = commonParents(classes.toList())
internal fun commonParents(classes: Iterable<KClass<*>>): List<KClass<*>> = when {
    !classes.any() -> emptyList()

    classes.all { it == Nothing::class } -> listOf(Nothing::class)

    else -> {
        classes
            .distinct()
            .filterNot { it == Nothing::class } // Nothing is a subtype of everything
            .let {
                when {
                    // if there is only one class - return it
                    it.size == 1 && it[0].visibility == KVisibility.PUBLIC -> {
                        listOf(it[0])
                    }

                    else -> it.fold(null as (Set<KClass<*>>?)) { set, clazz ->
                        // collect a set of all common superclasses from original classes
                        val superclasses =
                            (clazz.allSuperclasses + clazz).filter { it.visibility == KVisibility.PUBLIC }.toSet()
                        set?.intersect(superclasses) ?: superclasses
                    }!!.let {
                        it - it.flatMap { it.superclasses }
                            .toSet() // leave only 'leaf' classes, that are not super to some other class in a set
                    }.toList()
                }
            }
    }
}.sortedBy { it.simpleName } // make sure the order is stable to avoid bugs

/**
 * Returns the common type of the given types including "listify" behaviour.
 * Values and nulls will be wrapped in a list if they appear among other lists.
 * For example: `[Int, Nothing?, List<Int>]` will become `List<Int>` instead of `Any?`. If there is another collection
 * in there, it will become `Any?` anyway.
 *
 * @receiver the types to find the common type for
 * @return the common type including listify behaviour
 *
 * @param useStar if true, `*` will be used to fill in generic type parameters instead of `Any?`
 * (for invariant/out variance) or `Nothing` (for in variance)
 *
 * @see Iterable.commonType
 */
internal fun Iterable<KType>.commonTypeListifyValues(useStar: Boolean = true): KType {
    val distinct = distinct()
    val nullable = distinct.any { it.isMarkedNullable }
    return when {
        distinct.isEmpty() -> typeOf<Any>().withNullability(nullable)

        distinct.size == 1 -> distinct.single()

        else -> {
            val classes = distinct.map {
                if (it == nothingType(false) || it == nothingType(true)) {
                    Nothing::class
                } else {
                    it.jvmErasure
                }
            }.distinct()
            when {
                classes.size == 1 -> {
                    val typeProjections = classes.single().typeParameters.mapIndexed { index, parameter ->
                        val arguments = distinct.map { it.arguments[index].type }.toSet()
                        if (arguments.contains(null)) {
                            KTypeProjection.STAR
                        } else {
                            val type = arguments.filterNotNull().commonTypeListifyValues()
                            KTypeProjection(parameter.variance, type)
                        }
                    }

                    if (classes.single() == Nothing::class) {
                        nothingType(distinct.any { it.isMarkedNullable })
                    } else {
                        classes[0].createType(typeProjections, distinct.any { it.isMarkedNullable })
                    }
                }

                classes.any { it == List::class } &&
                    classes.all {
                        it == List::class ||
                            !it.isSubclassOf(
                                Collection::class,
                            )
                    } -> {
                    val distinctNoNothing = distinct.filterNot {
                        it == nothingType(false) || it == nothingType(true)
                    }
                    val listTypes = distinctNoNothing.map {
                        if (it.classifier == List::class) {
                            it.arguments[0].type
                        } else {
                            it
                        }
                    }.toMutableSet()
                    val type = listTypes
                        .filterNotNull()
                        .commonTypeListifyValues()
                        .withNullability(listTypes.any { it?.isMarkedNullable ?: true })

                    List::class.createType(
                        arguments = listOf(KTypeProjection.invariant(type)),
                        nullable = distinctNoNothing.any { it.isMarkedNullable },
                    )
                }

                else -> {
                    val kClass = commonParent(distinct.map { it.jvmErasure })
                        ?: return typeOf<Any>().withNullability(nullable)
                    val projections = distinct
                        .map { it.projectUpTo(kClass).replaceGenericTypeParametersWithUpperbound() }
                    require(projections.all { it.jvmErasure == kClass })

                    val arguments = List(kClass.typeParameters.size) { i ->
                        val typeParameter = kClass.typeParameters[i]
                        val projectionTypes = projections
                            .map { it.arguments[i].type }
                            .filterNot { it in distinct } // avoid infinite recursion

                        when {
                            projectionTypes.isEmpty() && typeParameter.variance == KVariance.IN -> {
                                if (useStar) {
                                    KTypeProjection.STAR
                                } else {
                                    KTypeProjection.invariant(nothingType(false))
                                }
                            }

                            else -> {
                                val commonType = projectionTypes.filterNotNull().commonTypeListifyValues(useStar)
                                if (commonType == typeOf<Any?>() && useStar) {
                                    KTypeProjection.STAR
                                } else {
                                    KTypeProjection(typeParameter.variance, commonType)
                                }
                            }
                        }
                    }

                    if (kClass == Nothing::class) {
                        nothingType(nullable)
                    } else {
                        kClass.createType(arguments, nullable)
                    }
                }
            }
        }
    }
}

internal fun KClass<*>.createTypeWithArgument(argument: KType? = null, nullable: Boolean = false): KType {
    require(typeParameters.size == 1)
    return if (argument != null) {
        createType(listOf(KTypeProjection.invariant(argument)), nullable)
    } else {
        createStarProjectedType(nullable)
    }
}

internal inline fun <reified T> createTypeWithArgument(typeArgument: KType? = null) =
    T::class.createTypeWithArgument(typeArgument)

@PublishedApi
internal fun <T> getValuesType(values: List<T>, type: KType, infer: Infer): KType = when (infer) {
    Infer.Nulls -> type.withNullability(values.anyNull())
    Infer.Type -> guessValueType(values.asSequence(), type)
    Infer.None -> type
}

/**
 * Returns the value type of the given [values] sequence.
 *
 * @param values the values to guess the type from
 * @param upperBound the upper bound of the type to guess
 * @param listifyValues if true, then values and nulls will be wrapped in a list if they appear among other lists.
 *   For example: `[1, null, listOf(1, 2, 3)]` will become `List<Int>` instead of `Any?`
 *   Note: this parameter is ignored if another [Collection] is present in the values.
 */
@PublishedApi
internal fun guessValueType(values: Sequence<Any?>, upperBound: KType? = null, listifyValues: Boolean = false): KType {
    val classes = mutableSetOf<KClass<*>>()
    val collectionClasses = mutableSetOf<KClass<out Collection<*>>>()
    var hasNulls = false
    var hasFrames = false
    var hasRows = false
    var hasList = false
    var allListsAreEmpty = true
    val classesInCollection = mutableSetOf<KClass<*>>()
    var nullsInCollection = false
    var listifyValues = listifyValues
    values.forEach {
        when (it) {
            null -> hasNulls = true

            is AnyRow -> hasRows = true

            is AnyFrame -> hasFrames = true

            is List<*> -> {
                hasList = true
                if (it.isNotEmpty()) allListsAreEmpty = false
                it.forEach {
                    if (it == null) {
                        nullsInCollection = true
                    } else {
                        classesInCollection.add(it.javaClass.kotlin)
                    }
                }
            }

            is Collection<*> -> {
                listifyValues = false // turn it off for when another collection is present
                it.forEach {
                    if (it == null) {
                        nullsInCollection = true
                    } else {
                        classesInCollection.add(it.javaClass.kotlin)
                    }
                }
                collectionClasses.add(it.javaClass.kotlin)
            }

            else -> classes.add(it.javaClass.kotlin)
        }
    }
    val allListsWithRows = classesInCollection.isNotEmpty() &&
        classesInCollection.all { it.isSubclassOf(DataRow::class) } &&
        !nullsInCollection

    return when {
        classes.isNotEmpty() -> {
            if (hasRows) classes.add(DataRow::class)
            if (hasFrames) classes.add(DataFrame::class)
            if (hasList) {
                if (listifyValues) {
                    val typeInLists = classesInCollection.commonType(
                        nullable = nullsInCollection || allListsAreEmpty,
                        // for when the list is empty, make it Nothing instead of Any?
                        upperBound = nothingType(nullable = false),
                    )
                    val typeOfOthers = classes.commonType(nullable = nullsInCollection, upperBound = upperBound)
                    val commonType = listOf(typeInLists, typeOfOthers).commonTypeListifyValues()
                    return List::class.createTypeWithArgument(argument = commonType, nullable = false)
                }
                classes.add(List::class)
            }
            if (collectionClasses.isNotEmpty()) classes.addAll(collectionClasses)
            return classes.commonType(hasNulls, upperBound)
        }

        hasNulls && !hasFrames && !hasRows && !hasList -> nothingType(nullable = true)

        (hasFrames && (!hasList || allListsWithRows)) || (!hasFrames && allListsWithRows) ->
            DataFrame::class.createStarProjectedType(hasNulls)

        hasRows && !hasFrames && !hasList ->
            DataRow::class.createStarProjectedType(false)

        collectionClasses.isNotEmpty() && !hasFrames && !hasRows -> {
            val elementType = upperBound?.let {
                if (it.jvmErasure.isSubclassOf(Collection::class)) {
                    it.projectUpTo(Collection::class).arguments[0].type
                } else {
                    null
                }
            }
            if (hasList) collectionClasses.add(List::class)
            (commonParent(collectionClasses) ?: Collection::class)
                .createTypeWithArgument(
                    classesInCollection.commonType(
                        nullable = nullsInCollection,
                        upperBound = elementType ?: nothingType(nullable = nullsInCollection),
                    ),
                ).withNullability(hasNulls)
        }

        hasList && collectionClasses.isEmpty() && !hasFrames && !hasRows -> {
            val elementType = upperBound?.let { if (it.jvmErasure == List::class) it.arguments[0].type else null }
            List::class.createTypeWithArgument(
                classesInCollection.commonType(
                    nullable = nullsInCollection,
                    upperBound = elementType ?: nothingType(nullable = nullsInCollection),
                ),
            ).withNullability(hasNulls && !listifyValues)
        }

        else -> {
            if (hasRows) classes.add(DataRow::class)
            if (hasFrames) classes.add(DataFrame::class)
            if (hasList) classes.add(List::class)
            if (collectionClasses.isNotEmpty()) classes.addAll(collectionClasses)
            return classes.commonType(hasNulls, upperBound)
        }
    }
}

internal fun nothingType(nullable: Boolean): KType = if (nullable) {
    typeOf<List<Nothing?>>()
} else {
    typeOf<List<Nothing>>()
}.arguments.first().type!!

@OptIn(ExperimentalUnsignedTypes::class)
private val primitiveArrayClasses = setOf(
    BooleanArray::class,
    ByteArray::class,
    ShortArray::class,
    IntArray::class,
    LongArray::class,
    FloatArray::class,
    DoubleArray::class,
    CharArray::class,

    UByteArray::class,
    UShortArray::class,
    UIntArray::class,
    ULongArray::class,
)

/**
 * Returns `true` if this class is a primitive array class like `XArray`.
 *
 * Use [KClass.isArray] to also check for `Array<>`.
 */
internal val KClass<*>.isPrimitiveArray: Boolean
    get() = this in primitiveArrayClasses

/**
 * Returns `true` if this class is an array, either a primitive array like `XArray` or `Array<>`.
 *
 * Use [KClass.isPrimitiveArray] to only check for primitive arrays.
 */
internal val KClass<*>.isArray: Boolean
    get() = this in primitiveArrayClasses ||
        this.qualifiedName == Array::class.qualifiedName // instance check fails

/**
 * Returns `true` if this type is of a primitive array like `XArray`.
 *
 * Use [KType.isArray] to also check for `Array<>`.
 */
internal val KType.isPrimitiveArray: Boolean
    get() =
        if (arguments.isNotEmpty()) {
            // Catching https://github.com/Kotlin/dataframe/issues/678
            // as typeOf<Array<Int>>().classifier == IntArray::class
            false
        } else {
            (classifier as? KClass<*>)?.isPrimitiveArray == true
        }

/**
 * Returns `true` if this type is of an array, either a primitive array like `XArray` or `Array<>`.
 *
 * Use [KType.isPrimitiveArray] to only check for primitive arrays.
 */
internal val KType.isArray: Boolean
    get() = (classifier as? KClass<*>)?.isArray == true

/**
 * Returns `true` if this object is a primitive array like `XArray`.
 *
 * Use [Any.isArray] to also check for the `Array<>` object.
 */
internal val Any.isPrimitiveArray: Boolean
    get() = this::class.isPrimitiveArray

/**
 * Returns `true` if this object is an array, either a primitive array like `XArray` or `Array<>`.
 *
 * Use [Any.isPrimitiveArray] to only check for primitive arrays.
 */
internal val Any.isArray: Boolean
    get() = this::class.isArray

/**
 * If [this] is an array of any kind, the function returns it as a list of values,
 * else it returns `null`.
 */
internal fun Any.asArrayAsListOrNull(): List<*>? = when (this) {
    is BooleanArray -> asList()
    is ByteArray -> asList()
    is ShortArray -> asList()
    is IntArray -> asList()
    is LongArray -> asList()
    is FloatArray -> asList()
    is DoubleArray -> asList()
    is CharArray -> asList()
    is UByteArray -> asList()
    is UShortArray -> asList()
    is UIntArray -> asList()
    is ULongArray -> asList()
    is Array<*> -> asList()
    else -> null
}
