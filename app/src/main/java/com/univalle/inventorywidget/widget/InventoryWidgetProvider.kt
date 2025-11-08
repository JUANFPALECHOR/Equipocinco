package com.univalle.inventorywidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.univalle.inventorywidget.MainActivity
import com.univalle.inventorywidget.R
import java.text.NumberFormat
import java.util.*

class InventoryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            actualizarWidget(context, manager, id)
        }
    }

    private fun actualizarWidget(context: Context, manager: AppWidgetManager, id: Int) {
        val prefs = context.getSharedPreferences("inventory_prefs", Context.MODE_PRIVATE)
        val mostrarSaldo = prefs.getBoolean("mostrarSaldo", false)

        val views = RemoteViews(context.packageName, R.layout.widget_inventory)

        // Accion de Mostra u ocultar saldo
        val saldoTexto = if (mostrarSaldo) {
            "$" + NumberFormat.getNumberInstance(Locale("es", "CO")).format(3326.0) + ",00"
        } else {
            "$****"
        }

        views.setTextViewText(R.id.tv_saldo, saldoTexto)
        views.setImageViewResource(
            R.id.iv_ojo,
            if (mostrarSaldo) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
        )

        // Acción del ojo
        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = "com.univalle.inventorywidget.TOGGLE_SALDO"
        }

        val togglePendingIntent = PendingIntent.getBroadcast(
            context, 0, toggleIntent, PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.iv_ojo, togglePendingIntent)

        // Acción de "Gestionar inventario" para solo la hu1
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.tv_gestionar, pendingIntent)

        manager.updateAppWidget(id, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "com.univalle.inventorywidget.TOGGLE_SALDO") {
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
}
