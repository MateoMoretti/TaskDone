package my.life.tracker.agenda.interfaces

import my.life.tracker.IndexValue
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.components.Celda

interface CeldaClickListener {

    fun onCeldaClicked(celda: Celda)
    fun onValueSelected(actividad: Actividad)

    fun onHintAdded(indexValue: IndexValue)
}