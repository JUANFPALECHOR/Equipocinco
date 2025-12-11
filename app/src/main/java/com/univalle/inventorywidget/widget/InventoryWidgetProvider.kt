package com.univalle.inventorywidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.univalle.inventorywidget.R
import java.text.NumberFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking


class InventoryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            actualizarWidget(context, manager, id)
        }
    }
    private fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }


    private fun actualizarWidget(context: Context, manager: AppWidgetManager, id: Int) {
        val prefs = context.getSharedPreferences("inventory_prefs", Context.MODE_PRIVATE)
        val mostrarSaldo = prefs.getBoolean("mostrarSaldo", false)

        val views = RemoteViews(context.packageName, R.layout.widget_inventory)


        val totalCacheado = prefs.getFloat("saldo_total", 0f).toDouble()

        // Formatear con separadores y dos decimales
        val formato = NumberFormat.getNumberInstance(Locale("es", "CO"))
        formato.minimumFractionDigits = 2
        formato.maximumFractionDigits = 2

        val saldoTexto = if (mostrarSaldo && isUserLoggedIn()) {
            "$" + formato.format(totalCacheado)
        } else {
            "$****"
        }




        views.setTextViewText(R.id.tv_saldo, saldoTexto)
        views.setImageViewResource(
            R.id.iv_ojo,
            if (mostrarSaldo && isUserLoggedIn()) R.drawable.ic_eye_open else R.drawable.ic_eye_closed

        )

        // Acción del ojo
        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = "com.univalle.inventorywidget.TOGGLE_SALDO"
        }

        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            toggleIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.iv_ojo, togglePendingIntent)


        // Acción de "Gestionar inventario"
        val gestinarIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = "com.univalle.inventorywidget.GESTIONAR_INVENTARIO"
        }
        val gestionarPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            gestinarIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.tv_gestionar, gestionarPendingIntent)


        manager.updateAppWidget(id, views)

    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            "com.univalle.inventorywidget.TOGGLE_SALDO" -> {
                if (!isUserLoggedIn()) {
                    val loginIntent = Intent(context, com.univalle.inventorywidget.ui.login.LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("FROM_WIDGET", "EYE")
                    }
                    context.startActivity(loginIntent)
                } else {
                    val prefs = context.getSharedPreferences("inventory_prefs", Context.MODE_PRIVATE)
                    val actual = prefs.getBoolean("mostrarSaldo", false)
                    prefs.edit().putBoolean("mostrarSaldo", !actual).apply()

                    val manager = AppWidgetManager.getInstance(context)
                    val ids = manager.getAppWidgetIds(
                        ComponentName(context, InventoryWidgetProvider::class.java)
                    )
                    onUpdate(context, manager, ids)
                }
            }

            "com.univalle.inventorywidget.GESTIONAR_INVENTARIO" -> {

                if (!isUserLoggedIn()) {
                    val loginIntent = Intent(context, com.univalle.inventorywidget.ui.login.LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("FROM_WIDGET", "MANAGE")
                    }
                    context.startActivity(loginIntent)
                } else {

                    val mainIntent = Intent(context, com.univalle.inventorywidget.MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(mainIntent)
                }
            }
        }
    }


}
