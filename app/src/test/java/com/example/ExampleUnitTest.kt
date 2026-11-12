package com.example

import com.example.data.GreekAndTemplatesData
import com.example.data.InorganicChemData
import com.example.data.OrganicChemData
import com.example.data.PeriodicTableData
import com.example.data.PhysicalChemData
import com.example.structure.BondOrder
import com.example.structure.StructureDocument
import com.example.structure.StructureEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun periodicTable_hasAll118UniqueElements() {
        val elements = PeriodicTableData.elements
        assertEquals(118, elements.size)
        assertEquals(118, elements.map { it.atomicNumber }.distinct().size)
        assertEquals(118, elements.map { it.symbol }.distinct().size)
        assertEquals("H", elements.first().symbol)
        assertEquals("Og", elements.last().symbol)
    }

    @Test
    fun chemistryCategories_includeRequiredSymbols() {
        assertTrue(OrganicChemData.ringStructures.any { it.insertText == "⌬" })
        assertTrue(OrganicChemData.functionalGroups.any { it.insertText == "–COOH" })
        assertTrue(OrganicChemData.reactionArrows.any { it.insertText == "⇌" })
        assertTrue(InorganicChemData.commonIons.any { it.insertText == "SO₄²⁻" })
        assertTrue(InorganicChemData.specialSymbols.any { it.insertText == "≡" })
        assertTrue(PhysicalChemData.thermodynamics.any { it.insertText == "ΔG" })
        assertTrue(PhysicalChemData.electrochemistry.any { it.insertText == "E°cell" })
        assertEquals(10, GreekAndTemplatesData.subscripts.size)
        assertEquals(12, GreekAndTemplatesData.superscripts.size)
    }

    @Test
    fun structureEngine_insertsBenzeneAndAttachesGroup() {
        val doc0 = StructureDocument()
        val (withBenzene, vertexId) = StructureEngine.insertRing(
            doc = doc0,
            sides = 6,
            isAromatic = true,
            kekuleAlternating = true
        )
        assertEquals(6, withBenzene.nodes.size)
        assertEquals(6, withBenzene.bonds.size)
        assertEquals(1, withBenzene.aromaticCircles.size)

        val (withOh, ohId) = StructureEngine.attachSubstituentOrBranch(
            doc = withBenzene,
            selectedNodeId = vertexId,
            label = "OH",
            bondOrder = BondOrder.SINGLE
        )
        assertEquals(7, withOh.nodes.size)
        assertEquals(7, withOh.bonds.size)
        assertNotNull(ohId)
        assertTrue(StructureEngine.computeCopyableSummary(withOh).contains("⌬"))
    }

    @Test
    fun searchAll_findsElementsRingsAndIons() {
        assertTrue(GreekAndTemplatesData.searchAll("benzene").any { it.insertText == "⌬" })
        assertTrue(GreekAndTemplatesData.searchAll("79").any { it.insertText == "Au" })
        assertTrue(GreekAndTemplatesData.searchAll("sulfate").any { it.insertText == "SO₄²⁻" })
    }
}
