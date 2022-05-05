package org.cdsframework.rest.opencds.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.rest.opencds.pojos.CDMUpdate;
import org.cdsframework.rest.opencds.pojos.CDMUpdateResult;
import org.cdsframework.rest.opencds.pojos.KMUpdate;
import org.cdsframework.rest.opencds.pojos.KMUpdateResult;
import org.cdsframework.rest.opencds.pojos.UpdateResponse;
import org.cdsframework.rest.opencds.pojos.UpdateResponseResult;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KMStatus;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.TraitId;
import org.opencds.config.api.model.impl.CDMIdImpl;
import org.opencds.config.api.model.impl.KnowledgeModuleImpl;
import org.opencds.config.api.model.impl.SSIdImpl;
import org.opencds.config.mapper.ConceptDeterminationMethodMapper;
import org.opencds.config.schema.ConceptDeterminationMethod;
import org.opencds.config.schema.ConceptDeterminationMethods;

/**
 *
 * @author sdn
 */
public class ConfigUtils {

    private static final Log log = LogFactory.getLog(ConfigUtils.class);

    /**
     * Does a CDM exist.
     *
     * @param cdmId
     * @param configurationService
     * @return
     */
    public static boolean isCdmExists(final CDMId cdmId, final ConfigurationService configurationService) {
        boolean result = false;
        if (configurationService.getKnowledgeRepository().getConceptDeterminationMethodService().find(cdmId) != null) {
            result = true;
        }
        return result;
    }

    /**
     * Does a KM exist.
     *
     * @param kmId
     * @param configurationService
     * @return
     */
    public static boolean isKmExists(final KMId kmId, final ConfigurationService configurationService) {
        boolean result = false;
        if (configurationService.getKnowledgeRepository().getKnowledgeModuleService().find(kmId) != null) {
            result = true;
        }
        return result;
    }

    /**
     * Processes a cdm and km update.
     *
     * @param updateResponse
     * @param configurationService
     * @return result
     */
    public static UpdateResponseResult update(final UpdateResponse updateResponse,
            final ConfigurationService configurationService, final String environment, final String instanceId) {
        final String METHODNAME = "update ";
        final UpdateResponseResult result = new UpdateResponseResult();
        result.setEnvironment(environment);
        result.setInstanceId(instanceId);
        Objects.requireNonNull(updateResponse, "updateResponse cannot be null");

        if (updateResponse.getCdmUpdates() != null) {
            for (final CDMUpdate cdmUpdate : updateResponse.getCdmUpdates()) {
                if (cdmUpdate != null && cdmUpdate.isValid()) {
                    final CDMId cdmId = cdmUpdate.getCdmId();

                    log.debug(String.format("%sCDM: code=%s; codeSystem=%s; version=%s; length=%d",
                            METHODNAME, cdmId.getCode(), cdmId.getCodeSystem(), cdmId.getVersion(), cdmUpdate.getCdm().length));

                    ConceptDeterminationMethods cdms = null;

                    try {
                        cdms = readConceptDeterminationMethods(new ByteArrayInputStream(cdmUpdate.getCdm()));
                    } catch (JAXBException | TransformerException e) {
                        final String error = ExceptionUtils.getStackTrace(e);
                        result.getCdms().add(new CDMUpdateResult(cdmId, 500, error));
                        log.error(e);
                    }

                    if (cdms != null) {
                        if (cdms.getConceptDeterminationMethod() == null) {
                            final String error = "cdms.getConceptDeterminationMethod() is null!";
                            result.getCdms().add(new CDMUpdateResult(cdmId, 500, error));
                            log.error(METHODNAME + error);
                        } else if (cdms.getConceptDeterminationMethod().isEmpty()) {
                            final String error = "cdms.getConceptDeterminationMethod() is empty!";
                            result.getCdms().add(new CDMUpdateResult(cdmId, 500, error));
                            log.error(METHODNAME + error);
                        } else if (cdms.getConceptDeterminationMethod().size() > 1) {
                            final String error = "cdms.getConceptDeterminationMethod() is only allow to have one cdm!";
                            result.getCdms().add(new CDMUpdateResult(cdmId, 500, error));
                            log.error(METHODNAME + error);
                        } else {
                            final ConceptDeterminationMethod cdm = cdms.getConceptDeterminationMethod().get(0);
                            if (cdm == null) {
                                final String error = "cdm is null!";
                                result.getCdms().add(new CDMUpdateResult(cdmId, 500, error));
                                log.error(METHODNAME + error);
                            } else {
                                try {
                                    ConfigUtils.updateConceptDeterminationMethod(cdmId, cdm, configurationService);
                                    log.info(METHODNAME + "CDM successfully updated: " + cdmUpdate);
                                } catch (final Exception e) {
                                    final String error = ExceptionUtils.getStackTrace(e);
                                    result.getCdms().add(new CDMUpdateResult(cdmId, 500, error));
                                    log.error(e);
                                }
                            }
                        }
                    }
                } else {
                    log.warn(METHODNAME + "cdmUpdate is incomplete: " + cdmUpdate);
                }
            }
        } else {
            log.debug(METHODNAME + "No cdmUpdates to process.");
        }

        if (updateResponse.getKmUpdates() != null) {
            for (final KMUpdate kmUpdate : updateResponse.getKmUpdates()) {
                if (kmUpdate != null && kmUpdate.isValid()) {

                    final KMId kmId = kmUpdate.getKmId();

                    log.debug(String.format("%sKM: scopingEntityId=%s; businessId=%s; version=%s; length=%d",
                            METHODNAME, kmId.getScopingEntityId(), kmId.getBusinessId(), kmId.getVersion(), kmUpdate.getKmPackage().length));

                    if (log.isTraceEnabled())
                        log.trace("sha1Hash=" + toSha1Hash(kmUpdate.getKmPackage()));

                    try {
                        final InputStream kmPackageInputStream = new ByteArrayInputStream(kmUpdate.getKmPackage());
                        ConfigUtils.updateKnowledgeModulePackage(kmId, kmPackageInputStream, configurationService);
                        log.info(METHODNAME + "KM package successfully updated: " + kmUpdate);
                    } catch (final Exception e) {
                        final String error = ExceptionUtils.getStackTrace(e);
                        result.getKms().add(new KMUpdateResult(kmId, 500, error));
                        log.error(e);
                    }
                } else {
                    log.warn(METHODNAME + "kmUpdate is incomplete: " + kmUpdate);
                }
            }
        } else {
            log.debug(METHODNAME + "No kmUpdates to process.");
        }
        return result;
    }

