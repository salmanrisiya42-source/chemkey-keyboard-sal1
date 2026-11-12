package com.example.ime

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
import com.example.keyboard.ChemistryKeyboardView
import com.example.keyboard.KeyboardTarget
import com.example.storage.ChemStorageRepository
import com.example.ui.theme.ChemistryKeyboardTheme

class ChemistryInputMethodService :
    InputMethodService(),
    LifecycleOwner,
    SavedStateRegistryOwner,
    ViewModelStoreOwner,
    KeyboardTarget {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    private lateinit var repository: ChemStorageRepository

    private sealed class EditAction {
        data class Insert(val text: String) : EditAction()
        data class Delete(val text: String) : EditAction()
    }

    private val undoStack = ArrayDeque<EditAction>()
    private val redoStack = ArrayDeque<EditAction>()

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val viewModelStore: ViewModelStore
        get() = store

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        repository = ChemStorageRepository.getInstance(applicationContext)
    }

    override fun onCreateInputView(): View {
        val decorView = window?.window?.decorView
        decorView?.setViewTreeLifecycleOwner(this)
        decorView?.setViewTreeSavedStateRegistryOwner(this)
        decorView?.setViewTreeViewModelStoreOwner(this)

        return ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@ChemistryInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@ChemistryInputMethodService)
            setViewTreeViewModelStoreOwner(this@ChemistryInputMethodService)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ChemistryKeyboardTheme {
                    ChemistryKeyboardView(
                        repository = repository,
                        target = this@ChemistryInputMethodService,
                        heightDp = 344,
                        onFeedbackMessage = { msg ->
                            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }

    override fun commitText(text: String) {
        val ic = currentInputConnection ?: return

        // Expand custom shortcuts when user taps Space right after a trigger (e.g. "/bz ")
        if (text == " ") {
            val before = ic.getTextBeforeCursor(24, 0)?.toString().orEmpty()
            val lastToken = before.takeLastWhile { !it.isWhitespace() }
            if (lastToken.isNotEmpty()) {
                val shortcut = repository.findShortcutExpansion(lastToken)
                if (shortcut != null) {
                    ic.beginBatchEdit()
                    ic.deleteSurroundingText(lastToken.length, 0)
                    ic.commitText(shortcut.expansion + " ", 1)
                    ic.endBatchEdit()
                    pushUndo(EditAction.Insert(shortcut.expansion + " "))
                    repository.recordRecentSymbol(shortcut.expansion)
                    return
                }
            }
        }

        ic.commitText(text, 1)
        pushUndo(EditAction.Insert(text))
    }

    override fun deleteBack() {
        val ic = currentInputConnection ?: return
        val selected = ic.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            ic.commitText("", 1)
            pushUndo(EditAction.Delete(selected.toString()))
            return
        }

        val before = ic.getTextBeforeCursor(4, 0)?.toString().orEmpty()
        if (before.isEmpty()) {
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            return
        }

        val lastCodePoint = Character.codePointBefore(before, before.length)
        val charCount = Character.charCount(lastCodePoint)
        val deleted = before.takeLast(charCount)
        ic.deleteSurroundingText(charCount, 0)
        pushUndo(EditAction.Delete(deleted))
    }

    override fun performEnter() {
        val ic = currentInputConnection ?: return
        val info = currentInputEditorInfo
        val actionId = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
        val isMultiLine = (info?.inputType ?: 0) and EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE != 0

        if (!isMultiLine && actionId != EditorInfo.IME_ACTION_NONE && actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.performEditorAction(actionId)
        } else {
            ic.commitText("\n", 1)
            pushUndo(EditAction.Insert("\n"))
        }
    }

    override fun undo() {
        val ic = currentInputConnection ?: return
        val last = undoStack.removeLastOrNull()
        if (last == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ic.performContextMenuAction(android.R.id.undo)
            }
            return
        }
        when (last) {
            is EditAction.Insert -> {
                ic.deleteSurroundingText(last.text.length, 0)
                redoStack.addLast(last)
            }
            is EditAction.Delete -> {
                ic.commitText(last.text, 1)
                redoStack.addLast(last)
            }
        }
    }

    override fun redo() {
        val ic = currentInputConnection ?: return
        val next = redoStack.removeLastOrNull()
        if (next == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ic.performContextMenuAction(android.R.id.redo)
            }
            return
        }
        when (next) {
            is EditAction.Insert -> {
                ic.commitText(next.text, 1)
                undoStack.addLast(next)
            }
            is EditAction.Delete -> {
                ic.deleteSurroundingText(next.text.length, 0)
                undoStack.addLast(next)
            }
        }
    }

    override fun copySelectionOrAll(): String? {
        val ic = currentInputConnection ?: return null
        val selected = ic.getSelectedText(0)?.toString()
        if (!selected.isNullOrEmpty()) return selected
        val before = ic.getTextBeforeCursor(500, 0)?.toString().orEmpty()
        val after = ic.getTextAfterCursor(500, 0)?.toString().orEmpty()
        return (before + after).takeIf { it.isNotEmpty() }
    }

    override fun pasteText(text: String) {
        commitText(text)
    }

    override fun switchKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showInputMethodPicker()
    }

    override fun openStructureEditor() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(MainActivity.EXTRA_START_TAB, "structure_editor")
        }
        startActivity(intent)
    }

    private fun pushUndo(action: EditAction) {
        if (undoStack.size >= 80) undoStack.removeFirst()
        undoStack.addLast(action)
        redoStack.clear()
    }
}
