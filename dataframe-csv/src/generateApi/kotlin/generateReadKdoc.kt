internal fun generateReadKdoc(
    fileTypeTitle: String,
    dataTitle: String,
    fileType: String,
    data: String,
): String =
    """
    #### Read $fileTypeTitle $dataTitle to [DataFrame]
    
    Reads any $fileType $data to a [DataFrame][DataFrame].
    
    blah blah...
    """.trimIndent()
