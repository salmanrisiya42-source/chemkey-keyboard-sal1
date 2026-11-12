package com.example.structure

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

object StructureEngine {

    const val DEFAULT_BOND_LENGTH = 96f

    private fun newId(prefix: String): String = "${prefix}_${UUID.randomUUID().toString().take(8)}"

    fun createInitialBenzene(): StructureDocument {
        return insertRing(
            doc = StructureDocument(title = "Benzene"),
            sides = 6,
            isAromatic = true,
            kekuleAlternating = true,
            selectedNodeId = null,
            centerX = 0f,
            centerY = 0f
        ).first
    }

    fun insertRing(
        doc: StructureDocument,
        sides: Int,
        isAromatic: Boolean = false,
        kekuleAlternating: Boolean = false,
        heteroAtomAtZero: String? = null,
        selectedNodeId: String? = null,
        centerX: Float = 0f,
        centerY: Float = 0f
    ): Pair<StructureDocument, String?> {
        val bondLen = DEFAULT_BOND_LENGTH
        val radius = bondLen / (2f * sin((PI / sides).toFloat()))
        val anchor = selectedNodeId?.let { id -> doc.nodes.firstOrNull { it.id == id } }

        val cx: Float
        val cy: Float
        val attachAngle: Float
        if (anchor != null) {
            attachAngle = findBestOutwardAngle(doc, anchor)
            cx = anchor.x + cos(attachAngle) * (radius + bondLen * 0.65f)
            cy = anchor.y + sin(attachAngle) * (radius + bondLen * 0.65f)
        } else {
            val offset = if (doc.nodes.isEmpty()) 0f else (doc.nodes.size * 18f) % 140f
            cx = centerX + offset
            cy = centerY
            attachAngle = -PI.toFloat() / 2f
        }

        val ringNodes = mutableListOf<ChemNode>()
        val startAngle = if (sides == 6) -PI.toFloat() / 2f else -PI.toFloat() / 2f
        for (i in 0 until sides) {
            val angle = startAngle + (2f * PI.toFloat() * i / sides)
            val nx = cx + radius * cos(angle)
            val ny = cy + radius * sin(angle)
            val atomLabel = if (i == 0 && heteroAtomAtZero != null) heteroAtomAtZero else "C"
            val implicit = atomLabel == "C"
            ringNodes.add(
                ChemNode(
                    id = newId("n"),
                    x = nx,
                    y = ny,
                    label = atomLabel,
                    isImplicitCarbon = implicit
                )
            )
        }

        val newBonds = mutableListOf<ChemBond>()
        for (i in 0 until sides) {
            val a = ringNodes[i]
            val b = ringNodes[(i + 1) % sides]
            val order = if (kekuleAlternating && i % 2 == 0) BondOrder.DOUBLE else BondOrder.SINGLE
            newBonds.add(ChemBond(id = newId("b"), fromId = a.id, toId = b.id, order = order))
        }

        // If anchored to an existing node, connect the closest ring vertex
        if (anchor != null) {
            val closest = ringNodes.minByOrNull { hypot(it.x - anchor.x, it.y - anchor.y) }
            if (closest != null) {
                newBonds.add(ChemBond(id = newId("b"), fromId = anchor.id, toId = closest.id, order = BondOrder.SINGLE))
            }
        }

        val newCircles = if (isAromatic && !kekuleAlternating) {
            doc.aromaticCircles + AromaticCircle(
                id = newId("ar"),
                centerX = cx,
                centerY = cy,
                radius = radius * 0.56f
            )
        } else if (isAromatic) {
            // Also record aromatic ring center for clear visual benzene recognition if user wants Kekulé+circle or alternating
            doc.aromaticCircles + AromaticCircle(
                id = newId("ar"),
                centerX = cx,
                centerY = cy,
                radius = radius * 0.52f
            )
        } else {
            doc.aromaticCircles
        }

        val updatedDoc = doc.copy(
            nodes = doc.nodes + ringNodes,
            bonds = doc.bonds + newBonds,
            aromaticCircles = newCircles
        )
        return updatedDoc to ringNodes.firstOrNull()?.id
    }

