package com.pln.lexer;

import java.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VerbConjugator {
    private static final Set<String> ALL_VERB_FORMS = new HashSet<>();

    private static Map<String, Map<String, String[]>> ENDINGS;
    private static Map<String, String> GERUNDS;
    private static Map<String, String> PARTICIPLES;
    private static String[] FUTURE_ENDINGS;
    private static String[] CONDITIONAL_ENDINGS;

    public static Set<String> getAllVerbForms() {
        return ALL_VERB_FORMS;
    }

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = mapper.readValue(
                VerbConjugator.class.getResourceAsStream("/conjugation-rules.json"),
                new TypeReference<Map<String, Object>>() {}
            );

            Map<String, Map<String, List<String>>> rawEndings = (Map<String, Map<String, List<String>>>) data.get("endings");
            ENDINGS = new HashMap<>();
            for (Map.Entry<String, Map<String, List<String>>> tenseEntry : rawEndings.entrySet()) {
                Map<String, String[]> conjMap = new HashMap<>();
                for (Map.Entry<String, List<String>> conjEntry : tenseEntry.getValue().entrySet()) {
                    conjMap.put(conjEntry.getKey(), conjEntry.getValue().toArray(new String[0]));
                }
                ENDINGS.put(tenseEntry.getKey(), conjMap);
            }

            Map<String, Map<String, String>> nonPersonal = (Map<String, Map<String, String>>) data.get("nonPersonal");
            GERUNDS = new HashMap<>();
            PARTICIPLES = new HashMap<>();
            for (Map.Entry<String, Map<String, String>> entry : nonPersonal.entrySet()) {
                GERUNDS.put(entry.getKey(), entry.getValue().get("gerund"));
                PARTICIPLES.put(entry.getKey(), entry.getValue().get("participle"));
            }

            Map<String, List<String>> extraEndings = (Map<String, List<String>>) data.get("extraEndings");
            FUTURE_ENDINGS = extraEndings.get("future").toArray(new String[0]);
            CONDITIONAL_ENDINGS = extraEndings.get("conditional").toArray(new String[0]);

            loadVerbData();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load conjugation-rules.json", e);
        }
    }

    private static void loadVerbData() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> verbsData = mapper.readValue(
            VerbConjugator.class.getResourceAsStream("/verbs.json"),
            new TypeReference<Map<String, Object>>() {}
        );

        @SuppressWarnings("unchecked")
        List<Map<String, String>> regular = (List<Map<String, String>>) verbsData.get("regular");
        for (Map<String, String> entry : regular) {
            String infinitive = entry.get("infinitive");
            String type = entry.get("conjugation");
            switch (type) {
                case "ar_regular" -> conjugateRegularAR(infinitive);
                case "er_regular" -> conjugateRegularER(infinitive);
                case "ir_regular" -> conjugateRegularIR(infinitive);
                case "ar_stem_eie" -> conjugateStemAR(infinitive, "e", "ie");
                case "ar_stem_oue" -> conjugateStemAR(infinitive, "o", "ue");
                case "ar_stem_uue" -> conjugateStemAR(infinitive, "u", "ue");
                case "er_stem_eie" -> conjugateStemER(infinitive, "e", "ie");
                case "er_stem_oue" -> conjugateStemER(infinitive, "o", "ue");
                case "er_stem_i" -> conjugateStemER(infinitive, "e", "i");
                case "ir_stem_eie" -> conjugateStemIR(infinitive, "e", "ie");
                case "ir_stem_ei" -> conjugateStemIR(infinitive, "e", "i");
                case "ir_stem_oue" -> conjugateStemIR(infinitive, "o", "ue");
            }
        }

        @SuppressWarnings("unchecked")
        List<String> irregular = (List<String>) verbsData.get("irregular");
        ALL_VERB_FORMS.addAll(irregular);

        @SuppressWarnings("unchecked")
        List<String> special = (List<String>) verbsData.get("special");
        ALL_VERB_FORMS.addAll(special);
    }

    private static String[] endings(String tense, String conj) {
        return ENDINGS.get(tense).get(conj);
    }

    private static String gerund(String conj) {
        return GERUNDS.get(conj);
    }

    private static String participle(String conj) {
        return PARTICIPLES.get(conj);
    }

    private static void conjugateRegularAR(String infinitive) {
        String stem = infinitive.substring(0, infinitive.length() - 2);
        addForms(infinitive, stem, "ar");
    }

    private static void conjugateRegularER(String infinitive) {
        String stem = infinitive.substring(0, infinitive.length() - 2);
        addForms(infinitive, stem, "er");
    }

    private static void conjugateRegularIR(String infinitive) {
        String stem = infinitive.substring(0, infinitive.length() - 2);
        addForms(infinitive, stem, "ir");
    }

    private static void conjugateStemAR(String infinitive, String from, String to) {
        String stem = infinitive.substring(0, infinitive.length() - 2);
        String stemChanged = stem.replace(from, to);
        String[] presStem =   { stemChanged, stemChanged, stemChanged, stem, stem, stemChanged };
        String[] subjStem =   { stemChanged, stemChanged, stemChanged, stem, stem, stemChanged };
        String[] impvStem =   { stemChanged, stemChanged, stem, stem, stemChanged };
        addFormsCustom(infinitive, stem, presStem, endings("present", "ar"),
                       endings("preterite", "ar"), endings("imperfect", "ar"),
                       subjStem, endings("subjunctive", "ar"),
                       impvStem, endings("imperative", "ar"),
                       gerund("ar"), participle("ar"));
    }

    private static void conjugateStemER(String infinitive, String from, String to) {
        String stem = infinitive.substring(0, infinitive.length() - 2);
        String stemChanged = stem.replace(from, to);
        String[] presStem =   { stemChanged, stemChanged, stemChanged, stem, stem, stemChanged };
        String[] subjStem =   { stemChanged, stemChanged, stemChanged, stem, stem, stemChanged };
        String[] impvStem =   { stemChanged, stemChanged, stem, stem, stemChanged };
        addFormsCustom(infinitive, stem, presStem, endings("present", "er"),
                       endings("preterite", "er"), endings("imperfect", "er"),
                       subjStem, endings("subjunctive", "er"),
                       impvStem, endings("imperative", "er"),
                       gerund("er"), participle("er"));
    }

    private static void conjugateStemIR(String infinitive, String from, String to) {
        String stem = infinitive.substring(0, infinitive.length() - 2);
        String stemChanged = stem.replace(from, to);
        String[] presStem = { stemChanged, stemChanged, stemChanged, stem, stem, stemChanged };
        String[] subjStem = { stemChanged, stemChanged, stemChanged, stem, stem, stemChanged };
        String[] impvStem = { stemChanged, stemChanged, stem, stem, stemChanged };

        String pretChangeTo;
        if (to.equals("ie")) pretChangeTo = "i";
        else if (to.equals("ue")) pretChangeTo = "u";
        else pretChangeTo = to;
        String stemPret3 = stem.replace(from, pretChangeTo);
        String[] pretStem = { stem, stem, stemPret3, stem, stem, stemPret3 };

        addFormsCustomIR(infinitive, stem, presStem, endings("present", "ir"),
                         pretStem, endings("preterite", "ir"),
                         endings("imperfect", "ir"),
                         subjStem, endings("subjunctive", "ir"),
                         impvStem, endings("imperative", "ir"),
                         gerund("ir"), participle("ir"));
    }

    private static void addForms(String infinitive, String stem, String conj) {
        ALL_VERB_FORMS.add(infinitive);
        String[] pres = endings("present", conj);
        String[] pret = endings("preterite", conj);
        String[] impf = endings("imperfect", conj);
        String[] subj = endings("subjunctive", conj);
        String[] impv = endings("imperative", conj);
        for (String s : pres) ALL_VERB_FORMS.add(stem + s);
        for (String s : pret) ALL_VERB_FORMS.add(stem + s);
        for (String s : impf) ALL_VERB_FORMS.add(stem + s);
        for (String s : subj) ALL_VERB_FORMS.add(stem + s);
        for (String s : impv) ALL_VERB_FORMS.add(stem + s);
        for (String s : FUTURE_ENDINGS) ALL_VERB_FORMS.add(infinitive + s);
        for (String s : CONDITIONAL_ENDINGS) ALL_VERB_FORMS.add(infinitive + s);
        ALL_VERB_FORMS.add(stem + gerund(conj));
        ALL_VERB_FORMS.add(stem + participle(conj));
    }

    private static void addFormsCustom(String infinitive, String stem,
                                        String[] presStem, String[] presSuf,
                                        String[] pret, String[] impf,
                                        String[] subjStem, String[] subjSuf,
                                        String[] impvStem, String[] impvSuf,
                                        String gerund, String participle) {
        ALL_VERB_FORMS.add(infinitive);
        for (int i = 0; i < presStem.length; i++) ALL_VERB_FORMS.add(presStem[i] + presSuf[i]);
        for (String s : pret) ALL_VERB_FORMS.add(stem + s);
        for (String s : impf) ALL_VERB_FORMS.add(stem + s);
        for (int i = 0; i < subjStem.length; i++) ALL_VERB_FORMS.add(subjStem[i] + subjSuf[i]);
        for (int i = 0; i < impvStem.length; i++) ALL_VERB_FORMS.add(impvStem[i] + impvSuf[i]);
        for (String s : FUTURE_ENDINGS) ALL_VERB_FORMS.add(infinitive + s);
        for (String s : CONDITIONAL_ENDINGS) ALL_VERB_FORMS.add(infinitive + s);
        ALL_VERB_FORMS.add(stem + gerund);
        ALL_VERB_FORMS.add(stem + participle);
    }

    private static void addFormsCustomIR(String infinitive, String stem,
                                           String[] presStem, String[] presSuf,
                                           String[] pretStem, String[] pretSuf,
                                           String[] impf,
                                           String[] subjStem, String[] subjSuf,
                                           String[] impvStem, String[] impvSuf,
                                           String gerund, String participle) {
        ALL_VERB_FORMS.add(infinitive);
        for (int i = 0; i < presStem.length; i++) ALL_VERB_FORMS.add(presStem[i] + presSuf[i]);
        for (int i = 0; i < pretStem.length; i++) ALL_VERB_FORMS.add(pretStem[i] + pretSuf[i]);
        for (String s : impf) ALL_VERB_FORMS.add(stem + s);
        for (int i = 0; i < subjStem.length; i++) ALL_VERB_FORMS.add(subjStem[i] + subjSuf[i]);
        for (int i = 0; i < impvStem.length; i++) ALL_VERB_FORMS.add(impvStem[i] + impvSuf[i]);
        for (String s : FUTURE_ENDINGS) ALL_VERB_FORMS.add(infinitive + s);
        for (String s : CONDITIONAL_ENDINGS) ALL_VERB_FORMS.add(infinitive + s);
        ALL_VERB_FORMS.add(stem + gerund);
        ALL_VERB_FORMS.add(stem + participle);
    }
}
