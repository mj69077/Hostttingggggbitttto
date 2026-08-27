package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.KhatmahPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhatmahPlanDialog(
    currentPlan: KhatmahPlan?,
    onSavePlan: (targetDays: Int, startPage: Int) -> Unit,
    onUpdatePage: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var targetDaysText by remember { mutableStateOf(currentPlan?.targetDays?.toString() ?: "30") }
    var startPageText by remember { mutableStateOf(currentPlan?.startPage?.toString() ?: "1") }
    var currentPageText by remember { mutableStateOf(currentPlan?.currentPage?.toString() ?: "1") }

    val daysInt = targetDaysText.toIntOrNull() ?: 30
    val startInt = startPageText.toIntOrNull() ?: 1
    val calculatedDailyPages = if (daysInt > 0) ((604 - startInt + 1) / daysInt).coerceAtLeast(1) else 20

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = "Khatmah",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "خطة ختم القرآن الكريم",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "حدد المدة المرغوبة لختم المصحف الشريف وسيقوم التطبيق بحساب الورد اليومي تلقائياً:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = targetDaysText,
                    onValueChange = { targetDaysText = it },
                    label = { Text("المدة المستهدفة (عدد الأيام)") },
                    placeholder = { Text("مثلاً: 30") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("khatmah_target_days_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = currentPageText,
                    onValueChange = { currentPageText = it },
                    label = { Text("الصفحة الحالية (من 1 إلى 604)") },
                    placeholder = { Text("مثلاً: 1") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("khatmah_current_page_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "الورد اليومي المطلوب:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$calculatedDailyPages صفحات يومياً",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val days = targetDaysText.toIntOrNull() ?: 30
                    val page = currentPageText.toIntOrNull() ?: 1
                    onSavePlan(days, page)
                    onUpdatePage(page)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_khatmah_plan_btn")
            ) {
                Text("حفظ الخطة")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_khatmah_plan_btn")
            ) {
                Text("إلغاء")
            }
        }
    )
}
