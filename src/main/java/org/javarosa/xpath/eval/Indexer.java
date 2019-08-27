package org.javarosa.xpath.eval;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.xpath.expr.XPathEqExpr;
import org.javarosa.xpath.expr.XPathPathExpr;
import org.javarosa.xpath.expr.XPathQName;
import org.javarosa.xpath.expr.XPathStep;
import org.javarosa.xpath.expr.XPathStringLiteral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @johnthebeloved
 *
 * Represents an indexed xpath expression which could be used to make expression evaluation faster
 * since the pre-evaluated expressions are stored in this index
 *
 * Used for pre-evaluating and indexing the pre-evaluated expression patterns,
 * so that results of expression evaluation during initialization and loading and filling of form can be
 * fetched from this index instead of
 *
 * Currently works for
 * ItemsetBinding.nodeset(nodeset attribute of <strong>itemsets</strong>) and
 * Recalculate(calculate attribute of <strong>bind</strong>)
 *
 */
public class Indexer {

    public TreeReference expressionRef; //The genericised expression to be indexed - used as the key
    public TreeReference resultRef;  //The genericised pattern of the result to be cached
    public PredicateStep[] predicateSteps; //The predicates applied to the expression
    public IndexerType indexerType; // Used to determine how expression would be indexed
    public Map<TreeReference, List<TreeReference>> nodesetExprDict; // Map  used if result is a list of collated nodeset refs
    public Map<TreeReference, IAnswerData> rawValueExprDict; // Used if indexed refs are single Answers

   //Used to keep keys/values before values/keys are reached
    private Map<TreeReference, TreeReference> tempKeyKepper = new HashMap();
    private Map<TreeReference, IAnswerData> tempValueKepper = new HashMap();

    public Indexer(IndexerType indexType, TreeReference expressionRef, TreeReference resultRef) {
        this(indexType, expressionRef, resultRef, null);
    }

    public Indexer(IndexerType indexType, TreeReference expressionRef, TreeReference resultRef, PredicateStep[] predicateSteps) {
        this.expressionRef = expressionRef.removePredicates().genericize();
        this.resultRef = resultRef.removePredicates().genericize();
        this.indexerType = indexType;
        this.predicateSteps = predicateSteps == null ? new PredicateStep[0] : predicateSteps;
        nodesetExprDict = new HashMap<>();
        rawValueExprDict = new HashMap<>();
    }


    public void addToIndex(TreeReference currentTreeReference, TreeElement currentTreeElement) {
        if (indexerType == IndexerType.GENERIC_PATH) {
            if (nodesetExprDict.get(expressionRef) == null) {
                nodesetExprDict.put(expressionRef, new ArrayList<>());
            }
            List<TreeReference> matches = nodesetExprDict.get(expressionRef);
            //TODO: equate with resultRef here instead of removing last, but this is correct since it's last - see trimToLevel
            matches.add(currentTreeReference);
        } else if (indexerType == IndexerType.LAST_EQUAL_PREDICATE_PATH) {
            if (currentTreeReference.genericize().removePredicates().equals(expressionRef)) {

                TreeReference currentReferenceClone = currentTreeReference.clone();
                TreeReference expressionRefIndex = withPredicates(currentReferenceClone, currentTreeElement.getValue().getDisplayText());

                TreeReference valueRef = currentTreeReference.getParentRef();
                if (nodesetExprDict.get(expressionRefIndex) == null) {
                    nodesetExprDict.put(expressionRefIndex, new ArrayList<>());
                }
                List<TreeReference> matches = nodesetExprDict.get(expressionRefIndex);
                //TODO: equate with resultRef here instead of removing last, but this is correct since it's last - see trimToLevel
                matches.add(valueRef);
            }


        } else if (indexerType == IndexerType.SINGLE_MID_EQUAL_PREDICATE_PATH) {
            if (currentTreeReference.genericize().removePredicates().equals(expressionRef)) {
                TreeReference currentReferenceClone = currentTreeReference.clone();
                TreeReference indexKey = withPredicates(currentReferenceClone, currentTreeElement.getValue() != null ? currentTreeElement.getValue().getDisplayText() : null);
                IAnswerData valueRef = tempValueKepper.get(currentTreeReference.getParentRef());
                boolean valueRefFound = valueRef != null;
                if (valueRefFound) {
                    if (nodesetExprDict.get(indexKey) == null) {
                        nodesetExprDict.put(indexKey, new ArrayList<>());
                    }
                    rawValueExprDict.put(indexKey, valueRef);
                } else {
                    //Put the common parent as the key
                    tempKeyKepper.put(currentTreeReference.getParentRef(), indexKey);
                }
            } else if(currentTreeReference.genericize().removePredicates().equals(resultRef)){
                TreeReference keyRef = tempKeyKepper.get(currentTreeReference.getParentRef());
                boolean keyRefFound = keyRef != null && keyRef.genericize().removePredicates().equals(expressionRef);
                if (keyRefFound ) {
                    rawValueExprDict.put(keyRef, currentTreeElement.getValue());
                }else{
                    tempValueKepper.put(currentTreeReference.getParentRef(), currentTreeElement.getValue());
                }

            }
        }
    }

