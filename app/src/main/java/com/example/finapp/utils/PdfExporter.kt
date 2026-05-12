package com.example.finapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.example.finapp.data.entity.TransactionEntity
import java.io.File
import java.io.FileOutputStream
import java.util.*

object PdfExporter {

    fun exportToPdf(
        context: Context,
        transactions: List<TransactionEntity>,
        balance: Double,
        income: Double,
        expense: Double
    ) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        titlePaint.textSize = 20f
        titlePaint.isFakeBoldText = true
        canvas.drawText("Фінансовий звіт", 20f, 40f, titlePaint)

        paint.textSize = 14f
        canvas.drawText("Дата створення: ${FormatUtils.formatDate(System.currentTimeMillis())}", 20f, 70f, paint)
        canvas.drawText("Поточний баланс: ${FormatUtils.formatCurrency(balance)}", 20f, 90f, paint)
        canvas.drawText("Загальні доходи: ${FormatUtils.formatCurrency(income)}", 20f, 110f, paint)
        canvas.drawText("Загальні витрати: ${FormatUtils.formatCurrency(expense)}", 20f, 130f, paint)

        canvas.drawText("Список операцій:", 20f, 170f, titlePaint)

        var y = 200f
        transactions.take(20).forEach {
            val type = if (it.type == "INCOME") "+" else "-"
            val text = "${FormatUtils.formatDate(it.date)} | ${it.categoryName} | $type${it.amount} ${it.currency}"
            canvas.drawText(text, 20f, y, paint)
            y += 20f
        }

        pdfDocument.finishPage(page)

        val filePath = File(context.getExternalFilesDir(null), "FinanceReport.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
            Toast.makeText(context, "Звіт збережено: ${filePath.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Помилка при збереженні PDF", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()
    }
}
