package my.life.tracker.agenda.interfaces

import my.life.tracker.agenda.model.Actividad
import my.life.tracker.components.Celda

interface CeldaClickListener {

    fun onCeldaClicked(celda: Celda) : Unit
    fun onValueSelected(actividad: Actividad) : Unit
}