    fun addCarbonChain(
        doc: StructureDocument,
        selectedNodeId: String?,
        steps: Int = 3,
        bondOrder: BondOrder = BondOrder.SINGLE
    ): Pair<StructureDocument, String?> {
        val nodes = doc.nodes.toMutableList()
        val bonds = doc.bonds.toMutableList()

        val startAnchor: ChemNode = selectedNodeId?.let { id -> nodes.firstOrNull { it.id == id } }
            ?: run {
                val startX = if (nodes.isEmpty()) -DEFAULT_BOND_LENGTH * 1.2f else nodes.maxOf { it.x } + DEFAULT_BOND_LENGTH
                ChemNode(id = newId("n"), x = startX, y = 0f, label = "C", isImplicitCarbon = true).also {
                    nodes.add(it)
                }
            }

        var prev: ChemNode = startAnchor
        var baseAngle = findBestOutwardAngle(doc, prev)
        // Snap base angle to nice 30-degree zig-zag
        for (step in 0 until steps) {
            val angle = if (step == 0) {
                baseAngle
            } else {
                val zig = if (step % 2 == 1) -PI.toFloat() / 6f else PI.toFloat() / 6f
                zig
            }
            val nx = prev.x + cos(angle) * DEFAULT_BOND_LENGTH
            val ny = prev.y + sin(angle) * DEFAULT_BOND_LENGTH
            val nextNode = ChemNode(id = newId("n"), x = nx, y = ny, label = "C", isImplicitCarbon = true)
            nodes.add(nextNode)
            bonds.add(ChemBond(id = newId("b"), fromId = prev.id, toId = nextNode.id, order = bondOrder))
            prev = nextNode
            baseAngle = angle
        }

        return doc.copy(nodes = nodes, bonds = bonds) to prev.id
    }

    fun attachSubstituentOrBranch(
        doc: StructureDocument,
        selectedNodeId: String?,
        label: String,
        bondOrder: BondOrder = BondOrder.SINGLE,
        isImplicitCarbon: Boolean = false
    ): Pair<StructureDocument, String?> {
        val anchor = selectedNodeId?.let { id -> doc.nodes.firstOrNull { it.id == id } }
            ?: doc.nodes.lastOrNull()
        if (anchor == null) {
            val single = ChemNode(
                id = newId("n"),
                x = 0f,
                y = 0f,
                label = label,
                isImplicitCarbon = isImplicitCarbon
            )
            return doc.copy(nodes = listOf(single)) to single.id
        }

        val angle = findBestOutwardAngle(doc, anchor)
        val nx = anchor.x + cos(angle) * DEFAULT_BOND_LENGTH
        val ny = anchor.y + sin(angle) * DEFAULT_BOND_LENGTH
        val newNode = ChemNode(
            id = newId("n"),
            x = nx,
            y = ny,
            label = label,
            isImplicitCarbon = isImplicitCarbon
        )
        val newBond = ChemBond(
            id = newId("b"),
            fromId = anchor.id,
            toId = newNode.id,
            order = bondOrder
        )
        return doc.copy(
            nodes = doc.nodes + newNode,
            bonds = doc.bonds + newBond
        ) to newNode.id
    }

    fun addBondToPosition(
        doc: StructureDocument,
        fromNodeId: String,
        targetX: Float,
        targetY: Float,
        bondOrder: BondOrder
    ): Pair<StructureDocument, String?> {
        val fromNode = doc.nodes.firstOrNull { it.id == fromNodeId } ?: return doc to null
        // Check if tapping near an existing node -> connect them
        val existingTarget = doc.nodes.firstOrNull { node ->
            node.id != fromNodeId && hypot(node.x - targetX, node.y - targetY) < 40f
        }
        if (existingTarget != null) {
            val existingBond = doc.bonds.firstOrNull {
                (it.fromId == fromNode.id && it.toId == existingTarget.id) ||
                    (it.fromId == existingTarget.id && it.toId == fromNode.id)
            }
            val updatedBonds = if (existingBond != null) {
                doc.bonds.map { b -> if (b.id == existingBond.id) b.copy(order = bondOrder) else b }
            } else {
                doc.bonds + ChemBond(id = newId("b"), fromId = fromNode.id, toId = existingTarget.id, order = bondOrder)
            }
            return doc.copy(bonds = updatedBonds) to existingTarget.id
        }

        // Snap angle to 30 degree increments and fixed bond length for crisp textbook appearance
        val rawAngle = atan2(targetY - fromNode.y, targetX - fromNode.x)
        val snappedAngle = ((rawAngle / (PI.toFloat() / 6f)).roundToInt()) * (PI.toFloat() / 6f)
        val nx = fromNode.x + cos(snappedAngle) * DEFAULT_BOND_LENGTH
        val ny = fromNode.y + sin(snappedAngle) * DEFAULT_BOND_LENGTH

        val newNode = ChemNode(id = newId("n"), x = nx, y = ny, label = "C", isImplicitCarbon = true)
        val newBond = ChemBond(id = newId("b"), fromId = fromNode.id, toId = newNode.id, order = bondOrder)
        return doc.copy(nodes = doc.nodes + newNode, bonds = doc.bonds + newBond) to newNode.id
    }

