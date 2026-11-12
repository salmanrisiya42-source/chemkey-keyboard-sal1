package com.example.data

object OrganicChemData {

    val ringStructures: List<ChemKey> = listOf(
        ChemKey(
            id = "org_ring_benzene",
            label = "⌬",
            subLabel = "Benzene",
            insertText = "⌬",
            description = "Benzene ring (C₆H₆)",
            category = "Rings",
            isHighlighted = true,
            ringType = RingGlyphType.BENZENE
        ),
        ChemKey(
            id = "org_ring_kekule",
            label = "C₆H₆",
            subLabel = "Kekulé",
            insertText = "⌬",
            description = "Benzene with alternating double bonds (Kekulé)",
            category = "Rings",
            isHighlighted = true,
            ringType = RingGlyphType.BENZENE_KEKULE
        ),
        ChemKey(
            id = "org_ring_hexagon",
            label = "⬡",
            subLabel = "Hexagon",
            insertText = "⬡",
            description = "Hexagon ring",
            category = "Rings",
            ringType = RingGlyphType.HEXAGON
        ),
        ChemKey(
            id = "org_ring_cyclohexane",
            label = "C₆H₁₂",
            subLabel = "Cyclohexane",
            insertText = "C₆H₁₂",
            description = "Cyclohexane ring",
            category = "Rings",
            ringType = RingGlyphType.CYCLOHEXANE
        ),
        ChemKey(
            id = "org_ring_cyclopentane",
            label = "C₅H₁₀",
            subLabel = "Cyclopentane",
            insertText = "C₅H₁₀",
            description = "Cyclopentane ring",
            category = "Rings",
            ringType = RingGlyphType.CYCLOPENTANE
        ),
        ChemKey(
            id = "org_ring_cyclobutane",
            label = "C₄H₈",
            subLabel = "Cyclobutane",
            insertText = "C₄H₈",
            description = "Cyclobutane ring",
            category = "Rings",
            ringType = RingGlyphType.CYCLOBUTANE
        ),
        ChemKey(
            id = "org_ring_cyclopropane",
            label = "C₃H₆",
            subLabel = "Cyclopropane",
            insertText = "C₃H₆",
            description = "Cyclopropane ring",
            category = "Rings",
            ringType = RingGlyphType.CYCLOPROPANE
        ),
        ChemKey(
            id = "org_ring_aromatic",
            label = "Ar–",
            subLabel = "Aromatic",
            insertText = "Ar–",
            description = "Aromatic ring substituent",
            category = "Rings",
            ringType = RingGlyphType.AROMATIC_RING
        ),
        ChemKey(
            id = "org_ring_naphthalene",
            label = "C₁₀H₈",
            subLabel = "Naphthalene",
            insertText = "C₁₀H₈",
            description = "Naphthalene fused bicyclic aromatic ring",
            category = "Rings",
            ringType = RingGlyphType.NAPHTHALENE
        ),
        ChemKey(
            id = "org_ring_anthracene",
            label = "C₁₄H₁₀",
            subLabel = "Anthracene",
            insertText = "C₁₄H₁₀",
            description = "Anthracene linear tricyclic aromatic ring",
            category = "Rings",
            ringType = RingGlyphType.ANTHRACENE
        ),
        ChemKey(
            id = "org_ring_phenanthrene",
            label = "Phen",
            subLabel = "Phenanthrene",
            insertText = "C₁₄H₁₀ (Phenanthrene)",
            description = "Phenanthrene angular tricyclic aromatic ring",
            category = "Rings",
            ringType = RingGlyphType.PHENANTHRENE
        ),
        ChemKey(
            id = "org_ring_pyridine",
            label = "C₅H₅N",
            subLabel = "Pyridine",
            insertText = "C₅H₅N",
            description = "Pyridine heteroaromatic ring",
            category = "Rings",
            ringType = RingGlyphType.PYRIDINE
        ),
        ChemKey(
            id = "org_ring_furan",
            label = "C₄H₄O",
            subLabel = "Furan",
            insertText = "C₄H₄O",
            description = "Furan 5-membered oxygen heterocycle",
            category = "Rings",
            ringType = RingGlyphType.FURAN
        ),
        ChemKey(
            id = "org_ring_thiophene",
            label = "C₄H₄S",
            subLabel = "Thiophene",
            insertText = "C₄H₄S",
            description = "Thiophene 5-membered sulfur heterocycle",
            category = "Rings",
            ringType = RingGlyphType.THIOPHENE
        ),
        ChemKey(
            id = "org_ring_pyrrole",
            label = "C₄H₅N",
            subLabel = "Pyrrole",
            insertText = "C₄H₅N",
            description = "Pyrrole 5-membered nitrogen heterocycle",
            category = "Rings",
            ringType = RingGlyphType.PYRROLE
        )
    )

