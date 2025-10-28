package io.github.kdroidfilter.seforimapp.core.presentation.typography

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.StringResource
import seforimapp.seforimapp.generated.resources.Res
import seforimapp.seforimapp.generated.resources.font_bona_nova_sc
import seforimapp.seforimapp.generated.resources.font_cardo
import seforimapp.seforimapp.generated.resources.font_frank_ruhl_libre
import seforimapp.seforimapp.generated.resources.font_libertinus_serif
import seforimapp.seforimapp.generated.resources.font_miriam_libre
import seforimapp.seforimapp.generated.resources.font_noto_rashi_hebrew
import seforimapp.seforimapp.generated.resources.font_noto_serif_hebrew
import seforimapp.seforimapp.generated.resources.font_tinos
import seforimapp.seforimapp.generated.resources.BonaNovaSC_Bold
import seforimapp.seforimapp.generated.resources.BonaNovaSC_Italic
import seforimapp.seforimapp.generated.resources.BonaNovaSC_Regular
import seforimapp.seforimapp.generated.resources.Cardo_Bold
import seforimapp.seforimapp.generated.resources.Cardo_Italic
import seforimapp.seforimapp.generated.resources.Cardo_Regular
import seforimapp.seforimapp.generated.resources.LibertinusSerif_Bold
import seforimapp.seforimapp.generated.resources.LibertinusSerif_BoldItalic
import seforimapp.seforimapp.generated.resources.LibertinusSerif_Italic
import seforimapp.seforimapp.generated.resources.LibertinusSerif_Regular
import seforimapp.seforimapp.generated.resources.LibertinusSerif_SemiBold
import seforimapp.seforimapp.generated.resources.LibertinusSerif_SemiBoldItalic
import seforimapp.seforimapp.generated.resources.MiriamLibre_VariableFont_wght
import seforimapp.seforimapp.generated.resources.Tinos_Bold
import seforimapp.seforimapp.generated.resources.Tinos_BoldItalic
import seforimapp.seforimapp.generated.resources.Tinos_Italic
import seforimapp.seforimapp.generated.resources.Tinos_Regular
import seforimapp.seforimapp.generated.resources.frankruhllibre
import seforimapp.seforimapp.generated.resources.notorashihebrew
import seforimapp.seforimapp.generated.resources.notoserifhebrew

/**
 * Central catalog of font families available in the app, with stable codes and localized labels.
 * Codes are persisted in settings; do not change them without a migration.
 */
@Immutable
data class FontOption(
    val code: String,
    val label: StringResource,
)

object FontCatalog {
    // Public list of options in a stable order (no Composable calls here)
    val options: List<FontOption> = listOf(
        FontOption(code = "notoserifhebrew", label = Res.string.font_noto_serif_hebrew),
        FontOption(code = "notorashihebrew", label = Res.string.font_noto_rashi_hebrew),
        FontOption(code = "frankruhllibre", label = Res.string.font_frank_ruhl_libre),
        FontOption(code = "miriamlibre", label = Res.string.font_miriam_libre),
        FontOption(code = "cardo", label = Res.string.font_cardo),
        FontOption(code = "tinos", label = Res.string.font_tinos),
        FontOption(code = "libertinusserif", label = Res.string.font_libertinus_serif),
        FontOption(code = "bonanovasc", label = Res.string.font_bona_nova_sc),
    )

    @Composable
    fun familyFor(code: String): FontFamily = when (code) {
        "notoserifhebrew" -> FontFamily(Font(resource = Res.font.notoserifhebrew, weight = FontWeight.Normal))
        "notorashihebrew" -> FontFamily(Font(resource = Res.font.notorashihebrew, weight = FontWeight.Normal))
        "frankruhllibre" -> FontFamily(Font(resource = Res.font.frankruhllibre, weight = FontWeight.Normal))
        "miriamlibre" -> FontFamily(
            Font(resource = Res.font.MiriamLibre_VariableFont_wght, weight = FontWeight.Normal),
            Font(resource = Res.font.MiriamLibre_VariableFont_wght, weight = FontWeight.Medium),
            Font(resource = Res.font.MiriamLibre_VariableFont_wght, weight = FontWeight.SemiBold),
            Font(resource = Res.font.MiriamLibre_VariableFont_wght, weight = FontWeight.Bold),
        )
        "cardo" -> FontFamily(
            Font(resource = Res.font.Cardo_Regular, weight = FontWeight.Normal),
            Font(resource = Res.font.Cardo_Bold, weight = FontWeight.Bold),
            Font(resource = Res.font.Cardo_Italic, style = FontStyle.Italic),
        )
        "tinos" -> FontFamily(
            Font(resource = Res.font.Tinos_Regular, weight = FontWeight.Normal),
            Font(resource = Res.font.Tinos_Bold, weight = FontWeight.Bold),
            Font(resource = Res.font.Tinos_Italic, style = FontStyle.Italic),
            Font(resource = Res.font.Tinos_BoldItalic, weight = FontWeight.Bold, style = FontStyle.Italic),
        )
        "libertinusserif" -> FontFamily(
            Font(resource = Res.font.LibertinusSerif_Regular, weight = FontWeight.Normal),
            Font(resource = Res.font.LibertinusSerif_Italic, style = FontStyle.Italic),
            Font(resource = Res.font.LibertinusSerif_SemiBold, weight = FontWeight.SemiBold),
            Font(resource = Res.font.LibertinusSerif_SemiBoldItalic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
            Font(resource = Res.font.LibertinusSerif_Bold, weight = FontWeight.Bold),
            Font(resource = Res.font.LibertinusSerif_BoldItalic, weight = FontWeight.Bold, style = FontStyle.Italic),
        )
        "bonanovasc" -> FontFamily(
            Font(resource = Res.font.BonaNovaSC_Regular, weight = FontWeight.Normal),
            Font(resource = Res.font.BonaNovaSC_Bold, weight = FontWeight.Bold),
            Font(resource = Res.font.BonaNovaSC_Italic, style = FontStyle.Italic),
        )
        else -> FontFamily(Font(resource = Res.font.notoserifhebrew, weight = FontWeight.Normal))
    }
}
