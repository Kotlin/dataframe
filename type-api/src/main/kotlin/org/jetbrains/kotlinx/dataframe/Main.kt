package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.symbols.impl.ConeClassLikeLookupTagImpl
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.impl.ConeClassLikeTypeImpl
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

val DF_CLASS_ID: ClassId
    get() = ClassId.topLevel(FqName.fromSegments(listOf("org", "jetbrains", "kotlinx", "dataframe", "DataFrame")))

val COLUM_GROUP_CLASS_ID: ClassId
    get() = ClassId(FqName("org.jetbrains.kotlinx.dataframe.columns"), Name.identifier("ColumnGroup"))

class KotlinTypeFacade(private val session: FirSession) {

    val anyDataFrame = ConeClassLikeTypeImpl(
        ConeClassLikeLookupTagImpl(DF_CLASS_ID),
        typeArguments = arrayOf(session.builtinTypes.anyType.type),
        isNullable = false
    ).wrap()

    fun Marker.toColumnGroup() = ConeClassLikeTypeImpl(
        ConeClassLikeLookupTagImpl(COLUM_GROUP_CLASS_ID),
        typeArguments = arrayOf(type.typeArguments[0]),
        isNullable = false
    ).wrap()

}

class Marker(internal val type: ConeKotlinType) {
    override fun toString(): String {
        return "Marker(type=$type)"
    }
}

class LazyMarker(internal val factory: (FirSession) -> ConeKotlinType)

fun ConeKotlinType.wrap(): Marker = Marker(this)

//fun ConeKotlinType