    fun applyChargeToNode(doc: StructureDocument, nodeId: String?, charge: String): StructureDocument {
        val targetId = nodeId ?: doc.nodes.lastOrNull()?.id ?: return doc
        val updatedNodes = doc.nodes.map { node ->
            if (node.id == targetId) {
                val nextCharge = if (node.charge == charge) "" else charge
                node.copy(charge = nextCharge, isImplicitCarbon = if (nextCharge.isNotEmpty()) false else node.isImplicitCarbon)
            } else {
                node
            }
        }
        return doc.copy(nodes = updatedNodes)
    }

    fun relabelNode(doc: StructureDocument, nodeId: String?, newLabel: String): StructureDocument {
        val targetId = nodeId ?: doc.nodes.lastOrNull()?.id ?: return doc
        val updatedNodes = doc.nodes.map { node ->
            if (node.id == targetId) {
                node.copy(label = newLabel, isImplicitCarbon = newLabel == "C")
            } else {
                node
            }
        }
        return doc.copy(nodes = updatedNodes)
    }

    fun cycleBondOrder(doc: StructureDocument, bondId: String, targetOrder: BondOrder? = null): StructureDocument {
        val updated = doc.bonds.map { b ->
            if (b.id == bondId) {
                val next = targetOrder ?: when (b.order) {
                    BondOrder.SINGLE -> BondOrder.DOUBLE
                    BondOrder.DOUBLE -> BondOrder.TRIPLE
                    BondOrder.TRIPLE -> BondOrder.SINGLE
                }
                b.copy(order = next)
            } else {
                b
            }
        }
        return doc.copy(bonds = updated)
    }

    fun eraseNode(doc: StructureDocument, nodeId: String): StructureDocument {
        val removedNode = doc.nodes.firstOrNull { it.id == nodeId }
        val remainingNodes = doc.nodes.filterNot { it.id == nodeId }
        val remainingBonds = doc.bonds.filterNot { it.fromId == nodeId || it.toId == nodeId }
        val remainingCircles = if (removedNode != null) {
            doc.aromaticCircles.filterNot { hypot(it.centerX - removedNode.x, it.centerY - removedNode.y) < it.radius * 2.2f }
        } else {
            doc.aromaticCircles
        }
        return doc.copy(nodes = remainingNodes, bonds = remainingBonds, aromaticCircles = remainingCircles)
    }

    fun eraseBond(doc: StructureDocument, bondId: String): StructureDocument {
        return doc.copy(bonds = doc.bonds.filterNot { it.id == bondId })
    }

    fun findBestOutwardAngle(doc: StructureDocument, anchor: ChemNode): Float {
        val connectedNodes = doc.bonds.mapNotNull { bond ->
            when {
                bond.fromId == anchor.id -> doc.nodes.firstOrNull { it.id == bond.toId }
                bond.toId == anchor.id -> doc.nodes.firstOrNull { it.id == bond.fromId }
                else -> null
            }
        }
        if (connectedNodes.isEmpty()) return -PI.toFloat() / 6f

        val existingAngles = connectedNodes.map { atan2(it.y - anchor.y, it.x - anchor.x) }
        val candidates = (0 until 12).map { it * (PI.toFloat() / 6f) - PI.toFloat() }
        return candidates.maxByOrNull { cand ->
            existingAngles.minOf { ex ->
                var diff = kotlin.math.abs(cand - ex)
                if (diff > PI.toFloat()) diff = (2f * PI.toFloat()) - diff
                diff
            }
        } ?: 0f
    }

