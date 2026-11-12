package com.example.keyboard

interface KeyboardTarget {
    fun commitText(text: String)
    fun deleteBack()
    fun performEnter()
    fun undo()
    fun redo()
    fun copySelectionOrAll(): String?
    fun pasteText(text: String)
    fun switchKeyboard()
    fun openStructureEditor()
}
