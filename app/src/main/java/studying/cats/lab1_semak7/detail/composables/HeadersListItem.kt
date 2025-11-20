package studying.cats.lab1_semak7.detail.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HeadersListItem(
    header: Comparable<*>,
    associatedList: List<Comparable<*>>,
    itemIndex: Int,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "${itemIndex + 1}. Заголовок: $header",
            style = MaterialTheme.typography.titleLarge
        )

        Column(modifier = Modifier.padding(start = 24.dp, top = 4.dp)) {
            if (associatedList.isEmpty()) {
                Text(
                    text = "(пусто)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                associatedList.forEach { item ->
                    Text(
                        text = "• $item",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }

    if (showDivider) {
        Spacer(modifier = Modifier.padding(top = 12.dp))
    }
}