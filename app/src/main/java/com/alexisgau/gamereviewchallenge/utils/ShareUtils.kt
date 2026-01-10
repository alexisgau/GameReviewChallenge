package com.alexisgau.gamereviewchallenge.utils

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import androidx.core.content.FileProvider
import com.alexisgau.gamereviewchallenge.BuildConfig
import java.io.File
import java.io.FileOutputStream

object ShareUtils {

    fun captureAndShare(context: Context, view: View) {
        val activity = context as? Activity ?: return
        val window = activity.window


        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )

        val locationOfViewInWindow = IntArray(2)
        view.getLocationInWindow(locationOfViewInWindow)

        try {
            val rect = Rect(
                locationOfViewInWindow[0],
                locationOfViewInWindow[1],
                locationOfViewInWindow[0] + view.width,
                locationOfViewInWindow[1] + view.height
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PixelCopy.request(
                    window,
                    rect,
                    bitmap,
                    { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) {
                            // Si tuvo éxito, procedemos a guardar y compartir
                            saveAndShareBitmap(context, bitmap)
                        } else {
                            println("Error al capturar pantalla: PixelCopy falló")
                        }
                    },
                    Handler(Looper.getMainLooper())
                )
            } else {
                // Fallback para Android muy viejos
                val canvas = android.graphics.Canvas(bitmap)
                view.draw(canvas)
                saveAndShareBitmap(context, bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveAndShareBitmap(context: Context, bitmap: Bitmap) {
        try {
            //Guardar archivo en caché
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs() // Crear carpeta si no existe

            // Sobreescribimos siempre el mismo archivo para no llenar memoria
            val file = File(cachePath, "score_share.png")
            val stream = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            //  Obtener URI Segura
            //  BuildConfig.APPLICATION_ID para que coincida siempre
            val authority = "${BuildConfig.APPLICATION_ID}.provider"
            val contentUri: Uri = FileProvider.getUriForFile(context, authority, file)



            //  Crear Intent de Compartir
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)


                // Esto asegura que la app receptora tenga permiso real para leer la URI
                clipData = ClipData.newRawUri(null, contentUri)

                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            //  Lanzar
            context.startActivity(Intent.createChooser(shareIntent, "Share Score"))
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al compartir: ${e.message}")
        }
    }
}