package com.example.resumecreaterapp.Objects

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import java.io.IOException

object PdfUtils {

    /**
     * Export a view to PDF and save to a given Uri
     */
    fun savePdf(activity: Activity, view: View, uri: Uri) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()
        val page = document.startPage(pageInfo)
        view.draw(page.canvas)
        document.finishPage(page)

        try {
            activity.contentResolver.openOutputStream(uri)?.use { out ->
                document.writeTo(out)
            }
            Toast.makeText(activity, "✅ PDF saved", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(activity, "❌ Failed to save PDF", Toast.LENGTH_SHORT).show()
        } finally {
            document.close()
        }
    }

    /**
     * Export a view to PNG and save to a given Uri
     */
    fun savePng(activity: Activity, view: View, uri: Uri) {
        val bitmap = getBitmapFromView(view)
        try {
            activity.contentResolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Toast.makeText(activity, "✅ PNG saved", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(activity, "❌ Failed to save PNG", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Convert a view into a Bitmap
     */
    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    /**
     * Create a rounded bitmap from original
     */
    fun getRoundedBitmap(bitmap: Bitmap, cornerRadius: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)

        canvas.drawARGB(0, 0, 0, 0) // transparent bg
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    /**
     * Apply rounded image to ImageView
     */
    fun applyRoundedImage(imageView: ImageView, cornerRadius: Float) {
        val drawable = imageView.drawable as? BitmapDrawable ?: return
        val original = drawable.bitmap
        val rounded = getRoundedBitmap(original, cornerRadius)
        imageView.setImageBitmap(rounded)
        imageView.setBackgroundColor(Color.TRANSPARENT)
    }

    /**
     * Show export options (PDF / PNG)
     * Instead of saving directly, it triggers an Intent (handled in Activity)
     */
    fun showExportOptions(
        activity: Activity,
        view: View,
        fileName: String,
        onPdfIntentReady: (Intent) -> Unit,
        onPngIntentReady: (Intent) -> Unit
    ) {
        val options = arrayOf("Download as PDF", "Download as PNG")
        AlertDialog.Builder(activity)
            .setTitle("Download Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Intent for PDF
                        val intent = android.content.Intent(android.content.Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(android.content.Intent.CATEGORY_OPENABLE)
                            type = "application/pdf"
                            putExtra(android.content.Intent.EXTRA_TITLE, "$fileName.pdf")
                        }
                        onPdfIntentReady(intent)
                    }
                    1 -> {
                        // Intent for PNG
                        val intent = android.content.Intent(android.content.Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(android.content.Intent.CATEGORY_OPENABLE)
                            type = "image/png"
                            putExtra(android.content.Intent.EXTRA_TITLE, "$fileName.png")
                        }
                        onPngIntentReady(intent)
                    }
                }
            }
            .show()
    }
}
