package com.example.data

object GreekAndTemplatesData {

    val subscripts: List<ChemKey> = listOf(
        ChemKey("sub_0", "₀", "₀", "Subscript 0", "Subscript"),
        ChemKey("sub_1", "₁", "₁", "Subscript 1", "Subscript"),
        ChemKey("sub_2", "₂", "₂", "Subscript 2", "Subscript", isHighlighted = true),
        ChemKey("sub_3", "₃", "₃", "Subscript 3", "Subscript", isHighlighted = true),
        ChemKey("sub_4", "₄", "₄", "Subscript 4", "Subscript", isHighlighted = true),
        ChemKey("sub_5", "₅", "₅", "Subscript 5", "Subscript"),
        ChemKey("sub_6", "₆", "₆", "Subscript 6", "Subscript"),
        ChemKey("sub_7", "₇", "₇", "Subscript 7", "Subscript"),
        ChemKey("sub_8", "₈", "₈", "Subscript 8", "Subscript"),
        ChemKey("sub_9", "₉", "₉", "Subscript 9", "Subscript")
    )

    val superscripts: List<ChemKey> = listOf(
        ChemKey("sup_0", "⁰", "⁰", "Superscript 0", "Superscript"),
        ChemKey("sup_1", "¹", "¹", "Superscript 1", "Superscript"),
        ChemKey("sup_2", "²", "²", "Superscript 2", "Superscript", isHighlighted = true),
        ChemKey("sup_3", "³", "³", "Superscript 3", "Superscript"),
        ChemKey("sup_4", "⁴", "⁴", "Superscript 4", "Superscript"),
        ChemKey("sup_5", "⁵", "⁵", "Superscript 5", "Superscript"),
        ChemKey("sup_6", "⁶", "⁶", "Superscript 6", "Superscript"),
        ChemKey("sup_7", "⁷", "⁷", "Superscript 7", "Superscript"),
        ChemKey("sup_8", "⁸", "⁸", "Superscript 8", "Superscript"),
        ChemKey("sup_9", "⁹", "⁹", "Superscript 9", "Superscript"),
        ChemKey("sup_plus", "⁺", "⁺", "Positive charge (⁺)", "Charge", isHighlighted = true),
        ChemKey("sup_minus", "⁻", "⁻", "Negative charge (⁻)", "Charge", isHighlighted = true)
    )

    val greekLowercase: List<ChemKey> = listOf(
        ChemKey("gr_alpha", "α", "α", "Alpha (α)", "Greek Lowercase", "alpha", isHighlighted = true),
        ChemKey("gr_beta", "β", "β", "Beta (β)", "Greek Lowercase", "beta", isHighlighted = true),
        ChemKey("gr_gamma", "γ", "γ", "Gamma (γ)", "Greek Lowercase", "gamma"),
        ChemKey("gr_delta", "δ", "δ", "Delta (δ partial charge)", "Greek Lowercase", "delta"),
        ChemKey("gr_epsilon", "ε", "ε", "Epsilon (ε molar absorptivity)", "Greek Lowercase", "epsilon"),
        ChemKey("gr_zeta", "ζ", "ζ", "Zeta (ζ)", "Greek Lowercase", "zeta"),
        ChemKey("gr_eta", "η", "η", "Eta (η viscosity/efficiency)", "Greek Lowercase", "eta"),
        ChemKey("gr_theta", "θ", "θ", "Theta (θ)", "Greek Lowercase", "theta"),
        ChemKey("gr_iota", "ι", "ι", "Iota (ι)", "Greek Lowercase", "iota"),
        ChemKey("gr_kappa", "κ", "κ", "Kappa (κ conductivity)", "Greek Lowercase", "kappa"),
        ChemKey("gr_lambda", "λ", "λ", "Lambda (λ wavelength)", "Greek Lowercase", "lambda", isHighlighted = true),
        ChemKey("gr_mu", "μ", "μ", "Mu (μ micro / dipole)", "Greek Lowercase", "mu", isHighlighted = true),
        ChemKey("gr_nu", "ν", "ν", "Nu (ν frequency)", "Greek Lowercase", "nu"),
        ChemKey("gr_xi", "ξ", "ξ", "Xi (ξ extent of reaction)", "Greek Lowercase", "xi"),
        ChemKey("gr_omicron", "ο", "ο", "Omicron (ο)", "Greek Lowercase", "omicron"),
        ChemKey("gr_pi", "π", "π", "Pi (π osmotic / pi bond)", "Greek Lowercase", "pi", isHighlighted = true),
        ChemKey("gr_rho", "ρ", "ρ", "Rho (ρ density)", "Greek Lowercase", "rho"),
        ChemKey("gr_sigma", "σ", "σ", "Sigma (σ sigma bond)", "Greek Lowercase", "sigma"),
        ChemKey("gr_tau", "τ", "τ", "Tau (τ lifetime)", "Greek Lowercase", "tau"),
        ChemKey("gr_upsilon", "υ", "υ", "Upsilon (υ)", "Greek Lowercase", "upsilon"),
        ChemKey("gr_phi", "φ", "φ", "Phi (φ)", "Greek Lowercase", "phi"),
        ChemKey("gr_chi", "χ", "χ", "Chi (χ mole fraction / electronegativity)", "Greek Lowercase", "chi"),
        ChemKey("gr_psi", "ψ", "ψ", "Psi (ψ wave function)", "Greek Lowercase", "psi"),
        ChemKey("gr_omega", "ω", "ω", "Omega (ω)", "Greek Lowercase", "omega")
    )

