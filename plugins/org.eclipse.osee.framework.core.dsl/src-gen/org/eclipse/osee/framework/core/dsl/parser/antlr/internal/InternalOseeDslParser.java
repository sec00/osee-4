package org.eclipse.osee.framework.core.dsl.parser.antlr.internal; 

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import org.eclipse.osee.framework.core.dsl.services.OseeDslGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalOseeDslParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_ID", "RULE_HEX_INT", "RULE_WHOLE_NUM_STR", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'import'", "'.'", "'abstract'", "'artifactType'", "'extends'", "','", "'{'", "'guid'", "'uuid'", "'}'", "'attribute'", "'branchGuid'", "'attributeType'", "'overrides'", "'dataProvider'", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'min'", "'max'", "'unlimited'", "'taggerId'", "'DefaultAttributeTaggerProvider'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'ArtifactReferenceAttribute'", "'BranchReferenceAttribute'", "'WordAttribute'", "'OutlineNumberAttribute'", "'oseeEnumType'", "'entry'", "'entryGuid'", "'overrides enum'", "'inheritAll'", "'add'", "'remove'", "'overrides artifactType'", "'update'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'multiplicity'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'('", "')'", "'artifactMatcher'", "'where'", "';'", "'role'", "'accessContext'", "'childrenOf'", "'artifact'", "'edit'", "'of'", "'ONE_TO_ONE'", "'ONE_TO_MANY'", "'MANY_TO_ONE'", "'MANY_TO_MANY'", "'EQ'", "'LIKE'", "'AND'", "'OR'", "'artifactName'", "'artifactGuid'", "'branchName'", "'ALLOW'", "'DENY'", "'ALL'", "'SIDE_A'", "'SIDE_B'", "'BOTH'"
    };
    public static final int RULE_ID=5;
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int T__27=27;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int T__23=23;
    public static final int T__22=22;
    public static final int RULE_ANY_OTHER=12;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int EOF=-1;
    public static final int T__93=93;
    public static final int T__19=19;
    public static final int T__94=94;
    public static final int T__91=91;
    public static final int T__92=92;
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int T__90=90;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int T__97=97;
    public static final int T__96=96;
    public static final int T__95=95;
    public static final int T__80=80;
    public static final int T__81=81;
    public static final int T__82=82;
    public static final int T__83=83;
    public static final int T__85=85;
    public static final int T__84=84;
    public static final int T__87=87;
    public static final int T__86=86;
    public static final int T__89=89;
    public static final int T__88=88;
    public static final int RULE_ML_COMMENT=9;
    public static final int RULE_STRING=4;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int T__70=70;
    public static final int T__76=76;
    public static final int T__75=75;
    public static final int T__74=74;
    public static final int T__73=73;
    public static final int T__79=79;
    public static final int T__78=78;
    public static final int T__77=77;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__66=66;
    public static final int T__67=67;
    public static final int T__64=64;
    public static final int T__65=65;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int T__61=61;
    public static final int T__60=60;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__59=59;
    public static final int RULE_INT=8;
    public static final int T__50=50;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int RULE_WHOLE_NUM_STR=7;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int RULE_HEX_INT=6;
    public static final int RULE_SL_COMMENT=10;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int RULE_WS=11;

    // delegates
    // delegators


        public InternalOseeDslParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalOseeDslParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalOseeDslParser.tokenNames; }
    public String getGrammarFileName() { return "../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g"; }



     	private OseeDslGrammarAccess grammarAccess;
     	
        public InternalOseeDslParser(TokenStream input, OseeDslGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "OseeDsl";	
       	}
       	
       	@Override
       	protected OseeDslGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}



    // $ANTLR start "entryRuleOseeDsl"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:68:1: entryRuleOseeDsl returns [EObject current=null] : iv_ruleOseeDsl= ruleOseeDsl EOF ;
    public final EObject entryRuleOseeDsl() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeDsl = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:69:2: (iv_ruleOseeDsl= ruleOseeDsl EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:70:2: iv_ruleOseeDsl= ruleOseeDsl EOF
            {
             newCompositeNode(grammarAccess.getOseeDslRule()); 
            pushFollow(FOLLOW_ruleOseeDsl_in_entryRuleOseeDsl75);
            iv_ruleOseeDsl=ruleOseeDsl();

            state._fsp--;

             current =iv_ruleOseeDsl; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeDsl85); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleOseeDsl"


    // $ANTLR start "ruleOseeDsl"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:77:1: ruleOseeDsl returns [EObject current=null] : ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) | ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) ) )* ( ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) ) | ( (lv_accessDeclarations_8_0= ruleAccessContext ) ) | ( (lv_roleDeclarations_9_0= ruleRole ) ) )* ) ;
    public final EObject ruleOseeDsl() throws RecognitionException {
        EObject current = null;

        EObject lv_imports_0_0 = null;

        EObject lv_artifactTypes_1_0 = null;

        EObject lv_relationTypes_2_0 = null;

        EObject lv_attributeTypes_3_0 = null;

        EObject lv_enumTypes_4_0 = null;

        EObject lv_enumOverrides_5_0 = null;

        EObject lv_artifactTypeOverrides_6_0 = null;

        EObject lv_artifactMatchRefs_7_0 = null;

        EObject lv_accessDeclarations_8_0 = null;

        EObject lv_roleDeclarations_9_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:80:28: ( ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) | ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) ) )* ( ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) ) | ( (lv_accessDeclarations_8_0= ruleAccessContext ) ) | ( (lv_roleDeclarations_9_0= ruleRole ) ) )* ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:81:1: ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) | ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) ) )* ( ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) ) | ( (lv_accessDeclarations_8_0= ruleAccessContext ) ) | ( (lv_roleDeclarations_9_0= ruleRole ) ) )* )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:81:1: ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) | ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) ) )* ( ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) ) | ( (lv_accessDeclarations_8_0= ruleAccessContext ) ) | ( (lv_roleDeclarations_9_0= ruleRole ) ) )* )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:81:2: ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) | ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) ) )* ( ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) ) | ( (lv_accessDeclarations_8_0= ruleAccessContext ) ) | ( (lv_roleDeclarations_9_0= ruleRole ) ) )*
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:81:2: ( (lv_imports_0_0= ruleImport ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==13) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:82:1: (lv_imports_0_0= ruleImport )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:82:1: (lv_imports_0_0= ruleImport )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:83:3: lv_imports_0_0= ruleImport
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getImportsImportParserRuleCall_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleImport_in_ruleOseeDsl131);
            	    lv_imports_0_0=ruleImport();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"imports",
            	            		lv_imports_0_0, 
            	            		"Import");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:99:3: ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) | ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) ) )*
            loop2:
            do {
                int alt2=7;
                switch ( input.LA(1) ) {
                case 15:
                case 16:
                    {
                    alt2=1;
                    }
                    break;
                case 60:
                    {
                    alt2=2;
                    }
                    break;
                case 25:
                    {
                    alt2=3;
                    }
                    break;
                case 51:
                    {
                    alt2=4;
                    }
                    break;
                case 54:
                    {
                    alt2=5;
                    }
                    break;
                case 58:
                    {
                    alt2=6;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:99:4: ( (lv_artifactTypes_1_0= ruleXArtifactType ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:99:4: ( (lv_artifactTypes_1_0= ruleXArtifactType ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:100:1: (lv_artifactTypes_1_0= ruleXArtifactType )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:100:1: (lv_artifactTypes_1_0= ruleXArtifactType )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:101:3: lv_artifactTypes_1_0= ruleXArtifactType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getArtifactTypesXArtifactTypeParserRuleCall_1_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXArtifactType_in_ruleOseeDsl154);
            	    lv_artifactTypes_1_0=ruleXArtifactType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"artifactTypes",
            	            		lv_artifactTypes_1_0, 
            	            		"XArtifactType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:118:6: ( (lv_relationTypes_2_0= ruleXRelationType ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:118:6: ( (lv_relationTypes_2_0= ruleXRelationType ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:119:1: (lv_relationTypes_2_0= ruleXRelationType )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:119:1: (lv_relationTypes_2_0= ruleXRelationType )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:120:3: lv_relationTypes_2_0= ruleXRelationType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getRelationTypesXRelationTypeParserRuleCall_1_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXRelationType_in_ruleOseeDsl181);
            	    lv_relationTypes_2_0=ruleXRelationType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"relationTypes",
            	            		lv_relationTypes_2_0, 
            	            		"XRelationType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:137:6: ( (lv_attributeTypes_3_0= ruleXAttributeType ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:137:6: ( (lv_attributeTypes_3_0= ruleXAttributeType ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:138:1: (lv_attributeTypes_3_0= ruleXAttributeType )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:138:1: (lv_attributeTypes_3_0= ruleXAttributeType )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:139:3: lv_attributeTypes_3_0= ruleXAttributeType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getAttributeTypesXAttributeTypeParserRuleCall_1_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXAttributeType_in_ruleOseeDsl208);
            	    lv_attributeTypes_3_0=ruleXAttributeType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"attributeTypes",
            	            		lv_attributeTypes_3_0, 
            	            		"XAttributeType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 4 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:156:6: ( (lv_enumTypes_4_0= ruleXOseeEnumType ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:156:6: ( (lv_enumTypes_4_0= ruleXOseeEnumType ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:157:1: (lv_enumTypes_4_0= ruleXOseeEnumType )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:157:1: (lv_enumTypes_4_0= ruleXOseeEnumType )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:158:3: lv_enumTypes_4_0= ruleXOseeEnumType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getEnumTypesXOseeEnumTypeParserRuleCall_1_3_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumType_in_ruleOseeDsl235);
            	    lv_enumTypes_4_0=ruleXOseeEnumType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"enumTypes",
            	            		lv_enumTypes_4_0, 
            	            		"XOseeEnumType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 5 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:175:6: ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:175:6: ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:176:1: (lv_enumOverrides_5_0= ruleXOseeEnumOverride )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:176:1: (lv_enumOverrides_5_0= ruleXOseeEnumOverride )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:177:3: lv_enumOverrides_5_0= ruleXOseeEnumOverride
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getEnumOverridesXOseeEnumOverrideParserRuleCall_1_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumOverride_in_ruleOseeDsl262);
            	    lv_enumOverrides_5_0=ruleXOseeEnumOverride();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"enumOverrides",
            	            		lv_enumOverrides_5_0, 
            	            		"XOseeEnumOverride");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 6 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:194:6: ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:194:6: ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:195:1: (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:195:1: (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:196:3: lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getArtifactTypeOverridesXOseeArtifactTypeOverrideParserRuleCall_1_5_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeArtifactTypeOverride_in_ruleOseeDsl289);
            	    lv_artifactTypeOverrides_6_0=ruleXOseeArtifactTypeOverride();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"artifactTypeOverrides",
            	            		lv_artifactTypeOverrides_6_0, 
            	            		"XOseeArtifactTypeOverride");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:212:4: ( ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) ) | ( (lv_accessDeclarations_8_0= ruleAccessContext ) ) | ( (lv_roleDeclarations_9_0= ruleRole ) ) )*
            loop3:
            do {
                int alt3=4;
                switch ( input.LA(1) ) {
                case 72:
                    {
                    alt3=1;
                    }
                    break;
                case 76:
                    {
                    alt3=2;
                    }
                    break;
                case 75:
                    {
                    alt3=3;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:212:5: ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:212:5: ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:213:1: (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:213:1: (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:214:3: lv_artifactMatchRefs_7_0= ruleXArtifactMatcher
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getArtifactMatchRefsXArtifactMatcherParserRuleCall_2_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXArtifactMatcher_in_ruleOseeDsl313);
            	    lv_artifactMatchRefs_7_0=ruleXArtifactMatcher();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"artifactMatchRefs",
            	            		lv_artifactMatchRefs_7_0, 
            	            		"XArtifactMatcher");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:231:6: ( (lv_accessDeclarations_8_0= ruleAccessContext ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:231:6: ( (lv_accessDeclarations_8_0= ruleAccessContext ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:232:1: (lv_accessDeclarations_8_0= ruleAccessContext )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:232:1: (lv_accessDeclarations_8_0= ruleAccessContext )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:233:3: lv_accessDeclarations_8_0= ruleAccessContext
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getAccessDeclarationsAccessContextParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleAccessContext_in_ruleOseeDsl340);
            	    lv_accessDeclarations_8_0=ruleAccessContext();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"accessDeclarations",
            	            		lv_accessDeclarations_8_0, 
            	            		"AccessContext");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:250:6: ( (lv_roleDeclarations_9_0= ruleRole ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:250:6: ( (lv_roleDeclarations_9_0= ruleRole ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:251:1: (lv_roleDeclarations_9_0= ruleRole )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:251:1: (lv_roleDeclarations_9_0= ruleRole )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:252:3: lv_roleDeclarations_9_0= ruleRole
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getRoleDeclarationsRoleParserRuleCall_2_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleRole_in_ruleOseeDsl367);
            	    lv_roleDeclarations_9_0=ruleRole();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"roleDeclarations",
            	            		lv_roleDeclarations_9_0, 
            	            		"Role");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleOseeDsl"


    // $ANTLR start "entryRuleImport"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:276:1: entryRuleImport returns [EObject current=null] : iv_ruleImport= ruleImport EOF ;
    public final EObject entryRuleImport() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImport = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:277:2: (iv_ruleImport= ruleImport EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:278:2: iv_ruleImport= ruleImport EOF
            {
             newCompositeNode(grammarAccess.getImportRule()); 
            pushFollow(FOLLOW_ruleImport_in_entryRuleImport405);
            iv_ruleImport=ruleImport();

            state._fsp--;

             current =iv_ruleImport; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleImport415); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleImport"


    // $ANTLR start "ruleImport"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:285:1: ruleImport returns [EObject current=null] : (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleImport() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_importURI_1_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:288:28: ( (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:289:1: (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:289:1: (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:289:3: otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,13,FOLLOW_13_in_ruleImport452); 

                	newLeafNode(otherlv_0, grammarAccess.getImportAccess().getImportKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:293:1: ( (lv_importURI_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:294:1: (lv_importURI_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:294:1: (lv_importURI_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:295:3: lv_importURI_1_0= RULE_STRING
            {
            lv_importURI_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleImport469); 

            			newLeafNode(lv_importURI_1_0, grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getImportRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"importURI",
                    		lv_importURI_1_0, 
                    		"STRING");
            	    

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleImport"


    // $ANTLR start "entryRuleQUALIFIED_NAME"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:319:1: entryRuleQUALIFIED_NAME returns [String current=null] : iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF ;
    public final String entryRuleQUALIFIED_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQUALIFIED_NAME = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:320:2: (iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:321:2: iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF
            {
             newCompositeNode(grammarAccess.getQUALIFIED_NAMERule()); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME511);
            iv_ruleQUALIFIED_NAME=ruleQUALIFIED_NAME();

            state._fsp--;

             current =iv_ruleQUALIFIED_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleQUALIFIED_NAME522); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleQUALIFIED_NAME"


    // $ANTLR start "ruleQUALIFIED_NAME"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:328:1: ruleQUALIFIED_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQUALIFIED_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:331:28: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:332:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:332:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:332:6: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME562); 

            		current.merge(this_ID_0);
                
             
                newLeafNode(this_ID_0, grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:339:1: (kw= '.' this_ID_2= RULE_ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==14) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:340:2: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)match(input,14,FOLLOW_14_in_ruleQUALIFIED_NAME581); 

            	            current.merge(kw);
            	            newLeafNode(kw, grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 
            	        
            	    this_ID_2=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME596); 

            	    		current.merge(this_ID_2);
            	        
            	     
            	        newLeafNode(this_ID_2, grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1()); 
            	        

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleQUALIFIED_NAME"


    // $ANTLR start "entryRuleOseeType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:362:1: entryRuleOseeType returns [EObject current=null] : iv_ruleOseeType= ruleOseeType EOF ;
    public final EObject entryRuleOseeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:363:2: (iv_ruleOseeType= ruleOseeType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:364:2: iv_ruleOseeType= ruleOseeType EOF
            {
             newCompositeNode(grammarAccess.getOseeTypeRule()); 
            pushFollow(FOLLOW_ruleOseeType_in_entryRuleOseeType645);
            iv_ruleOseeType=ruleOseeType();

            state._fsp--;

             current =iv_ruleOseeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeType655); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleOseeType"


    // $ANTLR start "ruleOseeType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:371:1: ruleOseeType returns [EObject current=null] : (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType ) ;
    public final EObject ruleOseeType() throws RecognitionException {
        EObject current = null;

        EObject this_XArtifactType_0 = null;

        EObject this_XRelationType_1 = null;

        EObject this_XAttributeType_2 = null;

        EObject this_XOseeEnumType_3 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:374:28: ( (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:375:1: (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:375:1: (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType )
            int alt5=4;
            switch ( input.LA(1) ) {
            case 15:
            case 16:
                {
                alt5=1;
                }
                break;
            case 60:
                {
                alt5=2;
                }
                break;
            case 25:
                {
                alt5=3;
                }
                break;
            case 51:
                {
                alt5=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:376:5: this_XArtifactType_0= ruleXArtifactType
                    {
                     
                            newCompositeNode(grammarAccess.getOseeTypeAccess().getXArtifactTypeParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleXArtifactType_in_ruleOseeType702);
                    this_XArtifactType_0=ruleXArtifactType();

                    state._fsp--;

                     
                            current = this_XArtifactType_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:386:5: this_XRelationType_1= ruleXRelationType
                    {
                     
                            newCompositeNode(grammarAccess.getOseeTypeAccess().getXRelationTypeParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleXRelationType_in_ruleOseeType729);
                    this_XRelationType_1=ruleXRelationType();

                    state._fsp--;

                     
                            current = this_XRelationType_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:396:5: this_XAttributeType_2= ruleXAttributeType
                    {
                     
                            newCompositeNode(grammarAccess.getOseeTypeAccess().getXAttributeTypeParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleXAttributeType_in_ruleOseeType756);
                    this_XAttributeType_2=ruleXAttributeType();

                    state._fsp--;

                     
                            current = this_XAttributeType_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:406:5: this_XOseeEnumType_3= ruleXOseeEnumType
                    {
                     
                            newCompositeNode(grammarAccess.getOseeTypeAccess().getXOseeEnumTypeParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_ruleXOseeEnumType_in_ruleOseeType783);
                    this_XOseeEnumType_3=ruleXOseeEnumType();

                    state._fsp--;

                     
                            current = this_XOseeEnumType_3; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleOseeType"


    // $ANTLR start "entryRuleXArtifactType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:422:1: entryRuleXArtifactType returns [EObject current=null] : iv_ruleXArtifactType= ruleXArtifactType EOF ;
    public final EObject entryRuleXArtifactType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXArtifactType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:423:2: (iv_ruleXArtifactType= ruleXArtifactType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:424:2: iv_ruleXArtifactType= ruleXArtifactType EOF
            {
             newCompositeNode(grammarAccess.getXArtifactTypeRule()); 
            pushFollow(FOLLOW_ruleXArtifactType_in_entryRuleXArtifactType818);
            iv_ruleXArtifactType=ruleXArtifactType();

            state._fsp--;

             current =iv_ruleXArtifactType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXArtifactType828); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleXArtifactType"


    // $ANTLR start "ruleXArtifactType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:431:1: ruleXArtifactType returns [EObject current=null] : ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' ) ;
    public final EObject ruleXArtifactType() throws RecognitionException {
        EObject current = null;

        Token lv_abstract_0_0=null;
        Token otherlv_1=null;
        Token lv_name_2_0=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token lv_typeGuid_9_0=null;
        Token otherlv_10=null;
        Token lv_uuid_11_0=null;
        Token otherlv_13=null;
        EObject lv_validAttributeTypes_12_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:434:28: ( ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:435:1: ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:435:1: ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:435:2: ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:435:2: ( (lv_abstract_0_0= 'abstract' ) )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==15) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:436:1: (lv_abstract_0_0= 'abstract' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:436:1: (lv_abstract_0_0= 'abstract' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:437:3: lv_abstract_0_0= 'abstract'
                    {
                    lv_abstract_0_0=(Token)match(input,15,FOLLOW_15_in_ruleXArtifactType871); 

                            newLeafNode(lv_abstract_0_0, grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
                    	        }
                           		setWithLastConsumed(current, "abstract", true, "abstract");
                    	    

                    }


                    }
                    break;

            }

            otherlv_1=(Token)match(input,16,FOLLOW_16_in_ruleXArtifactType897); 

                	newLeafNode(otherlv_1, grammarAccess.getXArtifactTypeAccess().getArtifactTypeKeyword_1());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:454:1: ( (lv_name_2_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:455:1: (lv_name_2_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:455:1: (lv_name_2_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:456:3: lv_name_2_0= RULE_STRING
            {
            lv_name_2_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactType914); 

            			newLeafNode(lv_name_2_0, grammarAccess.getXArtifactTypeAccess().getNameSTRINGTerminalRuleCall_2_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_2_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:472:2: (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==17) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:472:4: otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )*
                    {
                    otherlv_3=(Token)match(input,17,FOLLOW_17_in_ruleXArtifactType932); 

                        	newLeafNode(otherlv_3, grammarAccess.getXArtifactTypeAccess().getExtendsKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:476:1: ( (otherlv_4= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:477:1: (otherlv_4= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:477:1: (otherlv_4= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:478:3: otherlv_4= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
                    	        }
                            
                    otherlv_4=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactType952); 

                    		newLeafNode(otherlv_4, grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_1_0()); 
                    	

                    }


                    }

                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:489:2: (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==18) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:489:4: otherlv_5= ',' ( (otherlv_6= RULE_STRING ) )
                    	    {
                    	    otherlv_5=(Token)match(input,18,FOLLOW_18_in_ruleXArtifactType965); 

                    	        	newLeafNode(otherlv_5, grammarAccess.getXArtifactTypeAccess().getCommaKeyword_3_2_0());
                    	        
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:493:1: ( (otherlv_6= RULE_STRING ) )
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:494:1: (otherlv_6= RULE_STRING )
                    	    {
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:494:1: (otherlv_6= RULE_STRING )
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:495:3: otherlv_6= RULE_STRING
                    	    {

                    	    			if (current==null) {
                    	    	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
                    	    	        }
                    	            
                    	    otherlv_6=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactType985); 

                    	    		newLeafNode(otherlv_6, grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_2_1_0()); 
                    	    	

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_7=(Token)match(input,19,FOLLOW_19_in_ruleXArtifactType1001); 

                	newLeafNode(otherlv_7, grammarAccess.getXArtifactTypeAccess().getLeftCurlyBracketKeyword_4());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:510:1: (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==20) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:510:3: otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) )
                    {
                    otherlv_8=(Token)match(input,20,FOLLOW_20_in_ruleXArtifactType1014); 

                        	newLeafNode(otherlv_8, grammarAccess.getXArtifactTypeAccess().getGuidKeyword_5_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:514:1: ( (lv_typeGuid_9_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:515:1: (lv_typeGuid_9_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:515:1: (lv_typeGuid_9_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:516:3: lv_typeGuid_9_0= RULE_STRING
                    {
                    lv_typeGuid_9_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactType1031); 

                    			newLeafNode(lv_typeGuid_9_0, grammarAccess.getXArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_5_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"typeGuid",
                            		lv_typeGuid_9_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_10=(Token)match(input,21,FOLLOW_21_in_ruleXArtifactType1050); 

                	newLeafNode(otherlv_10, grammarAccess.getXArtifactTypeAccess().getUuidKeyword_6());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:536:1: ( (lv_uuid_11_0= RULE_HEX_INT ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:537:1: (lv_uuid_11_0= RULE_HEX_INT )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:537:1: (lv_uuid_11_0= RULE_HEX_INT )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:538:3: lv_uuid_11_0= RULE_HEX_INT
            {
            lv_uuid_11_0=(Token)match(input,RULE_HEX_INT,FOLLOW_RULE_HEX_INT_in_ruleXArtifactType1067); 

            			newLeafNode(lv_uuid_11_0, grammarAccess.getXArtifactTypeAccess().getUuidHEX_INTTerminalRuleCall_7_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"uuid",
                    		lv_uuid_11_0, 
                    		"HEX_INT");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:554:2: ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==23) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:555:1: (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:555:1: (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:556:3: lv_validAttributeTypes_12_0= ruleXAttributeTypeRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesXAttributeTypeRefParserRuleCall_8_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXAttributeTypeRef_in_ruleXArtifactType1093);
            	    lv_validAttributeTypes_12_0=ruleXAttributeTypeRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXArtifactTypeRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"validAttributeTypes",
            	            		lv_validAttributeTypes_12_0, 
            	            		"XAttributeTypeRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            otherlv_13=(Token)match(input,22,FOLLOW_22_in_ruleXArtifactType1106); 

                	newLeafNode(otherlv_13, grammarAccess.getXArtifactTypeAccess().getRightCurlyBracketKeyword_9());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleXArtifactType"


    // $ANTLR start "entryRuleXAttributeTypeRef"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:584:1: entryRuleXAttributeTypeRef returns [EObject current=null] : iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF ;
    public final EObject entryRuleXAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXAttributeTypeRef = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:585:2: (iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:586:2: iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF
            {
             newCompositeNode(grammarAccess.getXAttributeTypeRefRule()); 
            pushFollow(FOLLOW_ruleXAttributeTypeRef_in_entryRuleXAttributeTypeRef1142);
            iv_ruleXAttributeTypeRef=ruleXAttributeTypeRef();

            state._fsp--;

             current =iv_ruleXAttributeTypeRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXAttributeTypeRef1152); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleXAttributeTypeRef"


    // $ANTLR start "ruleXAttributeTypeRef"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:593:1: ruleXAttributeTypeRef returns [EObject current=null] : (otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleXAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_branchGuid_3_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:596:28: ( (otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:597:1: (otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:597:1: (otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:597:3: otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )?
            {
            otherlv_0=(Token)match(input,23,FOLLOW_23_in_ruleXAttributeTypeRef1189); 

                	newLeafNode(otherlv_0, grammarAccess.getXAttributeTypeRefAccess().getAttributeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:601:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:602:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:602:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:603:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRefRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1209); 

            		newLeafNode(otherlv_1, grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeCrossReference_1_0()); 
            	

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:614:2: (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==24) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:614:4: otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) )
                    {
                    otherlv_2=(Token)match(input,24,FOLLOW_24_in_ruleXAttributeTypeRef1222); 

                        	newLeafNode(otherlv_2, grammarAccess.getXAttributeTypeRefAccess().getBranchGuidKeyword_2_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:618:1: ( (lv_branchGuid_3_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:619:1: (lv_branchGuid_3_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:619:1: (lv_branchGuid_3_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:620:3: lv_branchGuid_3_0= RULE_STRING
                    {
                    lv_branchGuid_3_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1239); 

                    			newLeafNode(lv_branchGuid_3_0, grammarAccess.getXAttributeTypeRefAccess().getBranchGuidSTRINGTerminalRuleCall_2_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"branchGuid",
                            		lv_branchGuid_3_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleXAttributeTypeRef"


    // $ANTLR start "entryRuleXAttributeType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:644:1: entryRuleXAttributeType returns [EObject current=null] : iv_ruleXAttributeType= ruleXAttributeType EOF ;
    public final EObject entryRuleXAttributeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXAttributeType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:645:2: (iv_ruleXAttributeType= ruleXAttributeType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:646:2: iv_ruleXAttributeType= ruleXAttributeType EOF
            {
             newCompositeNode(grammarAccess.getXAttributeTypeRule()); 
            pushFollow(FOLLOW_ruleXAttributeType_in_entryRuleXAttributeType1282);
            iv_ruleXAttributeType=ruleXAttributeType();

            state._fsp--;

             current =iv_ruleXAttributeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXAttributeType1292); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleXAttributeType"


    // $ANTLR start "ruleXAttributeType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:653:1: ruleXAttributeType returns [EObject current=null] : (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}' ) ;
    public final EObject ruleXAttributeType() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token lv_typeGuid_8_0=null;
        Token otherlv_9=null;
        Token lv_uuid_10_0=null;
        Token otherlv_11=null;
        Token lv_dataProvider_12_1=null;
        Token lv_dataProvider_12_2=null;
        Token otherlv_13=null;
        Token lv_min_14_0=null;
        Token otherlv_15=null;
        Token lv_max_16_1=null;
        Token lv_max_16_2=null;
        Token otherlv_17=null;
        Token lv_taggerId_18_1=null;
        Token otherlv_19=null;
        Token otherlv_20=null;
        Token otherlv_21=null;
        Token lv_description_22_0=null;
        Token otherlv_23=null;
        Token lv_defaultValue_24_0=null;
        Token otherlv_25=null;
        Token lv_fileExtension_26_0=null;
        Token otherlv_27=null;
        AntlrDatatypeRuleToken lv_baseAttributeType_3_0 = null;

        AntlrDatatypeRuleToken lv_dataProvider_12_3 = null;

        AntlrDatatypeRuleToken lv_taggerId_18_2 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:656:28: ( (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:657:1: (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:657:1: (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:657:3: otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}'
            {
            otherlv_0=(Token)match(input,25,FOLLOW_25_in_ruleXAttributeType1329); 

                	newLeafNode(otherlv_0, grammarAccess.getXAttributeTypeAccess().getAttributeTypeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:661:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:662:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:662:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:663:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1346); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXAttributeTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:679:2: (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:679:4: otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            {
            otherlv_2=(Token)match(input,17,FOLLOW_17_in_ruleXAttributeType1364); 

                	newLeafNode(otherlv_2, grammarAccess.getXAttributeTypeAccess().getExtendsKeyword_2_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:683:1: ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:684:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:684:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:685:3: lv_baseAttributeType_3_0= ruleAttributeBaseType
            {
             
            	        newCompositeNode(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0()); 
            	    
            pushFollow(FOLLOW_ruleAttributeBaseType_in_ruleXAttributeType1385);
            lv_baseAttributeType_3_0=ruleAttributeBaseType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getXAttributeTypeRule());
            	        }
                   		set(
                   			current, 
                   			"baseAttributeType",
                    		lv_baseAttributeType_3_0, 
                    		"AttributeBaseType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:701:3: (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==26) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:701:5: otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) )
                    {
                    otherlv_4=(Token)match(input,26,FOLLOW_26_in_ruleXAttributeType1399); 

                        	newLeafNode(otherlv_4, grammarAccess.getXAttributeTypeAccess().getOverridesKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:705:1: ( (otherlv_5= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:706:1: (otherlv_5= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:706:1: (otherlv_5= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:707:3: otherlv_5= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                            
                    otherlv_5=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1419); 

                    		newLeafNode(otherlv_5, grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeCrossReference_3_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_6=(Token)match(input,19,FOLLOW_19_in_ruleXAttributeType1433); 

                	newLeafNode(otherlv_6, grammarAccess.getXAttributeTypeAccess().getLeftCurlyBracketKeyword_4());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:722:1: (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==20) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:722:3: otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,20,FOLLOW_20_in_ruleXAttributeType1446); 

                        	newLeafNode(otherlv_7, grammarAccess.getXAttributeTypeAccess().getGuidKeyword_5_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:726:1: ( (lv_typeGuid_8_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:727:1: (lv_typeGuid_8_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:727:1: (lv_typeGuid_8_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:728:3: lv_typeGuid_8_0= RULE_STRING
                    {
                    lv_typeGuid_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1463); 

                    			newLeafNode(lv_typeGuid_8_0, grammarAccess.getXAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_5_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"typeGuid",
                            		lv_typeGuid_8_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_9=(Token)match(input,21,FOLLOW_21_in_ruleXAttributeType1482); 

                	newLeafNode(otherlv_9, grammarAccess.getXAttributeTypeAccess().getUuidKeyword_6());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:748:1: ( (lv_uuid_10_0= RULE_HEX_INT ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:749:1: (lv_uuid_10_0= RULE_HEX_INT )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:749:1: (lv_uuid_10_0= RULE_HEX_INT )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:750:3: lv_uuid_10_0= RULE_HEX_INT
            {
            lv_uuid_10_0=(Token)match(input,RULE_HEX_INT,FOLLOW_RULE_HEX_INT_in_ruleXAttributeType1499); 

            			newLeafNode(lv_uuid_10_0, grammarAccess.getXAttributeTypeAccess().getUuidHEX_INTTerminalRuleCall_7_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"uuid",
                    		lv_uuid_10_0, 
                    		"HEX_INT");
            	    

            }


            }

            otherlv_11=(Token)match(input,27,FOLLOW_27_in_ruleXAttributeType1516); 

                	newLeafNode(otherlv_11, grammarAccess.getXAttributeTypeAccess().getDataProviderKeyword_8());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:770:1: ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:771:1: ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:771:1: ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:772:1: (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:772:1: (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME )
            int alt14=3;
            switch ( input.LA(1) ) {
            case 28:
                {
                alt14=1;
                }
                break;
            case 29:
                {
                alt14=2;
                }
                break;
            case RULE_ID:
                {
                alt14=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:773:3: lv_dataProvider_12_1= 'DefaultAttributeDataProvider'
                    {
                    lv_dataProvider_12_1=(Token)match(input,28,FOLLOW_28_in_ruleXAttributeType1536); 

                            newLeafNode(lv_dataProvider_12_1, grammarAccess.getXAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_9_0_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(current, "dataProvider", lv_dataProvider_12_1, null);
                    	    

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:785:8: lv_dataProvider_12_2= 'UriAttributeDataProvider'
                    {
                    lv_dataProvider_12_2=(Token)match(input,29,FOLLOW_29_in_ruleXAttributeType1565); 

                            newLeafNode(lv_dataProvider_12_2, grammarAccess.getXAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_9_0_1());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(current, "dataProvider", lv_dataProvider_12_2, null);
                    	    

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:797:8: lv_dataProvider_12_3= ruleQUALIFIED_NAME
                    {
                     
                    	        newCompositeNode(grammarAccess.getXAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_9_0_2()); 
                    	    
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1597);
                    lv_dataProvider_12_3=ruleQUALIFIED_NAME();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		set(
                           			current, 
                           			"dataProvider",
                            		lv_dataProvider_12_3, 
                            		"QUALIFIED_NAME");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }
                    break;

            }


            }


            }

            otherlv_13=(Token)match(input,30,FOLLOW_30_in_ruleXAttributeType1612); 

                	newLeafNode(otherlv_13, grammarAccess.getXAttributeTypeAccess().getMinKeyword_10());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:819:1: ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:820:1: (lv_min_14_0= RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:820:1: (lv_min_14_0= RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:821:3: lv_min_14_0= RULE_WHOLE_NUM_STR
            {
            lv_min_14_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1629); 

            			newLeafNode(lv_min_14_0, grammarAccess.getXAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_11_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"min",
                    		lv_min_14_0, 
                    		"WHOLE_NUM_STR");
            	    

            }


            }

            otherlv_15=(Token)match(input,31,FOLLOW_31_in_ruleXAttributeType1646); 

                	newLeafNode(otherlv_15, grammarAccess.getXAttributeTypeAccess().getMaxKeyword_12());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:841:1: ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:842:1: ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:842:1: ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:843:1: (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:843:1: (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==RULE_WHOLE_NUM_STR) ) {
                alt15=1;
            }
            else if ( (LA15_0==32) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:844:3: lv_max_16_1= RULE_WHOLE_NUM_STR
                    {
                    lv_max_16_1=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1665); 

                    			newLeafNode(lv_max_16_1, grammarAccess.getXAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_13_0_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"max",
                            		lv_max_16_1, 
                            		"WHOLE_NUM_STR");
                    	    

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:859:8: lv_max_16_2= 'unlimited'
                    {
                    lv_max_16_2=(Token)match(input,32,FOLLOW_32_in_ruleXAttributeType1686); 

                            newLeafNode(lv_max_16_2, grammarAccess.getXAttributeTypeAccess().getMaxUnlimitedKeyword_13_0_1());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(current, "max", lv_max_16_2, null);
                    	    

                    }
                    break;

            }


            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:874:2: (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==33) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:874:4: otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) )
                    {
                    otherlv_17=(Token)match(input,33,FOLLOW_33_in_ruleXAttributeType1715); 

                        	newLeafNode(otherlv_17, grammarAccess.getXAttributeTypeAccess().getTaggerIdKeyword_14_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:878:1: ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:879:1: ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:879:1: ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:880:1: (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:880:1: (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME )
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==34) ) {
                        alt16=1;
                    }
                    else if ( (LA16_0==RULE_ID) ) {
                        alt16=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 16, 0, input);

                        throw nvae;
                    }
                    switch (alt16) {
                        case 1 :
                            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:881:3: lv_taggerId_18_1= 'DefaultAttributeTaggerProvider'
                            {
                            lv_taggerId_18_1=(Token)match(input,34,FOLLOW_34_in_ruleXAttributeType1735); 

                                    newLeafNode(lv_taggerId_18_1, grammarAccess.getXAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_14_1_0_0());
                                

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                            	        }
                                   		setWithLastConsumed(current, "taggerId", lv_taggerId_18_1, null);
                            	    

                            }
                            break;
                        case 2 :
                            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:893:8: lv_taggerId_18_2= ruleQUALIFIED_NAME
                            {
                             
                            	        newCompositeNode(grammarAccess.getXAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_14_1_0_1()); 
                            	    
                            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1767);
                            lv_taggerId_18_2=ruleQUALIFIED_NAME();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getXAttributeTypeRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"taggerId",
                                    		lv_taggerId_18_2, 
                                    		"QUALIFIED_NAME");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }
                            break;

                    }


                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:911:4: (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==35) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:911:6: otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) )
                    {
                    otherlv_19=(Token)match(input,35,FOLLOW_35_in_ruleXAttributeType1785); 

                        	newLeafNode(otherlv_19, grammarAccess.getXAttributeTypeAccess().getEnumTypeKeyword_15_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:915:1: ( (otherlv_20= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:916:1: (otherlv_20= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:916:1: (otherlv_20= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:917:3: otherlv_20= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                            
                    otherlv_20=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1805); 

                    		newLeafNode(otherlv_20, grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeCrossReference_15_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:928:4: (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==36) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:928:6: otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) )
                    {
                    otherlv_21=(Token)match(input,36,FOLLOW_36_in_ruleXAttributeType1820); 

                        	newLeafNode(otherlv_21, grammarAccess.getXAttributeTypeAccess().getDescriptionKeyword_16_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:932:1: ( (lv_description_22_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:933:1: (lv_description_22_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:933:1: (lv_description_22_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:934:3: lv_description_22_0= RULE_STRING
                    {
                    lv_description_22_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1837); 

                    			newLeafNode(lv_description_22_0, grammarAccess.getXAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_16_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_22_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:950:4: (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==37) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:950:6: otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) )
                    {
                    otherlv_23=(Token)match(input,37,FOLLOW_37_in_ruleXAttributeType1857); 

                        	newLeafNode(otherlv_23, grammarAccess.getXAttributeTypeAccess().getDefaultValueKeyword_17_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:954:1: ( (lv_defaultValue_24_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:955:1: (lv_defaultValue_24_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:955:1: (lv_defaultValue_24_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:956:3: lv_defaultValue_24_0= RULE_STRING
                    {
                    lv_defaultValue_24_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1874); 

                    			newLeafNode(lv_defaultValue_24_0, grammarAccess.getXAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_17_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"defaultValue",
                            		lv_defaultValue_24_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:972:4: (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==38) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:972:6: otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) )
                    {
                    otherlv_25=(Token)match(input,38,FOLLOW_38_in_ruleXAttributeType1894); 

                        	newLeafNode(otherlv_25, grammarAccess.getXAttributeTypeAccess().getFileExtensionKeyword_18_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:976:1: ( (lv_fileExtension_26_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:977:1: (lv_fileExtension_26_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:977:1: (lv_fileExtension_26_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:978:3: lv_fileExtension_26_0= RULE_STRING
                    {
                    lv_fileExtension_26_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1911); 

                    			newLeafNode(lv_fileExtension_26_0, grammarAccess.getXAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_18_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"fileExtension",
                            		lv_fileExtension_26_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_27=(Token)match(input,22,FOLLOW_22_in_ruleXAttributeType1930); 

                	newLeafNode(otherlv_27, grammarAccess.getXAttributeTypeAccess().getRightCurlyBracketKeyword_19());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleXAttributeType"


    // $ANTLR start "entryRuleAttributeBaseType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1006:1: entryRuleAttributeBaseType returns [String current=null] : iv_ruleAttributeBaseType= ruleAttributeBaseType EOF ;
    public final String entryRuleAttributeBaseType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAttributeBaseType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1007:2: (iv_ruleAttributeBaseType= ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1008:2: iv_ruleAttributeBaseType= ruleAttributeBaseType EOF
            {
             newCompositeNode(grammarAccess.getAttributeBaseTypeRule()); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType1967);
            iv_ruleAttributeBaseType=ruleAttributeBaseType();

            state._fsp--;

             current =iv_ruleAttributeBaseType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeBaseType1978); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttributeBaseType"


    // $ANTLR start "ruleAttributeBaseType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1015:1: ruleAttributeBaseType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'ArtifactReferenceAttribute' | kw= 'BranchReferenceAttribute' | kw= 'WordAttribute' | kw= 'OutlineNumberAttribute' | this_QUALIFIED_NAME_12= ruleQUALIFIED_NAME ) ;
    public final AntlrDatatypeRuleToken ruleAttributeBaseType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_QUALIFIED_NAME_12 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1018:28: ( (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'ArtifactReferenceAttribute' | kw= 'BranchReferenceAttribute' | kw= 'WordAttribute' | kw= 'OutlineNumberAttribute' | this_QUALIFIED_NAME_12= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1019:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'ArtifactReferenceAttribute' | kw= 'BranchReferenceAttribute' | kw= 'WordAttribute' | kw= 'OutlineNumberAttribute' | this_QUALIFIED_NAME_12= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1019:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'ArtifactReferenceAttribute' | kw= 'BranchReferenceAttribute' | kw= 'WordAttribute' | kw= 'OutlineNumberAttribute' | this_QUALIFIED_NAME_12= ruleQUALIFIED_NAME )
            int alt22=13;
            switch ( input.LA(1) ) {
            case 39:
                {
                alt22=1;
                }
                break;
            case 40:
                {
                alt22=2;
                }
                break;
            case 41:
                {
                alt22=3;
                }
                break;
            case 42:
                {
                alt22=4;
                }
                break;
            case 43:
                {
                alt22=5;
                }
                break;
            case 44:
                {
                alt22=6;
                }
                break;
            case 45:
                {
                alt22=7;
                }
                break;
            case 46:
                {
                alt22=8;
                }
                break;
            case 47:
                {
                alt22=9;
                }
                break;
            case 48:
                {
                alt22=10;
                }
                break;
            case 49:
                {
                alt22=11;
                }
                break;
            case 50:
                {
                alt22=12;
                }
                break;
            case RULE_ID:
                {
                alt22=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1020:2: kw= 'BooleanAttribute'
                    {
                    kw=(Token)match(input,39,FOLLOW_39_in_ruleAttributeBaseType2016); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1027:2: kw= 'CompressedContentAttribute'
                    {
                    kw=(Token)match(input,40,FOLLOW_40_in_ruleAttributeBaseType2035); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1034:2: kw= 'DateAttribute'
                    {
                    kw=(Token)match(input,41,FOLLOW_41_in_ruleAttributeBaseType2054); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1041:2: kw= 'EnumeratedAttribute'
                    {
                    kw=(Token)match(input,42,FOLLOW_42_in_ruleAttributeBaseType2073); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1048:2: kw= 'FloatingPointAttribute'
                    {
                    kw=(Token)match(input,43,FOLLOW_43_in_ruleAttributeBaseType2092); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1055:2: kw= 'IntegerAttribute'
                    {
                    kw=(Token)match(input,44,FOLLOW_44_in_ruleAttributeBaseType2111); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1062:2: kw= 'JavaObjectAttribute'
                    {
                    kw=(Token)match(input,45,FOLLOW_45_in_ruleAttributeBaseType2130); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1069:2: kw= 'StringAttribute'
                    {
                    kw=(Token)match(input,46,FOLLOW_46_in_ruleAttributeBaseType2149); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1076:2: kw= 'ArtifactReferenceAttribute'
                    {
                    kw=(Token)match(input,47,FOLLOW_47_in_ruleAttributeBaseType2168); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getArtifactReferenceAttributeKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1083:2: kw= 'BranchReferenceAttribute'
                    {
                    kw=(Token)match(input,48,FOLLOW_48_in_ruleAttributeBaseType2187); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getBranchReferenceAttributeKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1090:2: kw= 'WordAttribute'
                    {
                    kw=(Token)match(input,49,FOLLOW_49_in_ruleAttributeBaseType2206); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1097:2: kw= 'OutlineNumberAttribute'
                    {
                    kw=(Token)match(input,50,FOLLOW_50_in_ruleAttributeBaseType2225); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getOutlineNumberAttributeKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1104:5: this_QUALIFIED_NAME_12= ruleQUALIFIED_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_12()); 
                        
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2253);
                    this_QUALIFIED_NAME_12=ruleQUALIFIED_NAME();

                    state._fsp--;


                    		current.merge(this_QUALIFIED_NAME_12);
                        
                     
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttributeBaseType"


    // $ANTLR start "entryRuleXOseeEnumType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1122:1: entryRuleXOseeEnumType returns [EObject current=null] : iv_ruleXOseeEnumType= ruleXOseeEnumType EOF ;
    public final EObject entryRuleXOseeEnumType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1123:2: (iv_ruleXOseeEnumType= ruleXOseeEnumType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1124:2: iv_ruleXOseeEnumType= ruleXOseeEnumType EOF
            {
             newCompositeNode(grammarAccess.getXOseeEnumTypeRule()); 
            pushFollow(FOLLOW_ruleXOseeEnumType_in_entryRuleXOseeEnumType2298);
            iv_ruleXOseeEnumType=ruleXOseeEnumType();

            state._fsp--;

             current =iv_ruleXOseeEnumType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumType2308); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleXOseeEnumType"


    // $ANTLR start "ruleXOseeEnumType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1131:1: ruleXOseeEnumType returns [EObject current=null] : (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' ) ;
    public final EObject ruleXOseeEnumType() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_typeGuid_4_0=null;
        Token otherlv_5=null;
        Token lv_uuid_6_0=null;
        Token otherlv_8=null;
        EObject lv_enumEntries_7_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1134:28: ( (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1135:1: (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1135:1: (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1135:3: otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}'
            {
            otherlv_0=(Token)match(input,51,FOLLOW_51_in_ruleXOseeEnumType2345); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeEnumTypeAccess().getOseeEnumTypeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1139:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1140:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1140:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1141:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumType2362); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXOseeEnumTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleXOseeEnumType2379); 

                	newLeafNode(otherlv_2, grammarAccess.getXOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1161:1: (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==20) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1161:3: otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,20,FOLLOW_20_in_ruleXOseeEnumType2392); 

                        	newLeafNode(otherlv_3, grammarAccess.getXOseeEnumTypeAccess().getGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1165:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1166:1: (lv_typeGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1166:1: (lv_typeGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1167:3: lv_typeGuid_4_0= RULE_STRING
                    {
                    lv_typeGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumType2409); 

                    			newLeafNode(lv_typeGuid_4_0, grammarAccess.getXOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"typeGuid",
                            		lv_typeGuid_4_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_5=(Token)match(input,21,FOLLOW_21_in_ruleXOseeEnumType2428); 

                	newLeafNode(otherlv_5, grammarAccess.getXOseeEnumTypeAccess().getUuidKeyword_4());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1187:1: ( (lv_uuid_6_0= RULE_HEX_INT ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1188:1: (lv_uuid_6_0= RULE_HEX_INT )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1188:1: (lv_uuid_6_0= RULE_HEX_INT )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1189:3: lv_uuid_6_0= RULE_HEX_INT
            {
            lv_uuid_6_0=(Token)match(input,RULE_HEX_INT,FOLLOW_RULE_HEX_INT_in_ruleXOseeEnumType2445); 

            			newLeafNode(lv_uuid_6_0, grammarAccess.getXOseeEnumTypeAccess().getUuidHEX_INTTerminalRuleCall_5_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"uuid",
                    		lv_uuid_6_0, 
                    		"HEX_INT");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1205:2: ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==52) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1206:1: (lv_enumEntries_7_0= ruleXOseeEnumEntry )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1206:1: (lv_enumEntries_7_0= ruleXOseeEnumEntry )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1207:3: lv_enumEntries_7_0= ruleXOseeEnumEntry
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesXOseeEnumEntryParserRuleCall_6_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumEntry_in_ruleXOseeEnumType2471);
            	    lv_enumEntries_7_0=ruleXOseeEnumEntry();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXOseeEnumTypeRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"enumEntries",
            	            		lv_enumEntries_7_0, 
            	            		"XOseeEnumEntry");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);

            otherlv_8=(Token)match(input,22,FOLLOW_22_in_ruleXOseeEnumType2484); 

                	newLeafNode(otherlv_8, grammarAccess.getXOseeEnumTypeAccess().getRightCurlyBracketKeyword_7());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleXOseeEnumType"


    // $ANTLR start "entryRuleXOseeEnumEntry"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1235:1: entryRuleXOseeEnumEntry returns [EObject current=null] : iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF ;
    public final EObject entryRuleXOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumEntry = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1236:2: (iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1237:2: iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF
            {
             newCompositeNode(grammarAccess.getXOseeEnumEntryRule()); 
            pushFollow(FOLLOW_ruleXOseeEnumEntry_in_entryRuleXOseeEnumEntry2520);
            iv_ruleXOseeEnumEntry=ruleXOseeEnumEntry();

            state._fsp--;

             current =iv_ruleXOseeEnumEntry; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumEntry2530); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleXOseeEnumEntry"


    // $ANTLR start "ruleXOseeEnumEntry"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1244:1: ruleXOseeEnumEntry returns [EObject current=null] : (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleXOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token lv_ordinal_2_0=null;
        Token otherlv_3=null;
        Token lv_entryGuid_4_0=null;
        Token otherlv_5=null;
        Token lv_description_6_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1247:28: ( (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1248:1: (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1248:1: (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1248:3: otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            {
            otherlv_0=(Token)match(input,52,FOLLOW_52_in_ruleXOseeEnumEntry2567); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeEnumEntryAccess().getEntryKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1252:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1253:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1253:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1254:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2584); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXOseeEnumEntryAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumEntryRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1270:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==RULE_WHOLE_NUM_STR) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1271:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1271:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1272:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXOseeEnumEntry2606); 

                    			newLeafNode(lv_ordinal_2_0, grammarAccess.getXOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumEntryRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"ordinal",
                            		lv_ordinal_2_0, 
                            		"WHOLE_NUM_STR");
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1288:3: (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==53) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1288:5: otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,53,FOLLOW_53_in_ruleXOseeEnumEntry2625); 

                        	newLeafNode(otherlv_3, grammarAccess.getXOseeEnumEntryAccess().getEntryGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1292:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1293:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1293:1: (lv_entryGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1294:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2642); 

                    			newLeafNode(lv_entryGuid_4_0, grammarAccess.getXOseeEnumEntryAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumEntryRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"entryGuid",
                            		lv_entryGuid_4_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1310:4: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==36) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1310:6: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,36,FOLLOW_36_in_ruleXOseeEnumEntry2662); 

                        	newLeafNode(otherlv_5, grammarAccess.getXOseeEnumEntryAccess().getDescriptionKeyword_4_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1314:1: ( (lv_description_6_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1315:1: (lv_description_6_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1315:1: (lv_description_6_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1316:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2679); 

                    			newLeafNode(lv_description_6_0, grammarAccess.getXOseeEnumEntryAccess().getDescriptionSTRINGTerminalRuleCall_4_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumEntryRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_6_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleXOseeEnumEntry"


    // $ANTLR start "entryRuleXOseeEnumOverride"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1340:1: entryRuleXOseeEnumOverride returns [EObject current=null] : iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF ;
    public final EObject entryRuleXOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumOverride = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1341:2: (iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1342:2: iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF
            {
             newCompositeNode(grammarAccess.getXOseeEnumOverrideRule()); 
            pushFollow(FOLLOW_ruleXOseeEnumOverride_in_entryRuleXOseeEnumOverride2722);
            iv_ruleXOseeEnumOverride=ruleXOseeEnumOverride();

            state._fsp--;

             current =iv_ruleXOseeEnumOverride; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumOverride2732); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleXOseeEnumOverride"


    // $ANTLR start "ruleXOseeEnumOverride"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1349:1: ruleXOseeEnumOverride returns [EObject current=null] : (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' ) ;
    public final EObject ruleXOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_inheritAll_3_0=null;
        Token otherlv_5=null;
        EObject lv_overrideOptions_4_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1352:28: ( (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1353:1: (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1353:1: (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1353:3: otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}'
            {
            otherlv_0=(Token)match(input,54,FOLLOW_54_in_ruleXOseeEnumOverride2769); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeEnumOverrideAccess().getOverridesEnumKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1357:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1358:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1358:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1359:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumOverrideRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumOverride2789); 

            		newLeafNode(otherlv_1, grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeCrossReference_1_0()); 
            	

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleXOseeEnumOverride2801); 

                	newLeafNode(otherlv_2, grammarAccess.getXOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1374:1: ( (lv_inheritAll_3_0= 'inheritAll' ) )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==55) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1375:1: (lv_inheritAll_3_0= 'inheritAll' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1375:1: (lv_inheritAll_3_0= 'inheritAll' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1376:3: lv_inheritAll_3_0= 'inheritAll'
                    {
                    lv_inheritAll_3_0=(Token)match(input,55,FOLLOW_55_in_ruleXOseeEnumOverride2819); 

                            newLeafNode(lv_inheritAll_3_0, grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumOverrideRule());
                    	        }
                           		setWithLastConsumed(current, "inheritAll", true, "inheritAll");
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1389:3: ( (lv_overrideOptions_4_0= ruleOverrideOption ) )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( ((LA29_0>=56 && LA29_0<=57)) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1390:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1390:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1391:3: lv_overrideOptions_4_0= ruleOverrideOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleOverrideOption_in_ruleXOseeEnumOverride2854);
            	    lv_overrideOptions_4_0=ruleOverrideOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXOseeEnumOverrideRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"overrideOptions",
            	            		lv_overrideOptions_4_0, 
            	            		"OverrideOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);

            otherlv_5=(Token)match(input,22,FOLLOW_22_in_ruleXOseeEnumOverride2867); 

                	newLeafNode(otherlv_5, grammarAccess.getXOseeEnumOverrideAccess().getRightCurlyBracketKeyword_5());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleXOseeEnumOverride"


    // $ANTLR start "entryRuleOverrideOption"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1419:1: entryRuleOverrideOption returns [EObject current=null] : iv_ruleOverrideOption= ruleOverrideOption EOF ;
    public final EObject entryRuleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOverrideOption = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1420:2: (iv_ruleOverrideOption= ruleOverrideOption EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1421:2: iv_ruleOverrideOption= ruleOverrideOption EOF
            {
             newCompositeNode(grammarAccess.getOverrideOptionRule()); 
            pushFollow(FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption2903);
            iv_ruleOverrideOption=ruleOverrideOption();

            state._fsp--;

             current =iv_ruleOverrideOption; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOverrideOption2913); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleOverrideOption"


    // $ANTLR start "ruleOverrideOption"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1428:1: ruleOverrideOption returns [EObject current=null] : (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) ;
    public final EObject ruleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject this_AddEnum_0 = null;

        EObject this_RemoveEnum_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1431:28: ( (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1432:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1432:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==56) ) {
                alt30=1;
            }
            else if ( (LA30_0==57) ) {
                alt30=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1433:5: this_AddEnum_0= ruleAddEnum
                    {
                     
                            newCompositeNode(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleAddEnum_in_ruleOverrideOption2960);
                    this_AddEnum_0=ruleAddEnum();

                    state._fsp--;

                     
                            current = this_AddEnum_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1443:5: this_RemoveEnum_1= ruleRemoveEnum
                    {
                     
                            newCompositeNode(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleRemoveEnum_in_ruleOverrideOption2987);
                    this_RemoveEnum_1=ruleRemoveEnum();

                    state._fsp--;

                     
                            current = this_RemoveEnum_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleOverrideOption"


    // $ANTLR start "entryRuleAddEnum"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1459:1: entryRuleAddEnum returns [EObject current=null] : iv_ruleAddEnum= ruleAddEnum EOF ;
    public final EObject entryRuleAddEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddEnum = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1460:2: (iv_ruleAddEnum= ruleAddEnum EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1461:2: iv_ruleAddEnum= ruleAddEnum EOF
            {
             newCompositeNode(grammarAccess.getAddEnumRule()); 
            pushFollow(FOLLOW_ruleAddEnum_in_entryRuleAddEnum3022);
            iv_ruleAddEnum=ruleAddEnum();

            state._fsp--;

             current =iv_ruleAddEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAddEnum3032); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAddEnum"


    // $ANTLR start "ruleAddEnum"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1468:1: ruleAddEnum returns [EObject current=null] : (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleAddEnum() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_enumEntry_1_0=null;
        Token lv_ordinal_2_0=null;
        Token otherlv_3=null;
        Token lv_entryGuid_4_0=null;
        Token otherlv_5=null;
        Token lv_description_6_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1471:28: ( (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1472:1: (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1472:1: (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1472:3: otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            {
            otherlv_0=(Token)match(input,56,FOLLOW_56_in_ruleAddEnum3069); 

                	newLeafNode(otherlv_0, grammarAccess.getAddEnumAccess().getAddKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1476:1: ( (lv_enumEntry_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1477:1: (lv_enumEntry_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1477:1: (lv_enumEntry_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1478:3: lv_enumEntry_1_0= RULE_STRING
            {
            lv_enumEntry_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAddEnum3086); 

            			newLeafNode(lv_enumEntry_1_0, grammarAccess.getAddEnumAccess().getEnumEntrySTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAddEnumRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"enumEntry",
                    		lv_enumEntry_1_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1494:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==RULE_WHOLE_NUM_STR) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1495:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1495:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1496:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum3108); 

                    			newLeafNode(lv_ordinal_2_0, grammarAccess.getAddEnumAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getAddEnumRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"ordinal",
                            		lv_ordinal_2_0, 
                            		"WHOLE_NUM_STR");
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1512:3: (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==53) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1512:5: otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,53,FOLLOW_53_in_ruleAddEnum3127); 

                        	newLeafNode(otherlv_3, grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1516:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1517:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1517:1: (lv_entryGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1518:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAddEnum3144); 

                    			newLeafNode(lv_entryGuid_4_0, grammarAccess.getAddEnumAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getAddEnumRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"entryGuid",
                            		lv_entryGuid_4_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1534:4: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==36) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1534:6: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,36,FOLLOW_36_in_ruleAddEnum3164); 

                        	newLeafNode(otherlv_5, grammarAccess.getAddEnumAccess().getDescriptionKeyword_4_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1538:1: ( (lv_description_6_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1539:1: (lv_description_6_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1539:1: (lv_description_6_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1540:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAddEnum3181); 

                    			newLeafNode(lv_description_6_0, grammarAccess.getAddEnumAccess().getDescriptionSTRINGTerminalRuleCall_4_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getAddEnumRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_6_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAddEnum"


    // $ANTLR start "entryRuleRemoveEnum"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1564:1: entryRuleRemoveEnum returns [EObject current=null] : iv_ruleRemoveEnum= ruleRemoveEnum EOF ;
    public final EObject entryRuleRemoveEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRemoveEnum = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1565:2: (iv_ruleRemoveEnum= ruleRemoveEnum EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1566:2: iv_ruleRemoveEnum= ruleRemoveEnum EOF
            {
             newCompositeNode(grammarAccess.getRemoveEnumRule()); 
            pushFollow(FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum3224);
            iv_ruleRemoveEnum=ruleRemoveEnum();

            state._fsp--;

             current =iv_ruleRemoveEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRemoveEnum3234); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRemoveEnum"


    // $ANTLR start "ruleRemoveEnum"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1573:1: ruleRemoveEnum returns [EObject current=null] : (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) ) ;
    public final EObject ruleRemoveEnum() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1576:28: ( (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1577:1: (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1577:1: (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1577:3: otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,57,FOLLOW_57_in_ruleRemoveEnum3271); 

                	newLeafNode(otherlv_0, grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1581:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1582:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1582:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1583:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRemoveEnumRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRemoveEnum3291); 

            		newLeafNode(otherlv_1, grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntryCrossReference_1_0()); 
            	

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRemoveEnum"


    // $ANTLR start "entryRuleXOseeArtifactTypeOverride"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1602:1: entryRuleXOseeArtifactTypeOverride returns [EObject current=null] : iv_ruleXOseeArtifactTypeOverride= ruleXOseeArtifactTypeOverride EOF ;
    public final EObject entryRuleXOseeArtifactTypeOverride() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeArtifactTypeOverride = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1603:2: (iv_ruleXOseeArtifactTypeOverride= ruleXOseeArtifactTypeOverride EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1604:2: iv_ruleXOseeArtifactTypeOverride= ruleXOseeArtifactTypeOverride EOF
            {
             newCompositeNode(grammarAccess.getXOseeArtifactTypeOverrideRule()); 
            pushFollow(FOLLOW_ruleXOseeArtifactTypeOverride_in_entryRuleXOseeArtifactTypeOverride3327);
            iv_ruleXOseeArtifactTypeOverride=ruleXOseeArtifactTypeOverride();

            state._fsp--;

             current =iv_ruleXOseeArtifactTypeOverride; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeArtifactTypeOverride3337); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleXOseeArtifactTypeOverride"


    // $ANTLR start "ruleXOseeArtifactTypeOverride"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1611:1: ruleXOseeArtifactTypeOverride returns [EObject current=null] : (otherlv_0= 'overrides artifactType' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleAttributeOverrideOption ) )+ otherlv_5= '}' ) ;
    public final EObject ruleXOseeArtifactTypeOverride() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_inheritAll_3_0=null;
        Token otherlv_5=null;
        EObject lv_overrideOptions_4_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1614:28: ( (otherlv_0= 'overrides artifactType' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleAttributeOverrideOption ) )+ otherlv_5= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1615:1: (otherlv_0= 'overrides artifactType' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleAttributeOverrideOption ) )+ otherlv_5= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1615:1: (otherlv_0= 'overrides artifactType' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleAttributeOverrideOption ) )+ otherlv_5= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1615:3: otherlv_0= 'overrides artifactType' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleAttributeOverrideOption ) )+ otherlv_5= '}'
            {
            otherlv_0=(Token)match(input,58,FOLLOW_58_in_ruleXOseeArtifactTypeOverride3374); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridesArtifactTypeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1619:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1620:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1620:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1621:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeArtifactTypeOverrideRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeArtifactTypeOverride3394); 

            		newLeafNode(otherlv_1, grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridenArtifactTypeXArtifactTypeCrossReference_1_0()); 
            	

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleXOseeArtifactTypeOverride3406); 

                	newLeafNode(otherlv_2, grammarAccess.getXOseeArtifactTypeOverrideAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1636:1: ( (lv_inheritAll_3_0= 'inheritAll' ) )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==55) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1637:1: (lv_inheritAll_3_0= 'inheritAll' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1637:1: (lv_inheritAll_3_0= 'inheritAll' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1638:3: lv_inheritAll_3_0= 'inheritAll'
                    {
                    lv_inheritAll_3_0=(Token)match(input,55,FOLLOW_55_in_ruleXOseeArtifactTypeOverride3424); 

                            newLeafNode(lv_inheritAll_3_0, grammarAccess.getXOseeArtifactTypeOverrideAccess().getInheritAllInheritAllKeyword_3_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeArtifactTypeOverrideRule());
                    	        }
                           		setWithLastConsumed(current, "inheritAll", true, "inheritAll");
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1651:3: ( (lv_overrideOptions_4_0= ruleAttributeOverrideOption ) )+
            int cnt35=0;
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( ((LA35_0>=56 && LA35_0<=57)||LA35_0==59) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1652:1: (lv_overrideOptions_4_0= ruleAttributeOverrideOption )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1652:1: (lv_overrideOptions_4_0= ruleAttributeOverrideOption )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1653:3: lv_overrideOptions_4_0= ruleAttributeOverrideOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverrideOptionsAttributeOverrideOptionParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleAttributeOverrideOption_in_ruleXOseeArtifactTypeOverride3459);
            	    lv_overrideOptions_4_0=ruleAttributeOverrideOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXOseeArtifactTypeOverrideRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"overrideOptions",
            	            		lv_overrideOptions_4_0, 
            	            		"AttributeOverrideOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt35 >= 1 ) break loop35;
                        EarlyExitException eee =
                            new EarlyExitException(35, input);
                        throw eee;
                }
                cnt35++;
            } while (true);

            otherlv_5=(Token)match(input,22,FOLLOW_22_in_ruleXOseeArtifactTypeOverride3472); 

                	newLeafNode(otherlv_5, grammarAccess.getXOseeArtifactTypeOverrideAccess().getRightCurlyBracketKeyword_5());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleXOseeArtifactTypeOverride"


    // $ANTLR start "entryRuleAttributeOverrideOption"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1681:1: entryRuleAttributeOverrideOption returns [EObject current=null] : iv_ruleAttributeOverrideOption= ruleAttributeOverrideOption EOF ;
    public final EObject entryRuleAttributeOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeOverrideOption = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1682:2: (iv_ruleAttributeOverrideOption= ruleAttributeOverrideOption EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1683:2: iv_ruleAttributeOverrideOption= ruleAttributeOverrideOption EOF
            {
             newCompositeNode(grammarAccess.getAttributeOverrideOptionRule()); 
            pushFollow(FOLLOW_ruleAttributeOverrideOption_in_entryRuleAttributeOverrideOption3508);
            iv_ruleAttributeOverrideOption=ruleAttributeOverrideOption();

            state._fsp--;

             current =iv_ruleAttributeOverrideOption; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeOverrideOption3518); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttributeOverrideOption"


    // $ANTLR start "ruleAttributeOverrideOption"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1690:1: ruleAttributeOverrideOption returns [EObject current=null] : (this_AddAttribute_0= ruleAddAttribute | this_RemoveAttribute_1= ruleRemoveAttribute | this_UpdateAttribute_2= ruleUpdateAttribute ) ;
    public final EObject ruleAttributeOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject this_AddAttribute_0 = null;

        EObject this_RemoveAttribute_1 = null;

        EObject this_UpdateAttribute_2 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1693:28: ( (this_AddAttribute_0= ruleAddAttribute | this_RemoveAttribute_1= ruleRemoveAttribute | this_UpdateAttribute_2= ruleUpdateAttribute ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1694:1: (this_AddAttribute_0= ruleAddAttribute | this_RemoveAttribute_1= ruleRemoveAttribute | this_UpdateAttribute_2= ruleUpdateAttribute )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1694:1: (this_AddAttribute_0= ruleAddAttribute | this_RemoveAttribute_1= ruleRemoveAttribute | this_UpdateAttribute_2= ruleUpdateAttribute )
            int alt36=3;
            switch ( input.LA(1) ) {
            case 56:
                {
                alt36=1;
                }
                break;
            case 57:
                {
                alt36=2;
                }
                break;
            case 59:
                {
                alt36=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }

            switch (alt36) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1695:5: this_AddAttribute_0= ruleAddAttribute
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeOverrideOptionAccess().getAddAttributeParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleAddAttribute_in_ruleAttributeOverrideOption3565);
                    this_AddAttribute_0=ruleAddAttribute();

                    state._fsp--;

                     
                            current = this_AddAttribute_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1705:5: this_RemoveAttribute_1= ruleRemoveAttribute
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeOverrideOptionAccess().getRemoveAttributeParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleRemoveAttribute_in_ruleAttributeOverrideOption3592);
                    this_RemoveAttribute_1=ruleRemoveAttribute();

                    state._fsp--;

                     
                            current = this_RemoveAttribute_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1715:5: this_UpdateAttribute_2= ruleUpdateAttribute
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeOverrideOptionAccess().getUpdateAttributeParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleUpdateAttribute_in_ruleAttributeOverrideOption3619);
                    this_UpdateAttribute_2=ruleUpdateAttribute();

                    state._fsp--;

                     
                            current = this_UpdateAttribute_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttributeOverrideOption"


    // $ANTLR start "entryRuleAddAttribute"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1731:1: entryRuleAddAttribute returns [EObject current=null] : iv_ruleAddAttribute= ruleAddAttribute EOF ;
    public final EObject entryRuleAddAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddAttribute = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1732:2: (iv_ruleAddAttribute= ruleAddAttribute EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1733:2: iv_ruleAddAttribute= ruleAddAttribute EOF
            {
             newCompositeNode(grammarAccess.getAddAttributeRule()); 
            pushFollow(FOLLOW_ruleAddAttribute_in_entryRuleAddAttribute3654);
            iv_ruleAddAttribute=ruleAddAttribute();

            state._fsp--;

             current =iv_ruleAddAttribute; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAddAttribute3664); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAddAttribute"


    // $ANTLR start "ruleAddAttribute"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1740:1: ruleAddAttribute returns [EObject current=null] : (otherlv_0= 'add' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) ) ;
    public final EObject ruleAddAttribute() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        EObject lv_attribute_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1743:28: ( (otherlv_0= 'add' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1744:1: (otherlv_0= 'add' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1744:1: (otherlv_0= 'add' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1744:3: otherlv_0= 'add' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) )
            {
            otherlv_0=(Token)match(input,56,FOLLOW_56_in_ruleAddAttribute3701); 

                	newLeafNode(otherlv_0, grammarAccess.getAddAttributeAccess().getAddKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1748:1: ( (lv_attribute_1_0= ruleXAttributeTypeRef ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1749:1: (lv_attribute_1_0= ruleXAttributeTypeRef )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1749:1: (lv_attribute_1_0= ruleXAttributeTypeRef )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1750:3: lv_attribute_1_0= ruleXAttributeTypeRef
            {
             
            	        newCompositeNode(grammarAccess.getAddAttributeAccess().getAttributeXAttributeTypeRefParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleXAttributeTypeRef_in_ruleAddAttribute3722);
            lv_attribute_1_0=ruleXAttributeTypeRef();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAddAttributeRule());
            	        }
                   		set(
                   			current, 
                   			"attribute",
                    		lv_attribute_1_0, 
                    		"XAttributeTypeRef");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAddAttribute"


    // $ANTLR start "entryRuleRemoveAttribute"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1774:1: entryRuleRemoveAttribute returns [EObject current=null] : iv_ruleRemoveAttribute= ruleRemoveAttribute EOF ;
    public final EObject entryRuleRemoveAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRemoveAttribute = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1775:2: (iv_ruleRemoveAttribute= ruleRemoveAttribute EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1776:2: iv_ruleRemoveAttribute= ruleRemoveAttribute EOF
            {
             newCompositeNode(grammarAccess.getRemoveAttributeRule()); 
            pushFollow(FOLLOW_ruleRemoveAttribute_in_entryRuleRemoveAttribute3758);
            iv_ruleRemoveAttribute=ruleRemoveAttribute();

            state._fsp--;

             current =iv_ruleRemoveAttribute; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRemoveAttribute3768); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRemoveAttribute"


    // $ANTLR start "ruleRemoveAttribute"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1783:1: ruleRemoveAttribute returns [EObject current=null] : (otherlv_0= 'remove' otherlv_1= 'attribute' ( (otherlv_2= RULE_STRING ) ) ) ;
    public final EObject ruleRemoveAttribute() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1786:28: ( (otherlv_0= 'remove' otherlv_1= 'attribute' ( (otherlv_2= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1787:1: (otherlv_0= 'remove' otherlv_1= 'attribute' ( (otherlv_2= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1787:1: (otherlv_0= 'remove' otherlv_1= 'attribute' ( (otherlv_2= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1787:3: otherlv_0= 'remove' otherlv_1= 'attribute' ( (otherlv_2= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,57,FOLLOW_57_in_ruleRemoveAttribute3805); 

                	newLeafNode(otherlv_0, grammarAccess.getRemoveAttributeAccess().getRemoveKeyword_0());
                
            otherlv_1=(Token)match(input,23,FOLLOW_23_in_ruleRemoveAttribute3817); 

                	newLeafNode(otherlv_1, grammarAccess.getRemoveAttributeAccess().getAttributeKeyword_1());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1795:1: ( (otherlv_2= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1796:1: (otherlv_2= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1796:1: (otherlv_2= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1797:3: otherlv_2= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRemoveAttributeRule());
            	        }
                    
            otherlv_2=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRemoveAttribute3837); 

            		newLeafNode(otherlv_2, grammarAccess.getRemoveAttributeAccess().getAttributeXAttributeTypeCrossReference_2_0()); 
            	

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRemoveAttribute"


    // $ANTLR start "entryRuleUpdateAttribute"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1816:1: entryRuleUpdateAttribute returns [EObject current=null] : iv_ruleUpdateAttribute= ruleUpdateAttribute EOF ;
    public final EObject entryRuleUpdateAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUpdateAttribute = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1817:2: (iv_ruleUpdateAttribute= ruleUpdateAttribute EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1818:2: iv_ruleUpdateAttribute= ruleUpdateAttribute EOF
            {
             newCompositeNode(grammarAccess.getUpdateAttributeRule()); 
            pushFollow(FOLLOW_ruleUpdateAttribute_in_entryRuleUpdateAttribute3873);
            iv_ruleUpdateAttribute=ruleUpdateAttribute();

            state._fsp--;

             current =iv_ruleUpdateAttribute; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUpdateAttribute3883); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUpdateAttribute"


    // $ANTLR start "ruleUpdateAttribute"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1825:1: ruleUpdateAttribute returns [EObject current=null] : (otherlv_0= 'update' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) ) ;
    public final EObject ruleUpdateAttribute() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        EObject lv_attribute_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1828:28: ( (otherlv_0= 'update' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1829:1: (otherlv_0= 'update' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1829:1: (otherlv_0= 'update' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1829:3: otherlv_0= 'update' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) )
            {
            otherlv_0=(Token)match(input,59,FOLLOW_59_in_ruleUpdateAttribute3920); 

                	newLeafNode(otherlv_0, grammarAccess.getUpdateAttributeAccess().getUpdateKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1833:1: ( (lv_attribute_1_0= ruleXAttributeTypeRef ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1834:1: (lv_attribute_1_0= ruleXAttributeTypeRef )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1834:1: (lv_attribute_1_0= ruleXAttributeTypeRef )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1835:3: lv_attribute_1_0= ruleXAttributeTypeRef
            {
             
            	        newCompositeNode(grammarAccess.getUpdateAttributeAccess().getAttributeXAttributeTypeRefParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleXAttributeTypeRef_in_ruleUpdateAttribute3941);
            lv_attribute_1_0=ruleXAttributeTypeRef();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getUpdateAttributeRule());
            	        }
                   		set(
                   			current, 
                   			"attribute",
                    		lv_attribute_1_0, 
                    		"XAttributeTypeRef");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUpdateAttribute"


    // $ANTLR start "entryRuleXRelationType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1859:1: entryRuleXRelationType returns [EObject current=null] : iv_ruleXRelationType= ruleXRelationType EOF ;
    public final EObject entryRuleXRelationType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXRelationType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1860:2: (iv_ruleXRelationType= ruleXRelationType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1861:2: iv_ruleXRelationType= ruleXRelationType EOF
            {
             newCompositeNode(grammarAccess.getXRelationTypeRule()); 
            pushFollow(FOLLOW_ruleXRelationType_in_entryRuleXRelationType3977);
            iv_ruleXRelationType=ruleXRelationType();

            state._fsp--;

             current =iv_ruleXRelationType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXRelationType3987); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleXRelationType"


    // $ANTLR start "ruleXRelationType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1868:1: ruleXRelationType returns [EObject current=null] : (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' ) ;
    public final EObject ruleXRelationType() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_typeGuid_4_0=null;
        Token otherlv_5=null;
        Token lv_uuid_6_0=null;
        Token otherlv_7=null;
        Token lv_sideAName_8_0=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token lv_sideBName_12_0=null;
        Token otherlv_13=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        Token otherlv_19=null;
        AntlrDatatypeRuleToken lv_defaultOrderType_16_0 = null;

        Enumerator lv_multiplicity_18_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1871:28: ( (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1872:1: (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1872:1: (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1872:3: otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}'
            {
            otherlv_0=(Token)match(input,60,FOLLOW_60_in_ruleXRelationType4024); 

                	newLeafNode(otherlv_0, grammarAccess.getXRelationTypeAccess().getRelationTypeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1876:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1877:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1877:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1878:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType4041); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXRelationTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleXRelationType4058); 

                	newLeafNode(otherlv_2, grammarAccess.getXRelationTypeAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1898:1: (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==20) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1898:3: otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,20,FOLLOW_20_in_ruleXRelationType4071); 

                        	newLeafNode(otherlv_3, grammarAccess.getXRelationTypeAccess().getGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1902:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1903:1: (lv_typeGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1903:1: (lv_typeGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1904:3: lv_typeGuid_4_0= RULE_STRING
                    {
                    lv_typeGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType4088); 

                    			newLeafNode(lv_typeGuid_4_0, grammarAccess.getXRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXRelationTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"typeGuid",
                            		lv_typeGuid_4_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_5=(Token)match(input,21,FOLLOW_21_in_ruleXRelationType4107); 

                	newLeafNode(otherlv_5, grammarAccess.getXRelationTypeAccess().getUuidKeyword_4());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1924:1: ( (lv_uuid_6_0= RULE_HEX_INT ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1925:1: (lv_uuid_6_0= RULE_HEX_INT )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1925:1: (lv_uuid_6_0= RULE_HEX_INT )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1926:3: lv_uuid_6_0= RULE_HEX_INT
            {
            lv_uuid_6_0=(Token)match(input,RULE_HEX_INT,FOLLOW_RULE_HEX_INT_in_ruleXRelationType4124); 

            			newLeafNode(lv_uuid_6_0, grammarAccess.getXRelationTypeAccess().getUuidHEX_INTTerminalRuleCall_5_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"uuid",
                    		lv_uuid_6_0, 
                    		"HEX_INT");
            	    

            }


            }

            otherlv_7=(Token)match(input,61,FOLLOW_61_in_ruleXRelationType4141); 

                	newLeafNode(otherlv_7, grammarAccess.getXRelationTypeAccess().getSideANameKeyword_6());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1946:1: ( (lv_sideAName_8_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1947:1: (lv_sideAName_8_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1947:1: (lv_sideAName_8_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1948:3: lv_sideAName_8_0= RULE_STRING
            {
            lv_sideAName_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType4158); 

            			newLeafNode(lv_sideAName_8_0, grammarAccess.getXRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_7_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"sideAName",
                    		lv_sideAName_8_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_9=(Token)match(input,62,FOLLOW_62_in_ruleXRelationType4175); 

                	newLeafNode(otherlv_9, grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeKeyword_8());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1968:1: ( (otherlv_10= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1969:1: (otherlv_10= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1969:1: (otherlv_10= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1970:3: otherlv_10= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                    
            otherlv_10=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType4195); 

            		newLeafNode(otherlv_10, grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeCrossReference_9_0()); 
            	

            }


            }

            otherlv_11=(Token)match(input,63,FOLLOW_63_in_ruleXRelationType4207); 

                	newLeafNode(otherlv_11, grammarAccess.getXRelationTypeAccess().getSideBNameKeyword_10());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1985:1: ( (lv_sideBName_12_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1986:1: (lv_sideBName_12_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1986:1: (lv_sideBName_12_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1987:3: lv_sideBName_12_0= RULE_STRING
            {
            lv_sideBName_12_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType4224); 

            			newLeafNode(lv_sideBName_12_0, grammarAccess.getXRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_11_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"sideBName",
                    		lv_sideBName_12_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_13=(Token)match(input,64,FOLLOW_64_in_ruleXRelationType4241); 

                	newLeafNode(otherlv_13, grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeKeyword_12());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2007:1: ( (otherlv_14= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2008:1: (otherlv_14= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2008:1: (otherlv_14= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2009:3: otherlv_14= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                    
            otherlv_14=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType4261); 

            		newLeafNode(otherlv_14, grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeCrossReference_13_0()); 
            	

            }


            }

            otherlv_15=(Token)match(input,65,FOLLOW_65_in_ruleXRelationType4273); 

                	newLeafNode(otherlv_15, grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeKeyword_14());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2024:1: ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2025:1: (lv_defaultOrderType_16_0= ruleRelationOrderType )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2025:1: (lv_defaultOrderType_16_0= ruleRelationOrderType )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2026:3: lv_defaultOrderType_16_0= ruleRelationOrderType
            {
             
            	        newCompositeNode(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_15_0()); 
            	    
            pushFollow(FOLLOW_ruleRelationOrderType_in_ruleXRelationType4294);
            lv_defaultOrderType_16_0=ruleRelationOrderType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getXRelationTypeRule());
            	        }
                   		set(
                   			current, 
                   			"defaultOrderType",
                    		lv_defaultOrderType_16_0, 
                    		"RelationOrderType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_17=(Token)match(input,66,FOLLOW_66_in_ruleXRelationType4306); 

                	newLeafNode(otherlv_17, grammarAccess.getXRelationTypeAccess().getMultiplicityKeyword_16());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2046:1: ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2047:1: (lv_multiplicity_18_0= ruleRelationMultiplicityEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2047:1: (lv_multiplicity_18_0= ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2048:3: lv_multiplicity_18_0= ruleRelationMultiplicityEnum
            {
             
            	        newCompositeNode(grammarAccess.getXRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_17_0()); 
            	    
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_ruleXRelationType4327);
            lv_multiplicity_18_0=ruleRelationMultiplicityEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getXRelationTypeRule());
            	        }
                   		set(
                   			current, 
                   			"multiplicity",
                    		lv_multiplicity_18_0, 
                    		"RelationMultiplicityEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_19=(Token)match(input,22,FOLLOW_22_in_ruleXRelationType4339); 

                	newLeafNode(otherlv_19, grammarAccess.getXRelationTypeAccess().getRightCurlyBracketKeyword_18());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleXRelationType"


    // $ANTLR start "entryRuleRelationOrderType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2076:1: entryRuleRelationOrderType returns [String current=null] : iv_ruleRelationOrderType= ruleRelationOrderType EOF ;
    public final String entryRuleRelationOrderType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRelationOrderType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2077:2: (iv_ruleRelationOrderType= ruleRelationOrderType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2078:2: iv_ruleRelationOrderType= ruleRelationOrderType EOF
            {
             newCompositeNode(grammarAccess.getRelationOrderTypeRule()); 
            pushFollow(FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType4376);
            iv_ruleRelationOrderType=ruleRelationOrderType();

            state._fsp--;

             current =iv_ruleRelationOrderType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationOrderType4387); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRelationOrderType"


    // $ANTLR start "ruleRelationOrderType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2085:1: ruleRelationOrderType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) ;
    public final AntlrDatatypeRuleToken ruleRelationOrderType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_ID_3=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2088:28: ( (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2089:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2089:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            int alt38=4;
            switch ( input.LA(1) ) {
            case 67:
                {
                alt38=1;
                }
                break;
            case 68:
                {
                alt38=2;
                }
                break;
            case 69:
                {
                alt38=3;
                }
                break;
            case RULE_ID:
                {
                alt38=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2090:2: kw= 'Lexicographical_Ascending'
                    {
                    kw=(Token)match(input,67,FOLLOW_67_in_ruleRelationOrderType4425); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2097:2: kw= 'Lexicographical_Descending'
                    {
                    kw=(Token)match(input,68,FOLLOW_68_in_ruleRelationOrderType4444); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2104:2: kw= 'Unordered'
                    {
                    kw=(Token)match(input,69,FOLLOW_69_in_ruleRelationOrderType4463); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2110:10: this_ID_3= RULE_ID
                    {
                    this_ID_3=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleRelationOrderType4484); 

                    		current.merge(this_ID_3);
                        
                     
                        newLeafNode(this_ID_3, grammarAccess.getRelationOrderTypeAccess().getIDTerminalRuleCall_3()); 
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRelationOrderType"


    // $ANTLR start "entryRuleCondition"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2127:1: entryRuleCondition returns [EObject current=null] : iv_ruleCondition= ruleCondition EOF ;
    public final EObject entryRuleCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCondition = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2128:2: (iv_ruleCondition= ruleCondition EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2129:2: iv_ruleCondition= ruleCondition EOF
            {
             newCompositeNode(grammarAccess.getConditionRule()); 
            pushFollow(FOLLOW_ruleCondition_in_entryRuleCondition4531);
            iv_ruleCondition=ruleCondition();

            state._fsp--;

             current =iv_ruleCondition; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCondition4541); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleCondition"


    // $ANTLR start "ruleCondition"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2136:1: ruleCondition returns [EObject current=null] : (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition ) ;
    public final EObject ruleCondition() throws RecognitionException {
        EObject current = null;

        EObject this_SimpleCondition_0 = null;

        EObject this_CompoundCondition_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2139:28: ( (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2140:1: (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2140:1: (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==24||(LA39_0>=89 && LA39_0<=91)) ) {
                alt39=1;
            }
            else if ( (LA39_0==70) ) {
                alt39=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2141:5: this_SimpleCondition_0= ruleSimpleCondition
                    {
                     
                            newCompositeNode(grammarAccess.getConditionAccess().getSimpleConditionParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleSimpleCondition_in_ruleCondition4588);
                    this_SimpleCondition_0=ruleSimpleCondition();

                    state._fsp--;

                     
                            current = this_SimpleCondition_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2151:5: this_CompoundCondition_1= ruleCompoundCondition
                    {
                     
                            newCompositeNode(grammarAccess.getConditionAccess().getCompoundConditionParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleCompoundCondition_in_ruleCondition4615);
                    this_CompoundCondition_1=ruleCompoundCondition();

                    state._fsp--;

                     
                            current = this_CompoundCondition_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleCondition"


    // $ANTLR start "entryRuleSimpleCondition"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2167:1: entryRuleSimpleCondition returns [EObject current=null] : iv_ruleSimpleCondition= ruleSimpleCondition EOF ;
    public final EObject entryRuleSimpleCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSimpleCondition = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2168:2: (iv_ruleSimpleCondition= ruleSimpleCondition EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2169:2: iv_ruleSimpleCondition= ruleSimpleCondition EOF
            {
             newCompositeNode(grammarAccess.getSimpleConditionRule()); 
            pushFollow(FOLLOW_ruleSimpleCondition_in_entryRuleSimpleCondition4650);
            iv_ruleSimpleCondition=ruleSimpleCondition();

            state._fsp--;

             current =iv_ruleSimpleCondition; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSimpleCondition4660); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSimpleCondition"


    // $ANTLR start "ruleSimpleCondition"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2176:1: ruleSimpleCondition returns [EObject current=null] : ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) ) ;
    public final EObject ruleSimpleCondition() throws RecognitionException {
        EObject current = null;

        Token lv_expression_2_0=null;
        Enumerator lv_field_0_0 = null;

        Enumerator lv_op_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2179:28: ( ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2180:1: ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2180:1: ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2180:2: ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2180:2: ( (lv_field_0_0= ruleMatchField ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2181:1: (lv_field_0_0= ruleMatchField )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2181:1: (lv_field_0_0= ruleMatchField )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2182:3: lv_field_0_0= ruleMatchField
            {
             
            	        newCompositeNode(grammarAccess.getSimpleConditionAccess().getFieldMatchFieldEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleMatchField_in_ruleSimpleCondition4706);
            lv_field_0_0=ruleMatchField();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getSimpleConditionRule());
            	        }
                   		set(
                   			current, 
                   			"field",
                    		lv_field_0_0, 
                    		"MatchField");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2198:2: ( (lv_op_1_0= ruleCompareOp ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2199:1: (lv_op_1_0= ruleCompareOp )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2199:1: (lv_op_1_0= ruleCompareOp )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2200:3: lv_op_1_0= ruleCompareOp
            {
             
            	        newCompositeNode(grammarAccess.getSimpleConditionAccess().getOpCompareOpEnumRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleCompareOp_in_ruleSimpleCondition4727);
            lv_op_1_0=ruleCompareOp();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getSimpleConditionRule());
            	        }
                   		set(
                   			current, 
                   			"op",
                    		lv_op_1_0, 
                    		"CompareOp");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2216:2: ( (lv_expression_2_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2217:1: (lv_expression_2_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2217:1: (lv_expression_2_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2218:3: lv_expression_2_0= RULE_STRING
            {
            lv_expression_2_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleSimpleCondition4744); 

            			newLeafNode(lv_expression_2_0, grammarAccess.getSimpleConditionAccess().getExpressionSTRINGTerminalRuleCall_2_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getSimpleConditionRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"expression",
                    		lv_expression_2_0, 
                    		"STRING");
            	    

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSimpleCondition"


    // $ANTLR start "entryRuleCompoundCondition"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2242:1: entryRuleCompoundCondition returns [EObject current=null] : iv_ruleCompoundCondition= ruleCompoundCondition EOF ;
    public final EObject entryRuleCompoundCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCompoundCondition = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2243:2: (iv_ruleCompoundCondition= ruleCompoundCondition EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2244:2: iv_ruleCompoundCondition= ruleCompoundCondition EOF
            {
             newCompositeNode(grammarAccess.getCompoundConditionRule()); 
            pushFollow(FOLLOW_ruleCompoundCondition_in_entryRuleCompoundCondition4785);
            iv_ruleCompoundCondition=ruleCompoundCondition();

            state._fsp--;

             current =iv_ruleCompoundCondition; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCompoundCondition4795); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleCompoundCondition"


    // $ANTLR start "ruleCompoundCondition"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2251:1: ruleCompoundCondition returns [EObject current=null] : (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' ) ;
    public final EObject ruleCompoundCondition() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_4=null;
        EObject lv_conditions_1_0 = null;

        Enumerator lv_operators_2_0 = null;

        EObject lv_conditions_3_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2254:28: ( (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2255:1: (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2255:1: (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2255:3: otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')'
            {
            otherlv_0=(Token)match(input,70,FOLLOW_70_in_ruleCompoundCondition4832); 

                	newLeafNode(otherlv_0, grammarAccess.getCompoundConditionAccess().getLeftParenthesisKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2259:1: ( (lv_conditions_1_0= ruleSimpleCondition ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2260:1: (lv_conditions_1_0= ruleSimpleCondition )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2260:1: (lv_conditions_1_0= ruleSimpleCondition )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2261:3: lv_conditions_1_0= ruleSimpleCondition
            {
             
            	        newCompositeNode(grammarAccess.getCompoundConditionAccess().getConditionsSimpleConditionParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSimpleCondition_in_ruleCompoundCondition4853);
            lv_conditions_1_0=ruleSimpleCondition();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCompoundConditionRule());
            	        }
                   		add(
                   			current, 
                   			"conditions",
                    		lv_conditions_1_0, 
                    		"SimpleCondition");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2277:2: ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+
            int cnt40=0;
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( ((LA40_0>=87 && LA40_0<=88)) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2277:3: ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2277:3: ( (lv_operators_2_0= ruleXLogicOperator ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2278:1: (lv_operators_2_0= ruleXLogicOperator )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2278:1: (lv_operators_2_0= ruleXLogicOperator )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2279:3: lv_operators_2_0= ruleXLogicOperator
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompoundConditionAccess().getOperatorsXLogicOperatorEnumRuleCall_2_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXLogicOperator_in_ruleCompoundCondition4875);
            	    lv_operators_2_0=ruleXLogicOperator();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCompoundConditionRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"operators",
            	            		lv_operators_2_0, 
            	            		"XLogicOperator");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }

            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2295:2: ( (lv_conditions_3_0= ruleSimpleCondition ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2296:1: (lv_conditions_3_0= ruleSimpleCondition )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2296:1: (lv_conditions_3_0= ruleSimpleCondition )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2297:3: lv_conditions_3_0= ruleSimpleCondition
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompoundConditionAccess().getConditionsSimpleConditionParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleSimpleCondition_in_ruleCompoundCondition4896);
            	    lv_conditions_3_0=ruleSimpleCondition();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCompoundConditionRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"conditions",
            	            		lv_conditions_3_0, 
            	            		"SimpleCondition");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt40 >= 1 ) break loop40;
                        EarlyExitException eee =
                            new EarlyExitException(40, input);
                        throw eee;
                }
                cnt40++;
            } while (true);

            otherlv_4=(Token)match(input,71,FOLLOW_71_in_ruleCompoundCondition4910); 

                	newLeafNode(otherlv_4, grammarAccess.getCompoundConditionAccess().getRightParenthesisKeyword_3());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleCompoundCondition"


    // $ANTLR start "entryRuleXArtifactMatcher"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2325:1: entryRuleXArtifactMatcher returns [EObject current=null] : iv_ruleXArtifactMatcher= ruleXArtifactMatcher EOF ;
    public final EObject entryRuleXArtifactMatcher() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXArtifactMatcher = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2326:2: (iv_ruleXArtifactMatcher= ruleXArtifactMatcher EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2327:2: iv_ruleXArtifactMatcher= ruleXArtifactMatcher EOF
            {
             newCompositeNode(grammarAccess.getXArtifactMatcherRule()); 
            pushFollow(FOLLOW_ruleXArtifactMatcher_in_entryRuleXArtifactMatcher4946);
            iv_ruleXArtifactMatcher=ruleXArtifactMatcher();

            state._fsp--;

             current =iv_ruleXArtifactMatcher; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXArtifactMatcher4956); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleXArtifactMatcher"


    // $ANTLR start "ruleXArtifactMatcher"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2334:1: ruleXArtifactMatcher returns [EObject current=null] : (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' ) ;
    public final EObject ruleXArtifactMatcher() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_6=null;
        EObject lv_conditions_3_0 = null;

        Enumerator lv_operators_4_0 = null;

        EObject lv_conditions_5_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2337:28: ( (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2338:1: (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2338:1: (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2338:3: otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';'
            {
            otherlv_0=(Token)match(input,72,FOLLOW_72_in_ruleXArtifactMatcher4993); 

                	newLeafNode(otherlv_0, grammarAccess.getXArtifactMatcherAccess().getArtifactMatcherKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2342:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2343:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2343:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2344:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactMatcher5010); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXArtifactMatcherAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXArtifactMatcherRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,73,FOLLOW_73_in_ruleXArtifactMatcher5027); 

                	newLeafNode(otherlv_2, grammarAccess.getXArtifactMatcherAccess().getWhereKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2364:1: ( (lv_conditions_3_0= ruleCondition ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2365:1: (lv_conditions_3_0= ruleCondition )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2365:1: (lv_conditions_3_0= ruleCondition )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2366:3: lv_conditions_3_0= ruleCondition
            {
             
            	        newCompositeNode(grammarAccess.getXArtifactMatcherAccess().getConditionsConditionParserRuleCall_3_0()); 
            	    
            pushFollow(FOLLOW_ruleCondition_in_ruleXArtifactMatcher5048);
            lv_conditions_3_0=ruleCondition();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getXArtifactMatcherRule());
            	        }
                   		add(
                   			current, 
                   			"conditions",
                    		lv_conditions_3_0, 
                    		"Condition");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2382:2: ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )*
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( ((LA41_0>=87 && LA41_0<=88)) ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2382:3: ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2382:3: ( (lv_operators_4_0= ruleXLogicOperator ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2383:1: (lv_operators_4_0= ruleXLogicOperator )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2383:1: (lv_operators_4_0= ruleXLogicOperator )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2384:3: lv_operators_4_0= ruleXLogicOperator
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXArtifactMatcherAccess().getOperatorsXLogicOperatorEnumRuleCall_4_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXLogicOperator_in_ruleXArtifactMatcher5070);
            	    lv_operators_4_0=ruleXLogicOperator();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXArtifactMatcherRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"operators",
            	            		lv_operators_4_0, 
            	            		"XLogicOperator");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }

            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2400:2: ( (lv_conditions_5_0= ruleCondition ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2401:1: (lv_conditions_5_0= ruleCondition )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2401:1: (lv_conditions_5_0= ruleCondition )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2402:3: lv_conditions_5_0= ruleCondition
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXArtifactMatcherAccess().getConditionsConditionParserRuleCall_4_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleCondition_in_ruleXArtifactMatcher5091);
            	    lv_conditions_5_0=ruleCondition();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXArtifactMatcherRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"conditions",
            	            		lv_conditions_5_0, 
            	            		"Condition");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop41;
                }
            } while (true);

            otherlv_6=(Token)match(input,74,FOLLOW_74_in_ruleXArtifactMatcher5105); 

                	newLeafNode(otherlv_6, grammarAccess.getXArtifactMatcherAccess().getSemicolonKeyword_5());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleXArtifactMatcher"


    // $ANTLR start "entryRuleRole"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2430:1: entryRuleRole returns [EObject current=null] : iv_ruleRole= ruleRole EOF ;
    public final EObject entryRuleRole() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRole = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2431:2: (iv_ruleRole= ruleRole EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2432:2: iv_ruleRole= ruleRole EOF
            {
             newCompositeNode(grammarAccess.getRoleRule()); 
            pushFollow(FOLLOW_ruleRole_in_entryRuleRole5141);
            iv_ruleRole=ruleRole();

            state._fsp--;

             current =iv_ruleRole; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRole5151); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRole"


    // $ANTLR start "ruleRole"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2439:1: ruleRole returns [EObject current=null] : (otherlv_0= 'role' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' ( ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) ) | ( (lv_referencedContexts_6_0= ruleReferencedContext ) ) )+ otherlv_7= '}' ) ;
    public final EObject ruleRole() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_7=null;
        EObject lv_usersAndGroups_5_0 = null;

        EObject lv_referencedContexts_6_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2442:28: ( (otherlv_0= 'role' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' ( ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) ) | ( (lv_referencedContexts_6_0= ruleReferencedContext ) ) )+ otherlv_7= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2443:1: (otherlv_0= 'role' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' ( ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) ) | ( (lv_referencedContexts_6_0= ruleReferencedContext ) ) )+ otherlv_7= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2443:1: (otherlv_0= 'role' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' ( ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) ) | ( (lv_referencedContexts_6_0= ruleReferencedContext ) ) )+ otherlv_7= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2443:3: otherlv_0= 'role' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' ( ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) ) | ( (lv_referencedContexts_6_0= ruleReferencedContext ) ) )+ otherlv_7= '}'
            {
            otherlv_0=(Token)match(input,75,FOLLOW_75_in_ruleRole5188); 

                	newLeafNode(otherlv_0, grammarAccess.getRoleAccess().getRoleKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2447:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2448:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2448:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2449:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRole5205); 

            			newLeafNode(lv_name_1_0, grammarAccess.getRoleAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getRoleRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2465:2: (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==17) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2465:4: otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) )
                    {
                    otherlv_2=(Token)match(input,17,FOLLOW_17_in_ruleRole5223); 

                        	newLeafNode(otherlv_2, grammarAccess.getRoleAccess().getExtendsKeyword_2_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2469:1: ( (otherlv_3= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2470:1: (otherlv_3= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2470:1: (otherlv_3= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2471:3: otherlv_3= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getRoleRule());
                    	        }
                            
                    otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRole5243); 

                    		newLeafNode(otherlv_3, grammarAccess.getRoleAccess().getSuperRolesRoleCrossReference_2_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,19,FOLLOW_19_in_ruleRole5257); 

                	newLeafNode(otherlv_4, grammarAccess.getRoleAccess().getLeftCurlyBracketKeyword_3());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2486:1: ( ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) ) | ( (lv_referencedContexts_6_0= ruleReferencedContext ) ) )+
            int cnt43=0;
            loop43:
            do {
                int alt43=3;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==20) ) {
                    alt43=1;
                }
                else if ( (LA43_0==76) ) {
                    alt43=2;
                }


                switch (alt43) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2486:2: ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2486:2: ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2487:1: (lv_usersAndGroups_5_0= ruleUsersAndGroups )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2487:1: (lv_usersAndGroups_5_0= ruleUsersAndGroups )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2488:3: lv_usersAndGroups_5_0= ruleUsersAndGroups
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getRoleAccess().getUsersAndGroupsUsersAndGroupsParserRuleCall_4_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUsersAndGroups_in_ruleRole5279);
            	    lv_usersAndGroups_5_0=ruleUsersAndGroups();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getRoleRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"usersAndGroups",
            	            		lv_usersAndGroups_5_0, 
            	            		"UsersAndGroups");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2505:6: ( (lv_referencedContexts_6_0= ruleReferencedContext ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2505:6: ( (lv_referencedContexts_6_0= ruleReferencedContext ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2506:1: (lv_referencedContexts_6_0= ruleReferencedContext )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2506:1: (lv_referencedContexts_6_0= ruleReferencedContext )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2507:3: lv_referencedContexts_6_0= ruleReferencedContext
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getRoleAccess().getReferencedContextsReferencedContextParserRuleCall_4_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleReferencedContext_in_ruleRole5306);
            	    lv_referencedContexts_6_0=ruleReferencedContext();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getRoleRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"referencedContexts",
            	            		lv_referencedContexts_6_0, 
            	            		"ReferencedContext");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt43 >= 1 ) break loop43;
                        EarlyExitException eee =
                            new EarlyExitException(43, input);
                        throw eee;
                }
                cnt43++;
            } while (true);

            otherlv_7=(Token)match(input,22,FOLLOW_22_in_ruleRole5320); 

                	newLeafNode(otherlv_7, grammarAccess.getRoleAccess().getRightCurlyBracketKeyword_5());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRole"


    // $ANTLR start "entryRuleReferencedContext"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2535:1: entryRuleReferencedContext returns [EObject current=null] : iv_ruleReferencedContext= ruleReferencedContext EOF ;
    public final EObject entryRuleReferencedContext() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleReferencedContext = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2536:2: (iv_ruleReferencedContext= ruleReferencedContext EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2537:2: iv_ruleReferencedContext= ruleReferencedContext EOF
            {
             newCompositeNode(grammarAccess.getReferencedContextRule()); 
            pushFollow(FOLLOW_ruleReferencedContext_in_entryRuleReferencedContext5356);
            iv_ruleReferencedContext=ruleReferencedContext();

            state._fsp--;

             current =iv_ruleReferencedContext; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleReferencedContext5366); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleReferencedContext"


    // $ANTLR start "ruleReferencedContext"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2544:1: ruleReferencedContext returns [EObject current=null] : (otherlv_0= 'accessContext' ( (lv_accessContextRef_1_0= RULE_STRING ) ) otherlv_2= ';' ) ;
    public final EObject ruleReferencedContext() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_accessContextRef_1_0=null;
        Token otherlv_2=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2547:28: ( (otherlv_0= 'accessContext' ( (lv_accessContextRef_1_0= RULE_STRING ) ) otherlv_2= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2548:1: (otherlv_0= 'accessContext' ( (lv_accessContextRef_1_0= RULE_STRING ) ) otherlv_2= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2548:1: (otherlv_0= 'accessContext' ( (lv_accessContextRef_1_0= RULE_STRING ) ) otherlv_2= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2548:3: otherlv_0= 'accessContext' ( (lv_accessContextRef_1_0= RULE_STRING ) ) otherlv_2= ';'
            {
            otherlv_0=(Token)match(input,76,FOLLOW_76_in_ruleReferencedContext5403); 

                	newLeafNode(otherlv_0, grammarAccess.getReferencedContextAccess().getAccessContextKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2552:1: ( (lv_accessContextRef_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2553:1: (lv_accessContextRef_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2553:1: (lv_accessContextRef_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2554:3: lv_accessContextRef_1_0= RULE_STRING
            {
            lv_accessContextRef_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleReferencedContext5420); 

            			newLeafNode(lv_accessContextRef_1_0, grammarAccess.getReferencedContextAccess().getAccessContextRefSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getReferencedContextRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"accessContextRef",
                    		lv_accessContextRef_1_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,74,FOLLOW_74_in_ruleReferencedContext5437); 

                	newLeafNode(otherlv_2, grammarAccess.getReferencedContextAccess().getSemicolonKeyword_2());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleReferencedContext"


    // $ANTLR start "entryRuleUsersAndGroups"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2582:1: entryRuleUsersAndGroups returns [EObject current=null] : iv_ruleUsersAndGroups= ruleUsersAndGroups EOF ;
    public final EObject entryRuleUsersAndGroups() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUsersAndGroups = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2583:2: (iv_ruleUsersAndGroups= ruleUsersAndGroups EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2584:2: iv_ruleUsersAndGroups= ruleUsersAndGroups EOF
            {
             newCompositeNode(grammarAccess.getUsersAndGroupsRule()); 
            pushFollow(FOLLOW_ruleUsersAndGroups_in_entryRuleUsersAndGroups5473);
            iv_ruleUsersAndGroups=ruleUsersAndGroups();

            state._fsp--;

             current =iv_ruleUsersAndGroups; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUsersAndGroups5483); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUsersAndGroups"


    // $ANTLR start "ruleUsersAndGroups"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2591:1: ruleUsersAndGroups returns [EObject current=null] : (otherlv_0= 'guid' ( (lv_userOrGroupGuid_1_0= RULE_STRING ) ) otherlv_2= ';' ) ;
    public final EObject ruleUsersAndGroups() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_userOrGroupGuid_1_0=null;
        Token otherlv_2=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2594:28: ( (otherlv_0= 'guid' ( (lv_userOrGroupGuid_1_0= RULE_STRING ) ) otherlv_2= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2595:1: (otherlv_0= 'guid' ( (lv_userOrGroupGuid_1_0= RULE_STRING ) ) otherlv_2= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2595:1: (otherlv_0= 'guid' ( (lv_userOrGroupGuid_1_0= RULE_STRING ) ) otherlv_2= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2595:3: otherlv_0= 'guid' ( (lv_userOrGroupGuid_1_0= RULE_STRING ) ) otherlv_2= ';'
            {
            otherlv_0=(Token)match(input,20,FOLLOW_20_in_ruleUsersAndGroups5520); 

                	newLeafNode(otherlv_0, grammarAccess.getUsersAndGroupsAccess().getGuidKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2599:1: ( (lv_userOrGroupGuid_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2600:1: (lv_userOrGroupGuid_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2600:1: (lv_userOrGroupGuid_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2601:3: lv_userOrGroupGuid_1_0= RULE_STRING
            {
            lv_userOrGroupGuid_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUsersAndGroups5537); 

            			newLeafNode(lv_userOrGroupGuid_1_0, grammarAccess.getUsersAndGroupsAccess().getUserOrGroupGuidSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getUsersAndGroupsRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"userOrGroupGuid",
                    		lv_userOrGroupGuid_1_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,74,FOLLOW_74_in_ruleUsersAndGroups5554); 

                	newLeafNode(otherlv_2, grammarAccess.getUsersAndGroupsAccess().getSemicolonKeyword_2());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUsersAndGroups"


    // $ANTLR start "entryRuleAccessContext"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2629:1: entryRuleAccessContext returns [EObject current=null] : iv_ruleAccessContext= ruleAccessContext EOF ;
    public final EObject entryRuleAccessContext() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAccessContext = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2630:2: (iv_ruleAccessContext= ruleAccessContext EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2631:2: iv_ruleAccessContext= ruleAccessContext EOF
            {
             newCompositeNode(grammarAccess.getAccessContextRule()); 
            pushFollow(FOLLOW_ruleAccessContext_in_entryRuleAccessContext5590);
            iv_ruleAccessContext=ruleAccessContext();

            state._fsp--;

             current =iv_ruleAccessContext; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAccessContext5600); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAccessContext"


    // $ANTLR start "ruleAccessContext"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2638:1: ruleAccessContext returns [EObject current=null] : (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' ) ;
    public final EObject ruleAccessContext() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token lv_guid_6_0=null;
        Token otherlv_7=null;
        Token otherlv_10=null;
        EObject lv_accessRules_8_0 = null;

        EObject lv_hierarchyRestrictions_9_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2641:28: ( (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2642:1: (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2642:1: (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2642:3: otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}'
            {
            otherlv_0=(Token)match(input,76,FOLLOW_76_in_ruleAccessContext5637); 

                	newLeafNode(otherlv_0, grammarAccess.getAccessContextAccess().getAccessContextKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2646:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2647:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2647:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2648:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAccessContext5654); 

            			newLeafNode(lv_name_1_0, grammarAccess.getAccessContextAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAccessContextRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2664:2: (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==17) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2664:4: otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) )
                    {
                    otherlv_2=(Token)match(input,17,FOLLOW_17_in_ruleAccessContext5672); 

                        	newLeafNode(otherlv_2, grammarAccess.getAccessContextAccess().getExtendsKeyword_2_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2668:1: ( (otherlv_3= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2669:1: (otherlv_3= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2669:1: (otherlv_3= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2670:3: otherlv_3= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getAccessContextRule());
                    	        }
                            
                    otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAccessContext5692); 

                    		newLeafNode(otherlv_3, grammarAccess.getAccessContextAccess().getSuperAccessContextsAccessContextCrossReference_2_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,19,FOLLOW_19_in_ruleAccessContext5706); 

                	newLeafNode(otherlv_4, grammarAccess.getAccessContextAccess().getLeftCurlyBracketKeyword_3());
                
            otherlv_5=(Token)match(input,20,FOLLOW_20_in_ruleAccessContext5718); 

                	newLeafNode(otherlv_5, grammarAccess.getAccessContextAccess().getGuidKeyword_4());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2689:1: ( (lv_guid_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2690:1: (lv_guid_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2690:1: (lv_guid_6_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2691:3: lv_guid_6_0= RULE_STRING
            {
            lv_guid_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAccessContext5735); 

            			newLeafNode(lv_guid_6_0, grammarAccess.getAccessContextAccess().getGuidSTRINGTerminalRuleCall_5_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAccessContextRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"guid",
                    		lv_guid_6_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_7=(Token)match(input,74,FOLLOW_74_in_ruleAccessContext5752); 

                	newLeafNode(otherlv_7, grammarAccess.getAccessContextAccess().getSemicolonKeyword_6());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2711:1: ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+
            int cnt45=0;
            loop45:
            do {
                int alt45=3;
                int LA45_0 = input.LA(1);

                if ( ((LA45_0>=92 && LA45_0<=93)) ) {
                    alt45=1;
                }
                else if ( (LA45_0==77) ) {
                    alt45=2;
                }


                switch (alt45) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2711:2: ( (lv_accessRules_8_0= ruleObjectRestriction ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2711:2: ( (lv_accessRules_8_0= ruleObjectRestriction ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2712:1: (lv_accessRules_8_0= ruleObjectRestriction )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2712:1: (lv_accessRules_8_0= ruleObjectRestriction )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2713:3: lv_accessRules_8_0= ruleObjectRestriction
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAccessContextAccess().getAccessRulesObjectRestrictionParserRuleCall_7_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleObjectRestriction_in_ruleAccessContext5774);
            	    lv_accessRules_8_0=ruleObjectRestriction();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAccessContextRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"accessRules",
            	            		lv_accessRules_8_0, 
            	            		"ObjectRestriction");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2730:6: ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2730:6: ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2731:1: (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2731:1: (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2732:3: lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAccessContextAccess().getHierarchyRestrictionsHierarchyRestrictionParserRuleCall_7_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleHierarchyRestriction_in_ruleAccessContext5801);
            	    lv_hierarchyRestrictions_9_0=ruleHierarchyRestriction();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAccessContextRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"hierarchyRestrictions",
            	            		lv_hierarchyRestrictions_9_0, 
            	            		"HierarchyRestriction");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt45 >= 1 ) break loop45;
                        EarlyExitException eee =
                            new EarlyExitException(45, input);
                        throw eee;
                }
                cnt45++;
            } while (true);

            otherlv_10=(Token)match(input,22,FOLLOW_22_in_ruleAccessContext5815); 

                	newLeafNode(otherlv_10, grammarAccess.getAccessContextAccess().getRightCurlyBracketKeyword_8());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAccessContext"


    // $ANTLR start "entryRuleHierarchyRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2760:1: entryRuleHierarchyRestriction returns [EObject current=null] : iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF ;
    public final EObject entryRuleHierarchyRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleHierarchyRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2761:2: (iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2762:2: iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF
            {
             newCompositeNode(grammarAccess.getHierarchyRestrictionRule()); 
            pushFollow(FOLLOW_ruleHierarchyRestriction_in_entryRuleHierarchyRestriction5851);
            iv_ruleHierarchyRestriction=ruleHierarchyRestriction();

            state._fsp--;

             current =iv_ruleHierarchyRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleHierarchyRestriction5861); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleHierarchyRestriction"


    // $ANTLR start "ruleHierarchyRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2769:1: ruleHierarchyRestriction returns [EObject current=null] : (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' ) ;
    public final EObject ruleHierarchyRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_accessRules_3_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2772:28: ( (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2773:1: (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2773:1: (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2773:3: otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}'
            {
            otherlv_0=(Token)match(input,77,FOLLOW_77_in_ruleHierarchyRestriction5898); 

                	newLeafNode(otherlv_0, grammarAccess.getHierarchyRestrictionAccess().getChildrenOfKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2777:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2778:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2778:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2779:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getHierarchyRestrictionRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleHierarchyRestriction5918); 

            		newLeafNode(otherlv_1, grammarAccess.getHierarchyRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_1_0()); 
            	

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleHierarchyRestriction5930); 

                	newLeafNode(otherlv_2, grammarAccess.getHierarchyRestrictionAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2794:1: ( (lv_accessRules_3_0= ruleObjectRestriction ) )+
            int cnt46=0;
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( ((LA46_0>=92 && LA46_0<=93)) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2795:1: (lv_accessRules_3_0= ruleObjectRestriction )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2795:1: (lv_accessRules_3_0= ruleObjectRestriction )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2796:3: lv_accessRules_3_0= ruleObjectRestriction
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getHierarchyRestrictionAccess().getAccessRulesObjectRestrictionParserRuleCall_3_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleObjectRestriction_in_ruleHierarchyRestriction5951);
            	    lv_accessRules_3_0=ruleObjectRestriction();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getHierarchyRestrictionRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"accessRules",
            	            		lv_accessRules_3_0, 
            	            		"ObjectRestriction");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt46 >= 1 ) break loop46;
                        EarlyExitException eee =
                            new EarlyExitException(46, input);
                        throw eee;
                }
                cnt46++;
            } while (true);

            otherlv_4=(Token)match(input,22,FOLLOW_22_in_ruleHierarchyRestriction5964); 

                	newLeafNode(otherlv_4, grammarAccess.getHierarchyRestrictionAccess().getRightCurlyBracketKeyword_4());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleHierarchyRestriction"


    // $ANTLR start "entryRuleRelationTypeArtifactTypePredicate"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2824:1: entryRuleRelationTypeArtifactTypePredicate returns [EObject current=null] : iv_ruleRelationTypeArtifactTypePredicate= ruleRelationTypeArtifactTypePredicate EOF ;
    public final EObject entryRuleRelationTypeArtifactTypePredicate() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypeArtifactTypePredicate = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2825:2: (iv_ruleRelationTypeArtifactTypePredicate= ruleRelationTypeArtifactTypePredicate EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2826:2: iv_ruleRelationTypeArtifactTypePredicate= ruleRelationTypeArtifactTypePredicate EOF
            {
             newCompositeNode(grammarAccess.getRelationTypeArtifactTypePredicateRule()); 
            pushFollow(FOLLOW_ruleRelationTypeArtifactTypePredicate_in_entryRuleRelationTypeArtifactTypePredicate6000);
            iv_ruleRelationTypeArtifactTypePredicate=ruleRelationTypeArtifactTypePredicate();

            state._fsp--;

             current =iv_ruleRelationTypeArtifactTypePredicate; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationTypeArtifactTypePredicate6010); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRelationTypeArtifactTypePredicate"


    // $ANTLR start "ruleRelationTypeArtifactTypePredicate"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2833:1: ruleRelationTypeArtifactTypePredicate returns [EObject current=null] : (otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) ) ) ;
    public final EObject ruleRelationTypeArtifactTypePredicate() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2836:28: ( (otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2837:1: (otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2837:1: (otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2837:3: otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,16,FOLLOW_16_in_ruleRelationTypeArtifactTypePredicate6047); 

                	newLeafNode(otherlv_0, grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2841:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2842:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2842:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2843:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRelationTypeArtifactTypePredicateRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationTypeArtifactTypePredicate6067); 

            		newLeafNode(otherlv_1, grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeRefXArtifactTypeCrossReference_1_0()); 
            	

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRelationTypeArtifactTypePredicate"


    // $ANTLR start "entryRuleRelationTypeArtifactPredicate"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2862:1: entryRuleRelationTypeArtifactPredicate returns [EObject current=null] : iv_ruleRelationTypeArtifactPredicate= ruleRelationTypeArtifactPredicate EOF ;
    public final EObject entryRuleRelationTypeArtifactPredicate() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypeArtifactPredicate = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2863:2: (iv_ruleRelationTypeArtifactPredicate= ruleRelationTypeArtifactPredicate EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2864:2: iv_ruleRelationTypeArtifactPredicate= ruleRelationTypeArtifactPredicate EOF
            {
             newCompositeNode(grammarAccess.getRelationTypeArtifactPredicateRule()); 
            pushFollow(FOLLOW_ruleRelationTypeArtifactPredicate_in_entryRuleRelationTypeArtifactPredicate6103);
            iv_ruleRelationTypeArtifactPredicate=ruleRelationTypeArtifactPredicate();

            state._fsp--;

             current =iv_ruleRelationTypeArtifactPredicate; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationTypeArtifactPredicate6113); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRelationTypeArtifactPredicate"


    // $ANTLR start "ruleRelationTypeArtifactPredicate"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2871:1: ruleRelationTypeArtifactPredicate returns [EObject current=null] : (otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) ) ) ;
    public final EObject ruleRelationTypeArtifactPredicate() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2874:28: ( (otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2875:1: (otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2875:1: (otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2875:3: otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,78,FOLLOW_78_in_ruleRelationTypeArtifactPredicate6150); 

                	newLeafNode(otherlv_0, grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2879:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2880:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2880:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2881:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRelationTypeArtifactPredicateRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationTypeArtifactPredicate6170); 

            		newLeafNode(otherlv_1, grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_1_0()); 
            	

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRelationTypeArtifactPredicate"


    // $ANTLR start "entryRuleRelationTypePredicate"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2900:1: entryRuleRelationTypePredicate returns [EObject current=null] : iv_ruleRelationTypePredicate= ruleRelationTypePredicate EOF ;
    public final EObject entryRuleRelationTypePredicate() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypePredicate = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2901:2: (iv_ruleRelationTypePredicate= ruleRelationTypePredicate EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2902:2: iv_ruleRelationTypePredicate= ruleRelationTypePredicate EOF
            {
             newCompositeNode(grammarAccess.getRelationTypePredicateRule()); 
            pushFollow(FOLLOW_ruleRelationTypePredicate_in_entryRuleRelationTypePredicate6206);
            iv_ruleRelationTypePredicate=ruleRelationTypePredicate();

            state._fsp--;

             current =iv_ruleRelationTypePredicate; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationTypePredicate6216); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRelationTypePredicate"


    // $ANTLR start "ruleRelationTypePredicate"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2909:1: ruleRelationTypePredicate returns [EObject current=null] : (this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate | this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate ) ;
    public final EObject ruleRelationTypePredicate() throws RecognitionException {
        EObject current = null;

        EObject this_RelationTypeArtifactPredicate_0 = null;

        EObject this_RelationTypeArtifactTypePredicate_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2912:28: ( (this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate | this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2913:1: (this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate | this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2913:1: (this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate | this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate )
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==78) ) {
                alt47=1;
            }
            else if ( (LA47_0==16) ) {
                alt47=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2914:5: this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate
                    {
                     
                            newCompositeNode(grammarAccess.getRelationTypePredicateAccess().getRelationTypeArtifactPredicateParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleRelationTypeArtifactPredicate_in_ruleRelationTypePredicate6263);
                    this_RelationTypeArtifactPredicate_0=ruleRelationTypeArtifactPredicate();

                    state._fsp--;

                     
                            current = this_RelationTypeArtifactPredicate_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2924:5: this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate
                    {
                     
                            newCompositeNode(grammarAccess.getRelationTypePredicateAccess().getRelationTypeArtifactTypePredicateParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleRelationTypeArtifactTypePredicate_in_ruleRelationTypePredicate6290);
                    this_RelationTypeArtifactTypePredicate_1=ruleRelationTypeArtifactTypePredicate();

                    state._fsp--;

                     
                            current = this_RelationTypeArtifactTypePredicate_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRelationTypePredicate"


    // $ANTLR start "entryRuleObjectRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2940:1: entryRuleObjectRestriction returns [EObject current=null] : iv_ruleObjectRestriction= ruleObjectRestriction EOF ;
    public final EObject entryRuleObjectRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleObjectRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2941:2: (iv_ruleObjectRestriction= ruleObjectRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2942:2: iv_ruleObjectRestriction= ruleObjectRestriction EOF
            {
             newCompositeNode(grammarAccess.getObjectRestrictionRule()); 
            pushFollow(FOLLOW_ruleObjectRestriction_in_entryRuleObjectRestriction6325);
            iv_ruleObjectRestriction=ruleObjectRestriction();

            state._fsp--;

             current =iv_ruleObjectRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleObjectRestriction6335); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleObjectRestriction"


    // $ANTLR start "ruleObjectRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2949:1: ruleObjectRestriction returns [EObject current=null] : (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction ) ;
    public final EObject ruleObjectRestriction() throws RecognitionException {
        EObject current = null;

        EObject this_ArtifactMatchRestriction_0 = null;

        EObject this_ArtifactTypeRestriction_1 = null;

        EObject this_RelationTypeRestriction_2 = null;

        EObject this_AttributeTypeRestriction_3 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2952:28: ( (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2953:1: (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2953:1: (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )
            int alt48=4;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==92) ) {
                int LA48_1 = input.LA(2);

                if ( (LA48_1==79) ) {
                    switch ( input.LA(3) ) {
                    case 78:
                        {
                        alt48=1;
                        }
                        break;
                    case 60:
                        {
                        alt48=3;
                        }
                        break;
                    case 16:
                        {
                        alt48=2;
                        }
                        break;
                    case 25:
                        {
                        alt48=4;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 48, 3, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA48_0==93) ) {
                int LA48_2 = input.LA(2);

                if ( (LA48_2==79) ) {
                    switch ( input.LA(3) ) {
                    case 78:
                        {
                        alt48=1;
                        }
                        break;
                    case 60:
                        {
                        alt48=3;
                        }
                        break;
                    case 16:
                        {
                        alt48=2;
                        }
                        break;
                    case 25:
                        {
                        alt48=4;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 48, 3, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 48, 0, input);

                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2954:5: this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getArtifactMatchRestrictionParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleArtifactMatchRestriction_in_ruleObjectRestriction6382);
                    this_ArtifactMatchRestriction_0=ruleArtifactMatchRestriction();

                    state._fsp--;

                     
                            current = this_ArtifactMatchRestriction_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2964:5: this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getArtifactTypeRestrictionParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleArtifactTypeRestriction_in_ruleObjectRestriction6409);
                    this_ArtifactTypeRestriction_1=ruleArtifactTypeRestriction();

                    state._fsp--;

                     
                            current = this_ArtifactTypeRestriction_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2974:5: this_RelationTypeRestriction_2= ruleRelationTypeRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getRelationTypeRestrictionParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleRelationTypeRestriction_in_ruleObjectRestriction6436);
                    this_RelationTypeRestriction_2=ruleRelationTypeRestriction();

                    state._fsp--;

                     
                            current = this_RelationTypeRestriction_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2984:5: this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getAttributeTypeRestrictionParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_ruleAttributeTypeRestriction_in_ruleObjectRestriction6463);
                    this_AttributeTypeRestriction_3=ruleAttributeTypeRestriction();

                    state._fsp--;

                     
                            current = this_AttributeTypeRestriction_3; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleObjectRestriction"


    // $ANTLR start "entryRuleArtifactMatchRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3000:1: entryRuleArtifactMatchRestriction returns [EObject current=null] : iv_ruleArtifactMatchRestriction= ruleArtifactMatchRestriction EOF ;
    public final EObject entryRuleArtifactMatchRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactMatchRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3001:2: (iv_ruleArtifactMatchRestriction= ruleArtifactMatchRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3002:2: iv_ruleArtifactMatchRestriction= ruleArtifactMatchRestriction EOF
            {
             newCompositeNode(grammarAccess.getArtifactMatchRestrictionRule()); 
            pushFollow(FOLLOW_ruleArtifactMatchRestriction_in_entryRuleArtifactMatchRestriction6498);
            iv_ruleArtifactMatchRestriction=ruleArtifactMatchRestriction();

            state._fsp--;

             current =iv_ruleArtifactMatchRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactMatchRestriction6508); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleArtifactMatchRestriction"


    // $ANTLR start "ruleArtifactMatchRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3009:1: ruleArtifactMatchRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) ;
    public final EObject ruleArtifactMatchRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Enumerator lv_permission_0_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3012:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3013:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3013:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3013:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3013:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3014:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3014:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3015:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getArtifactMatchRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactMatchRestriction6554);
            lv_permission_0_0=ruleAccessPermissionEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getArtifactMatchRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"permission",
                    		lv_permission_0_0, 
                    		"AccessPermissionEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,79,FOLLOW_79_in_ruleArtifactMatchRestriction6566); 

                	newLeafNode(otherlv_1, grammarAccess.getArtifactMatchRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,78,FOLLOW_78_in_ruleArtifactMatchRestriction6578); 

                	newLeafNode(otherlv_2, grammarAccess.getArtifactMatchRestrictionAccess().getArtifactKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3039:1: ( (otherlv_3= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3040:1: (otherlv_3= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3040:1: (otherlv_3= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3041:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getArtifactMatchRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleArtifactMatchRestriction6598); 

            		newLeafNode(otherlv_3, grammarAccess.getArtifactMatchRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_3_0()); 
            	

            }


            }

            otherlv_4=(Token)match(input,74,FOLLOW_74_in_ruleArtifactMatchRestriction6610); 

                	newLeafNode(otherlv_4, grammarAccess.getArtifactMatchRestrictionAccess().getSemicolonKeyword_4());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleArtifactMatchRestriction"


    // $ANTLR start "entryRuleArtifactTypeRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3064:1: entryRuleArtifactTypeRestriction returns [EObject current=null] : iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF ;
    public final EObject entryRuleArtifactTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactTypeRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3065:2: (iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3066:2: iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF
            {
             newCompositeNode(grammarAccess.getArtifactTypeRestrictionRule()); 
            pushFollow(FOLLOW_ruleArtifactTypeRestriction_in_entryRuleArtifactTypeRestriction6646);
            iv_ruleArtifactTypeRestriction=ruleArtifactTypeRestriction();

            state._fsp--;

             current =iv_ruleArtifactTypeRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactTypeRestriction6656); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleArtifactTypeRestriction"


    // $ANTLR start "ruleArtifactTypeRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3073:1: ruleArtifactTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) ;
    public final EObject ruleArtifactTypeRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Enumerator lv_permission_0_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3076:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3077:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3077:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3077:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3077:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3078:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3078:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3079:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getArtifactTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactTypeRestriction6702);
            lv_permission_0_0=ruleAccessPermissionEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getArtifactTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"permission",
                    		lv_permission_0_0, 
                    		"AccessPermissionEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,79,FOLLOW_79_in_ruleArtifactTypeRestriction6714); 

                	newLeafNode(otherlv_1, grammarAccess.getArtifactTypeRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,16,FOLLOW_16_in_ruleArtifactTypeRestriction6726); 

                	newLeafNode(otherlv_2, grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3103:1: ( (otherlv_3= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3104:1: (otherlv_3= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3104:1: (otherlv_3= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3105:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getArtifactTypeRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleArtifactTypeRestriction6746); 

            		newLeafNode(otherlv_3, grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_3_0()); 
            	

            }


            }

            otherlv_4=(Token)match(input,74,FOLLOW_74_in_ruleArtifactTypeRestriction6758); 

                	newLeafNode(otherlv_4, grammarAccess.getArtifactTypeRestrictionAccess().getSemicolonKeyword_4());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleArtifactTypeRestriction"


    // $ANTLR start "entryRuleAttributeTypeRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3128:1: entryRuleAttributeTypeRestriction returns [EObject current=null] : iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF ;
    public final EObject entryRuleAttributeTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeTypeRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3129:2: (iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3130:2: iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF
            {
             newCompositeNode(grammarAccess.getAttributeTypeRestrictionRule()); 
            pushFollow(FOLLOW_ruleAttributeTypeRestriction_in_entryRuleAttributeTypeRestriction6794);
            iv_ruleAttributeTypeRestriction=ruleAttributeTypeRestriction();

            state._fsp--;

             current =iv_ruleAttributeTypeRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeTypeRestriction6804); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttributeTypeRestriction"


    // $ANTLR start "ruleAttributeTypeRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3137:1: ruleAttributeTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' ) ;
    public final EObject ruleAttributeTypeRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Enumerator lv_permission_0_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3140:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3141:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3141:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3141:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3141:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3142:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3142:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3143:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getAttributeTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleAttributeTypeRestriction6850);
            lv_permission_0_0=ruleAccessPermissionEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttributeTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"permission",
                    		lv_permission_0_0, 
                    		"AccessPermissionEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,79,FOLLOW_79_in_ruleAttributeTypeRestriction6862); 

                	newLeafNode(otherlv_1, grammarAccess.getAttributeTypeRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,25,FOLLOW_25_in_ruleAttributeTypeRestriction6874); 

                	newLeafNode(otherlv_2, grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3167:1: ( (otherlv_3= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3168:1: (otherlv_3= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3168:1: (otherlv_3= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3169:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getAttributeTypeRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeTypeRestriction6894); 

            		newLeafNode(otherlv_3, grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeRefXAttributeTypeCrossReference_3_0()); 
            	

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3180:2: (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==80) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3180:4: otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) )
                    {
                    otherlv_4=(Token)match(input,80,FOLLOW_80_in_ruleAttributeTypeRestriction6907); 

                        	newLeafNode(otherlv_4, grammarAccess.getAttributeTypeRestrictionAccess().getOfKeyword_4_0());
                        
                    otherlv_5=(Token)match(input,16,FOLLOW_16_in_ruleAttributeTypeRestriction6919); 

                        	newLeafNode(otherlv_5, grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeKeyword_4_1());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3188:1: ( (otherlv_6= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3189:1: (otherlv_6= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3189:1: (otherlv_6= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3190:3: otherlv_6= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getAttributeTypeRestrictionRule());
                    	        }
                            
                    otherlv_6=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeTypeRestriction6939); 

                    		newLeafNode(otherlv_6, grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_4_2_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_7=(Token)match(input,74,FOLLOW_74_in_ruleAttributeTypeRestriction6953); 

                	newLeafNode(otherlv_7, grammarAccess.getAttributeTypeRestrictionAccess().getSemicolonKeyword_5());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttributeTypeRestriction"


    // $ANTLR start "entryRuleRelationTypeRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3215:1: entryRuleRelationTypeRestriction returns [EObject current=null] : iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF ;
    public final EObject entryRuleRelationTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypeRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3216:2: (iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3217:2: iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF
            {
             newCompositeNode(grammarAccess.getRelationTypeRestrictionRule()); 
            pushFollow(FOLLOW_ruleRelationTypeRestriction_in_entryRuleRelationTypeRestriction6991);
            iv_ruleRelationTypeRestriction=ruleRelationTypeRestriction();

            state._fsp--;

             current =iv_ruleRelationTypeRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationTypeRestriction7001); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRelationTypeRestriction"


    // $ANTLR start "ruleRelationTypeRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3224:1: ruleRelationTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';' ) ;
    public final EObject ruleRelationTypeRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_7=null;
        Enumerator lv_permission_0_0 = null;

        Enumerator lv_relationTypeMatch_3_0 = null;

        Enumerator lv_restrictedToSide_5_0 = null;

        EObject lv_predicate_6_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3227:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3228:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3228:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3228:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3228:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3229:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3229:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3230:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleRelationTypeRestriction7047);
            lv_permission_0_0=ruleAccessPermissionEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"permission",
                    		lv_permission_0_0, 
                    		"AccessPermissionEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,79,FOLLOW_79_in_ruleRelationTypeRestriction7059); 

                	newLeafNode(otherlv_1, grammarAccess.getRelationTypeRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,60,FOLLOW_60_in_ruleRelationTypeRestriction7071); 

                	newLeafNode(otherlv_2, grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3254:1: ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) )
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==94) ) {
                alt50=1;
            }
            else if ( (LA50_0==RULE_STRING) ) {
                alt50=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3254:2: ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3254:2: ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3255:1: (lv_relationTypeMatch_3_0= ruleRelationTypeMatch )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3255:1: (lv_relationTypeMatch_3_0= ruleRelationTypeMatch )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3256:3: lv_relationTypeMatch_3_0= ruleRelationTypeMatch
                    {
                     
                    	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeMatchRelationTypeMatchEnumRuleCall_3_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleRelationTypeMatch_in_ruleRelationTypeRestriction7093);
                    lv_relationTypeMatch_3_0=ruleRelationTypeMatch();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
                    	        }
                           		set(
                           			current, 
                           			"relationTypeMatch",
                            		true, 
                            		"RelationTypeMatch");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3273:6: ( (otherlv_4= RULE_STRING ) )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3273:6: ( (otherlv_4= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3274:1: (otherlv_4= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3274:1: (otherlv_4= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3275:3: otherlv_4= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getRelationTypeRestrictionRule());
                    	        }
                            
                    otherlv_4=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationTypeRestriction7119); 

                    		newLeafNode(otherlv_4, grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeRefXRelationTypeCrossReference_3_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3286:3: ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3287:1: (lv_restrictedToSide_5_0= ruleXRelationSideEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3287:1: (lv_restrictedToSide_5_0= ruleXRelationSideEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3288:3: lv_restrictedToSide_5_0= ruleXRelationSideEnum
            {
             
            	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getRestrictedToSideXRelationSideEnumEnumRuleCall_4_0()); 
            	    
            pushFollow(FOLLOW_ruleXRelationSideEnum_in_ruleRelationTypeRestriction7141);
            lv_restrictedToSide_5_0=ruleXRelationSideEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"restrictedToSide",
                    		lv_restrictedToSide_5_0, 
                    		"XRelationSideEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3304:2: ( (lv_predicate_6_0= ruleRelationTypePredicate ) )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==16||LA51_0==78) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3305:1: (lv_predicate_6_0= ruleRelationTypePredicate )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3305:1: (lv_predicate_6_0= ruleRelationTypePredicate )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3306:3: lv_predicate_6_0= ruleRelationTypePredicate
                    {
                     
                    	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getPredicateRelationTypePredicateParserRuleCall_5_0()); 
                    	    
                    pushFollow(FOLLOW_ruleRelationTypePredicate_in_ruleRelationTypeRestriction7162);
                    lv_predicate_6_0=ruleRelationTypePredicate();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
                    	        }
                           		set(
                           			current, 
                           			"predicate",
                            		lv_predicate_6_0, 
                            		"RelationTypePredicate");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }
                    break;

            }

            otherlv_7=(Token)match(input,74,FOLLOW_74_in_ruleRelationTypeRestriction7175); 

                	newLeafNode(otherlv_7, grammarAccess.getRelationTypeRestrictionAccess().getSemicolonKeyword_6());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRelationTypeRestriction"


    // $ANTLR start "ruleRelationMultiplicityEnum"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3334:1: ruleRelationMultiplicityEnum returns [Enumerator current=null] : ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) ) ;
    public final Enumerator ruleRelationMultiplicityEnum() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3336:28: ( ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3337:1: ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3337:1: ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) )
            int alt52=4;
            switch ( input.LA(1) ) {
            case 81:
                {
                alt52=1;
                }
                break;
            case 82:
                {
                alt52=2;
                }
                break;
            case 83:
                {
                alt52=3;
                }
                break;
            case 84:
                {
                alt52=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }

            switch (alt52) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3337:2: (enumLiteral_0= 'ONE_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3337:2: (enumLiteral_0= 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3337:4: enumLiteral_0= 'ONE_TO_ONE'
                    {
                    enumLiteral_0=(Token)match(input,81,FOLLOW_81_in_ruleRelationMultiplicityEnum7225); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3343:6: (enumLiteral_1= 'ONE_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3343:6: (enumLiteral_1= 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3343:8: enumLiteral_1= 'ONE_TO_MANY'
                    {
                    enumLiteral_1=(Token)match(input,82,FOLLOW_82_in_ruleRelationMultiplicityEnum7242); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3349:6: (enumLiteral_2= 'MANY_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3349:6: (enumLiteral_2= 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3349:8: enumLiteral_2= 'MANY_TO_ONE'
                    {
                    enumLiteral_2=(Token)match(input,83,FOLLOW_83_in_ruleRelationMultiplicityEnum7259); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3355:6: (enumLiteral_3= 'MANY_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3355:6: (enumLiteral_3= 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3355:8: enumLiteral_3= 'MANY_TO_MANY'
                    {
                    enumLiteral_3=(Token)match(input,84,FOLLOW_84_in_ruleRelationMultiplicityEnum7276); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_3, grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3()); 
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRelationMultiplicityEnum"


    // $ANTLR start "ruleCompareOp"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3365:1: ruleCompareOp returns [Enumerator current=null] : ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) ) ;
    public final Enumerator ruleCompareOp() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3367:28: ( ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3368:1: ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3368:1: ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) )
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==85) ) {
                alt53=1;
            }
            else if ( (LA53_0==86) ) {
                alt53=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;
            }
            switch (alt53) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3368:2: (enumLiteral_0= 'EQ' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3368:2: (enumLiteral_0= 'EQ' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3368:4: enumLiteral_0= 'EQ'
                    {
                    enumLiteral_0=(Token)match(input,85,FOLLOW_85_in_ruleCompareOp7321); 

                            current = grammarAccess.getCompareOpAccess().getEQEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getCompareOpAccess().getEQEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3374:6: (enumLiteral_1= 'LIKE' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3374:6: (enumLiteral_1= 'LIKE' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3374:8: enumLiteral_1= 'LIKE'
                    {
                    enumLiteral_1=(Token)match(input,86,FOLLOW_86_in_ruleCompareOp7338); 

                            current = grammarAccess.getCompareOpAccess().getLIKEEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getCompareOpAccess().getLIKEEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleCompareOp"


    // $ANTLR start "ruleXLogicOperator"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3384:1: ruleXLogicOperator returns [Enumerator current=null] : ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) ) ;
    public final Enumerator ruleXLogicOperator() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3386:28: ( ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3387:1: ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3387:1: ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) )
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==87) ) {
                alt54=1;
            }
            else if ( (LA54_0==88) ) {
                alt54=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 54, 0, input);

                throw nvae;
            }
            switch (alt54) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3387:2: (enumLiteral_0= 'AND' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3387:2: (enumLiteral_0= 'AND' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3387:4: enumLiteral_0= 'AND'
                    {
                    enumLiteral_0=(Token)match(input,87,FOLLOW_87_in_ruleXLogicOperator7383); 

                            current = grammarAccess.getXLogicOperatorAccess().getANDEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getXLogicOperatorAccess().getANDEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3393:6: (enumLiteral_1= 'OR' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3393:6: (enumLiteral_1= 'OR' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3393:8: enumLiteral_1= 'OR'
                    {
                    enumLiteral_1=(Token)match(input,88,FOLLOW_88_in_ruleXLogicOperator7400); 

                            current = grammarAccess.getXLogicOperatorAccess().getOREnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getXLogicOperatorAccess().getOREnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleXLogicOperator"


    // $ANTLR start "ruleMatchField"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3403:1: ruleMatchField returns [Enumerator current=null] : ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchGuid' ) ) ;
    public final Enumerator ruleMatchField() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3405:28: ( ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchGuid' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3406:1: ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchGuid' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3406:1: ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchGuid' ) )
            int alt55=4;
            switch ( input.LA(1) ) {
            case 89:
                {
                alt55=1;
                }
                break;
            case 90:
                {
                alt55=2;
                }
                break;
            case 91:
                {
                alt55=3;
                }
                break;
            case 24:
                {
                alt55=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }

            switch (alt55) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3406:2: (enumLiteral_0= 'artifactName' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3406:2: (enumLiteral_0= 'artifactName' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3406:4: enumLiteral_0= 'artifactName'
                    {
                    enumLiteral_0=(Token)match(input,89,FOLLOW_89_in_ruleMatchField7445); 

                            current = grammarAccess.getMatchFieldAccess().getArtifactNameEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getMatchFieldAccess().getArtifactNameEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3412:6: (enumLiteral_1= 'artifactGuid' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3412:6: (enumLiteral_1= 'artifactGuid' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3412:8: enumLiteral_1= 'artifactGuid'
                    {
                    enumLiteral_1=(Token)match(input,90,FOLLOW_90_in_ruleMatchField7462); 

                            current = grammarAccess.getMatchFieldAccess().getArtifactGuidEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getMatchFieldAccess().getArtifactGuidEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3418:6: (enumLiteral_2= 'branchName' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3418:6: (enumLiteral_2= 'branchName' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3418:8: enumLiteral_2= 'branchName'
                    {
                    enumLiteral_2=(Token)match(input,91,FOLLOW_91_in_ruleMatchField7479); 

                            current = grammarAccess.getMatchFieldAccess().getBranchNameEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getMatchFieldAccess().getBranchNameEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3424:6: (enumLiteral_3= 'branchGuid' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3424:6: (enumLiteral_3= 'branchGuid' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3424:8: enumLiteral_3= 'branchGuid'
                    {
                    enumLiteral_3=(Token)match(input,24,FOLLOW_24_in_ruleMatchField7496); 

                            current = grammarAccess.getMatchFieldAccess().getBranchGuidEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_3, grammarAccess.getMatchFieldAccess().getBranchGuidEnumLiteralDeclaration_3()); 
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleMatchField"


    // $ANTLR start "ruleAccessPermissionEnum"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3434:1: ruleAccessPermissionEnum returns [Enumerator current=null] : ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) ) ;
    public final Enumerator ruleAccessPermissionEnum() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3436:28: ( ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3437:1: ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3437:1: ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==92) ) {
                alt56=1;
            }
            else if ( (LA56_0==93) ) {
                alt56=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3437:2: (enumLiteral_0= 'ALLOW' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3437:2: (enumLiteral_0= 'ALLOW' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3437:4: enumLiteral_0= 'ALLOW'
                    {
                    enumLiteral_0=(Token)match(input,92,FOLLOW_92_in_ruleAccessPermissionEnum7541); 

                            current = grammarAccess.getAccessPermissionEnumAccess().getALLOWEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getAccessPermissionEnumAccess().getALLOWEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3443:6: (enumLiteral_1= 'DENY' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3443:6: (enumLiteral_1= 'DENY' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3443:8: enumLiteral_1= 'DENY'
                    {
                    enumLiteral_1=(Token)match(input,93,FOLLOW_93_in_ruleAccessPermissionEnum7558); 

                            current = grammarAccess.getAccessPermissionEnumAccess().getDENYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getAccessPermissionEnumAccess().getDENYEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAccessPermissionEnum"


    // $ANTLR start "ruleRelationTypeMatch"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3453:1: ruleRelationTypeMatch returns [Enumerator current=null] : (enumLiteral_0= 'ALL' ) ;
    public final Enumerator ruleRelationTypeMatch() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3455:28: ( (enumLiteral_0= 'ALL' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3456:1: (enumLiteral_0= 'ALL' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3456:1: (enumLiteral_0= 'ALL' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3456:3: enumLiteral_0= 'ALL'
            {
            enumLiteral_0=(Token)match(input,94,FOLLOW_94_in_ruleRelationTypeMatch7602); 

                    current = grammarAccess.getRelationTypeMatchAccess().getALLEnumLiteralDeclaration().getEnumLiteral().getInstance();
                    newLeafNode(enumLiteral_0, grammarAccess.getRelationTypeMatchAccess().getALLEnumLiteralDeclaration()); 
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRelationTypeMatch"


    // $ANTLR start "ruleXRelationSideEnum"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3466:1: ruleXRelationSideEnum returns [Enumerator current=null] : ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) ) ;
    public final Enumerator ruleXRelationSideEnum() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3468:28: ( ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3469:1: ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3469:1: ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) )
            int alt57=3;
            switch ( input.LA(1) ) {
            case 95:
                {
                alt57=1;
                }
                break;
            case 96:
                {
                alt57=2;
                }
                break;
            case 97:
                {
                alt57=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 57, 0, input);

                throw nvae;
            }

            switch (alt57) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3469:2: (enumLiteral_0= 'SIDE_A' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3469:2: (enumLiteral_0= 'SIDE_A' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3469:4: enumLiteral_0= 'SIDE_A'
                    {
                    enumLiteral_0=(Token)match(input,95,FOLLOW_95_in_ruleXRelationSideEnum7646); 

                            current = grammarAccess.getXRelationSideEnumAccess().getSIDE_AEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getXRelationSideEnumAccess().getSIDE_AEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3475:6: (enumLiteral_1= 'SIDE_B' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3475:6: (enumLiteral_1= 'SIDE_B' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3475:8: enumLiteral_1= 'SIDE_B'
                    {
                    enumLiteral_1=(Token)match(input,96,FOLLOW_96_in_ruleXRelationSideEnum7663); 

                            current = grammarAccess.getXRelationSideEnumAccess().getSIDE_BEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getXRelationSideEnumAccess().getSIDE_BEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3481:6: (enumLiteral_2= 'BOTH' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3481:6: (enumLiteral_2= 'BOTH' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3481:8: enumLiteral_2= 'BOTH'
                    {
                    enumLiteral_2=(Token)match(input,97,FOLLOW_97_in_ruleXRelationSideEnum7680); 

                            current = grammarAccess.getXRelationSideEnumAccess().getBOTHEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getXRelationSideEnumAccess().getBOTHEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleXRelationSideEnum"

    // Delegated rules


 

    public static final BitSet FOLLOW_ruleOseeDsl_in_entryRuleOseeDsl75 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeDsl85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_ruleOseeDsl131 = new BitSet(new long[]{0x144800000201A002L,0x0000000000001900L});
    public static final BitSet FOLLOW_ruleXArtifactType_in_ruleOseeDsl154 = new BitSet(new long[]{0x1448000002018002L,0x0000000000001900L});
    public static final BitSet FOLLOW_ruleXRelationType_in_ruleOseeDsl181 = new BitSet(new long[]{0x1448000002018002L,0x0000000000001900L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_ruleOseeDsl208 = new BitSet(new long[]{0x1448000002018002L,0x0000000000001900L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_ruleOseeDsl235 = new BitSet(new long[]{0x1448000002018002L,0x0000000000001900L});
    public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_ruleOseeDsl262 = new BitSet(new long[]{0x1448000002018002L,0x0000000000001900L});
    public static final BitSet FOLLOW_ruleXOseeArtifactTypeOverride_in_ruleOseeDsl289 = new BitSet(new long[]{0x1448000002018002L,0x0000000000001900L});
    public static final BitSet FOLLOW_ruleXArtifactMatcher_in_ruleOseeDsl313 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001900L});
    public static final BitSet FOLLOW_ruleAccessContext_in_ruleOseeDsl340 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001900L});
    public static final BitSet FOLLOW_ruleRole_in_ruleOseeDsl367 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001900L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport405 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_ruleImport452 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleImport469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME511 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleQUALIFIED_NAME522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME562 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_14_in_ruleQUALIFIED_NAME581 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME596 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_ruleOseeType_in_entryRuleOseeType645 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeType655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXArtifactType_in_ruleOseeType702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRelationType_in_ruleOseeType729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_ruleOseeType756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_ruleOseeType783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXArtifactType_in_entryRuleXArtifactType818 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXArtifactType828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_ruleXArtifactType871 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleXArtifactType897 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactType914 = new BitSet(new long[]{0x00000000000A0000L});
    public static final BitSet FOLLOW_17_in_ruleXArtifactType932 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactType952 = new BitSet(new long[]{0x00000000000C0000L});
    public static final BitSet FOLLOW_18_in_ruleXArtifactType965 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactType985 = new BitSet(new long[]{0x00000000000C0000L});
    public static final BitSet FOLLOW_19_in_ruleXArtifactType1001 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleXArtifactType1014 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactType1031 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleXArtifactType1050 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_HEX_INT_in_ruleXArtifactType1067 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_ruleXArtifactType1093 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_22_in_ruleXArtifactType1106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_entryRuleXAttributeTypeRef1142 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXAttributeTypeRef1152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleXAttributeTypeRef1189 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1209 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_24_in_ruleXAttributeTypeRef1222 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_entryRuleXAttributeType1282 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXAttributeType1292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_ruleXAttributeType1329 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1346 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_ruleXAttributeType1364 = new BitSet(new long[]{0x0007FF8000000020L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_ruleXAttributeType1385 = new BitSet(new long[]{0x0000000004080000L});
    public static final BitSet FOLLOW_26_in_ruleXAttributeType1399 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1419 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXAttributeType1433 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleXAttributeType1446 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1463 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleXAttributeType1482 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_HEX_INT_in_ruleXAttributeType1499 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_ruleXAttributeType1516 = new BitSet(new long[]{0x0007FF8030000020L});
    public static final BitSet FOLLOW_28_in_ruleXAttributeType1536 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_29_in_ruleXAttributeType1565 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1597 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_ruleXAttributeType1612 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1629 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_ruleXAttributeType1646 = new BitSet(new long[]{0x0000000100000080L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1665 = new BitSet(new long[]{0x0000007A00400000L});
    public static final BitSet FOLLOW_32_in_ruleXAttributeType1686 = new BitSet(new long[]{0x0000007A00400000L});
    public static final BitSet FOLLOW_33_in_ruleXAttributeType1715 = new BitSet(new long[]{0x0007FF8400000020L});
    public static final BitSet FOLLOW_34_in_ruleXAttributeType1735 = new BitSet(new long[]{0x0000007800400000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1767 = new BitSet(new long[]{0x0000007800400000L});
    public static final BitSet FOLLOW_35_in_ruleXAttributeType1785 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1805 = new BitSet(new long[]{0x0000007000400000L});
    public static final BitSet FOLLOW_36_in_ruleXAttributeType1820 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1837 = new BitSet(new long[]{0x0000006000400000L});
    public static final BitSet FOLLOW_37_in_ruleXAttributeType1857 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1874 = new BitSet(new long[]{0x0000004000400000L});
    public static final BitSet FOLLOW_38_in_ruleXAttributeType1894 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1911 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXAttributeType1930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType1967 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType1978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_ruleAttributeBaseType2016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_ruleAttributeBaseType2035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_ruleAttributeBaseType2054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_ruleAttributeBaseType2073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_ruleAttributeBaseType2092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_ruleAttributeBaseType2111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_ruleAttributeBaseType2130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_ruleAttributeBaseType2149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleAttributeBaseType2168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_ruleAttributeBaseType2187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_ruleAttributeBaseType2206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_ruleAttributeBaseType2225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_entryRuleXOseeEnumType2298 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumType2308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_ruleXOseeEnumType2345 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumType2362 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXOseeEnumType2379 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleXOseeEnumType2392 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumType2409 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleXOseeEnumType2428 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_HEX_INT_in_ruleXOseeEnumType2445 = new BitSet(new long[]{0x0010000000400000L});
    public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_ruleXOseeEnumType2471 = new BitSet(new long[]{0x0010000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXOseeEnumType2484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_entryRuleXOseeEnumEntry2520 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumEntry2530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_ruleXOseeEnumEntry2567 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2584 = new BitSet(new long[]{0x0020001000000082L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXOseeEnumEntry2606 = new BitSet(new long[]{0x0020001000000002L});
    public static final BitSet FOLLOW_53_in_ruleXOseeEnumEntry2625 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2642 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_ruleXOseeEnumEntry2662 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_entryRuleXOseeEnumOverride2722 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumOverride2732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_ruleXOseeEnumOverride2769 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumOverride2789 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXOseeEnumOverride2801 = new BitSet(new long[]{0x0380000000400000L});
    public static final BitSet FOLLOW_55_in_ruleXOseeEnumOverride2819 = new BitSet(new long[]{0x0300000000400000L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_ruleXOseeEnumOverride2854 = new BitSet(new long[]{0x0300000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXOseeEnumOverride2867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption2903 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOverrideOption2913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_ruleOverrideOption2960 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_ruleOverrideOption2987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_entryRuleAddEnum3022 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAddEnum3032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_ruleAddEnum3069 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAddEnum3086 = new BitSet(new long[]{0x0020001000000082L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum3108 = new BitSet(new long[]{0x0020001000000002L});
    public static final BitSet FOLLOW_53_in_ruleAddEnum3127 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAddEnum3144 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_ruleAddEnum3164 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAddEnum3181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum3224 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRemoveEnum3234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_ruleRemoveEnum3271 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRemoveEnum3291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeArtifactTypeOverride_in_entryRuleXOseeArtifactTypeOverride3327 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeArtifactTypeOverride3337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_ruleXOseeArtifactTypeOverride3374 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeArtifactTypeOverride3394 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXOseeArtifactTypeOverride3406 = new BitSet(new long[]{0x0B80000000000000L});
    public static final BitSet FOLLOW_55_in_ruleXOseeArtifactTypeOverride3424 = new BitSet(new long[]{0x0B80000000000000L});
    public static final BitSet FOLLOW_ruleAttributeOverrideOption_in_ruleXOseeArtifactTypeOverride3459 = new BitSet(new long[]{0x0B80000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXOseeArtifactTypeOverride3472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeOverrideOption_in_entryRuleAttributeOverrideOption3508 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeOverrideOption3518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddAttribute_in_ruleAttributeOverrideOption3565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveAttribute_in_ruleAttributeOverrideOption3592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUpdateAttribute_in_ruleAttributeOverrideOption3619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddAttribute_in_entryRuleAddAttribute3654 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAddAttribute3664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_ruleAddAttribute3701 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_ruleAddAttribute3722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveAttribute_in_entryRuleRemoveAttribute3758 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRemoveAttribute3768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_ruleRemoveAttribute3805 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_ruleRemoveAttribute3817 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRemoveAttribute3837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUpdateAttribute_in_entryRuleUpdateAttribute3873 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUpdateAttribute3883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_ruleUpdateAttribute3920 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_ruleUpdateAttribute3941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRelationType_in_entryRuleXRelationType3977 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXRelationType3987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_ruleXRelationType4024 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType4041 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXRelationType4058 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleXRelationType4071 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType4088 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleXRelationType4107 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_HEX_INT_in_ruleXRelationType4124 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_61_in_ruleXRelationType4141 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType4158 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_62_in_ruleXRelationType4175 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType4195 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_ruleXRelationType4207 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType4224 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_ruleXRelationType4241 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType4261 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleXRelationType4273 = new BitSet(new long[]{0x0000000000000020L,0x0000000000000038L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_ruleXRelationType4294 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_ruleXRelationType4306 = new BitSet(new long[]{0x0000000000000000L,0x00000000001E0000L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_ruleXRelationType4327 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXRelationType4339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType4376 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationOrderType4387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_ruleRelationOrderType4425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_ruleRelationOrderType4444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_ruleRelationOrderType4463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleRelationOrderType4484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCondition_in_entryRuleCondition4531 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCondition4541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSimpleCondition_in_ruleCondition4588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCompoundCondition_in_ruleCondition4615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSimpleCondition_in_entryRuleSimpleCondition4650 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSimpleCondition4660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMatchField_in_ruleSimpleCondition4706 = new BitSet(new long[]{0x0000000000000000L,0x0000000000600000L});
    public static final BitSet FOLLOW_ruleCompareOp_in_ruleSimpleCondition4727 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleSimpleCondition4744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCompoundCondition_in_entryRuleCompoundCondition4785 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCompoundCondition4795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_ruleCompoundCondition4832 = new BitSet(new long[]{0x0000000001000000L,0x000000000E000000L});
    public static final BitSet FOLLOW_ruleSimpleCondition_in_ruleCompoundCondition4853 = new BitSet(new long[]{0x0000000000000000L,0x0000000001800000L});
    public static final BitSet FOLLOW_ruleXLogicOperator_in_ruleCompoundCondition4875 = new BitSet(new long[]{0x0000000001000000L,0x000000000E000000L});
    public static final BitSet FOLLOW_ruleSimpleCondition_in_ruleCompoundCondition4896 = new BitSet(new long[]{0x0000000000000000L,0x0000000001800080L});
    public static final BitSet FOLLOW_71_in_ruleCompoundCondition4910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXArtifactMatcher_in_entryRuleXArtifactMatcher4946 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXArtifactMatcher4956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_ruleXArtifactMatcher4993 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactMatcher5010 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_ruleXArtifactMatcher5027 = new BitSet(new long[]{0x0000000001000000L,0x000000000E000040L});
    public static final BitSet FOLLOW_ruleCondition_in_ruleXArtifactMatcher5048 = new BitSet(new long[]{0x0000000000000000L,0x0000000001800400L});
    public static final BitSet FOLLOW_ruleXLogicOperator_in_ruleXArtifactMatcher5070 = new BitSet(new long[]{0x0000000001000000L,0x000000000E000040L});
    public static final BitSet FOLLOW_ruleCondition_in_ruleXArtifactMatcher5091 = new BitSet(new long[]{0x0000000000000000L,0x0000000001800400L});
    public static final BitSet FOLLOW_74_in_ruleXArtifactMatcher5105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRole_in_entryRuleRole5141 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRole5151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_ruleRole5188 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRole5205 = new BitSet(new long[]{0x00000000000A0000L});
    public static final BitSet FOLLOW_17_in_ruleRole5223 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRole5243 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleRole5257 = new BitSet(new long[]{0x0000000000100000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ruleUsersAndGroups_in_ruleRole5279 = new BitSet(new long[]{0x0000000000500000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ruleReferencedContext_in_ruleRole5306 = new BitSet(new long[]{0x0000000000500000L,0x0000000000001000L});
    public static final BitSet FOLLOW_22_in_ruleRole5320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleReferencedContext_in_entryRuleReferencedContext5356 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleReferencedContext5366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_ruleReferencedContext5403 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleReferencedContext5420 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_ruleReferencedContext5437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUsersAndGroups_in_entryRuleUsersAndGroups5473 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUsersAndGroups5483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_ruleUsersAndGroups5520 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUsersAndGroups5537 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_ruleUsersAndGroups5554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessContext_in_entryRuleAccessContext5590 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAccessContext5600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_ruleAccessContext5637 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAccessContext5654 = new BitSet(new long[]{0x00000000000A0000L});
    public static final BitSet FOLLOW_17_in_ruleAccessContext5672 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAccessContext5692 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleAccessContext5706 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleAccessContext5718 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAccessContext5735 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_ruleAccessContext5752 = new BitSet(new long[]{0x0000000000000000L,0x0000000030002000L});
    public static final BitSet FOLLOW_ruleObjectRestriction_in_ruleAccessContext5774 = new BitSet(new long[]{0x0000000000400000L,0x0000000030002000L});
    public static final BitSet FOLLOW_ruleHierarchyRestriction_in_ruleAccessContext5801 = new BitSet(new long[]{0x0000000000400000L,0x0000000030002000L});
    public static final BitSet FOLLOW_22_in_ruleAccessContext5815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleHierarchyRestriction_in_entryRuleHierarchyRestriction5851 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleHierarchyRestriction5861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_ruleHierarchyRestriction5898 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleHierarchyRestriction5918 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleHierarchyRestriction5930 = new BitSet(new long[]{0x0000000000000000L,0x0000000030000000L});
    public static final BitSet FOLLOW_ruleObjectRestriction_in_ruleHierarchyRestriction5951 = new BitSet(new long[]{0x0000000000400000L,0x0000000030000000L});
    public static final BitSet FOLLOW_22_in_ruleHierarchyRestriction5964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeArtifactTypePredicate_in_entryRuleRelationTypeArtifactTypePredicate6000 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationTypeArtifactTypePredicate6010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_ruleRelationTypeArtifactTypePredicate6047 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationTypeArtifactTypePredicate6067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeArtifactPredicate_in_entryRuleRelationTypeArtifactPredicate6103 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationTypeArtifactPredicate6113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_ruleRelationTypeArtifactPredicate6150 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationTypeArtifactPredicate6170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypePredicate_in_entryRuleRelationTypePredicate6206 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationTypePredicate6216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeArtifactPredicate_in_ruleRelationTypePredicate6263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeArtifactTypePredicate_in_ruleRelationTypePredicate6290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleObjectRestriction_in_entryRuleObjectRestriction6325 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleObjectRestriction6335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactMatchRestriction_in_ruleObjectRestriction6382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactTypeRestriction_in_ruleObjectRestriction6409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeRestriction_in_ruleObjectRestriction6436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRestriction_in_ruleObjectRestriction6463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactMatchRestriction_in_entryRuleArtifactMatchRestriction6498 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactMatchRestriction6508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactMatchRestriction6554 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_ruleArtifactMatchRestriction6566 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_78_in_ruleArtifactMatchRestriction6578 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleArtifactMatchRestriction6598 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_ruleArtifactMatchRestriction6610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactTypeRestriction_in_entryRuleArtifactTypeRestriction6646 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactTypeRestriction6656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactTypeRestriction6702 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_ruleArtifactTypeRestriction6714 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleArtifactTypeRestriction6726 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleArtifactTypeRestriction6746 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_ruleArtifactTypeRestriction6758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRestriction_in_entryRuleAttributeTypeRestriction6794 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeTypeRestriction6804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleAttributeTypeRestriction6850 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_ruleAttributeTypeRestriction6862 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_ruleAttributeTypeRestriction6874 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeTypeRestriction6894 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010400L});
    public static final BitSet FOLLOW_80_in_ruleAttributeTypeRestriction6907 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleAttributeTypeRestriction6919 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeTypeRestriction6939 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_ruleAttributeTypeRestriction6953 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeRestriction_in_entryRuleRelationTypeRestriction6991 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationTypeRestriction7001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleRelationTypeRestriction7047 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_ruleRelationTypeRestriction7059 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_60_in_ruleRelationTypeRestriction7071 = new BitSet(new long[]{0x0000000000000010L,0x0000000040000000L});
    public static final BitSet FOLLOW_ruleRelationTypeMatch_in_ruleRelationTypeRestriction7093 = new BitSet(new long[]{0x0000000000000000L,0x0000000380000000L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationTypeRestriction7119 = new BitSet(new long[]{0x0000000000000000L,0x0000000380000000L});
    public static final BitSet FOLLOW_ruleXRelationSideEnum_in_ruleRelationTypeRestriction7141 = new BitSet(new long[]{0x0000000000010000L,0x0000000000004400L});
    public static final BitSet FOLLOW_ruleRelationTypePredicate_in_ruleRelationTypeRestriction7162 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_ruleRelationTypeRestriction7175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_ruleRelationMultiplicityEnum7225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_ruleRelationMultiplicityEnum7242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_ruleRelationMultiplicityEnum7259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_ruleRelationMultiplicityEnum7276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_ruleCompareOp7321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_ruleCompareOp7338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_ruleXLogicOperator7383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_88_in_ruleXLogicOperator7400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_ruleMatchField7445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_ruleMatchField7462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_ruleMatchField7479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_ruleMatchField7496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_ruleAccessPermissionEnum7541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_ruleAccessPermissionEnum7558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_ruleRelationTypeMatch7602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_ruleXRelationSideEnum7646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_ruleXRelationSideEnum7663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_97_in_ruleXRelationSideEnum7680 = new BitSet(new long[]{0x0000000000000002L});

}