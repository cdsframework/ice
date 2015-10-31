
package org.omg.dss;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import org.omg.dss.common.DescribedDO;
import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.InteractionIdentifier;
import org.omg.dss.common.ItemIdentifier;
import org.omg.dss.common.ScopedDO;
import org.omg.dss.common.ScopingEntity;
import org.omg.dss.common.SemanticPayload;
import org.omg.dss.common.ServiceRequestBase;
import org.omg.dss.evaluation.Evaluate;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTime;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTimeResponse;
import org.omg.dss.evaluation.EvaluateIteratively;
import org.omg.dss.evaluation.EvaluateIterativelyAtSpecifiedTime;
import org.omg.dss.evaluation.EvaluateIterativelyAtSpecifiedTimeResponse;
import org.omg.dss.evaluation.EvaluateIterativelyResponse;
import org.omg.dss.evaluation.EvaluateResponse;
import org.omg.dss.evaluation.requestresponse.DataRequirementItemData;
import org.omg.dss.evaluation.requestresponse.EvaluationRequest;
import org.omg.dss.evaluation.requestresponse.EvaluationRequestBase;
import org.omg.dss.evaluation.requestresponse.EvaluationResponse;
import org.omg.dss.evaluation.requestresponse.EvaluationResponseBase;
import org.omg.dss.evaluation.requestresponse.FinalKMEvaluationResponse;
import org.omg.dss.evaluation.requestresponse.IntermediateKMEvaluationResponse;
import org.omg.dss.evaluation.requestresponse.IntermediateState;
import org.omg.dss.evaluation.requestresponse.IterativeEvaluationRequest;
import org.omg.dss.evaluation.requestresponse.IterativeEvaluationResponse;
import org.omg.dss.evaluation.requestresponse.IterativeKMEvaluationRequest;
import org.omg.dss.evaluation.requestresponse.IterativeKMEvaluationResponse;
import org.omg.dss.evaluation.requestresponse.KMEvaluationRequest;
import org.omg.dss.evaluation.requestresponse.KMEvaluationRequestBase;
import org.omg.dss.evaluation.requestresponse.KMEvaluationResponse;
import org.omg.dss.evaluation.requestresponse.KMEvaluationResultData;
import org.omg.dss.evaluation.requestresponse.Warning;
import org.omg.dss.exception.DSSException;
import org.omg.dss.exception.DSSRuntimeException;
import org.omg.dss.exception.EvaluationException;
import org.omg.dss.exception.InvalidDataFormatException;
import org.omg.dss.exception.InvalidDriDataFormatException;
import org.omg.dss.exception.InvalidTimeZoneOffsetException;
import org.omg.dss.exception.InvalidTraitCriterionDataFormatException;
import org.omg.dss.exception.RequiredDataNotProvidedException;
import org.omg.dss.exception.UnrecognizedLanguageException;
import org.omg.dss.exception.UnrecognizedScopedEntityException;
import org.omg.dss.exception.UnrecognizedScopingEntityException;
import org.omg.dss.exception.UnrecognizedTraitCriterionException;
import org.omg.dss.exception.UnsupportedLanguageException;
import org.omg.dss.knowledgemodule.CPQPInUse;
import org.omg.dss.knowledgemodule.DataRequirementBase;
import org.omg.dss.knowledgemodule.ExtendedKMDescription;
import org.omg.dss.knowledgemodule.InformationModelAlternative;
import org.omg.dss.knowledgemodule.KMConsumerProvidedQueryParameter;
import org.omg.dss.knowledgemodule.KMDataRequirementGroup;
import org.omg.dss.knowledgemodule.KMDataRequirementItem;
import org.omg.dss.knowledgemodule.KMDataRequirements;
import org.omg.dss.knowledgemodule.KMDescription;
import org.omg.dss.knowledgemodule.KMDescriptionBase;
import org.omg.dss.knowledgemodule.KMEvaluationResultSemantics;
import org.omg.dss.knowledgemodule.KMItem;
import org.omg.dss.knowledgemodule.KMLocalizedTraitValue;
import org.omg.dss.knowledgemodule.KMTraitValue;
import org.omg.dss.metadata.DescribeProfile;
import org.omg.dss.metadata.DescribeProfileResponse;
import org.omg.dss.metadata.DescribeScopingEntity;
import org.omg.dss.metadata.DescribeScopingEntityHierarchy;
import org.omg.dss.metadata.DescribeScopingEntityHierarchyResponse;
import org.omg.dss.metadata.DescribeScopingEntityResponse;
import org.omg.dss.metadata.DescribeSemanticRequirement;
import org.omg.dss.metadata.DescribeSemanticRequirementResponse;
import org.omg.dss.metadata.DescribeSemanticSignifier;
import org.omg.dss.metadata.DescribeSemanticSignifierResponse;
import org.omg.dss.metadata.DescribeTrait;
import org.omg.dss.metadata.DescribeTraitResponse;
import org.omg.dss.metadata.ListProfiles;
import org.omg.dss.metadata.ListProfilesResponse;
import org.omg.dss.metadata.profile.ConformanceProfile;
import org.omg.dss.metadata.profile.FunctionalProfile;
import org.omg.dss.metadata.profile.ProfilesByType;
import org.omg.dss.metadata.profile.ProfilesOfType;
import org.omg.dss.metadata.profile.SemanticProfile;
import org.omg.dss.metadata.profile.ServiceProfile;
import org.omg.dss.metadata.semanticrequirement.AllowedDataRequirement;
import org.omg.dss.metadata.semanticrequirement.InformationModelRequirement;
import org.omg.dss.metadata.semanticrequirement.LanguageSupportRequirement;
import org.omg.dss.metadata.semanticrequirement.OtherSemanticRequirement;
import org.omg.dss.metadata.semanticrequirement.RequiredDataRequirement;
import org.omg.dss.metadata.semanticrequirement.SemanticRequirement;
import org.omg.dss.metadata.semanticrequirement.TraitRequirement;
import org.omg.dss.metadata.semanticrequirement.TraitSetRequirement;
import org.omg.dss.metadata.semanticsignifier.ComputableDefinition;
import org.omg.dss.metadata.semanticsignifier.SemanticSignifier;
import org.omg.dss.metadata.semanticsignifier.XSDComputableDefinition;
import org.omg.dss.metadata.trait.Trait;
import org.omg.dss.metadata.trait.TraitCriterion;
import org.omg.dss.query.FindKMs;
import org.omg.dss.query.FindKMsResponse;
import org.omg.dss.query.GetKMDataRequirements;
import org.omg.dss.query.GetKMDataRequirementsForEvaluationAtSpecifiedTime;
import org.omg.dss.query.GetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse;
import org.omg.dss.query.GetKMDataRequirementsResponse;
import org.omg.dss.query.GetKMDescription;
import org.omg.dss.query.GetKMDescriptionResponse;
import org.omg.dss.query.GetKMEvaluationResultSemantics;
import org.omg.dss.query.GetKMEvaluationResultSemanticsResponse;
import org.omg.dss.query.ListKMs;
import org.omg.dss.query.ListKMsResponse;
import org.omg.dss.query.requests.DataRequirementCriterion;
import org.omg.dss.query.requests.EvaluationResultCriterion;
import org.omg.dss.query.requests.KMCriterion;
import org.omg.dss.query.requests.KMSearchCriteria;
import org.omg.dss.query.requests.KMStatusCriterion;
import org.omg.dss.query.requests.KMTraitCriterion;
import org.omg.dss.query.requests.KMTraitCriterionValue;
import org.omg.dss.query.requests.KMTraitInclusionSpecification;
import org.omg.dss.query.requests.RelatedKMSearchCriterion;
import org.omg.dss.query.responses.KMEvaluationResultSemanticsList;
import org.omg.dss.query.responses.KMList;
import org.omg.dss.query.responses.RankedKM;
import org.omg.dss.query.responses.RankedKMList;
import org.omg.dss.query.responses.RelatedKM;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.omg.spec.cdss._201105.dss package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory { 

    private final static QName _KMDescriptionBase_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMDescriptionBase");
    private final static QName _UnrecognizedScopingEntityException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "UnrecognizedScopingEntityException");
    private final static QName _UnsupportedLanguageException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "UnsupportedLanguageException");
    private final static QName _SemanticRequirement_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "SemanticRequirement");
    private final static QName _GetKMDataRequirementsResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "getKMDataRequirementsResponse");
    private final static QName _KMEvaluationRequestBase_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMEvaluationRequestBase");
    private final static QName _CPQPInUse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "CPQPInUse");
    private final static QName _ListProfiles_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "listProfiles");
    private final static QName _ServiceRequestBase_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "ServiceRequestBase");
    private final static QName _IntermediateState_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "IntermediateState");
    private final static QName _KMDataRequirementGroup_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMDataRequirementGroup");
    private final static QName _ListKMsResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "listKMsResponse");
    private final static QName _KMTraitCriterion_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMTraitCriterion");
    private final static QName _RequiredDataRequirement_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "RequiredDataRequirement");
    private final static QName _IterativeKMEvaluationResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "IterativeKMEvaluationResponse");
    private final static QName _IterativeEvaluationResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "IterativeEvaluationResponse");
    private final static QName _ProfilesByType_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "ProfilesByType");
    private final static QName _SemanticPayload_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "SemanticPayload");
    private final static QName _EvaluationRequest_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "EvaluationRequest");
    private final static QName _EvaluateResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateResponse");
    private final static QName _ConformanceProfile_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "ConformanceProfile");
    private final static QName _InvalidTraitCriterionDataFormatException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "InvalidTraitCriterionDataFormatException");
    private final static QName _EvaluationResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "EvaluationResponse");
    private final static QName _KMEvaluationResultSemanticsList_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMEvaluationResultSemanticsList");
    private final static QName _EvaluateIterativelyResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateIterativelyResponse");
    private final static QName _TraitRequirement_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "TraitRequirement");
    private final static QName _EvaluateIterativelyAtSpecifiedTime_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateIterativelyAtSpecifiedTime");
    private final static QName _DescribeScopingEntityHierarchy_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "describeScopingEntityHierarchy");
    private final static QName _EvaluationResponseBase_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "EvaluationResponseBase");
    private final static QName _GetKMDescription_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "getKMDescription");
    private final static QName _DescribedDO_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "DescribedDO");
    private final static QName _KMDataRequirementItem_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMDataRequirementItem");
    private final static QName _ListKMs_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "listKMs");
    private final static QName _EvaluateIteratively_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateIteratively");
    private final static QName _DescribeSemanticSignifierResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "describeSemanticSignifierResponse");
    private final static QName _RelatedKM_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "RelatedKM");
    private final static QName _DescribeTrait_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "describeTrait");
    private final static QName _KMDataRequirements_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMDataRequirements");
    private final static QName _KMItem_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMItem");
    private final static QName _KMCriterion_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMCriterion");
    private final static QName _EvaluateIterativelyAtSpecifiedTimeResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateIterativelyAtSpecifiedTimeResponse");
    private final static QName _RankedKM_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "RankedKM");
    private final static QName _ExtendedKMDescription_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "ExtendedKMDescription");
    private final static QName _KMTraitCriterionValue_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMTraitCriterionValue");
    private final static QName _ServiceProfile_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "ServiceProfile");
    private final static QName _Trait_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "Trait");
    private final static QName _KMDescription_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMDescription");
    private final static QName _LanguageSupportRequirement_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "LanguageSupportRequirement");
    private final static QName _EvaluationResultCriterion_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "EvaluationResultCriterion");
    private final static QName _ScopingEntity_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "ScopingEntity");
    private final static QName _IterativeEvaluationRequest_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "IterativeEvaluationRequest");
    private final static QName _GetKMEvaluationResultSemanticsResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "getKMEvaluationResultSemanticsResponse");
    private final static QName _AllowedDataRequirement_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "AllowedDataRequirement");
    private final static QName _DescribeTraitResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "describeTraitResponse");
    private final static QName _DescribeProfileResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "describeProfileResponse");
    private final static QName _UnrecognizedTraitCriterionException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "UnrecognizedTraitCriterionException");
    private final static QName _EvaluationRequestBase_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "EvaluationRequestBase");
    private final static QName _ComputableDefinition_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "ComputableDefinition");
    private final static QName _ItemIdentifier_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "ItemIdentifier");
    private final static QName _EntityIdentifier_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "EntityIdentifier");
    private final static QName _KMTraitInclusionSpecification_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMTraitInclusionSpecification");
    private final static QName _GetKMEvaluationResultSemantics_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "getKMEvaluationResultSemantics");
    private final static QName _IntermediateKMEvaluationResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "IntermediateKMEvaluationResponse");
    private final static QName _Evaluate_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluate");
    private final static QName _ListProfilesResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "listProfilesResponse");
    private final static QName _FunctionalProfile_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "FunctionalProfile");
    private final static QName _DataRequirementItemData_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "DataRequirementItemData");
    private final static QName _KMTraitValue_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMTraitValue");
    private final static QName _DescribeSemanticRequirementResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "describeSemanticRequirementResponse");
    private final static QName _DescribeSemanticSignifier_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "describeSemanticSignifier");
    private final static QName _InvalidDataFormatException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "InvalidDataFormatException");
    private final static QName _RankedKMList_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "RankedKMList");
    private final static QName _Warning_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "Warning");
    private final static QName _FinalKMEvaluationResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "FinalKMEvaluationResponse");
    private final static QName _DataRequirementCriterion_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "DataRequirementCriterion");
    private final static QName _InvalidTimeZoneOffsetException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "InvalidTimeZoneOffsetException");
    private final static QName _KMEvaluationResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMEvaluationResponse");
    private final static QName _DescribeSemanticRequirement_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "describeSemanticRequirement");
    private final static QName _EvaluateAtSpecifiedTimeResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateAtSpecifiedTimeResponse");
    private final static QName _XSDComputableDefinition_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "XSDComputableDefinition");
    private final static QName _KMStatusCriterion_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMStatusCriterion");
    private final static QName _DataRequirementBase_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "DataRequirementBase");
    private final static QName _KMList_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMList");
    private final static QName _GetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "getKMDataRequirementsForEvaluationAtSpecifiedTimeResponse");
    private final static QName _KMEvaluationRequest_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMEvaluationRequest");
    private final static QName _UnrecognizedLanguageException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "UnrecognizedLanguageException");
    private final static QName _RelatedKMSearchCriterion_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "RelatedKMSearchCriterion");
    private final static QName _KMSearchCriteria_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMSearchCriteria");
    private final static QName _TraitSetRequirement_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "TraitSetRequirement");
    private final static QName _SemanticProfile_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "SemanticProfile");
    private final static QName _KMEvaluationResultSemantics_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMEvaluationResultSemantics");
    private final static QName _InteractionIdentifier_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "InteractionIdentifier");
    private final static QName _GetKMDescriptionResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "getKMDescriptionResponse");
    private final static QName _DescribeScopingEntity_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "describeScopingEntity");
    private final static QName _DSSException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "DSSException");
    private final static QName _KMLocalizedTraitValue_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMLocalizedTraitValue");
    private final static QName _EvaluationException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "EvaluationException");
    private final static QName _InformationModelAlternative_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "InformationModelAlternative");
    private final static QName _DescribeScopingEntityResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "describeScopingEntityResponse");
    private final static QName _GetKMDataRequirements_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "getKMDataRequirements");
    private final static QName _DSSRuntimeException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "DSSRuntimeException");
    private final static QName _FindKMs_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "findKMs");
    private final static QName _GetKMDataRequirementsForEvaluationAtSpecifiedTime_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "getKMDataRequirementsForEvaluationAtSpecifiedTime");
    private final static QName _FindKMsResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "findKMsResponse");
    private final static QName _RequiredDataNotProvidedException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "RequiredDataNotProvidedException");
    private final static QName _InvalidDriDataFormatException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "InvalidDriDataFormatException");
    private final static QName _UnrecognizedScopedEntityException_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "UnrecognizedScopedEntityException");
    private final static QName _DescribeScopingEntityHierarchyResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "describeScopingEntityHierarchyResponse");
    private final static QName _OtherSemanticRequirement_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "OtherSemanticRequirement");
    private final static QName _DescribeProfile_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "describeProfile");
    private final static QName _EvaluateAtSpecifiedTime_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateAtSpecifiedTime");
    private final static QName _KMConsumerProvidedQueryParameter_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMConsumerProvidedQueryParameter");
    private final static QName _ScopedDO_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "ScopedDO");
    private final static QName _InformationModelRequirement_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "InformationModelRequirement");
    private final static QName _KMEvaluationResultData_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "KMEvaluationResultData");
    private final static QName _TraitCriterion_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "TraitCriterion");
    private final static QName _ProfilesOfType_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "ProfilesOfType");
    private final static QName _SemanticSignifier_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "SemanticSignifier");
    private final static QName _IterativeKMEvaluationRequest_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "IterativeKMEvaluationRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.omg.spec.cdss._201105.dss
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DescribeScopingEntityHierarchy }
     * 
     */
    public DescribeScopingEntityHierarchy createDescribeScopingEntityHierarchy() {
        return new DescribeScopingEntityHierarchy();
    }

    /**
     * Create an instance of {@link RequiredDataNotProvidedException }
     * 
     */
    public RequiredDataNotProvidedException createRequiredDataNotProvidedException() {
        return new RequiredDataNotProvidedException();
    }

    /**
     * Create an instance of {@link DataRequirementItemData }
     * 
     */
    public DataRequirementItemData createDataRequirementItemData() {
        return new DataRequirementItemData();
    }

    /**
     * Create an instance of {@link InvalidDataFormatException }
     * 
     */
    public InvalidDataFormatException createInvalidDataFormatException() {
        return new InvalidDataFormatException();
    }

    /**
     * Create an instance of {@link UnrecognizedTraitCriterionException }
     * 
     */
    public UnrecognizedTraitCriterionException createUnrecognizedTraitCriterionException() {
        return new UnrecognizedTraitCriterionException();
    }

    /**
     * Create an instance of {@link KMEvaluationRequest }
     * 
     */
    public KMEvaluationRequest createKMEvaluationRequest() {
        return new KMEvaluationRequest();
    }

    /**
     * Create an instance of {@link AllowedDataRequirement }
     * 
     */
    public AllowedDataRequirement createAllowedDataRequirement() {
        return new AllowedDataRequirement();
    }

    /**
     * Create an instance of {@link RelatedKMSearchCriterion }
     * 
     */
    public RelatedKMSearchCriterion createRelatedKMSearchCriterion() {
        return new RelatedKMSearchCriterion();
    }

    /**
     * Create an instance of {@link TraitSetRequirement }
     * 
     */
    public TraitSetRequirement createTraitSetRequirement() {
        return new TraitSetRequirement();
    }

    /**
     * Create an instance of {@link GetKMDescription }
     * 
     */
    public GetKMDescription createGetKMDescription() {
        return new GetKMDescription();
    }

    /**
     * Create an instance of {@link KMDataRequirementGroup }
     * 
     */
    public KMDataRequirementGroup createKMDataRequirementGroup() {
        return new KMDataRequirementGroup();
    }

    /**
     * Create an instance of {@link UnrecognizedScopingEntityException }
     * 
     */
    public UnrecognizedScopingEntityException createUnrecognizedScopingEntityException() {
        return new UnrecognizedScopingEntityException();
    }

    /**
     * Create an instance of {@link ProfilesByType }
     * 
     */
    public ProfilesByType createProfilesByType() {
        return new ProfilesByType();
    }

    /**
     * Create an instance of {@link GetKMDataRequirementsForEvaluationAtSpecifiedTime }
     * 
     */
    public GetKMDataRequirementsForEvaluationAtSpecifiedTime createGetKMDataRequirementsForEvaluationAtSpecifiedTime() {
        return new GetKMDataRequirementsForEvaluationAtSpecifiedTime();
    }

    /**
     * Create an instance of {@link KMEvaluationResultSemantics }
     * 
     */
    public KMEvaluationResultSemantics createKMEvaluationResultSemantics() {
        return new KMEvaluationResultSemantics();
    }

    /**
     * Create an instance of {@link InformationModelRequirement }
     * 
     */
    public InformationModelRequirement createInformationModelRequirement() {
        return new InformationModelRequirement();
    }

    /**
     * Create an instance of {@link KMTraitValue }
     * 
     */
    public KMTraitValue createKMTraitValue() {
        return new KMTraitValue();
    }

    /**
     * Create an instance of {@link KMTraitCriterionValue }
     * 
     */
    public KMTraitCriterionValue createKMTraitCriterionValue() {
        return new KMTraitCriterionValue();
    }

    /**
     * Create an instance of {@link ConformanceProfile }
     * 
     */
    public ConformanceProfile createConformanceProfile() {
        return new ConformanceProfile();
    }

    /**
     * Create an instance of {@link SemanticSignifier }
     * 
     */
    public SemanticSignifier createSemanticSignifier() {
        return new SemanticSignifier();
    }

    /**
     * Create an instance of {@link IterativeEvaluationResponse }
     * 
     */
    public IterativeEvaluationResponse createIterativeEvaluationResponse() {
        return new IterativeEvaluationResponse();
    }

    /**
     * Create an instance of {@link ItemIdentifier }
     * 
     */
    public ItemIdentifier createItemIdentifier() {
        return new ItemIdentifier();
    }

    /**
     * Create an instance of {@link FindKMsResponse }
     * 
     */
    public FindKMsResponse createFindKMsResponse() {
        return new FindKMsResponse();
    }

    /**
     * Create an instance of {@link SemanticPayload }
     * 
     */
    public SemanticPayload createSemanticPayload() {
        return new SemanticPayload();
    }

    /**
     * Create an instance of {@link DescribeSemanticSignifier }
     * 
     */
    public DescribeSemanticSignifier createDescribeSemanticSignifier() {
        return new DescribeSemanticSignifier();
    }

    /**
     * Create an instance of {@link KMTraitCriterion }
     * 
     */
    public KMTraitCriterion createKMTraitCriterion() {
        return new KMTraitCriterion();
    }

    /**
     * Create an instance of {@link IntermediateKMEvaluationResponse }
     * 
     */
    public IntermediateKMEvaluationResponse createIntermediateKMEvaluationResponse() {
        return new IntermediateKMEvaluationResponse();
    }

    /**
     * Create an instance of {@link UnrecognizedLanguageException }
     * 
     */
    public UnrecognizedLanguageException createUnrecognizedLanguageException() {
        return new UnrecognizedLanguageException();
    }

    /**
     * Create an instance of {@link GetKMDescriptionResponse }
     * 
     */
    public GetKMDescriptionResponse createGetKMDescriptionResponse() {
        return new GetKMDescriptionResponse();
    }

    /**
     * Create an instance of {@link EvaluateAtSpecifiedTime }
     * 
     */
    public EvaluateAtSpecifiedTime createEvaluateAtSpecifiedTime() {
        return new EvaluateAtSpecifiedTime();
    }

    /**
     * Create an instance of {@link IterativeKMEvaluationRequest }
     * 
     */
    public IterativeKMEvaluationRequest createIterativeKMEvaluationRequest() {
        return new IterativeKMEvaluationRequest();
    }

    /**
     * Create an instance of {@link InformationModelAlternative }
     * 
     */
    public InformationModelAlternative createInformationModelAlternative() {
        return new InformationModelAlternative();
    }

    /**
     * Create an instance of {@link LanguageSupportRequirement }
     * 
     */
    public LanguageSupportRequirement createLanguageSupportRequirement() {
        return new LanguageSupportRequirement();
    }

    /**
     * Create an instance of {@link RelatedKM }
     * 
     */
    public RelatedKM createRelatedKM() {
        return new RelatedKM();
    }

    /**
     * Create an instance of {@link InvalidTraitCriterionDataFormatException }
     * 
     */
    public InvalidTraitCriterionDataFormatException createInvalidTraitCriterionDataFormatException() {
        return new InvalidTraitCriterionDataFormatException();
    }

    /**
     * Create an instance of {@link ListProfiles }
     * 
     */
    public ListProfiles createListProfiles() {
        return new ListProfiles();
    }

    /**
     * Create an instance of {@link KMEvaluationResultData }
     * 
     */
    public KMEvaluationResultData createKMEvaluationResultData() {
        return new KMEvaluationResultData();
    }

    /**
     * Create an instance of {@link KMTraitInclusionSpecification }
     * 
     */
    public KMTraitInclusionSpecification createKMTraitInclusionSpecification() {
        return new KMTraitInclusionSpecification();
    }

    /**
     * Create an instance of {@link GetKMEvaluationResultSemanticsResponse }
     * 
     */
    public GetKMEvaluationResultSemanticsResponse createGetKMEvaluationResultSemanticsResponse() {
        return new GetKMEvaluationResultSemanticsResponse();
    }

    /**
     * Create an instance of {@link KMStatusCriterion }
     * 
     */
    public KMStatusCriterion createKMStatusCriterion() {
        return new KMStatusCriterion();
    }

    /**
     * Create an instance of {@link DescribeTrait }
     * 
     */
    public DescribeTrait createDescribeTrait() {
        return new DescribeTrait();
    }

    /**
     * Create an instance of {@link KMSearchCriteria }
     * 
     */
    public KMSearchCriteria createKMSearchCriteria() {
        return new KMSearchCriteria();
    }

    /**
     * Create an instance of {@link KMDataRequirements }
     * 
     */
    public KMDataRequirements createKMDataRequirements() {
        return new KMDataRequirements();
    }

    /**
     * Create an instance of {@link KMConsumerProvidedQueryParameter }
     * 
     */
    public KMConsumerProvidedQueryParameter createKMConsumerProvidedQueryParameter() {
        return new KMConsumerProvidedQueryParameter();
    }

    /**
     * Create an instance of {@link GetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse }
     * 
     */
    public GetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse createGetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse() {
        return new GetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse();
    }

    /**
     * Create an instance of {@link ServiceRequestBase }
     * 
     */
    public ServiceRequestBase createServiceRequestBase() {
        return new ServiceRequestBase();
    }

    /**
     * Create an instance of {@link SemanticProfile }
     * 
     */
    public SemanticProfile createSemanticProfile() {
        return new SemanticProfile();
    }

    /**
     * Create an instance of {@link GetKMEvaluationResultSemantics }
     * 
     */
    public GetKMEvaluationResultSemantics createGetKMEvaluationResultSemantics() {
        return new GetKMEvaluationResultSemantics();
    }

    /**
     * Create an instance of {@link FinalKMEvaluationResponse }
     * 
     */
    public FinalKMEvaluationResponse createFinalKMEvaluationResponse() {
        return new FinalKMEvaluationResponse();
    }

    /**
     * Create an instance of {@link GetKMDataRequirements }
     * 
     */
    public GetKMDataRequirements createGetKMDataRequirements() {
        return new GetKMDataRequirements();
    }

    /**
     * Create an instance of {@link IterativeEvaluationRequest }
     * 
     */
    public IterativeEvaluationRequest createIterativeEvaluationRequest() {
        return new IterativeEvaluationRequest();
    }

    /**
     * Create an instance of {@link EvaluationRequest }
     * 
     */
    public EvaluationRequest createEvaluationRequest() {
        return new EvaluationRequest();
    }

    /**
     * Create an instance of {@link ExtendedKMDescription }
     * 
     */
    public ExtendedKMDescription createExtendedKMDescription() {
        return new ExtendedKMDescription();
    }

    /**
     * Create an instance of {@link DescribeSemanticSignifierResponse }
     * 
     */
    public DescribeSemanticSignifierResponse createDescribeSemanticSignifierResponse() {
        return new DescribeSemanticSignifierResponse();
    }

    /**
     * Create an instance of {@link ListProfilesResponse }
     * 
     */
    public ListProfilesResponse createListProfilesResponse() {
        return new ListProfilesResponse();
    }

    /**
     * Create an instance of {@link KMDescription }
     * 
     */
    public KMDescription createKMDescription() {
        return new KMDescription();
    }

    /**
     * Create an instance of {@link Warning }
     * 
     */
    public Warning createWarning() {
        return new Warning();
    }

    /**
     * Create an instance of {@link EvaluateIterativelyAtSpecifiedTimeResponse }
     * 
     */
    public EvaluateIterativelyAtSpecifiedTimeResponse createEvaluateIterativelyAtSpecifiedTimeResponse() {
        return new EvaluateIterativelyAtSpecifiedTimeResponse();
    }

    /**
     * Create an instance of {@link XSDComputableDefinition }
     * 
     */
    public XSDComputableDefinition createXSDComputableDefinition() {
        return new XSDComputableDefinition();
    }

    /**
     * Create an instance of {@link EvaluateIteratively }
     * 
     */
    public EvaluateIteratively createEvaluateIteratively() {
        return new EvaluateIteratively();
    }

    /**
     * Create an instance of {@link EvaluateIterativelyAtSpecifiedTime }
     * 
     */
    public EvaluateIterativelyAtSpecifiedTime createEvaluateIterativelyAtSpecifiedTime() {
        return new EvaluateIterativelyAtSpecifiedTime();
    }

    /**
     * Create an instance of {@link EvaluationResponse }
     * 
     */
    public EvaluationResponse createEvaluationResponse() {
        return new EvaluationResponse();
    }

    /**
     * Create an instance of {@link DescribeScopingEntity }
     * 
     */
    public DescribeScopingEntity createDescribeScopingEntity() {
        return new DescribeScopingEntity();
    }

    /**
     * Create an instance of {@link RankedKM }
     * 
     */
    public RankedKM createRankedKM() {
        return new RankedKM();
    }

    /**
     * Create an instance of {@link UnrecognizedScopedEntityException }
     * 
     */
    public UnrecognizedScopedEntityException createUnrecognizedScopedEntityException() {
        return new UnrecognizedScopedEntityException();
    }

    /**
     * Create an instance of {@link OtherSemanticRequirement }
     * 
     */
    public OtherSemanticRequirement createOtherSemanticRequirement() {
        return new OtherSemanticRequirement();
    }

    /**
     * Create an instance of {@link Evaluate }
     * 
     */
    public Evaluate createEvaluate() {
        return new Evaluate();
    }

    /**
     * Create an instance of {@link ListKMsResponse }
     * 
     */
    public ListKMsResponse createListKMsResponse() {
        return new ListKMsResponse();
    }

    /**
     * Create an instance of {@link UnsupportedLanguageException }
     * 
     */
    public UnsupportedLanguageException createUnsupportedLanguageException() {
        return new UnsupportedLanguageException();
    }

    /**
     * Create an instance of {@link InvalidTimeZoneOffsetException }
     * 
     */
    public InvalidTimeZoneOffsetException createInvalidTimeZoneOffsetException() {
        return new InvalidTimeZoneOffsetException();
    }

    /**
     * Create an instance of {@link DescribeScopingEntityResponse }
     * 
     */
    public DescribeScopingEntityResponse createDescribeScopingEntityResponse() {
        return new DescribeScopingEntityResponse();
    }

    /**
     * Create an instance of {@link KMDataRequirementItem }
     * 
     */
    public KMDataRequirementItem createKMDataRequirementItem() {
        return new KMDataRequirementItem();
    }

    /**
     * Create an instance of {@link ScopingEntity }
     * 
     */
    public ScopingEntity createScopingEntity() {
        return new ScopingEntity();
    }

    /**
     * Create an instance of {@link CPQPInUse }
     * 
     */
    public CPQPInUse createCPQPInUse() {
        return new CPQPInUse();
    }

    /**
     * Create an instance of {@link FunctionalProfile }
     * 
     */
    public FunctionalProfile createFunctionalProfile() {
        return new FunctionalProfile();
    }

    /**
     * Create an instance of {@link EvaluationResultCriterion }
     * 
     */
    public EvaluationResultCriterion createEvaluationResultCriterion() {
        return new EvaluationResultCriterion();
    }

    /**
     * Create an instance of {@link InteractionIdentifier }
     * 
     */
    public InteractionIdentifier createInteractionIdentifier() {
        return new InteractionIdentifier();
    }

    /**
     * Create an instance of {@link ProfilesOfType }
     * 
     */
    public ProfilesOfType createProfilesOfType() {
        return new ProfilesOfType();
    }

    /**
     * Create an instance of {@link DataRequirementCriterion }
     * 
     */
    public DataRequirementCriterion createDataRequirementCriterion() {
        return new DataRequirementCriterion();
    }

    /**
     * Create an instance of {@link DescribeSemanticRequirement }
     * 
     */
    public DescribeSemanticRequirement createDescribeSemanticRequirement() {
        return new DescribeSemanticRequirement();
    }

    /**
     * Create an instance of {@link IterativeKMEvaluationResponse }
     * 
     */
    public IterativeKMEvaluationResponse createIterativeKMEvaluationResponse() {
        return new IterativeKMEvaluationResponse();
    }

    /**
     * Create an instance of {@link InvalidDriDataFormatException }
     * 
     */
    public InvalidDriDataFormatException createInvalidDriDataFormatException() {
        return new InvalidDriDataFormatException();
    }

    /**
     * Create an instance of {@link Trait }
     * 
     */
    public Trait createTrait() {
        return new Trait();
    }

    /**
     * Create an instance of {@link EntityIdentifier }
     * 
     */
    public EntityIdentifier createEntityIdentifier() {
        return new EntityIdentifier();
    }

    /**
     * Create an instance of {@link IntermediateState }
     * 
     */
    public IntermediateState createIntermediateState() {
        return new IntermediateState();
    }

    /**
     * Create an instance of {@link RequiredDataRequirement }
     * 
     */
    public RequiredDataRequirement createRequiredDataRequirement() {
        return new RequiredDataRequirement();
    }

    /**
     * Create an instance of {@link TraitRequirement }
     * 
     */
    public TraitRequirement createTraitRequirement() {
        return new TraitRequirement();
    }

    /**
     * Create an instance of {@link DescribeTraitResponse }
     * 
     */
    public DescribeTraitResponse createDescribeTraitResponse() {
        return new DescribeTraitResponse();
    }

    /**
     * Create an instance of {@link KMEvaluationResultSemanticsList }
     * 
     */
    public KMEvaluationResultSemanticsList createKMEvaluationResultSemanticsList() {
        return new KMEvaluationResultSemanticsList();
    }

    /**
     * Create an instance of {@link DescribeSemanticRequirementResponse }
     * 
     */
    public DescribeSemanticRequirementResponse createDescribeSemanticRequirementResponse() {
        return new DescribeSemanticRequirementResponse();
    }

    /**
     * Create an instance of {@link DescribeScopingEntityHierarchyResponse }
     * 
     */
    public DescribeScopingEntityHierarchyResponse createDescribeScopingEntityHierarchyResponse() {
        return new DescribeScopingEntityHierarchyResponse();
    }

    /**
     * Create an instance of {@link KMLocalizedTraitValue }
     * 
     */
    public KMLocalizedTraitValue createKMLocalizedTraitValue() {
        return new KMLocalizedTraitValue();
    }

    /**
     * Create an instance of {@link TraitCriterion }
     * 
     */
    public TraitCriterion createTraitCriterion() {
        return new TraitCriterion();
    }

    /**
     * Create an instance of {@link EvaluateResponse }
     * 
     */
    public EvaluateResponse createEvaluateResponse() {
        return new EvaluateResponse();
    }

    /**
     * Create an instance of {@link FindKMs }
     * 
     */
    public FindKMs createFindKMs() {
        return new FindKMs();
    }

    /**
     * Create an instance of {@link ListKMs }
     * 
     */
    public ListKMs createListKMs() {
        return new ListKMs();
    }

    /**
     * Create an instance of {@link DescribeProfileResponse }
     * 
     */
    public DescribeProfileResponse createDescribeProfileResponse() {
        return new DescribeProfileResponse();
    }

    /**
     * Create an instance of {@link RankedKMList }
     * 
     */
    public RankedKMList createRankedKMList() {
        return new RankedKMList();
    }

    /**
     * Create an instance of {@link DescribeProfile }
     * 
     */
    public DescribeProfile createDescribeProfile() {
        return new DescribeProfile();
    }

    /**
     * Create an instance of {@link EvaluateAtSpecifiedTimeResponse }
     * 
     */
    public EvaluateAtSpecifiedTimeResponse createEvaluateAtSpecifiedTimeResponse() {
        return new EvaluateAtSpecifiedTimeResponse();
    }

    /**
     * Create an instance of {@link KMList }
     * 
     */
    public KMList createKMList() {
        return new KMList();
    }

    /**
     * Create an instance of {@link GetKMDataRequirementsResponse }
     * 
     */
    public GetKMDataRequirementsResponse createGetKMDataRequirementsResponse() {
        return new GetKMDataRequirementsResponse();
    }

    /**
     * Create an instance of {@link EvaluateIterativelyResponse }
     * 
     */
    public EvaluateIterativelyResponse createEvaluateIterativelyResponse() {
        return new EvaluateIterativelyResponse();
    }

    /**
     * Create an instance of {@link DSSRuntimeException }
     * 
     */
    public DSSRuntimeException createDSSRuntimeException() {
        return new DSSRuntimeException();
    }

    /**
     * Create an instance of {@link EvaluationException }
     * 
     */
    public EvaluationException createEvaluationException() {
        return new EvaluationException();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMDescriptionBase }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMDescriptionBase")
    public JAXBElement<KMDescriptionBase> createKMDescriptionBase(KMDescriptionBase value) {
        return new JAXBElement<KMDescriptionBase>(_KMDescriptionBase_QNAME, KMDescriptionBase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnrecognizedScopingEntityException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "UnrecognizedScopingEntityException")
    public JAXBElement<UnrecognizedScopingEntityException> createUnrecognizedScopingEntityException(UnrecognizedScopingEntityException value) {
        return new JAXBElement<UnrecognizedScopingEntityException>(_UnrecognizedScopingEntityException_QNAME, UnrecognizedScopingEntityException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnsupportedLanguageException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "UnsupportedLanguageException")
    public JAXBElement<UnsupportedLanguageException> createUnsupportedLanguageException(UnsupportedLanguageException value) {
        return new JAXBElement<UnsupportedLanguageException>(_UnsupportedLanguageException_QNAME, UnsupportedLanguageException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SemanticRequirement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "SemanticRequirement")
    public JAXBElement<SemanticRequirement> createSemanticRequirement(SemanticRequirement value) {
        return new JAXBElement<SemanticRequirement>(_SemanticRequirement_QNAME, SemanticRequirement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetKMDataRequirementsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "getKMDataRequirementsResponse")
    public JAXBElement<GetKMDataRequirementsResponse> createGetKMDataRequirementsResponse(GetKMDataRequirementsResponse value) {
        return new JAXBElement<GetKMDataRequirementsResponse>(_GetKMDataRequirementsResponse_QNAME, GetKMDataRequirementsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMEvaluationRequestBase }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMEvaluationRequestBase")
    public JAXBElement<KMEvaluationRequestBase> createKMEvaluationRequestBase(KMEvaluationRequestBase value) {
        return new JAXBElement<KMEvaluationRequestBase>(_KMEvaluationRequestBase_QNAME, KMEvaluationRequestBase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CPQPInUse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "CPQPInUse")
    public JAXBElement<CPQPInUse> createCPQPInUse(CPQPInUse value) {
        return new JAXBElement<CPQPInUse>(_CPQPInUse_QNAME, CPQPInUse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListProfiles }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "listProfiles")
    public JAXBElement<ListProfiles> createListProfiles(ListProfiles value) {
        return new JAXBElement<ListProfiles>(_ListProfiles_QNAME, ListProfiles.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceRequestBase }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "ServiceRequestBase")
    public JAXBElement<ServiceRequestBase> createServiceRequestBase(ServiceRequestBase value) {
        return new JAXBElement<ServiceRequestBase>(_ServiceRequestBase_QNAME, ServiceRequestBase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntermediateState }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "IntermediateState")
    public JAXBElement<IntermediateState> createIntermediateState(IntermediateState value) {
        return new JAXBElement<IntermediateState>(_IntermediateState_QNAME, IntermediateState.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMDataRequirementGroup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMDataRequirementGroup")
    public JAXBElement<KMDataRequirementGroup> createKMDataRequirementGroup(KMDataRequirementGroup value) {
        return new JAXBElement<KMDataRequirementGroup>(_KMDataRequirementGroup_QNAME, KMDataRequirementGroup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListKMsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "listKMsResponse")
    public JAXBElement<ListKMsResponse> createListKMsResponse(ListKMsResponse value) {
        return new JAXBElement<ListKMsResponse>(_ListKMsResponse_QNAME, ListKMsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMTraitCriterion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMTraitCriterion")
    public JAXBElement<KMTraitCriterion> createKMTraitCriterion(KMTraitCriterion value) {
        return new JAXBElement<KMTraitCriterion>(_KMTraitCriterion_QNAME, KMTraitCriterion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequiredDataRequirement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "RequiredDataRequirement")
    public JAXBElement<RequiredDataRequirement> createRequiredDataRequirement(RequiredDataRequirement value) {
        return new JAXBElement<RequiredDataRequirement>(_RequiredDataRequirement_QNAME, RequiredDataRequirement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IterativeKMEvaluationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "IterativeKMEvaluationResponse")
    public JAXBElement<IterativeKMEvaluationResponse> createIterativeKMEvaluationResponse(IterativeKMEvaluationResponse value) {
        return new JAXBElement<IterativeKMEvaluationResponse>(_IterativeKMEvaluationResponse_QNAME, IterativeKMEvaluationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IterativeEvaluationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "IterativeEvaluationResponse")
    public JAXBElement<IterativeEvaluationResponse> createIterativeEvaluationResponse(IterativeEvaluationResponse value) {
        return new JAXBElement<IterativeEvaluationResponse>(_IterativeEvaluationResponse_QNAME, IterativeEvaluationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProfilesByType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "ProfilesByType")
    public JAXBElement<ProfilesByType> createProfilesByType(ProfilesByType value) {
        return new JAXBElement<ProfilesByType>(_ProfilesByType_QNAME, ProfilesByType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SemanticPayload }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "SemanticPayload")
    public JAXBElement<SemanticPayload> createSemanticPayload(SemanticPayload value) {
        return new JAXBElement<SemanticPayload>(_SemanticPayload_QNAME, SemanticPayload.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "EvaluationRequest")
    public JAXBElement<EvaluationRequest> createEvaluationRequest(EvaluationRequest value) {
        return new JAXBElement<EvaluationRequest>(_EvaluationRequest_QNAME, EvaluationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateResponse")
    public JAXBElement<EvaluateResponse> createEvaluateResponse(EvaluateResponse value) {
        return new JAXBElement<EvaluateResponse>(_EvaluateResponse_QNAME, EvaluateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConformanceProfile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "ConformanceProfile")
    public JAXBElement<ConformanceProfile> createConformanceProfile(ConformanceProfile value) {
        return new JAXBElement<ConformanceProfile>(_ConformanceProfile_QNAME, ConformanceProfile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvalidTraitCriterionDataFormatException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "InvalidTraitCriterionDataFormatException")
    public JAXBElement<InvalidTraitCriterionDataFormatException> createInvalidTraitCriterionDataFormatException(InvalidTraitCriterionDataFormatException value) {
        return new JAXBElement<InvalidTraitCriterionDataFormatException>(_InvalidTraitCriterionDataFormatException_QNAME, InvalidTraitCriterionDataFormatException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "EvaluationResponse")
    public JAXBElement<EvaluationResponse> createEvaluationResponse(EvaluationResponse value) {
        return new JAXBElement<EvaluationResponse>(_EvaluationResponse_QNAME, EvaluationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMEvaluationResultSemanticsList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMEvaluationResultSemanticsList")
    public JAXBElement<KMEvaluationResultSemanticsList> createKMEvaluationResultSemanticsList(KMEvaluationResultSemanticsList value) {
        return new JAXBElement<KMEvaluationResultSemanticsList>(_KMEvaluationResultSemanticsList_QNAME, KMEvaluationResultSemanticsList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateIterativelyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateIterativelyResponse")
    public JAXBElement<EvaluateIterativelyResponse> createEvaluateIterativelyResponse(EvaluateIterativelyResponse value) {
        return new JAXBElement<EvaluateIterativelyResponse>(_EvaluateIterativelyResponse_QNAME, EvaluateIterativelyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraitRequirement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "TraitRequirement")
    public JAXBElement<TraitRequirement> createTraitRequirement(TraitRequirement value) {
        return new JAXBElement<TraitRequirement>(_TraitRequirement_QNAME, TraitRequirement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateIterativelyAtSpecifiedTime }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateIterativelyAtSpecifiedTime")
    public JAXBElement<EvaluateIterativelyAtSpecifiedTime> createEvaluateIterativelyAtSpecifiedTime(EvaluateIterativelyAtSpecifiedTime value) {
        return new JAXBElement<EvaluateIterativelyAtSpecifiedTime>(_EvaluateIterativelyAtSpecifiedTime_QNAME, EvaluateIterativelyAtSpecifiedTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeScopingEntityHierarchy }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "describeScopingEntityHierarchy")
    public JAXBElement<DescribeScopingEntityHierarchy> createDescribeScopingEntityHierarchy(DescribeScopingEntityHierarchy value) {
        return new JAXBElement<DescribeScopingEntityHierarchy>(_DescribeScopingEntityHierarchy_QNAME, DescribeScopingEntityHierarchy.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluationResponseBase }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "EvaluationResponseBase")
    public JAXBElement<EvaluationResponseBase> createEvaluationResponseBase(EvaluationResponseBase value) {
        return new JAXBElement<EvaluationResponseBase>(_EvaluationResponseBase_QNAME, EvaluationResponseBase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetKMDescription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "getKMDescription")
    public JAXBElement<GetKMDescription> createGetKMDescription(GetKMDescription value) {
        return new JAXBElement<GetKMDescription>(_GetKMDescription_QNAME, GetKMDescription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribedDO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "DescribedDO")
    public JAXBElement<DescribedDO> createDescribedDO(DescribedDO value) {
        return new JAXBElement<DescribedDO>(_DescribedDO_QNAME, DescribedDO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMDataRequirementItem }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMDataRequirementItem")
    public JAXBElement<KMDataRequirementItem> createKMDataRequirementItem(KMDataRequirementItem value) {
        return new JAXBElement<KMDataRequirementItem>(_KMDataRequirementItem_QNAME, KMDataRequirementItem.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListKMs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "listKMs")
    public JAXBElement<ListKMs> createListKMs(ListKMs value) {
        return new JAXBElement<ListKMs>(_ListKMs_QNAME, ListKMs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateIteratively }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateIteratively")
    public JAXBElement<EvaluateIteratively> createEvaluateIteratively(EvaluateIteratively value) {
        return new JAXBElement<EvaluateIteratively>(_EvaluateIteratively_QNAME, EvaluateIteratively.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeSemanticSignifierResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "describeSemanticSignifierResponse")
    public JAXBElement<DescribeSemanticSignifierResponse> createDescribeSemanticSignifierResponse(DescribeSemanticSignifierResponse value) {
        return new JAXBElement<DescribeSemanticSignifierResponse>(_DescribeSemanticSignifierResponse_QNAME, DescribeSemanticSignifierResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RelatedKM }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "RelatedKM")
    public JAXBElement<RelatedKM> createRelatedKM(RelatedKM value) {
        return new JAXBElement<RelatedKM>(_RelatedKM_QNAME, RelatedKM.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeTrait }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "describeTrait")
    public JAXBElement<DescribeTrait> createDescribeTrait(DescribeTrait value) {
        return new JAXBElement<DescribeTrait>(_DescribeTrait_QNAME, DescribeTrait.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMDataRequirements }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMDataRequirements")
    public JAXBElement<KMDataRequirements> createKMDataRequirements(KMDataRequirements value) {
        return new JAXBElement<KMDataRequirements>(_KMDataRequirements_QNAME, KMDataRequirements.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMItem }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMItem")
    public JAXBElement<KMItem> createKMItem(KMItem value) {
        return new JAXBElement<KMItem>(_KMItem_QNAME, KMItem.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMCriterion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMCriterion")
    public JAXBElement<KMCriterion> createKMCriterion(KMCriterion value) {
        return new JAXBElement<KMCriterion>(_KMCriterion_QNAME, KMCriterion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateIterativelyAtSpecifiedTimeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateIterativelyAtSpecifiedTimeResponse")
    public JAXBElement<EvaluateIterativelyAtSpecifiedTimeResponse> createEvaluateIterativelyAtSpecifiedTimeResponse(EvaluateIterativelyAtSpecifiedTimeResponse value) {
        return new JAXBElement<EvaluateIterativelyAtSpecifiedTimeResponse>(_EvaluateIterativelyAtSpecifiedTimeResponse_QNAME, EvaluateIterativelyAtSpecifiedTimeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RankedKM }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "RankedKM")
    public JAXBElement<RankedKM> createRankedKM(RankedKM value) {
        return new JAXBElement<RankedKM>(_RankedKM_QNAME, RankedKM.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtendedKMDescription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "ExtendedKMDescription")
    public JAXBElement<ExtendedKMDescription> createExtendedKMDescription(ExtendedKMDescription value) {
        return new JAXBElement<ExtendedKMDescription>(_ExtendedKMDescription_QNAME, ExtendedKMDescription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMTraitCriterionValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMTraitCriterionValue")
    public JAXBElement<KMTraitCriterionValue> createKMTraitCriterionValue(KMTraitCriterionValue value) {
        return new JAXBElement<KMTraitCriterionValue>(_KMTraitCriterionValue_QNAME, KMTraitCriterionValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceProfile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "ServiceProfile")
    public JAXBElement<ServiceProfile> createServiceProfile(ServiceProfile value) {
        return new JAXBElement<ServiceProfile>(_ServiceProfile_QNAME, ServiceProfile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Trait }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "Trait")
    public JAXBElement<Trait> createTrait(Trait value) {
        return new JAXBElement<Trait>(_Trait_QNAME, Trait.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMDescription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMDescription")
    public JAXBElement<KMDescription> createKMDescription(KMDescription value) {
        return new JAXBElement<KMDescription>(_KMDescription_QNAME, KMDescription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LanguageSupportRequirement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "LanguageSupportRequirement")
    public JAXBElement<LanguageSupportRequirement> createLanguageSupportRequirement(LanguageSupportRequirement value) {
        return new JAXBElement<LanguageSupportRequirement>(_LanguageSupportRequirement_QNAME, LanguageSupportRequirement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluationResultCriterion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "EvaluationResultCriterion")
    public JAXBElement<EvaluationResultCriterion> createEvaluationResultCriterion(EvaluationResultCriterion value) {
        return new JAXBElement<EvaluationResultCriterion>(_EvaluationResultCriterion_QNAME, EvaluationResultCriterion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ScopingEntity }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "ScopingEntity")
    public JAXBElement<ScopingEntity> createScopingEntity(ScopingEntity value) {
        return new JAXBElement<ScopingEntity>(_ScopingEntity_QNAME, ScopingEntity.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IterativeEvaluationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "IterativeEvaluationRequest")
    public JAXBElement<IterativeEvaluationRequest> createIterativeEvaluationRequest(IterativeEvaluationRequest value) {
        return new JAXBElement<IterativeEvaluationRequest>(_IterativeEvaluationRequest_QNAME, IterativeEvaluationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetKMEvaluationResultSemanticsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "getKMEvaluationResultSemanticsResponse")
    public JAXBElement<GetKMEvaluationResultSemanticsResponse> createGetKMEvaluationResultSemanticsResponse(GetKMEvaluationResultSemanticsResponse value) {
        return new JAXBElement<GetKMEvaluationResultSemanticsResponse>(_GetKMEvaluationResultSemanticsResponse_QNAME, GetKMEvaluationResultSemanticsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AllowedDataRequirement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "AllowedDataRequirement")
    public JAXBElement<AllowedDataRequirement> createAllowedDataRequirement(AllowedDataRequirement value) {
        return new JAXBElement<AllowedDataRequirement>(_AllowedDataRequirement_QNAME, AllowedDataRequirement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeTraitResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "describeTraitResponse")
    public JAXBElement<DescribeTraitResponse> createDescribeTraitResponse(DescribeTraitResponse value) {
        return new JAXBElement<DescribeTraitResponse>(_DescribeTraitResponse_QNAME, DescribeTraitResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeProfileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "describeProfileResponse")
    public JAXBElement<DescribeProfileResponse> createDescribeProfileResponse(DescribeProfileResponse value) {
        return new JAXBElement<DescribeProfileResponse>(_DescribeProfileResponse_QNAME, DescribeProfileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnrecognizedTraitCriterionException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "UnrecognizedTraitCriterionException")
    public JAXBElement<UnrecognizedTraitCriterionException> createUnrecognizedTraitCriterionException(UnrecognizedTraitCriterionException value) {
        return new JAXBElement<UnrecognizedTraitCriterionException>(_UnrecognizedTraitCriterionException_QNAME, UnrecognizedTraitCriterionException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluationRequestBase }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "EvaluationRequestBase")
    public JAXBElement<EvaluationRequestBase> createEvaluationRequestBase(EvaluationRequestBase value) {
        return new JAXBElement<EvaluationRequestBase>(_EvaluationRequestBase_QNAME, EvaluationRequestBase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComputableDefinition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "ComputableDefinition")
    public JAXBElement<ComputableDefinition> createComputableDefinition(ComputableDefinition value) {
        return new JAXBElement<ComputableDefinition>(_ComputableDefinition_QNAME, ComputableDefinition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ItemIdentifier }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "ItemIdentifier")
    public JAXBElement<ItemIdentifier> createItemIdentifier(ItemIdentifier value) {
        return new JAXBElement<ItemIdentifier>(_ItemIdentifier_QNAME, ItemIdentifier.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntityIdentifier }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "EntityIdentifier")
    public JAXBElement<EntityIdentifier> createEntityIdentifier(EntityIdentifier value) {
        return new JAXBElement<EntityIdentifier>(_EntityIdentifier_QNAME, EntityIdentifier.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMTraitInclusionSpecification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMTraitInclusionSpecification")
    public JAXBElement<KMTraitInclusionSpecification> createKMTraitInclusionSpecification(KMTraitInclusionSpecification value) {
        return new JAXBElement<KMTraitInclusionSpecification>(_KMTraitInclusionSpecification_QNAME, KMTraitInclusionSpecification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetKMEvaluationResultSemantics }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "getKMEvaluationResultSemantics")
    public JAXBElement<GetKMEvaluationResultSemantics> createGetKMEvaluationResultSemantics(GetKMEvaluationResultSemantics value) {
        return new JAXBElement<GetKMEvaluationResultSemantics>(_GetKMEvaluationResultSemantics_QNAME, GetKMEvaluationResultSemantics.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntermediateKMEvaluationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "IntermediateKMEvaluationResponse")
    public JAXBElement<IntermediateKMEvaluationResponse> createIntermediateKMEvaluationResponse(IntermediateKMEvaluationResponse value) {
        return new JAXBElement<IntermediateKMEvaluationResponse>(_IntermediateKMEvaluationResponse_QNAME, IntermediateKMEvaluationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Evaluate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluate")
    public JAXBElement<Evaluate> createEvaluate(Evaluate value) {
        return new JAXBElement<Evaluate>(_Evaluate_QNAME, Evaluate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListProfilesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "listProfilesResponse")
    public JAXBElement<ListProfilesResponse> createListProfilesResponse(ListProfilesResponse value) {
        return new JAXBElement<ListProfilesResponse>(_ListProfilesResponse_QNAME, ListProfilesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FunctionalProfile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "FunctionalProfile")
    public JAXBElement<FunctionalProfile> createFunctionalProfile(FunctionalProfile value) {
        return new JAXBElement<FunctionalProfile>(_FunctionalProfile_QNAME, FunctionalProfile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataRequirementItemData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "DataRequirementItemData")
    public JAXBElement<DataRequirementItemData> createDataRequirementItemData(DataRequirementItemData value) {
        return new JAXBElement<DataRequirementItemData>(_DataRequirementItemData_QNAME, DataRequirementItemData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMTraitValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMTraitValue")
    public JAXBElement<KMTraitValue> createKMTraitValue(KMTraitValue value) {
        return new JAXBElement<KMTraitValue>(_KMTraitValue_QNAME, KMTraitValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeSemanticRequirementResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "describeSemanticRequirementResponse")
    public JAXBElement<DescribeSemanticRequirementResponse> createDescribeSemanticRequirementResponse(DescribeSemanticRequirementResponse value) {
        return new JAXBElement<DescribeSemanticRequirementResponse>(_DescribeSemanticRequirementResponse_QNAME, DescribeSemanticRequirementResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeSemanticSignifier }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "describeSemanticSignifier")
    public JAXBElement<DescribeSemanticSignifier> createDescribeSemanticSignifier(DescribeSemanticSignifier value) {
        return new JAXBElement<DescribeSemanticSignifier>(_DescribeSemanticSignifier_QNAME, DescribeSemanticSignifier.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvalidDataFormatException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "InvalidDataFormatException")
    public JAXBElement<InvalidDataFormatException> createInvalidDataFormatException(InvalidDataFormatException value) {
        return new JAXBElement<InvalidDataFormatException>(_InvalidDataFormatException_QNAME, InvalidDataFormatException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RankedKMList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "RankedKMList")
    public JAXBElement<RankedKMList> createRankedKMList(RankedKMList value) {
        return new JAXBElement<RankedKMList>(_RankedKMList_QNAME, RankedKMList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Warning }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "Warning")
    public JAXBElement<Warning> createWarning(Warning value) {
        return new JAXBElement<Warning>(_Warning_QNAME, Warning.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FinalKMEvaluationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "FinalKMEvaluationResponse")
    public JAXBElement<FinalKMEvaluationResponse> createFinalKMEvaluationResponse(FinalKMEvaluationResponse value) {
        return new JAXBElement<FinalKMEvaluationResponse>(_FinalKMEvaluationResponse_QNAME, FinalKMEvaluationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataRequirementCriterion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "DataRequirementCriterion")
    public JAXBElement<DataRequirementCriterion> createDataRequirementCriterion(DataRequirementCriterion value) {
        return new JAXBElement<DataRequirementCriterion>(_DataRequirementCriterion_QNAME, DataRequirementCriterion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvalidTimeZoneOffsetException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "InvalidTimeZoneOffsetException")
    public JAXBElement<InvalidTimeZoneOffsetException> createInvalidTimeZoneOffsetException(InvalidTimeZoneOffsetException value) {
        return new JAXBElement<InvalidTimeZoneOffsetException>(_InvalidTimeZoneOffsetException_QNAME, InvalidTimeZoneOffsetException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMEvaluationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMEvaluationResponse")
    public JAXBElement<KMEvaluationResponse> createKMEvaluationResponse(KMEvaluationResponse value) {
        return new JAXBElement<KMEvaluationResponse>(_KMEvaluationResponse_QNAME, KMEvaluationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeSemanticRequirement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "describeSemanticRequirement")
    public JAXBElement<DescribeSemanticRequirement> createDescribeSemanticRequirement(DescribeSemanticRequirement value) {
        return new JAXBElement<DescribeSemanticRequirement>(_DescribeSemanticRequirement_QNAME, DescribeSemanticRequirement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateAtSpecifiedTimeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateAtSpecifiedTimeResponse")
    public JAXBElement<EvaluateAtSpecifiedTimeResponse> createEvaluateAtSpecifiedTimeResponse(EvaluateAtSpecifiedTimeResponse value) {
        return new JAXBElement<EvaluateAtSpecifiedTimeResponse>(_EvaluateAtSpecifiedTimeResponse_QNAME, EvaluateAtSpecifiedTimeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XSDComputableDefinition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "XSDComputableDefinition")
    public JAXBElement<XSDComputableDefinition> createXSDComputableDefinition(XSDComputableDefinition value) {
        return new JAXBElement<XSDComputableDefinition>(_XSDComputableDefinition_QNAME, XSDComputableDefinition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMStatusCriterion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMStatusCriterion")
    public JAXBElement<KMStatusCriterion> createKMStatusCriterion(KMStatusCriterion value) {
        return new JAXBElement<KMStatusCriterion>(_KMStatusCriterion_QNAME, KMStatusCriterion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataRequirementBase }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "DataRequirementBase")
    public JAXBElement<DataRequirementBase> createDataRequirementBase(DataRequirementBase value) {
        return new JAXBElement<DataRequirementBase>(_DataRequirementBase_QNAME, DataRequirementBase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMList")
    public JAXBElement<KMList> createKMList(KMList value) {
        return new JAXBElement<KMList>(_KMList_QNAME, KMList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "getKMDataRequirementsForEvaluationAtSpecifiedTimeResponse")
    public JAXBElement<GetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse> createGetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse(GetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse value) {
        return new JAXBElement<GetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse>(_GetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse_QNAME, GetKMDataRequirementsForEvaluationAtSpecifiedTimeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMEvaluationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMEvaluationRequest")
    public JAXBElement<KMEvaluationRequest> createKMEvaluationRequest(KMEvaluationRequest value) {
        return new JAXBElement<KMEvaluationRequest>(_KMEvaluationRequest_QNAME, KMEvaluationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnrecognizedLanguageException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "UnrecognizedLanguageException")
    public JAXBElement<UnrecognizedLanguageException> createUnrecognizedLanguageException(UnrecognizedLanguageException value) {
        return new JAXBElement<UnrecognizedLanguageException>(_UnrecognizedLanguageException_QNAME, UnrecognizedLanguageException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RelatedKMSearchCriterion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "RelatedKMSearchCriterion")
    public JAXBElement<RelatedKMSearchCriterion> createRelatedKMSearchCriterion(RelatedKMSearchCriterion value) {
        return new JAXBElement<RelatedKMSearchCriterion>(_RelatedKMSearchCriterion_QNAME, RelatedKMSearchCriterion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMSearchCriteria }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMSearchCriteria")
    public JAXBElement<KMSearchCriteria> createKMSearchCriteria(KMSearchCriteria value) {
        return new JAXBElement<KMSearchCriteria>(_KMSearchCriteria_QNAME, KMSearchCriteria.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraitSetRequirement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "TraitSetRequirement")
    public JAXBElement<TraitSetRequirement> createTraitSetRequirement(TraitSetRequirement value) {
        return new JAXBElement<TraitSetRequirement>(_TraitSetRequirement_QNAME, TraitSetRequirement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SemanticProfile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "SemanticProfile")
    public JAXBElement<SemanticProfile> createSemanticProfile(SemanticProfile value) {
        return new JAXBElement<SemanticProfile>(_SemanticProfile_QNAME, SemanticProfile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMEvaluationResultSemantics }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMEvaluationResultSemantics")
    public JAXBElement<KMEvaluationResultSemantics> createKMEvaluationResultSemantics(KMEvaluationResultSemantics value) {
        return new JAXBElement<KMEvaluationResultSemantics>(_KMEvaluationResultSemantics_QNAME, KMEvaluationResultSemantics.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InteractionIdentifier }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "InteractionIdentifier")
    public JAXBElement<InteractionIdentifier> createInteractionIdentifier(InteractionIdentifier value) {
        return new JAXBElement<InteractionIdentifier>(_InteractionIdentifier_QNAME, InteractionIdentifier.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetKMDescriptionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "getKMDescriptionResponse")
    public JAXBElement<GetKMDescriptionResponse> createGetKMDescriptionResponse(GetKMDescriptionResponse value) {
        return new JAXBElement<GetKMDescriptionResponse>(_GetKMDescriptionResponse_QNAME, GetKMDescriptionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeScopingEntity }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "describeScopingEntity")
    public JAXBElement<DescribeScopingEntity> createDescribeScopingEntity(DescribeScopingEntity value) {
        return new JAXBElement<DescribeScopingEntity>(_DescribeScopingEntity_QNAME, DescribeScopingEntity.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DSSException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "DSSException")
    public JAXBElement<DSSException> createDSSException(DSSException value) {
        return new JAXBElement<DSSException>(_DSSException_QNAME, DSSException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMLocalizedTraitValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMLocalizedTraitValue")
    public JAXBElement<KMLocalizedTraitValue> createKMLocalizedTraitValue(KMLocalizedTraitValue value) {
        return new JAXBElement<KMLocalizedTraitValue>(_KMLocalizedTraitValue_QNAME, KMLocalizedTraitValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluationException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "EvaluationException")
    public JAXBElement<EvaluationException> createEvaluationException(EvaluationException value) {
        return new JAXBElement<EvaluationException>(_EvaluationException_QNAME, EvaluationException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InformationModelAlternative }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "InformationModelAlternative")
    public JAXBElement<InformationModelAlternative> createInformationModelAlternative(InformationModelAlternative value) {
        return new JAXBElement<InformationModelAlternative>(_InformationModelAlternative_QNAME, InformationModelAlternative.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeScopingEntityResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "describeScopingEntityResponse")
    public JAXBElement<DescribeScopingEntityResponse> createDescribeScopingEntityResponse(DescribeScopingEntityResponse value) {
        return new JAXBElement<DescribeScopingEntityResponse>(_DescribeScopingEntityResponse_QNAME, DescribeScopingEntityResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetKMDataRequirements }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "getKMDataRequirements")
    public JAXBElement<GetKMDataRequirements> createGetKMDataRequirements(GetKMDataRequirements value) {
        return new JAXBElement<GetKMDataRequirements>(_GetKMDataRequirements_QNAME, GetKMDataRequirements.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DSSRuntimeException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "DSSRuntimeException")
    public JAXBElement<DSSRuntimeException> createDSSRuntimeException(DSSRuntimeException value) {
        return new JAXBElement<DSSRuntimeException>(_DSSRuntimeException_QNAME, DSSRuntimeException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindKMs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "findKMs")
    public JAXBElement<FindKMs> createFindKMs(FindKMs value) {
        return new JAXBElement<FindKMs>(_FindKMs_QNAME, FindKMs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetKMDataRequirementsForEvaluationAtSpecifiedTime }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "getKMDataRequirementsForEvaluationAtSpecifiedTime")
    public JAXBElement<GetKMDataRequirementsForEvaluationAtSpecifiedTime> createGetKMDataRequirementsForEvaluationAtSpecifiedTime(GetKMDataRequirementsForEvaluationAtSpecifiedTime value) {
        return new JAXBElement<GetKMDataRequirementsForEvaluationAtSpecifiedTime>(_GetKMDataRequirementsForEvaluationAtSpecifiedTime_QNAME, GetKMDataRequirementsForEvaluationAtSpecifiedTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindKMsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "findKMsResponse")
    public JAXBElement<FindKMsResponse> createFindKMsResponse(FindKMsResponse value) {
        return new JAXBElement<FindKMsResponse>(_FindKMsResponse_QNAME, FindKMsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequiredDataNotProvidedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "RequiredDataNotProvidedException")
    public JAXBElement<RequiredDataNotProvidedException> createRequiredDataNotProvidedException(RequiredDataNotProvidedException value) {
        return new JAXBElement<RequiredDataNotProvidedException>(_RequiredDataNotProvidedException_QNAME, RequiredDataNotProvidedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvalidDriDataFormatException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "InvalidDriDataFormatException")
    public JAXBElement<InvalidDriDataFormatException> createInvalidDriDataFormatException(InvalidDriDataFormatException value) {
        return new JAXBElement<InvalidDriDataFormatException>(_InvalidDriDataFormatException_QNAME, InvalidDriDataFormatException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnrecognizedScopedEntityException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "UnrecognizedScopedEntityException")
    public JAXBElement<UnrecognizedScopedEntityException> createUnrecognizedScopedEntityException(UnrecognizedScopedEntityException value) {
        return new JAXBElement<UnrecognizedScopedEntityException>(_UnrecognizedScopedEntityException_QNAME, UnrecognizedScopedEntityException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeScopingEntityHierarchyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "describeScopingEntityHierarchyResponse")
    public JAXBElement<DescribeScopingEntityHierarchyResponse> createDescribeScopingEntityHierarchyResponse(DescribeScopingEntityHierarchyResponse value) {
        return new JAXBElement<DescribeScopingEntityHierarchyResponse>(_DescribeScopingEntityHierarchyResponse_QNAME, DescribeScopingEntityHierarchyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OtherSemanticRequirement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "OtherSemanticRequirement")
    public JAXBElement<OtherSemanticRequirement> createOtherSemanticRequirement(OtherSemanticRequirement value) {
        return new JAXBElement<OtherSemanticRequirement>(_OtherSemanticRequirement_QNAME, OtherSemanticRequirement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeProfile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "describeProfile")
    public JAXBElement<DescribeProfile> createDescribeProfile(DescribeProfile value) {
        return new JAXBElement<DescribeProfile>(_DescribeProfile_QNAME, DescribeProfile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateAtSpecifiedTime }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateAtSpecifiedTime")
    public JAXBElement<EvaluateAtSpecifiedTime> createEvaluateAtSpecifiedTime(EvaluateAtSpecifiedTime value) {
        return new JAXBElement<EvaluateAtSpecifiedTime>(_EvaluateAtSpecifiedTime_QNAME, EvaluateAtSpecifiedTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMConsumerProvidedQueryParameter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMConsumerProvidedQueryParameter")
    public JAXBElement<KMConsumerProvidedQueryParameter> createKMConsumerProvidedQueryParameter(KMConsumerProvidedQueryParameter value) {
        return new JAXBElement<KMConsumerProvidedQueryParameter>(_KMConsumerProvidedQueryParameter_QNAME, KMConsumerProvidedQueryParameter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ScopedDO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "ScopedDO")
    public JAXBElement<ScopedDO> createScopedDO(ScopedDO value) {
        return new JAXBElement<ScopedDO>(_ScopedDO_QNAME, ScopedDO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InformationModelRequirement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "InformationModelRequirement")
    public JAXBElement<InformationModelRequirement> createInformationModelRequirement(InformationModelRequirement value) {
        return new JAXBElement<InformationModelRequirement>(_InformationModelRequirement_QNAME, InformationModelRequirement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KMEvaluationResultData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "KMEvaluationResultData")
    public JAXBElement<KMEvaluationResultData> createKMEvaluationResultData(KMEvaluationResultData value) {
        return new JAXBElement<KMEvaluationResultData>(_KMEvaluationResultData_QNAME, KMEvaluationResultData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraitCriterion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "TraitCriterion")
    public JAXBElement<TraitCriterion> createTraitCriterion(TraitCriterion value) {
        return new JAXBElement<TraitCriterion>(_TraitCriterion_QNAME, TraitCriterion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProfilesOfType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "ProfilesOfType")
    public JAXBElement<ProfilesOfType> createProfilesOfType(ProfilesOfType value) {
        return new JAXBElement<ProfilesOfType>(_ProfilesOfType_QNAME, ProfilesOfType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SemanticSignifier }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "SemanticSignifier")
    public JAXBElement<SemanticSignifier> createSemanticSignifier(SemanticSignifier value) {
        return new JAXBElement<SemanticSignifier>(_SemanticSignifier_QNAME, SemanticSignifier.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IterativeKMEvaluationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "IterativeKMEvaluationRequest")
    public JAXBElement<IterativeKMEvaluationRequest> createIterativeKMEvaluationRequest(IterativeKMEvaluationRequest value) {
        return new JAXBElement<IterativeKMEvaluationRequest>(_IterativeKMEvaluationRequest_QNAME, IterativeKMEvaluationRequest.class, null, value);
    }

}
