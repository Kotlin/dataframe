package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.rows

@DataSchema
data class Person(
    val age: Int,
    val name: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val df = dataFrameOf(
            "name" to listOf("Andrei", "Nikita", "Jolan"),
            "age" to listOf(22, 16, 37)
        ).cast<Person>()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DataFrameScreen(df)
                }
            }
        }
    }
}

@Composable
fun DataFrameScreen(df: DataFrame<Person>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Kotlin DataFrame on Android",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "df",
            modifier = Modifier
                .background(color = Color.LightGray)
                .padding(2.dp)
        )

        DataFrameTable(df)

        Text(
            text = "df.filter { age >= 20 }",
            modifier = Modifier
                .background(color = Color.LightGray)
                .padding(2.dp)
        )

        DataFrameTable(df.filter { age >= 20 })
    }
}

@Composable
fun DataFrameTable(df: DataFrame<*>) {
    val columnNames = df.columnNames()

    // Header
    Row {
        for (name in columnNames) {
            Text(
                text = name,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    Spacer(Modifier.height(4.dp))

    // Rows
    LazyColumn {
        items(df.rows().toList()) { row ->
            Row {
                for (cell in row.values()) {
                    Text(
                        text = cell.toString(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