    val greekUppercase: List<ChemKey> = listOf(
        ChemKey("gr_Alpha", "Α", "Α", "Capital Alpha", "Greek Uppercase", "Alpha"),
        ChemKey("gr_Beta", "Β", "Β", "Capital Beta", "Greek Uppercase", "Beta"),
        ChemKey("gr_Gamma", "Γ", "Γ", "Capital Gamma", "Greek Uppercase", "Gamma"),
        ChemKey("gr_Delta", "Δ", "Δ", "Capital Delta (change / heat)", "Greek Uppercase", "Delta", isHighlighted = true),
        ChemKey("gr_Epsilon", "Ε", "Ε", "Capital Epsilon", "Greek Uppercase", "Epsilon"),
        ChemKey("gr_Zeta", "Ζ", "Ζ", "Capital Zeta", "Greek Uppercase", "Zeta"),
        ChemKey("gr_Eta", "Η", "Η", "Capital Eta", "Greek Uppercase", "Eta"),
        ChemKey("gr_Theta", "Θ", "Θ", "Capital Theta", "Greek Uppercase", "Theta"),
        ChemKey("gr_Iota", "Ι", "Ι", "Capital Iota", "Greek Uppercase", "Iota"),
        ChemKey("gr_Kappa", "Κ", "Κ", "Capital Kappa", "Greek Uppercase", "Kappa"),
        ChemKey("gr_Lambda", "Λ", "Λ", "Capital Lambda (molar conductance)", "Greek Uppercase", "Lambda"),
        ChemKey("gr_Mu", "Μ", "Μ", "Capital Mu", "Greek Uppercase", "Mu"),
        ChemKey("gr_Nu", "Ν", "Ν", "Capital Nu", "Greek Uppercase", "Nu"),
        ChemKey("gr_Xi", "Ξ", "Ξ", "Capital Xi", "Greek Uppercase", "Xi"),
        ChemKey("gr_Omicron", "Ο", "Ο", "Capital Omicron", "Greek Uppercase", "Omicron"),
        ChemKey("gr_Pi", "Π", "Π", "Capital Pi (product / osmotic)", "Greek Uppercase", "Pi"),
        ChemKey("gr_Rho", "Ρ", "Ρ", "Capital Rho", "Greek Uppercase", "Rho"),
        ChemKey("gr_Sigma", "Σ", "Σ", "Capital Sigma (summation)", "Greek Uppercase", "Sigma", isHighlighted = true),
        ChemKey("gr_Tau", "Τ", "Τ", "Capital Tau", "Greek Uppercase", "Tau"),
        ChemKey("gr_Upsilon", "Υ", "Υ", "Capital Upsilon", "Greek Uppercase", "Upsilon"),
        ChemKey("gr_Phi", "Φ", "Φ", "Capital Phi (work function / quantum yield)", "Greek Uppercase", "Phi"),
        ChemKey("gr_Chi", "Χ", "Χ", "Capital Chi", "Greek Uppercase", "Chi"),
        ChemKey("gr_Psi", "Ψ", "Ψ", "Capital Psi (wave function)", "Greek Uppercase", "Psi"),
        ChemKey("gr_Omega", "Ω", "Ω", "Capital Omega (Ohm)", "Greek Uppercase", "Omega", isHighlighted = true)
    )

