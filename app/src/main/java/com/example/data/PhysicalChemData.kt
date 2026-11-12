package com.example.data

object PhysicalChemData {

    val mathematicalSymbols: List<ChemKey> = listOf(
        ChemKey("phys_m_sqrt", "√", "√", "Square root", "Mathematical Symbols", isHighlighted = true),
        ChemKey("phys_m_cbrt", "∛", "∛", "Cube root", "Mathematical Symbols"),
        ChemKey("phys_m_sum", "∑", "∑", "Summation", "Mathematical Symbols"),
        ChemKey("phys_m_int", "∫", "∫", "Integral", "Mathematical Symbols"),
        ChemKey("phys_m_inf", "∞", "∞", "Infinity", "Mathematical Symbols"),
        ChemKey("phys_m_pi", "π", "π", "Pi / Osmotic pressure", "Mathematical Symbols", isHighlighted = true),
        ChemKey("phys_m_theta", "θ", "θ", "Theta (angle / standard)", "Mathematical Symbols"),
        ChemKey("phys_m_alpha", "α", "α", "Alpha (degree of dissociation)", "Mathematical Symbols"),
        ChemKey("phys_m_beta", "β", "β", "Beta", "Mathematical Symbols"),
        ChemKey("phys_m_delta", "Δ", "Δ", "Delta (change in property)", "Mathematical Symbols", isHighlighted = true),
        ChemKey("phys_m_lambda", "λ", "λ", "Lambda (wavelength / molar conductivity)", "Mathematical Symbols"),
        ChemKey("phys_m_mu", "μ", "μ", "Mu (chemical potential / dipole moment)", "Mathematical Symbols"),
        ChemKey("phys_m_rho", "ρ", "ρ", "Rho (density / resistivity)", "Mathematical Symbols"),
        ChemKey("phys_m_sigma", "σ", "σ", "Sigma (surface tension / standard dev)", "Mathematical Symbols"),
        ChemKey("phys_m_omega", "Ω", "Ω", "Ohm (electrical resistance)", "Mathematical Symbols"),
        ChemKey("phys_m_pm", "±", "±", "Plus-minus uncertainty", "Mathematical Symbols"),
        ChemKey("phys_m_approx", "≈", "≈", "Approximately equal", "Mathematical Symbols"),
        ChemKey("phys_m_neq", "≠", "≠", "Not equal", "Mathematical Symbols"),
        ChemKey("phys_m_leq", "≤", "≤", "Less than or equal", "Mathematical Symbols"),
        ChemKey("phys_m_geq", "≥", "≥", "Greater than or equal", "Mathematical Symbols"),
        ChemKey("phys_m_mul", "×", "×", "Multiplication sign", "Mathematical Symbols"),
        ChemKey("phys_m_div", "÷", "÷", "Division sign", "Mathematical Symbols"),
        ChemKey("phys_m_arrow", "→", "→", "Approaches / Reaction arrow", "Mathematical Symbols", isHighlighted = true),
        ChemKey("phys_m_deg", "°", "°", "Degree / Standard state symbol", "Mathematical Symbols")
    )

    val thermodynamics: List<ChemKey> = listOf(
        ChemKey("phys_th_dh", "ΔH", "ΔH", "Enthalpy change", "Thermodynamics", "Enthalpy", isHighlighted = true),
        ChemKey("phys_th_dh0", "ΔH°", "ΔH°", "Standard enthalpy change", "Thermodynamics", "Std. ΔH°"),
        ChemKey("phys_th_ds", "ΔS", "ΔS", "Entropy change", "Thermodynamics", "Entropy", isHighlighted = true),
        ChemKey("phys_th_ds0", "ΔS°", "ΔS°", "Standard entropy change", "Thermodynamics", "Std. ΔS°"),
        ChemKey("phys_th_dg", "ΔG", "ΔG", "Gibbs free energy change", "Thermodynamics", "Gibbs Free", isHighlighted = true),
        ChemKey("phys_th_dg0", "ΔG°", "ΔG°", "Standard Gibbs free energy change", "Thermodynamics", "Std. ΔG°"),
        ChemKey("phys_th_q", "q", "q", "Heat transferred", "Thermodynamics", "Heat"),
        ChemKey("phys_th_w", "w", "w", "Work done", "Thermodynamics", "Work"),
        ChemKey("phys_th_u", "U", "U", "Internal energy", "Thermodynamics", "Internal E"),
        ChemKey("phys_th_du", "ΔU", "ΔU", "Change in internal energy", "Thermodynamics", "ΔInternal E"),
        ChemKey("phys_th_kp", "Kp", "Kp", "Equilibrium constant (partial pressures)", "Thermodynamics", "Kp"),
        ChemKey("phys_th_kc", "Kc", "Kc", "Equilibrium constant (concentrations)", "Thermodynamics", "Kc"),
        ChemKey("phys_th_gibbs_eq", "ΔG = ΔH − TΔS", "ΔG = ΔH − TΔS", "Gibbs-Helmholtz equation", "Thermodynamics", "Equation")
    )

