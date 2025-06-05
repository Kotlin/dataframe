# Kotlin DataFrame Features in Kotlin Notebook

<web-summary>
Discover how Kotlin DataFrame integrates with Kotlin Notebook for seamless interactive data analysis in IntelliJ IDEA.
</web-summary>

<card-summary>
Load, explore, and export your data interactively using Kotlin DataFrame in Kotlin Notebook.
</card-summary>

<link-summary>
Learn how to load, explore, drill into, export, and interact with data using Kotlin DataFrame in Kotlin Notebook.
</link-summary>


The [Kotlin Notebook Plugin for IntelliJ IDEA](https://plugins.jetbrains.com/plugin/16340-kotlin-notebook),
combined with Kotlin DataFrame, offers powerful data analysis capabilities within an interactive environment.
Here are the key features:

### Drag-and-Drop Data Files

You can quickly load data into `DataFrame` into a notebook by simply dragging and dropping a file 
(.csv/.json/.xlsx and .geojson/.shp) directly into the notebook editor:

<video src="ktnb_drag_n_drop.mp4" controls=""/>

### Visual Data Exploration
**Page through your data**:
The pagination feature lets you move through your data one page at a time, making it possible to view large datasets.

**Sort by column with a single click**:
You can sort any column with a click.
This is a convenient alternative to using `sortBy` in separate cells.

**Go straight to the data you need**:
You can jump directly to a particular row or column if you want something specific.
This makes working with large datasets more straightforward.


<video src="https://github.com/user-attachments/assets/aeae1c79-9755-4558-bac4-420bf1331f39" controls=""/>


### Drill down into nested data
When your data has multiple layers, like a table within a table,
you can now click on a cell containing a nested table to view these details directly.
This makes it easy to go deeper into your data and then return to where you were.


<video src="https://github.com/user-attachments/assets/ef9509be-e19b-469c-9bad-0ce81eec36b0" controls=""/>


### Visualize multiple tables via tabs
You can open and visualize multiple tables in separate tabs.
This feature is tailored to those who need to compare, contrast, or monitor different datasets simultaneously.


<video src="https://github.com/user-attachments/assets/51b7a6e3-0187-49b3-bf5e-0c4d60f8b769" controls=""/>


### Exporting to files

You can export data directly from the dataframe into various file formats.
This simplifies sharing and further analysis.
The interface supports exporting data to JSON for web applications,
CSV for spreadsheet tools, and XML for data interchange.


<video src="https://github.com/user-attachments/assets/ec28c59a-1555-44ce-98f6-a60d8feae347" controls=""/>


### Convenient copying of data from tables
You can click and drag to select the data you need,
or you can use keyboard shortcuts for quicker selection
and then copy what’s needed with a simple right-click or another shortcut.
It’s designed to feel intuitive,
like copying text from a document, but with the structure and format of your data preserved.


<video src="https://github.com/user-attachments/assets/88e53dfb-361f-40f8-bffb-52a512cdd3cd" controls=""/>


To get started, ensure you have the latest version of the Kotlin Notebook Plugin installed in IntelliJ IDEA,
and begin exploring your data using Kotlin DataFrame in your notebook cells.

For more information on using the Kotlin Notebook Plugin,
refer to the [official documentation](https://kotlinlang.org/docs/kotlin-notebook-overview.html).