    val functionalGroups: List<ChemKey> = listOf(
        ChemKey("org_fg_oh", "–OH", "–OH", "Hydroxyl (Alcohol / Phenol)", "Functional Groups", "Hydroxyl", isHighlighted = true),
        ChemKey("org_fg_cho", "–CHO", "–CHO", "Aldehyde group", "Functional Groups", "Aldehyde"),
        ChemKey("org_fg_cooh", "–COOH", "–COOH", "Carboxylic acid group", "Functional Groups", "Carboxyl", isHighlighted = true),
        ChemKey("org_fg_coor", "–COOR", "–COOR", "Ester group", "Functional Groups", "Ester"),
        ChemKey("org_fg_co", "–CO–", "–CO–", "Carbonyl / Ketone bridge", "Functional Groups", "Ketone"),
        ChemKey("org_fg_nh2", "–NH₂", "–NH₂", "Primary amino group", "Functional Groups", "Amine", isHighlighted = true),
        ChemKey("org_fg_nh", "–NH–", "–NH–", "Secondary amine bridge", "Functional Groups", "Imine/Amine"),
        ChemKey("org_fg_no2", "–NO₂", "–NO₂", "Nitro group", "Functional Groups", "Nitro"),
        ChemKey("org_fg_cn", "–CN", "–CN", "Nitrile / Cyano group", "Functional Groups", "Nitrile"),
        ChemKey("org_fg_conh2", "–CONH₂", "–CONH₂", "Amide group", "Functional Groups", "Amide"),
        ChemKey("org_fg_x", "–X", "–X", "Halide substituent (F, Cl, Br, I)", "Functional Groups", "Halide"),
        ChemKey("org_fg_o", "–O–", "–O–", "Ether oxygen linkage", "Functional Groups", "Ether"),
        ChemKey("org_fg_sh", "–SH", "–SH", "Thiol / Sulfhydryl group", "Functional Groups", "Thiol"),
        ChemKey("org_fg_so3h", "–SO₃H", "–SO₃H", "Sulfonic acid group", "Functional Groups", "Sulfonic")
    )

    val organicSymbols: List<ChemKey> = listOf(
        ChemKey("org_sym_ch3", "CH₃", "CH₃", "Methyl group", "Organic Symbols", isHighlighted = true),
        ChemKey("org_sym_ch2", "CH₂", "CH₂", "Methylene group", "Organic Symbols"),
        ChemKey("org_sym_ch", "CH", "CH", "Methine group", "Organic Symbols"),
        ChemKey("org_sym_c", "C", "C", "Carbon atom", "Organic Symbols"),
        ChemKey("org_sym_cc_double", "C=C", "C=C", "Alkene carbon-carbon double bond", "Organic Symbols", isHighlighted = true),
        ChemKey("org_sym_cc_triple", "C≡C", "C≡C", "Alkyne carbon-carbon triple bond", "Organic Symbols", isHighlighted = true),
        ChemKey("org_sym_co_double", "C=O", "C=O", "Carbonyl double bond", "Organic Symbols"),
        ChemKey("org_sym_co_single", "C–O", "C–O", "Carbon-oxygen single bond", "Organic Symbols"),
        ChemKey("org_sym_cn_single", "C–N", "C–N", "Carbon-nitrogen single bond", "Organic Symbols"),
        ChemKey("org_sym_roh", "R–OH", "R–OH", "General alcohol", "Organic Symbols"),
        ChemKey("org_sym_aroh", "Ar–OH", "Ar–OH", "General phenol", "Organic Symbols"),
        ChemKey("org_sym_rx", "R–X", "R–X", "Alkyl halide", "Organic Symbols"),
        ChemKey("org_sym_ph", "Ph–", "Ph–", "Phenyl group", "Organic Symbols"),
        ChemKey("org_sym_o", "o-", "o-", "Ortho position (1,2-disubstituted)", "Organic Symbols"),
        ChemKey("org_sym_m", "m-", "m-", "Meta position (1,3-disubstituted)", "Organic Symbols"),
        ChemKey("org_sym_p", "p-", "p-", "Para position (1,4-disubstituted)", "Organic Symbols")
    )

    val reactionArrows: List<ChemKey> = listOf(
        ChemKey("org_arr_fwd", "→", "→", "Forward reaction arrow", "Reaction Arrows", "Yields", isHighlighted = true),
        ChemKey("org_arr_eq", "⇌", "⇌", "Reversible equilibrium arrow", "Reaction Arrows", "Equilibrium", isHighlighted = true),
        ChemKey("org_arr_res", "↔", "↔", "Resonance double-headed arrow", "Reaction Arrows", "Resonance"),
        ChemKey("org_arr_long_fwd", "⟶", "⟶", "Long forward reaction arrow", "Reaction Arrows", "Long Arrow"),
        ChemKey("org_arr_dbl", "⟹", "⟹", "Retrosynthetic / Implies arrow", "Reaction Arrows", "Implies"),
        ChemKey("org_arr_long_rev", "⟵", "⟵", "Long reverse reaction arrow", "Reaction Arrows", "Reverse"),
        ChemKey("org_arr_up", "↑", "↑", "Gas evolved symbol", "Reaction Arrows", "Gas ↑"),
        ChemKey("org_arr_down", "↓", "↓", "Precipitate formed symbol", "Reaction Arrows", "Precipitate ↓"),
        ChemKey("org_arr_delta", "Δ", "Δ", "Heat / Delta condition", "Reaction Arrows", "Heat Δ", isHighlighted = true),
        ChemKey("org_arr_hv", "hν", "hν", "Photochemical / UV light", "Reaction Arrows", "Light hν"),
        ChemKey("org_arr_cat", "Catalyst", "Catalyst", "Catalyst condition label", "Reaction Arrows"),
        ChemKey("org_arr_heat", "Heat", "Heat", "Heat condition label", "Reaction Arrows"),
        ChemKey("org_arr_press", "Pressure", "Pressure", "High pressure condition label", "Reaction Arrows"),
        ChemKey("org_arr_cond_heat", "—Δ→", "—(Δ)→", "Reaction arrow with heat (Δ)", "Reaction Arrows"),
        ChemKey("org_arr_cond_hv", "—hν→", "—(hν)→", "Reaction arrow with light (hν)", "Reaction Arrows"),
        ChemKey("org_arr_cond_cat", "—Cat→", "—(Catalyst)→", "Reaction arrow with catalyst", "Reaction Arrows")
    )

    val allKeys: List<ChemKey> = ringStructures + functionalGroups + organicSymbols + reactionArrows
}
