package org.javacs.kt.diagnostic

import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.Diagnostic as LangServerDiagnostic
import org.javacs.kt.position.range
import org.javacs.kt.util.toPath
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.Diagnostic as KotlinDiagnostic
import java.nio.file.Path

fun convertDiagnostic(diagnostic: KotlinDiagnostic): List<Pair<Path, LangServerDiagnostic>> {
    val path = diagnostic.psiFile.toPath()
    val content = diagnostic.psiFile.text

    return diagnostic.textRanges.map {
        val d = LangServerDiagnostic(
                range(content, it),
                message(diagnostic),
                severity(diagnostic.severity),
                "kotlin",
                code(diagnostic))
        Pair(path, d)
    }
}

private fun code(diagnostic: KotlinDiagnostic) =
        diagnostic.factory.name

private fun message(diagnostic: KotlinDiagnostic) =
        DefaultErrorMessages.render(diagnostic)

private fun severity(severity: Severity): DiagnosticSeverity =
        when (severity) {
            Severity.INFO -> DiagnosticSeverity.Information
            Severity.ERROR -> DiagnosticSeverity.Error
            Severity.WARNING -> DiagnosticSeverity.Warning
        }

