import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import java.util.concurrent.atomic.AtomicBoolean

class MyFirExtensionRegistrar(val functions: MutableList<FirFunction>) : FirExtensionRegistrar() {

    override fun ExtensionRegistrarContext.configurePlugin() {
        +{ session: FirSession -> FunctionCollector(session, functions) }
    }
}

class FunctionCollector(session: FirSession, val functions: MutableList<FirFunction>) : FirAdditionalCheckersExtension(session) {

    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
        override val functionCheckers: Set<FirFunctionChecker>
            get() = setOf(NoopChecker(functions))
    }

    internal class NoopChecker(val functions: MutableList<FirFunction>) : FirFunctionChecker() {
        override fun check(declaration: FirFunction, context: CheckerContext, reporter: DiagnosticReporter) {
            functions.add(declaration)
        }
    }
}

class FunctionSaver(val functions: MutableList<FirFunction>) : IrGenerationExtension {
    private val done = AtomicBoolean(false)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        if (done.getAndSet(true)) {
            functions.forEach {
                it.returnTypeRef.psi
            }
            val df = functions.toDataFrame {
                properties(maxDepth = 2) {
//                    exclude()
                }
            }
        }

    }
}
