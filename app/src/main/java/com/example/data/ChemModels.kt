package com.example.data

import androidx.compose.ui.graphics.Color

enum class KeyboardTab(val title: String, val shortTitle: String) {
    ORGANIC("Organic", "Organic"),
    INORGANIC("Inorganic", "Inorganic"),
    PHYSICAL("Physical", "Physical"),
    PERIODIC_TABLE("Periodic Table", "Periodic"),
    STRUCTURES("Structures", "Struct"),
    GREEK("Greek Symbols", "Greek"),
    HISTORY("History", "History")
}

enum class RingGlyphType {
    BENZENE,
    HEXAGON,
    CYCLOHEXANE,
    CYCLOPENTANE,
    CYCLOBUTANE,
    CYCLOPROPANE,
    BENZENE_KEKULE,
    AROMATIC_RING,
    NAPHTHALENE,
    ANTHRACENE,
    PHENANTHRENE,
    PYRIDINE,
    FURAN,
    THIOPHENE,
    PYRROLE
}

data class ChemKey(
    val id: String,
    val label: String,
    val insertText: String,
    val description: String,
    val category: String,
    val subLabel: String? = null,
    val isHighlighted: Boolean = false,
    val ringType: RingGlyphType? = null
)

enum class ElementCategory(val displayName: String, val badgeColor: Color) {
    ALKALI_METAL("Alkali Metal", Color(0xFFFB7185)),
    ALKALINE_EARTH("Alkaline Earth", Color(0xFFFB923C)),
    TRANSITION_METAL("Transition Metal", Color(0xFFFACC15)),
    POST_TRANSITION("Post-Transition", Color(0xFF34D399)),
    METALLOID("Metalloid", Color(0xFF2DD4BF)),
    REACTIVE_NONMETAL("Reactive Nonmetal", Color(0xFF38BDF8)),
    NOBLE_GAS("Noble Gas", Color(0xFFA78BFA)),
    LANTHANIDE("Lanthanide", Color(0xFFF472B6)),
    ACTINIDE("Actinide", Color(0xFFE879F9)),
    HALOGEN("Halogen", Color(0xFF22D3EE))
}

data class PeriodicElement(
    val atomicNumber: Int,
    val symbol: String,
    val name: String,
    val atomicMass: String,
    val category: ElementCategory,
    val group: Int,
    val period: Int,
    val electronConfig: String,
    val oxidationStates: String,
    val electronegativity: String,
    val stateAtRoomTemp: String
)

data class ChemTemplate(
    val id: String,
    val title: String,
    val formula: String,
    val category: String,
    val description: String
)

data class CustomShortcut(
    val id: String,
    val trigger: String,
    val expansion: String
)

data class SavedStructureSummary(
    val id: String,
    val name: String,
    val formula: String,
    val serializedJson: String,
    val timestamp: Long
)

data class KeyboardSettings(
    val hapticFeedback: Boolean = true,
    val showSubSupBar: Boolean = true,
    val compactKeys: Boolean = false,
    val defaultTab: KeyboardTab = KeyboardTab.ORGANIC
)
