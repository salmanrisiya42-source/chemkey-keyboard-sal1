package com.example.data

object InorganicChemData {

    val commonIons: List<ChemKey> = listOf(
        ChemKey("inorg_ion_h", "H⁺", "H⁺", "Hydrogen ion / Proton", "Common Ions", isHighlighted = true),
        ChemKey("inorg_ion_oh", "OH⁻", "OH⁻", "Hydroxide ion", "Common Ions", isHighlighted = true),
        ChemKey("inorg_ion_na", "Na⁺", "Na⁺", "Sodium ion", "Common Ions"),
        ChemKey("inorg_ion_k", "K⁺", "K⁺", "Potassium ion", "Common Ions"),
        ChemKey("inorg_ion_ca", "Ca²⁺", "Ca²⁺", "Calcium ion", "Common Ions"),
        ChemKey("inorg_ion_mg", "Mg²⁺", "Mg²⁺", "Magnesium ion", "Common Ions"),
        ChemKey("inorg_ion_al", "Al³⁺", "Al³⁺", "Aluminum ion", "Common Ions"),
        ChemKey("inorg_ion_fe2", "Fe²⁺", "Fe²⁺", "Iron(II) / Ferrous ion", "Common Ions"),
        ChemKey("inorg_ion_fe3", "Fe³⁺", "Fe³⁺", "Iron(III) / Ferric ion", "Common Ions"),
        ChemKey("inorg_ion_cu2", "Cu²⁺", "Cu²⁺", "Copper(II) / Cupric ion", "Common Ions"),
        ChemKey("inorg_ion_zn2", "Zn²⁺", "Zn²⁺", "Zinc ion", "Common Ions"),
        ChemKey("inorg_ion_nh4", "NH₄⁺", "NH₄⁺", "Ammonium ion", "Common Ions", isHighlighted = true),
        ChemKey("inorg_ion_so4", "SO₄²⁻", "SO₄²⁻", "Sulfate ion", "Common Ions", isHighlighted = true),
        ChemKey("inorg_ion_co3", "CO₃²⁻", "CO₃²⁻", "Carbonate ion", "Common Ions"),
        ChemKey("inorg_ion_hco3", "HCO₃⁻", "HCO₃⁻", "Bicarbonate / Hydrogen carbonate ion", "Common Ions"),
        ChemKey("inorg_ion_no3", "NO₃⁻", "NO₃⁻", "Nitrate ion", "Common Ions", isHighlighted = true),
        ChemKey("inorg_ion_no2", "NO₂⁻", "NO₂⁻", "Nitrite ion", "Common Ions"),
        ChemKey("inorg_ion_po4", "PO₄³⁻", "PO₄³⁻", "Phosphate ion", "Common Ions"),
        ChemKey("inorg_ion_cl", "Cl⁻", "Cl⁻", "Chloride ion", "Common Ions"),
        ChemKey("inorg_ion_br", "Br⁻", "Br⁻", "Bromide ion", "Common Ions"),
        ChemKey("inorg_ion_i", "I⁻", "I⁻", "Iodide ion", "Common Ions"),
        ChemKey("inorg_ion_ag", "Ag⁺", "Ag⁺", "Silver(I) ion", "Common Ions"),
        ChemKey("inorg_ion_cr2o7", "Cr₂O₇²⁻", "Cr₂O₇²⁻", "Dichromate ion", "Common Ions"),
        ChemKey("inorg_ion_mno4", "MnO₄⁻", "MnO₄⁻", "Permanganate ion", "Common Ions")
    )

