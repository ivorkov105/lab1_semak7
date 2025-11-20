package studying.cats.lab1_semak7.detail.composables

import HeadersList.HeadersList
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HeadersListScreen(
    list: HeadersList<out Comparable<*>>,
    onItemClick: (itemName: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(count = list.size()) { index ->
            val header = list.getHeader(index)
            val associatedList = list.getList(index)
            HeadersListItem(
                header = header,
                associatedList = associatedList,
                itemIndex = index,
                showDivider = index < list.size() - 1,
                onClick = {
                    onItemClick(header.toString())
                }
            )
        }
    }
}