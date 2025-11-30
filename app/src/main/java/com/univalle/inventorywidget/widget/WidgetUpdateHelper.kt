package com.univalle.inventorywidget.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context

object WidgetUpdateHelper {

    fun updateWidget(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, InventoryWidgetProvider::class.java)
        )

        if (widgetIds.isNotEmpty()) {
            InventoryWidgetProvider().onUpdate(context, appWidgetManager, widgetIds)
        }
    }
}
