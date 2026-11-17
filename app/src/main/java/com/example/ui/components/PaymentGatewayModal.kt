package com.example.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FiscalInvoice
import com.example.data.model.PaymentMethod
import com.example.ui.theme.*

@Composable
fun PaymentGatewayModal(
  invoice: FiscalInvoice,
  onConfirmPayment: (PaymentMethod) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedMethod by remember { mutableStateOf(PaymentMethod.CIB_EDAHABIA) }
  var cardNumber by remember { mutableStateOf("6037 9901 **** ****") }
  var expDate by remember { mutableStateOf("09/28") }
  var isProcessing by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .testTag("payment_gateway_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MetalCard),
      border = BorderStroke(1.dp, MetalBorder)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "PAIEMENT SÉCURISÉ",
            style = MaterialTheme.typography.titleMedium.copy(color = PureWhite, fontWeight = FontWeight.Black)
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MetalSurface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "Montant de la facture", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted))
              Text(text = "Facture ${invoice.invoiceNumber}", style = MaterialTheme.typography.bodySmall.copy(color = OffWhite))
            }
            Text(
              text = "${invoice.totalTtcDzd.toInt()} DZD",
              style = MaterialTheme.typography.titleLarge.copy(color = ElectricGreen, fontWeight = FontWeight.Black)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Payment Method Selector
        PaymentMethod.values().forEach { method ->
          val isSelected = method == selectedMethod
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clickable { selectedMethod = method },
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) PerformanceRedDark.copy(alpha = 0.3f) else MetalSurface,
            border = BorderStroke(1.dp, if (isSelected) PerformanceRed else MetalBorder)
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = isSelected,
                onClick = { selectedMethod = method },
                colors = RadioButtonDefaults.colors(selectedColor = PerformanceRed)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = method.label,
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = if (isSelected) PureWhite else AluminumLight,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedMethod == PaymentMethod.CIB_EDAHABIA) {
          OutlinedTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it },
            label = { Text("Numéro de carte Edahabia / CIB") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = PureWhite,
              unfocusedTextColor = OffWhite,
              focusedBorderColor = TechCyan,
              unfocusedBorderColor = MetalBorder
            ),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = { onConfirmPayment(selectedMethod) },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_confirm_payment"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = PureWhite)
        ) {
          Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Confirmer le règlement (${invoice.totalTtcDzd.toInt()} DA)",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}
