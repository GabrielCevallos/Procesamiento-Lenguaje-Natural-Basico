package com.pln.ast;

/**
 * Representa un paso en la derivación por la izquierda.
 * Almacena la forma sentencial (cadena) en cada paso de derivación.
 */
public class DerivationStep {
    private final int stepNumber;
    private final String sententialForm;
    private final String rule;

    public DerivationStep(int stepNumber, String sententialForm, String rule) {
        this.stepNumber = stepNumber;
        this.sententialForm = sententialForm;
        this.rule = rule;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getSententialForm() {
        return sententialForm;
    }

    public String getRule() {
        return rule;
    }

    @Override
    public String toString() {
        return sententialForm;
    }
}
