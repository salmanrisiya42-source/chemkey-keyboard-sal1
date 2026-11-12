package com.example

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.storage.ChemStorageRepository
import com.example.structure.BondOrder
import com.example.structure.StructureDocument
import com.example.structure.StructureEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appNameAndImeService_areProperlyConfigured() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("Chemistry Keyboard – Pro", context.getString(R.string.app_name))

        val imeIntent = Intent("android.view.InputMethod")
        imeIntent.setPackage(context.packageName)
        val services = context.packageManager.queryIntentServices(imeIntent, 0)
        assertTrue("IME service should be registered", services.isNotEmpty())
    }

    @Test
    fun storageRepositoryAndStructureSerialization_workOffline() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = ChemStorageRepository.getInstance(context)
        repo.addOrUpdateShortcut("/testchem", "Al₂(SO₄)₃")
        assertEquals("Al₂(SO₄)₃", repo.findShortcutExpansion("/testchem")?.expansion)

        val (withBenzene, vertexId) = StructureEngine.insertRing(
            doc = StructureDocument(),
            sides = 6,
            isAromatic = true,
            kekuleAlternating = true
        )
        val (withOh, _) = StructureEngine.attachSubstituentOrBranch(
            doc = withBenzene,
            selectedNodeId = vertexId,
            label = "OH",
            bondOrder = BondOrder.SINGLE
        )
        val json = StructureEngine.serialize(withOh)
        val restored = StructureEngine.deserialize(json)
        assertNotNull(restored)
        assertEquals(7, restored!!.nodes.size)
    }

    @Test
    fun mainNavigationAndBenzeneKey_workEndToEnd() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("scratchpad_output").assertIsDisplayed()
        composeRule.onNodeWithTag("key_org_ring_benzene").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("scratchpad_output").assertTextContains("⌬", substring = true)

        composeRule.onNodeWithTag("nav_structures").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("editor_insert_benzene").assertIsDisplayed()

        composeRule.onNodeWithTag("nav_periodic").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("periodic_search_field").assertIsDisplayed()
    }
}