    val formulaTemplates: List<ChemTemplate> = listOf(
        ChemTemplate("tpl_h2so4", "Sulfuric Acid", "H₂SO₄", "Formula", "Strong diprotic mineral acid"),
        ChemTemplate("tpl_caoh2", "Calcium Hydroxide", "Ca(OH)₂", "Formula", "Slaked lime / Strong base"),
        ChemTemplate("tpl_al2so43", "Aluminum Sulfate", "Al₂(SO₄)₃", "Formula", "Ionic salt example"),
        ChemTemplate("tpl_ch3cooh", "Acetic Acid", "CH₃COOH", "Formula", "Ethanoic weak organic acid"),
        ChemTemplate("tpl_c6h6", "Benzene", "C₆H₆", "Formula", "Aromatic hydrocarbon ring"),
        ChemTemplate("tpl_kmno4", "Potassium Permanganate", "KMnO₄", "Formula", "Strong oxidizing agent"),
        ChemTemplate("tpl_k2cr2o7", "Potassium Dichromate", "K₂Cr₂O₇", "Formula", "Oxidizing agent"),
        ChemTemplate("tpl_nahco3", "Sodium Bicarbonate", "NaHCO₃", "Formula", "Baking soda / Amphoteric salt"),
        ChemTemplate("tpl_glucose", "Glucose", "C₆H₁₂O₆", "Formula", "Monosaccharide hexose sugar"),
        ChemTemplate("tpl_ethanol", "Ethanol", "CH₃CH₂OH", "Formula", "Primary alcohol"),
        ChemTemplate("tpl_cuso4", "Copper(II) Sulfate Pentahydrate", "CuSO₄·5H₂O", "Formula", "Blue vitriol hydrate"),
        ChemTemplate("tpl_nh4no3", "Ammonium Nitrate", "NH₄NO₃", "Formula", "Ionic nitrogen salt")
    )

    val reactionTemplates: List<ChemTemplate> = listOf(
        ChemTemplate(
            "rxn_neutralization",
            "Acid-Base Neutralization",
            "HCl(aq) + NaOH(aq) → NaCl(aq) + H₂O(l)",
            "Reaction",
            "Strong acid + strong base"
        ),
        ChemTemplate(
            "rxn_esterification",
            "Fischer Esterification",
            "CH₃COOH + C₂H₅OH ⇌ CH₃COOC₂H₅ + H₂O",
            "Reaction",
            "Carboxylic acid + alcohol equilibrium"
        ),
        ChemTemplate(
            "rxn_haber",
            "Haber Process",
            "N₂(g) + 3H₂(g) ⇌ 2NH₃(g)   ΔH = −92.4 kJ",
            "Reaction",
            "Industrial ammonia synthesis"
        ),
        ChemTemplate(
            "rxn_nitration",
            "Nitration of Benzene",
            "⌬ + HNO₃ —(H₂SO₄, Δ)→ ⌬–NO₂ + H₂O",
            "Reaction",
            "Electrophilic aromatic substitution"
        ),
        ChemTemplate(
            "rxn_combustion",
            "Methane Combustion",
            "CH₄(g) + 2O₂(g) → CO₂(g) + 2H₂O(g)",
            "Reaction",
            "Complete hydrocarbon combustion"
        ),
        ChemTemplate(
            "rxn_photosynthesis",
            "Photosynthesis",
            "6CO₂ + 6H₂O —(hν)→ C₆H₁₂O₆ + 6O₂↑",
            "Reaction",
            "Photochemical carbohydrate synthesis"
        ),
        ChemTemplate(
            "rxn_precipitate",
            "Silver Chloride Precipitation",
            "Ag⁺(aq) + Cl⁻(aq) → AgCl(s)↓",
            "Reaction",
            "Classic halide precipitation test"
        ),
        ChemTemplate(
            "rxn_redox",
            "Permanganate Redox",
            "MnO₄⁻ + 8H⁺ + 5Fe²⁺ → Mn²⁺ + 5Fe³⁺ + 4H₂O",
            "Reaction",
            "Acidic redox titration"
        )
    )

