package krangl.typed

import krangl.ColType
import krangl.DataFrame
import krangl.readCSV
import krangl.readDelim
import org.apache.commons.csv.CSVFormat
import java.net.URI

fun read(fileOrUrl: String,
                  format: CSVFormat = CSVFormat.DEFAULT.withHeader().withIgnoreSurroundingSpaces(),
                  colTypes: Map<String, ColType> = mapOf()) = DataFrame.readCSV(fileOrUrl, format, colTypes).typed<Unit>()