    fun computeCopyableSummary(doc: StructureDocument): String {
        if (doc.nodes.isEmpty()) return ""
        val subs = doc.nodes.filter { !it.isImplicitCarbon || it.charge.isNotEmpty() }
            .map { node ->
                val clean = node.label.removePrefix("–").removePrefix("-")
                if (node.charge.isNotEmpty()) "$clean${node.charge}" else clean
            }
        val ringCount = doc.aromaticCircles.size
        val cCount = doc.nodes.count { it.label == "C" }
        val doubleCount = doc.bonds.count { it.order == BondOrder.DOUBLE }
        val tripleCount = doc.bonds.count { it.order == BondOrder.TRIPLE }

        return buildString {
            if (ringCount > 0) {
                append("⌬")
                val nonCarbonSubs = subs.filter { it != "C" }
                if (nonCarbonSubs.isNotEmpty()) {
                    append(nonCarbonSubs.joinToString(separator = "") { "–$it" })
                }
            } else {
                append("C")
                if (cCount > 1) append(toSubscript(cCount))
                if (doubleCount > 0) append(" (C=C×$doubleCount)")
                if (tripleCount > 0) append(" (C≡C×$tripleCount)")
                val extra = subs.filter { it != "C" }
                if (extra.isNotEmpty()) {
                    append(" ")
                    append(extra.joinToString(" "))
                }
            }
        }
    }

    fun toSubscript(number: Int): String {
        val map = mapOf(
            '0' to '₀', '1' to '₁', '2' to '₂', '3' to '₃', '4' to '₄',
            '5' to '₅', '6' to '₆', '7' to '₇', '8' to '₈', '9' to '₉'
        )
        return number.toString().map { map[it] ?: it }.joinToString("")
    }

    fun serialize(doc: StructureDocument): String {
        val root = JSONObject()
        root.put("title", doc.title)
        val nodesArr = JSONArray()
        doc.nodes.forEach { n ->
            nodesArr.put(JSONObject().apply {
                put("id", n.id)
                put("x", n.x.toDouble())
                put("y", n.y.toDouble())
                put("label", n.label)
                put("charge", n.charge)
                put("implicit", n.isImplicitCarbon)
            })
        }
        val bondsArr = JSONArray()
        doc.bonds.forEach { b ->
            bondsArr.put(JSONObject().apply {
                put("id", b.id)
                put("from", b.fromId)
                put("to", b.toId)
                put("order", b.order.name)
            })
        }
        val circlesArr = JSONArray()
        doc.aromaticCircles.forEach { c ->
            circlesArr.put(JSONObject().apply {
                put("id", c.id)
                put("cx", c.centerX.toDouble())
                put("cy", c.centerY.toDouble())
                put("r", c.radius.toDouble())
            })
        }
        root.put("nodes", nodesArr)
        root.put("bonds", bondsArr)
        root.put("circles", circlesArr)
        return root.toString()
    }

    fun deserialize(raw: String): StructureDocument? {
        return try {
            val root = JSONObject(raw)
            val title = root.optString("title", "Structure")
            val nodesArr = root.optJSONArray("nodes") ?: JSONArray()
            val nodes = List(nodesArr.length()) { idx ->
                val o = nodesArr.getJSONObject(idx)
                ChemNode(
                    id = o.optString("id"),
                    x = o.optDouble("x").toFloat(),
                    y = o.optDouble("y").toFloat(),
                    label = o.optString("label", "C"),
                    charge = o.optString("charge", ""),
                    isImplicitCarbon = o.optBoolean("implicit", true)
                )
            }
            val bondsArr = root.optJSONArray("bonds") ?: JSONArray()
            val bonds = List(bondsArr.length()) { idx ->
                val o = bondsArr.getJSONObject(idx)
                ChemBond(
                    id = o.optString("id"),
                    fromId = o.optString("from"),
                    toId = o.optString("to"),
                    order = runCatching { BondOrder.valueOf(o.optString("order")) }
                        .getOrDefault(BondOrder.SINGLE)
                )
            }
            val circlesArr = root.optJSONArray("circles") ?: JSONArray()
            val circles = List(circlesArr.length()) { idx ->
                val o = circlesArr.getJSONObject(idx)
                AromaticCircle(
                    id = o.optString("id"),
                    centerX = o.optDouble("cx").toFloat(),
                    centerY = o.optDouble("cy").toFloat(),
                    radius = o.optDouble("r").toFloat()
                )
            }
            StructureDocument(nodes = nodes, bonds = bonds, aromaticCircles = circles, title = title)
        } catch (_: Exception) {
            null
        }
    }
}
