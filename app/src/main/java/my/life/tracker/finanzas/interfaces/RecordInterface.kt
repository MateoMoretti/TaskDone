package my.life.tracker.finanzas.interfaces

import my.life.tracker.finanzas.model.Tag


interface RecordInterface {

    fun getSelectedTags(): ArrayList<Tag>

    fun onClickTag(tag: Tag)
}