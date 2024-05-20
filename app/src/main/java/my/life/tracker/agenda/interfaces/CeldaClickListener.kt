package my.life.tracker.agenda.interfaces

import my.life.tracker.components.Celda

interface CeldaClickListener {

    fun onCeldaClicked(celda: Celda) : Unit
    fun onValueSelected(celda: Celda) : Unit
}