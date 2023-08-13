package mis.finanzas.diarias.finanzas.interfaces

import mis.finanzas.diarias.finanzas.model.Tag


interface RecordInterface {

    fun getSelectedTags(): ArrayList<Tag>

    fun onClickTag(tag: Tag)
}