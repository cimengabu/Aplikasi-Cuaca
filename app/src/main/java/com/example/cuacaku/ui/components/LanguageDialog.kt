package com.example.cuacaku.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.cuacaku.R

// =====================================================
// LanguageDialog — Dialog pemilihan bahasa aplikasi
// Mendukung Bahasa Indonesia, English, dan Basa Jawa
// =====================================================
@Composable
fun LanguageDialog(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.choose_language),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Bahasa Indonesia
                TextButton(
                    onClick = { onSelect("in") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🇮🇩  Bahasa Indonesia",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // English
                TextButton(
                    onClick = { onSelect("en") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🇬🇧  English",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Basa Jawa (Tahap 3)
                TextButton(
                    onClick = { onSelect("jv") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🇮🇩  Basa Jawa",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}
