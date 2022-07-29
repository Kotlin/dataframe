internal class Add0 : AbstractInterpreter<PluginDataFrameSchema>() {
    val Arguments.name: String by string()
    val Arguments.infer: Infer by enum()
    val Arguments.expression: TypeApproximation by type()
    val Arguments.receiver: PluginDataFrameSchema by dataFrame()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        TODO()
    }
}
