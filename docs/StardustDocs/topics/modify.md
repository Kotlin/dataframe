[//]: # (title: Modify)

`DataFrame` object is immutable and all operations return a new instance of `DataFrame`.

Similar operations for columns or rows modification have different names:

<table>
<tr><th>Column operation</th><th>Row operation</th></tr>
<tr><td>add</td><td>append</td></tr>
<tr><td>remove</td><td>drop</td></tr>
<tr><td>select</td><td>filter</td></tr>
<tr><td>group</td><td>groupBy</td></tr>
<tr><td>join</td><td>concat</td></tr>
</table>

**Horizontal (column) modification:**
* [add](add.md)
* [flatten](flatten.md)
* [group](group.md)
* [insert](insert.md)
* [map](map.md)
* [merge](merge.md)
* [move](move.md)
* [remove](remove.md)
* [rename](rename.md)
* [replace](replace.md)
* [select](select.md)
* [split](split.md)
* [ungroup](ungroup.md)

**Vertical (row) modification:**
* [append](append.md)
* [concat](concat.md)
* [drop](drop.md)
* [distinct](sortBy.md)
* [explode](explode.md)
* [filter](filter.md)
* [implode](explode.md)
* [shuffle](shuffle.md) 
* [sortBy](sortBy.md)
* [split](split.md)

**Data modification:**
* [convert](convert.md)
* [parse](parse.md)
* [update](update.md)

**Reshaping:**
* [pivot](pivot.md)
* [gather](gather.md)

**Learn how to:**
* [Slice rows](sliceRows.md)
* [Filter rows](filterRows.md)
* [Reorder rows](reorderRows.md)
* [Select columns](select.md)
* [Update/convert values](updateConvert.md)
* [Split/merge values](splitMerge.md)
* [Group rows by keys](groupBy.md)
* [Append values](append.md)
* [Add/map/remove columns](addRemove.md)
* [Move/rename columns](moveRename.md)
* [Insert/replace columns](insertReplace.md)
* [Explode/implode columns](explodeImplode.md)
* [Pivot/gather columns](pivotGather.md)