    val specialSymbols: List<ChemKey> = listOf(
        ChemKey("inorg_sp_ox1", "(I)", "(I)", "Oxidation state +1", "Special Symbols", "Oxidation No."),
        ChemKey("inorg_sp_ox2", "(II)", "(II)", "Oxidation state +2", "Special Symbols", "Oxidation No."),
        ChemKey("inorg_sp_ox3", "(III)", "(III)", "Oxidation state +3", "Special Symbols", "Oxidation No."),
        ChemKey("inorg_sp_ox4", "(IV)", "(IV)", "Oxidation state +4", "Special Symbols", "Oxidation No."),
        ChemKey("inorg_sp_ox5", "(V)", "(V)", "Oxidation state +5", "Special Symbols", "Oxidation No."),
        ChemKey("inorg_sp_ox6", "(VI)", "(VI)", "Oxidation state +6", "Special Symbols", "Oxidation No."),
        ChemKey("inorg_sp_coord_l", "←", "←", "Coordination / Dative bond (donor → metal)", "Special Symbols", "Coord. Bond", isHighlighted = true),
        ChemKey("inorg_sp_coord_r", "→", "→", "Coordination / Dative bond", "Special Symbols", "Coord. Bond"),
        ChemKey("inorg_sp_single", "–", "–", "Single covalent bond", "Special Symbols", "Single Bond", isHighlighted = true),
        ChemKey("inorg_sp_double", "=", "=", "Double bond", "Special Symbols", "Double Bond", isHighlighted = true),
        ChemKey("inorg_sp_triple", "≡", "≡", "Triple bond", "Special Symbols", "Triple Bond", isHighlighted = true),
        ChemKey("inorg_sp_ppt", "↓", "↓", "Precipitate formed", "Special Symbols", "Precipitate", isHighlighted = true),
        ChemKey("inorg_sp_gas", "↑", "↑", "Gas evolution", "Special Symbols", "Gas Evolved", isHighlighted = true),
        ChemKey("inorg_sp_lbrack", "[", "[", "Complex ion opening bracket", "Special Symbols", "Complex ["),
        ChemKey("inorg_sp_rbrack", "]", "]", "Complex ion closing bracket", "Special Symbols", "Complex ]"),
        ChemKey("inorg_sp_lig_nh3", "NH₃", "NH₃", "Ammine ligand", "Special Symbols", "Ligand"),
        ChemKey("inorg_sp_lig_h2o", "H₂O", "H₂O", "Aqua ligand / Water", "Special Symbols", "Ligand"),
        ChemKey("inorg_sp_lig_cn", "CN⁻", "CN⁻", "Cyano ligand", "Special Symbols", "Ligand"),
        ChemKey("inorg_sp_lig_en", "en", "en", "Ethylenediamine bidentate ligand", "Special Symbols", "Ligand"),
        ChemKey("inorg_sp_lig_edta", "EDTA⁴⁻", "EDTA⁴⁻", "Hexadentate EDTA ligand", "Special Symbols", "Ligand"),
        ChemKey("inorg_sp_cx_fe2", "[Fe(CN)₆]⁴⁻", "[Fe(CN)₆]⁴⁻", "Ferrocyanide complex ion", "Special Symbols", "Complex Ion"),
        ChemKey("inorg_sp_cx_fe3", "[Fe(CN)₆]³⁻", "[Fe(CN)₆]³⁻", "Ferricyanide complex ion", "Special Symbols", "Complex Ion"),
        ChemKey("inorg_sp_cx_cu", "[Cu(NH₃)₄]²⁺", "[Cu(NH₃)₄]²⁺", "Tetraamminecopper(II) complex ion", "Special Symbols", "Complex Ion"),
        ChemKey("inorg_sp_cx_ag", "[Ag(NH₃)₂]⁺", "[Ag(NH₃)₂]⁺", "Diamminesilver(I) Tollens complex ion", "Special Symbols", "Complex Ion"),
        ChemKey("inorg_sp_s", "(s)", "(s)", "Solid state", "Special Symbols", "State"),
        ChemKey("inorg_sp_l", "(l)", "(l)", "Liquid state", "Special Symbols", "State"),
        ChemKey("inorg_sp_g", "(g)", "(g)", "Gaseous state", "Special Symbols", "State"),
        ChemKey("inorg_sp_aq", "(aq)", "(aq)", "Aqueous solution", "Special Symbols", "State"),
        ChemKey("inorg_sp_dot", "·", "·", "Hydrate / Radical dot", "Special Symbols", "Hydrate")
    )

    val allKeys: List<ChemKey> = commonIons + specialSymbols
}
