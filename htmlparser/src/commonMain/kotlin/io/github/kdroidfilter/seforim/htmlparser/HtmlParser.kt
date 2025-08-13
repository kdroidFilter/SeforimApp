package io.github.kdroidfilter.seforim.htmlparser

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

data class ParsedHtmlElement(
    val text: String,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isHeader: Boolean = false,
    val headerLevel: Int? = null,
    val commentator: String? = null,
    val commentatorOrder: String? = null,
    val isLineBreak: Boolean = false
)

class HtmlParser {

    fun parse(html: String): List<ParsedHtmlElement> {
        val doc = Jsoup.parse(html)
        val out = mutableListOf<ParsedHtmlElement>()
        for (child in doc.body().childNodes()) {
            processNode(
                node = child,
                list = out,
                isBold = false,
                isItalic = false,
                isHeader = false,
                headerLevel = null,
                commentator = null,
                commentatorOrder = null
            )
        }
        // Avoids a "line after": removes terminal <br> tags
        while (out.lastOrNull()?.isLineBreak == true) {
            out.removeAt(out.lastIndex)
        }
        return out
    }

    private fun processNode(
        node: Node,
        list: MutableList<ParsedHtmlElement>,
        isBold: Boolean,
        isItalic: Boolean,
        isHeader: Boolean,
        headerLevel: Int?,
        commentator: String?,
        commentatorOrder: String?
    ) {
        when (node) {
            is TextNode -> {
                appendSegment(
                    list = list,
                    textRaw = node.text(),
                    isBold = isBold,
                    isItalic = isItalic,
                    isHeader = isHeader,
                    headerLevel = headerLevel,
                    commentator = commentator,
                    commentatorOrder = commentatorOrder
                )
            }
            is Element -> {
                val tag = node.normalName()

                if (tag == "br") {
                    appendLineBreak(list)
                    return
                }

                val nextBold = isBold || tag == "b" || tag == "strong"
                val nextItalic = isItalic || tag == "i" || tag == "em"
                val isHeaderTag = tag.length == 2 && tag[0] == 'h' && tag[1].isDigit()
                val nextHeader = isHeader || isHeaderTag
                val nextHeaderLevel = if (isHeaderTag) tag.substring(1).toInt() else headerLevel

                if (node.childNodeSize() == 1 && node.childNode(0) is TextNode) {
                    appendSegment(
                        list = list,
                        textRaw = (node.childNode(0) as TextNode).text(),
                        isBold = nextBold,
                        isItalic = nextItalic,
                        isHeader = nextHeader,
                        headerLevel = nextHeaderLevel,
                        commentator = commentator,
                        commentatorOrder = commentatorOrder
                    )
                    return
                }

                for (child in node.childNodes()) {
                    processNode(
                        node = child,
                        list = list,
                        isBold = nextBold,
                        isItalic = nextItalic,
                        isHeader = nextHeader,
                        headerLevel = nextHeaderLevel,
                        commentator = commentator,
                        commentatorOrder = commentatorOrder
                    )
                }
            }
        }
    }

    private fun appendSegment(
        list: MutableList<ParsedHtmlElement>,
        textRaw: String,
        isBold: Boolean,
        isItalic: Boolean,
        isHeader: Boolean,
        headerLevel: Int?,
        commentator: String?,
        commentatorOrder: String?
    ) {
        // Normalizes multiple spaces into a single space, but preserves leading/trailing spaces
        val normalizedText = textRaw.replace(Regex("\\s+"), " ")

        // Do not add empty segments (only spaces)
        if (normalizedText.isBlank()) return

        // Determines whether to preserve leading and trailing spaces
        val hasLeadingSpace = textRaw.isNotEmpty() && textRaw.first().isWhitespace()
        val hasTrailingSpace = textRaw.isNotEmpty() && textRaw.last().isWhitespace()

        // Trim le texte pour le contenu réel
        val trimmedText = normalizedText.trim()
        if (trimmedText.isEmpty()) return

        if (list.isNotEmpty()) {
            val last = list.last()
            val sameStyle =
                !last.isLineBreak &&
                        last.isBold == isBold &&
                        last.isItalic == isItalic &&
                        last.isHeader == isHeader &&
                        last.headerLevel == headerLevel &&
                        last.commentator == commentator &&
                        last.commentatorOrder == commentatorOrder

            if (sameStyle) {
                // Fusion avec le segment précédent du même style
                val separator = when {
                    // Si le segment actuel a un espace au début ou le précédent à la fin
                    hasLeadingSpace || last.text.lastOrNull()?.isWhitespace() == true -> ""
                    // Sinon, vérifie si on a besoin d'un espace entre les deux
                    needsSpaceBetween(last.text, trimmedText) -> " "
                    else -> ""
                }

                val newText = last.text + separator + trimmedText
                list[list.lastIndex] = last.copy(text = newText)

                // Gère l'espace de fin si nécessaire
                if (hasTrailingSpace && !newText.endsWith(" ")) {
                    list[list.lastIndex] = last.copy(text = newText + " ")
                }
                return
            }
        }

        // New segment with a different style
        // Adds a space at the beginning if necessary and if the previous segment does not end with a space
        val needsLeadingSpace = hasLeadingSpace &&
                list.isNotEmpty() &&
                !list.last().isLineBreak &&
                !list.last().text.endsWith(" ")

        val finalText = when {
            needsLeadingSpace && hasTrailingSpace -> " $trimmedText "
            needsLeadingSpace -> " $trimmedText"
            hasTrailingSpace -> "$trimmedText "
            else -> trimmedText
        }

        list.add(
            ParsedHtmlElement(
                text = finalText,
                isBold = isBold,
                isItalic = isItalic,
                isHeader = isHeader,
                headerLevel = headerLevel,
                commentator = commentator,
                commentatorOrder = commentatorOrder
            )
        )
    }

    // Adds a line break element only if necessary
    private fun appendLineBreak(list: MutableList<ParsedHtmlElement>) {
        if (list.isEmpty()) return                         // no <br> at the beginning
        val last = list.last()
        if (!last.isLineBreak) {                           // avoids <br><br> → double line
            list.add(ParsedHtmlElement(text = "", isLineBreak = true))
        }
    }

    private fun needsSpaceBetween(a: String, b: String): Boolean {
        return a.isNotEmpty() && !a.last().isWhitespace() &&
                b.isNotEmpty() && !b.first().isWhitespace()
    }
}