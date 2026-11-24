package com.example.cuacaku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cuacaku.R

data class LanguageOption(
    val code: String,
    val flag: String,
    val label: String
)

val APP_SUPPORTED_LANGUAGES = listOf(
    LanguageOption("in", "🇮🇩", "Bahasa Indonesia"),
    LanguageOption("en", "🇬🇧", "English"),
    LanguageOption("jv", "🇮🇩", "Basa Jawa"),
    LanguageOption("su", "🇮🇩", "Basa Sunda"),
    LanguageOption("ms", "🇲🇾", "Bahasa Melayu"),
    LanguageOption("es", "🇪🇸", "Español"),
    LanguageOption("fr", "🇫🇷", "Français"),
    LanguageOption("de", "🇩🇪", "Deutsch"),
    LanguageOption("ja", "🇯🇵", "日本語"),
    LanguageOption("zh", "🇨🇳", "简体中文"),
    LanguageOption("ar", "🇸🇦", "العربية"),
    LanguageOption("ru", "🇷🇺", "Русский"),
    LanguageOption("ko", "🇰🇷", "한국어"),
    LanguageOption("pt", "🇵🇹", "Português")
)

fun getLanguageDisplayName(code: String): String {
    return APP_SUPPORTED_LANGUAGES.find { it.code.equals(code, ignoreCase = true) }
        ?.let { "${it.flag}  ${it.label}" }
        ?: "🇮🇩  Bahasa Indonesia"
}

// =====================================================
// LanguageDialog — Dialog pemilihan bahasa aplikasi lengkap
// Mendukung 14 bahasa regional & internasional
// =====================================================
@Composable
fun LanguageDialog(
    currentLanguage: String = "in",
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.choose_language),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                APP_SUPPORTED_LANGUAGES.forEach { lang ->
                    val isSelected = lang.code.equals(currentLanguage, ignoreCase = true)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { onSelect(lang.code) }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = lang.flag,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = lang.label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
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