    val electrochemistry: List<ChemKey> = listOf(
        ChemKey("phys_el_e0", "E°", "E°", "Standard electrode potential", "Electrochemistry", "Std. Potential", isHighlighted = true),
        ChemKey("phys_el_ecell", "Ecell", "Ecell", "Cell potential", "Electrochemistry", "Cell EMF"),
        ChemKey("phys_el_e0cell", "E°cell", "E°cell", "Standard cell potential", "Electrochemistry", "Std. EMF", isHighlighted = true),
        ChemKey("phys_el_f", "F", "F", "Faraday constant (96485 C/mol)", "Electrochemistry", "Faraday"),
        ChemKey("phys_el_anode", "Anode", "Anode", "Oxidation electrode (Anode)", "Electrochemistry", "Oxidation"),
        ChemKey("phys_el_cathode", "Cathode", "Cathode", "Reduction electrode (Cathode)", "Electrochemistry", "Reduction"),
        ChemKey("phys_el_bridge", "‖", "‖", "Salt bridge in cell notation", "Electrochemistry", "Salt Bridge"),
        ChemKey("phys_el_nernst", "E = E° − (RT/nF)lnQ", "E = E° − (RT/nF)lnQ", "Nernst equation", "Electrochemistry", "Nernst")
    )

    val kinetics: List<ChemKey> = listOf(
        ChemKey("phys_kin_rate", "Rate", "Rate", "Reaction rate", "Chemical Kinetics", "Rate", isHighlighted = true),
        ChemKey("phys_kin_k", "k", "k", "Rate constant", "Chemical Kinetics", "Rate Const."),
        ChemKey("phys_kin_t12", "t½", "t½", "Half-life", "Chemical Kinetics", "Half-life", isHighlighted = true),
        ChemKey("phys_kin_order", "Order", "Order of reaction", "Order of reaction", "Chemical Kinetics", "Order"),
        ChemKey("phys_kin_ea", "Ea", "Ea", "Activation energy", "Chemical Kinetics", "Activation E", isHighlighted = true),
        ChemKey("phys_kin_arrh", "k = Ae^(-Ea/RT)", "k = Ae^(-Ea/RT)", "Arrhenius equation", "Chemical Kinetics", "Arrhenius")
    )

    val solutions: List<ChemKey> = listOf(
        ChemKey("phys_sol_m", "M", "M", "Molarity (mol/L)", "Solutions", "Molarity", isHighlighted = true),
        ChemKey("phys_sol_molal", "m", "m", "Molality (mol/kg)", "Solutions", "Molality"),
        ChemKey("phys_sol_chi", "χ", "χ", "Mole fraction", "Solutions", "Mole Frac.", isHighlighted = true),
        ChemKey("phys_sol_pi", "π", "π", "Osmotic pressure", "Solutions", "Osmotic P", isHighlighted = true),
        ChemKey("phys_sol_conc", "[C]", "[ ]", "Molar concentration brackets", "Solutions", "Conc."),
        ChemKey("phys_sol_norm", "N", "N", "Normality (eq/L)", "Solutions", "Normality"),
        ChemKey("phys_sol_ph", "pH", "pH", "pH = −log[H⁺]", "Solutions", "Acidity"),
        ChemKey("phys_sol_osm_eq", "π = CRT", "π = CRT", "Osmotic pressure equation", "Solutions", "Van 't Hoff")
    )

    val allKeys: List<ChemKey> = mathematicalSymbols + thermodynamics + electrochemistry + kinetics + solutions
}
