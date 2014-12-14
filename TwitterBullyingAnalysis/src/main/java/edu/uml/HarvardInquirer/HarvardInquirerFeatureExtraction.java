package edu.uml.HarvardInquirer;

import java.util.List;
import java.util.Map;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class HarvardInquirerFeatureExtraction {

    public static final String[] CATEGORIES_TO_lOOK_FOR = "Positiv Negativ Pstv Affil Ngtv Hostile Strong Power Weak Submit Active Passive Pleasur Pain Feel Arousal EMOT Virtue Vice Ovrst Undrst Academ Doctrin Econ@ Exch ECON Exprsv Legal Milit Polit@ POLIT Relig Role COLL Work Ritual SocRel Race Kin@ MALE Female Nonadlt HU ANI PLACE Social Region Route Aquatic Land Sky Object Tool Food Vehicle BldgPt ComnObj NatObj BodyPt ComForm COM Say Need Goal Try Means Persist Complet Fail NatrPro Begin Vary Increas Decreas Finish Stay Rise Exert Fetch Travel Fall Think Know Causal Ought Perceiv Compare Eval@ EVAL Solve Abs@ ABS Quality Quan NUMB ORD CARD FREQ DIST Time@ TIME Space POS DIM Rel COLOR Self Our You Name Yes No Negate Intrj IAV DAV SV IPadj IndAdj PowGain PowLoss PowEnds PowAren PowCon PowCoop PowAuPt PowPt PowDoct PowAuth PowOth PowTot RcEthic RcRelig RcGain RcLoss RcEnds RcTot RspGain RspLoss RspOth RspTot AffGain AffLoss AffPt AffOth AffTot WltPt WltTran WltOth WltTot WlbGain WlbLoss WlbPhys WlbPsyc WlbPt WlbTot EnlGain EnlLoss EnlEnds EnlPt EnlOth EnlTot SklAsth SklPt SklOth SklTot TrnGain TrnLoss TranLw MeansLw EndsLw ArenaLw PtLw Nation Anomie NegAff PosAff SureLw If NotLw TimeSpc FormLw".split(" ");
//    public static final String[] CATEGORIES_TO_lOOK_FOR = "Positiv Negativ Hostile Strong Power Weak Active Passive Pleasur Pain Virtue Vice Ovrst Undrst Race".split(" ");

    private Tagger tagger;
    private HarvardInquirer harvardInquirer;

    public HarvardInquirerFeatureExtraction(Tagger tagger, HarvardInquirer harvardInquirer) {
        this.tagger = tagger;
        this.harvardInquirer = harvardInquirer;
    }
    
    public double[] extractFeatures(String tweet) {
        List<TaggedToken> tagged = tagger.tokenizeAndTag(tweet);
        
        double[] features = new double[CATEGORIES_TO_lOOK_FOR.length];
        
        for (TaggedToken taggedToken : tagged) {
            String token = taggedToken.token.toLowerCase().replace("#", "");
            for (Map<String, String> entry : harvardInquirer.getEntriesForWord(token)) {
                if (hasTag(taggedToken.tag, entry)) {
                    for(int index = 0; index < CATEGORIES_TO_lOOK_FOR.length; index++) {
                        if(entry.containsKey(CATEGORIES_TO_lOOK_FOR[index])) {
                            features[index] = 1.0;
                        }
                    }
                }
            }
        }
        
        return features;
    }


    private static boolean hasTag(String tag, Map<String, String> entry) {

        if (!entry.containsKey("Othtags")) return false;

        String[] othtags = entry.get("Othtags").split(" ");
        for (int i = 0; i < othtags.length; i++) {
            othtags[i] = othtags[i].toUpperCase();
        }

        if (tag.equals("A")) {
            // adjective
            for (String othtag : othtags) {
                if (othtag.equals("MODIF")) return true;
            }
        } else if (tag.equals("N")) {
            // noun
            for (String othtag : othtags) {
                if (othtag.equals("NOUN")) return true;
            }
        } else if (tag.equals("V")) {
            // verb
            for (String othtag : othtags) {
                if (othtag.equals("VERB") || othtag.equals("SUPV")) return true;
            }
        } else if (tag.equals("R")) {
            // adverb
            for (String othtag : othtags) {
                if (othtag.equals("LY")) return true;
            }
        } else if (tag.equals("O")) {
            // pronoun
            for (String othtag : othtags) {
                if (othtag.equals("PRON")) return true;
            }
        } else if (tag.equals("P")) {
            // preposition
            for (String othtag : othtags) {
                if (othtag.equals("PREP")) return true;
            }
        } else if (tag.equals("S") || tag.equals("Z")) {
            //
            for (String othtag : othtags) {
                if (othtag.equals("DET")) return true;
            }
        }

        return false;
    }
}