    public List<TreeReference> getFromIndex(TreeReference treeReference) {
        return nodesetExprDict.get(treeReference);
    }

    public IAnswerData getRawValueFromIndex(TreeReference treeReference) {
        return rawValueExprDict.get(treeReference);
    }

    public boolean belong(TreeReference currentTreeReference) {
        if(!currentTreeReference.getInstanceName().equals(expressionRef.getInstanceName())){
            return  false;
        }
        String treeRefString = currentTreeReference.toString(false);
        if (indexerType.equals(IndexerType.GENERIC_PATH) ||
            indexerType.equals(IndexerType
                .LAST_EQUAL_PREDICATE_PATH)
        ) {
            return treeRefString.equals(expressionRef.toString(false));
        }else if (indexerType.equals(IndexerType.SINGLE_MID_EQUAL_PREDICATE_PATH)) {
            return treeRefString.equals(expressionRef.toString(false)) ||
                treeRefString.equals(resultRef.toString(false));
        }
        return false;
    }

    TreeReference withPredicates(TreeReference refToIndex, String value) {
        if(value == null){ return null; }
        if (indexerType == IndexerType.GENERIC_PATH) {
            return expressionRef;
        } else if (indexerType == IndexerType.LAST_EQUAL_PREDICATE_PATH) {

            PredicateStep predicateStep = predicateSteps[0];
            TreeReference genericizedRefToIndex = refToIndex.genericize();
            String refLastLevel = ((XPathPathExpr) ((XPathEqExpr) predicateStep.predicate).a).getReference().getNameLast();

            if (expressionRef.equals(genericizedRefToIndex)) {
                XPathStep[] xPathSteps = new XPathStep[]{new XPathStep(XPathStep.AXIS_CHILD, new XPathQName(refLastLevel))};
                XPathPathExpr a = new XPathPathExpr(XPathPathExpr.INIT_CONTEXT_RELATIVE, xPathSteps);
                XPathStringLiteral b = new XPathStringLiteral(value);
                XPathEqExpr xPathEqExpr = new XPathEqExpr(true, a, b);
                genericizedRefToIndex.addPredicate(predicateStep.stepIndex, Arrays.asList(xPathEqExpr));
                genericizedRefToIndex.removeLastLevel();
                return genericizedRefToIndex;
            }

        } else if (indexerType == IndexerType.SINGLE_MID_EQUAL_PREDICATE_PATH) {

            PredicateStep predicateStep = predicateSteps[0];
            TreeReference genericizedRefToIndex = refToIndex.genericize();
            String refLastLevel = ((XPathPathExpr) ((XPathEqExpr) predicateStep.predicate).a).getReference().getNameLast();

            if (expressionRef.equals(genericizedRefToIndex)) {
                XPathStep[] xPathSteps = new XPathStep[]{new XPathStep(XPathStep.AXIS_CHILD, new XPathQName(refLastLevel))};
                XPathPathExpr a = new XPathPathExpr(XPathPathExpr.INIT_CONTEXT_RELATIVE, xPathSteps);
                XPathStringLiteral b = new XPathStringLiteral(value);
                XPathEqExpr xPathEqExpr = new XPathEqExpr(true, a, b);
                genericizedRefToIndex.addPredicate(predicateStep.stepIndex, Arrays.asList(xPathEqExpr));
                genericizedRefToIndex.removeLastLevel();
                genericizedRefToIndex.add("label", -1);
                return genericizedRefToIndex;
            }
        }
        return null;
    }

    public void clearCaches(){
        tempValueKepper.clear();
        tempKeyKepper.clear();
    }


}
