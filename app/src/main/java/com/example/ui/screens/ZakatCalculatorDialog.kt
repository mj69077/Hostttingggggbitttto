package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZakatCalculatorDialog(
    onDismiss: () -> Unit
) {
    var cashAmount by remember { mutableStateOf("") }
    var goldGrams24 by remember { mutableStateOf("") }
    var goldGrams21 by remember { mutableStateOf("") }
    var goldPricePerGram24 by remember { mutableStateOf("260") } // Average estimated gold gram price
    var silverGrams by remember { mutableStateOf("") }
    var silverPricePerGram by remember { mutableStateOf("3.5") }
    var tradeGoodsAmount by remember { mutableStateOf("") }
    var debtsToPay by remember { mutableStateOf("") }

    val df = remember { DecimalFormat("#,###.##") }

    // Nisab calculation (85g of 24k Gold)
    val goldGramPrice = goldPricePerGram24.toDoubleOrNull() ?: 0.0
    val silverGramPrice = silverPricePerGram.toDoubleOrNull() ?: 0.0
    val goldNisab = 85.0 * goldGramPrice
    val silverNisab = 595.0 * silverGramPrice

    // User values
    val cash = cashAmount.toDoubleOrNull() ?: 0.0
    val g24 = (goldGrams24.toDoubleOrNull() ?: 0.0) * goldGramPrice
    val g21 = (goldGrams21.toDoubleOrNull() ?: 0.0) * (goldGramPrice * (21.0 / 24.0))
    val silverVal = (silverGrams.toDoubleOrNull() ?: 0.0) * silverGramPrice
    val trade = tradeGoodsAmount.toDoubleOrNull() ?: 0.0
    val debts = debtsToPay.toDoubleOrNull() ?: 0.0

    val totalWealth = (cash + g24 + g21 + silverVal + trade - debts).coerceAtLeast(0.0)
    val isNisabReached = totalWealth >= goldNisab && goldNisab > 0
    val zakatDue = if (isNisabReached) totalWealth * 0.025 else 0.0 // 2.5%

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Zakat Calculator",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "حاسبة الزكاة الشرعية",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_zakat_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Result Summary Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isNisabReached) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isNisabReached) "الزكاة الواجب إخراجها (٢.٥٪)" else "إجمالي المال الخاضع للزكاة",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isNisabReached) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = if (isNisabReached) "${df.format(zakatDue)} ر.س / د.إ" else "${df.format(totalWealth)} ر.س / د.إ",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isNisabReached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )

                            if (!isNisabReached && totalWealth > 0) {
                                Text(
                                    text = "لم يبلغ المال النصاب الشرعي بعد (نصاب الذهب 85 جرام: ${df.format(goldNisab)})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                            } else if (isNisabReached) {
                                Text(
                                    text = "بلغ المال النصاب وحال عليه الحول الهجري كاملاً",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Pricing Settings / Assumptions
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "أسعار الذهب والفضة (لحساب النصاب):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = goldPricePerGram24,
                                    onValueChange = { goldPricePerGram24 = it },
                                    label = { Text("سعر جرام الذهب عيار 24") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = silverPricePerGram,
                                    onValueChange = { silverPricePerGram = it },
                                    label = { Text("سعر جرام الفضة") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    // Cash and bank accounts
                    OutlinedTextField(
                        value = cashAmount,
                        onValueChange = { cashAmount = it },
                        label = { Text("المال النقدي والودائع البنكية (ر.س / د.إ)") },
                        placeholder = { Text("0.0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("zakat_cash_input")
                    )

                    // Gold 24k and 21k grams
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = goldGrams24,
                            onValueChange = { goldGrams24 = it },
                            label = { Text("ذهب عيار 24 (جرام)") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = goldGrams21,
                            onValueChange = { goldGrams21 = it },
                            label = { Text("ذهب عيار 21 (جرام)") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Silver and Trade goods
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = silverGrams,
                            onValueChange = { silverGrams = it },
                            label = { Text("الفضة المدخرة (جرام)") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = tradeGoodsAmount,
                            onValueChange = { tradeGoodsAmount = it },
                            label = { Text("عروض التجارة والأسهم") },
                            placeholder = { Text("0.0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Debts to deduct
                    OutlinedTextField(
                        value = debtsToPay,
                        onValueChange = { debtsToPay = it },
                        label = { Text("الديون الحالة المستحقة للدفع (تخصم من المال)") },
                        placeholder = { Text("0.0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Fatwa note
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "تجب الزكاة إذا بلغ المال النصاب وحال عليه الحول الهجري كاملاً، ومقدارها ٢.٥٪ (ربع العشر). حلي المرأة المعد للبس المعتاد لا زكاة فيه عند جمهور الفقهاء.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