    val quickStructureTemplates: List<ChemKey> = listOf(
        ChemKey("st_benzene", "⌬", "⌬", "Benzene ring", "Structures", "Benzene", isHighlighted = true, ringType = RingGlyphType.BENZENE),
        ChemKey("st_phenol", "⌬–OH", "⌬–OH", "Phenol (Hydroxybenzene)", "Structures", "Phenol", ringType = RingGlyphType.AROMATIC_RING),
        ChemKey("st_benzoic", "⌬–COOH", "⌬–COOH", "Benzoic acid", "Structures", "Benzoic Acid", ringType = RingGlyphType.AROMATIC_RING),
        ChemKey("st_toluene", "⌬–CH₃", "⌬–CH₃", "Toluene (Methylbenzene)", "Structures", "Toluene", ringType = RingGlyphType.AROMATIC_RING),
        ChemKey("st_aniline", "⌬–NH₂", "⌬–NH₂", "Aniline (Aminobenzene)", "Structures", "Aniline", ringType = RingGlyphType.AROMATIC_RING),
        ChemKey("st_nitrobenz", "⌬–NO₂", "⌬–NO₂", "Nitrobenzene", "Structures", "Nitrobenzene", ringType = RingGlyphType.AROMATIC_RING),
        ChemKey("st_naphth", "C₁₀H₈", "C₁₀H₈", "Naphthalene fused aromatic", "Structures", "Naphthalene", ringType = RingGlyphType.NAPHTHALENE),
        ChemKey("st_anthracene", "C₁₄H₁₀", "C₁₄H₁₀", "Anthracene tricyclic aromatic", "Structures", "Anthracene", ringType = RingGlyphType.ANTHRACENE),
        ChemKey("st_pyridine", "C₅H₅N", "C₅H₅N", "Pyridine heterocyclic ring", "Structures", "Pyridine", ringType = RingGlyphType.PYRIDINE),
        ChemKey("st_cyclohexane", "⬡", "C₆H₁₂", "Cyclohexane saturated ring", "Structures", "Cyclohexane", ringType = RingGlyphType.CYCLOHEXANE),
        ChemKey("st_acetic", "CH₃COOH", "CH₃COOH", "Acetic acid condensed structure", "Structures", "Acetic Acid"),
        ChemKey("st_acetone", "CH₃–CO–CH₃", "CH₃–CO–CH₃", "Acetone (Propanone)", "Structures", "Acetone")
    )

    val defaultShortcuts: List<CustomShortcut> = listOf(
        CustomShortcut("sc_bz", "/bz", "⌬"),
        CustomShortcut("sc_h2so4", "/h2so4", "H₂SO₄"),
        CustomShortcut("sc_eq", "/eq", "⇌"),
        CustomShortcut("sc_arr", "/rxn", "→"),
        CustomShortcut("sc_cooh", "/cooh", "–COOH"),
        CustomShortcut("sc_nh2", "/nh2", "–NH₂"),
        CustomShortcut("sc_dg", "/dg", "ΔG = ΔH − TΔS")
    )

    fun searchAll(query: String): List<ChemKey> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return emptyList()

        val baseKeys = OrganicChemData.allKeys +
            InorganicChemData.allKeys +
            PhysicalChemData.allKeys +
            subscripts +
            superscripts +
            greekLowercase +
            greekUppercase +
            quickStructureTemplates

        val matchingKeys = baseKeys.filter { key ->
            key.label.lowercase().contains(q) ||
                key.insertText.lowercase().contains(q) ||
                key.description.lowercase().contains(q) ||
                key.category.lowercase().contains(q) ||
                (key.subLabel?.lowercase()?.contains(q) == true)
        }

        val matchingElements = PeriodicTableData.elements
            .filter { el ->
                el.symbol.lowercase().contains(q) ||
                    el.name.lowercase().contains(q) ||
                    el.atomicNumber.toString() == q
            }
            .map { el ->
                ChemKey(
                    id = "el_${el.atomicNumber}",
                    label = el.symbol,
                    subLabel = "${el.atomicNumber} ${el.name}",
                    insertText = el.symbol,
                    description = "${el.name} (${el.symbol}) • Atomic #${el.atomicNumber} • ${el.atomicMass}",
                    category = "Element"
                )
            }

        val matchingTemplates = (formulaTemplates + reactionTemplates)
            .filter { tpl ->
                tpl.title.lowercase().contains(q) ||
                    tpl.formula.lowercase().contains(q) ||
                    tpl.description.lowercase().contains(q)
            }
            .map { tpl ->
                ChemKey(
                    id = tpl.id,
                    label = tpl.formula,
                    subLabel = tpl.title,
                    insertText = tpl.formula,
                    description = tpl.description,
                    category = tpl.category
                )
            }

        return (matchingKeys + matchingElements + matchingTemplates).distinctBy { it.insertText + it.label }.take(50)
    }
}