    private static String toSha1Hash(byte[] b)
    {
        StringBuilder sha1Hash = new StringBuilder();
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            final byte[] digest = md.digest(b);
            for (int i = 0; i < digest.length; i++)
                sha1Hash.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        catch (final NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return sha1Hash.toString();
    }

    public static ConceptDeterminationMethods readConceptDeterminationMethods(InputStream input)
            throws JAXBException, TransformerException
    {
        ConceptDeterminationMethods cdms = MarshalUtils.unmarshal(input, ConceptDeterminationMethods.class);
        return cdms;
    }

    /**
     * Updates the supplied knowledge module package.
     *
     * @param kmId
     * @param knowledgePackage
     * @param configurationService
     */
    public static void updateKnowledgeModulePackage(final KMId kmId, final InputStream knowledgePackage,
            final ConfigurationService configurationService) {
        final String METHODNAME = "updateKnowledgeModulePackage ";
        final boolean create = !isKmExists(kmId, configurationService);
        if (create) {

            final CDMId defaultCdmId = getDefaultCdmId();
            final SSId defaultSsId = getDefaultSsId();

            final String defaultCdmExecutionEngine = System.getProperty("defaultCdmExecutionEngine");
            if (defaultCdmExecutionEngine == null || defaultCdmExecutionEngine.trim().isEmpty()) {
                throw new IllegalStateException(METHODNAME + "defaultCdmExecutionEngine is null!");
            } else {
                log.debug(METHODNAME + "defaultCdmExecutionEngine: " + defaultCdmExecutionEngine);
            }

            final String defaultPrimaryProcess = System.getProperty("defaultPrimaryProcess");
            if (defaultPrimaryProcess == null || defaultPrimaryProcess.trim().isEmpty()) {
                throw new IllegalStateException(METHODNAME + "defaultPrimaryProcess is null!");
            } else {
                log.debug(METHODNAME + "defaultPrimaryProcess: " + defaultPrimaryProcess);
            }

            final KnowledgeModuleImpl knowledgeModule = KnowledgeModuleImpl.create(kmId, KMStatus.APPROVED,
                    defaultCdmExecutionEngine, defaultSsId, defaultCdmId, new ArrayList<SecondaryCDM>(), "PKG",
                    kmId.getScopingEntityId() + "^" + kmId.getBusinessId() + "^" + kmId.getVersion() + ".pkg", true,
                    defaultPrimaryProcess, new ArrayList<TraitId>(), new ArrayList<PluginId>(),
                    new ArrayList<PluginId>(), new Date(), "system");
            configurationService.getKnowledgeRepository().getKnowledgeModuleService().persist(knowledgeModule);
        }
        configurationService.getKnowledgeRepository().getKnowledgeModuleService().persistKnowledgePackage(kmId,
                knowledgePackage);
        log.info(METHODNAME + (create ? "created: " : "updated: ") + kmId.getScopingEntityId() + " - "
                + kmId.getBusinessId() + " - " + kmId.getVersion());
    }

    /**
     * Updates the supplied concept determination method.
     *
     * @param cdmId
     * @param cdm
     * @param configurationService
     */
    public static void updateConceptDeterminationMethod(final CDMId cdmId, final ConceptDeterminationMethod cdm,
            final ConfigurationService configurationService) {
        final String METHODNAME = "updateConceptDeterminationMethod ";
        final org.opencds.config.api.model.ConceptDeterminationMethod cdmInternal = ConceptDeterminationMethodMapper
                .internal(cdm);
        if (!cdmId.equals(cdmInternal.getCDMId())) {
            throw new IllegalStateException("CDMId of request and document do not match");
        }
        final boolean created = !isCdmExists(cdmId, configurationService);
        configurationService.getKnowledgeRepository().getConceptDeterminationMethodService().persist(cdmInternal);
        log.info(METHODNAME + (created ? "created: " : "updated: ") + cdmId.getCodeSystem() + " - "
                + cdmId.getCodeSystem() + " - " + cdmId.getVersion());
    }

    public static CDMId getDefaultCdmId() {
        final String METHODNAME = "getDefaultCdmId ";

        final String defaultCdmCode = System.getProperty("defaultCdmCode");
        if (defaultCdmCode == null || defaultCdmCode.trim().isEmpty()) {
            throw new IllegalStateException(METHODNAME + "defaultCdmCode is null!");
        }
        final String defaultCdmCodeSystem = System.getProperty("defaultCdmCodeSystem");
        if (defaultCdmCodeSystem == null || defaultCdmCodeSystem.trim().isEmpty()) {
            throw new IllegalStateException(METHODNAME + "defaultCdmCodeSystem is null!");
        }
        final String defaultCdmVersion = System.getProperty("defaultCdmVersion");
        if (defaultCdmVersion == null || defaultCdmVersion.trim().isEmpty()) {
            throw new IllegalStateException(METHODNAME + "defaultCdmVersion is null!");
        }

        return CDMIdImpl.create(defaultCdmCodeSystem, defaultCdmCode, defaultCdmVersion);
    }

    public static SSId getDefaultSsId() {
        final String METHODNAME = "getDefaultSsId ";

        final String defaultSsidScopingEntityId = System.getProperty("defaultSsidScopingEntityId");
        if (defaultSsidScopingEntityId == null || defaultSsidScopingEntityId.trim().isEmpty()) {
            throw new IllegalStateException(METHODNAME + "defaultSsidScopingEntityId is null!");
        } else {
            log.debug(METHODNAME + "defaultSsidScopingEntityId: " + defaultSsidScopingEntityId);
        }

        final String defaultSsidBusinessId = System.getProperty("defaultSsidBusinessId");
        if (defaultSsidBusinessId == null || defaultSsidBusinessId.trim().isEmpty()) {
            throw new IllegalStateException(METHODNAME + "defaultSsidBusinessId is null!");
        } else {
            log.debug(METHODNAME + "defaultSsidBusinessId: " + defaultSsidBusinessId);
        }

        final String defaultSsidVersion = System.getProperty("defaultSsidVersion");
        if (defaultSsidVersion == null || defaultSsidVersion.trim().isEmpty()) {
            throw new IllegalStateException(METHODNAME + "defaultSsidVersion is null!");
        } else {
            log.debug(METHODNAME + "defaultSsidVersion: " + defaultSsidVersion);
        }

        return SSIdImpl.create(defaultSsidScopingEntityId, defaultSsidBusinessId, defaultSsidVersion);
    }

}
