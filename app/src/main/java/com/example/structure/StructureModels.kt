package com.example.structure

enum class BondOrder(val label: String) {
    SINGLE("Single"),
    DOUBLE("Double"),
    TRIPLE("Triple")
}

enum class EditorTool(val label: String) {
    SELECT("Select"),
    SINGLE_BOND("Single —"),
    DOUBLE_BOND("Double ="),
    TRIPLE_BOND("Triple ≡"),
    ERASE("Erase")
}

data class ChemNode(
    val id: String,
    val x: Float,
    val y: Float,
    val label: String = "C",
    val charge: String = "",
    val isImplicitCarbon: Boolean = true
)

data class ChemBond(
    val id: String,
    val fromId: String,
    val toId: String,
    val order: BondOrder = BondOrder.SINGLE
)

data class AromaticCircle(
    val id: String,
    val centerX: Float,
    val centerY: Float,
    val radius: Float
)

data class StructureDocument(
    val nodes: List<ChemNode> = emptyList(),
    val bonds: List<ChemBond> = emptyList(),
    val aromaticCircles: List<AromaticCircle> = emptyList(),
    val title: String = "Untitled Molecule"
)
