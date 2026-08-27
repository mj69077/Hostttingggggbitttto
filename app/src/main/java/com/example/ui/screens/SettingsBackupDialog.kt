package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AdhanSound
import com.example.data.model.CalculationMethod
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBackupDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    var selectedMethod by remember { mutableStateOf(viewModel.getCalculationMethod()) }
    var selectedAdhanSound by remember { mutableStateOf(viewModel.getAdhanSound()) }
    var backupJsonInput by remember { mutableStateOf("") }
    var showImportField by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "الإعدادات والنسخ الاحتياطي",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Preferences
                Text(
                    text = "تفضيلات الحساب والأصوات",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Haptic feedback toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "الاهتزاز التفاعلي (Haptics)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "اهتزاز لطيف عند النقر على المسبحة والأذكار",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = hapticEnabled,
                        onCheckedChange = { viewModel.setHapticEnabled(it) },
                        modifier = Modifier.testTag("haptic_toggle_switch")
                    )
                }

                Divider()

                // Calculation Method
                Text(
                    text = "طريقة حساب مواقيت الصلاة:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                CalculationMethod.values().forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMethod == method,
                            onClick = {
                                selectedMethod = method
                                viewModel.setCalculationMethod(method)
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = method.titleArabic,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Divider()

                // Adhan Sound
                Text(
                    text = "صوت الأذان المفضل:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                AdhanSound.values().forEach { sound ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedAdhanSound == sound,
                            onClick = {
                                selectedAdhanSound = sound
                                viewModel.setAdhanSound(sound)
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = sound.titleArabic,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Divider()

                // Section: JSON Backup & Restore
                Text(
                    text = "النسخ الاحتياطي للبيانات (JSON)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Export Button
                    Button(
                        onClick = {
                            scope.launch {
                                val json = viewModel.exportBackupJson()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("QuranApp_Backup", json)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ بياناتك واكتمال الختمة إلى الحافظة بنجاح", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_backup_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تصدير ونسخ")
                    }

                    // Import toggle button
                    OutlinedButton(
                        onClick = { showImportField = !showImportField },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("toggle_import_field_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("استيراد")
                    }
                }

                if (showImportField) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = backupJsonInput,
                            onValueChange = { backupJsonInput = it },
                            label = { Text("الصق كود النسخ الاحتياطي (JSON)") },
                            placeholder = { Text("{\n  \"exportDate\": ...\n}") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("import_json_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (backupJsonInput.isNotBlank()) {
                                    scope.launch {
                                        val ok = viewModel.importBackupJson(backupJsonInput.trim())
                                        if (ok) {
                                            Toast.makeText(context, "تمت استعادة البيانات بنجاح!", Toast.LENGTH_LONG).show()
                                            onDismiss()
                                        } else {
                                            Toast.makeText(context, "خطأ في بنية كود الاستيراد", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_import_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("تأكيد استعادة النسخة")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_settings_dialog")
            ) {
                Text("إغلاق")
            }
        }
    )
}
