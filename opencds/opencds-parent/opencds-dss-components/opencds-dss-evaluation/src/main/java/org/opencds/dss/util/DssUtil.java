package org.opencds.dss.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.SemanticPayload;
import org.omg.dss.evaluation.requestresponse.DataRequirementItemData;
import org.opencds.common.structures.EvaluationRequestDataItem;

public class DssUtil {

    private static final Log log = LogFactory.getLog(DssUtil.class);

    public static EntityIdentifier makeEIFromCommon(org.opencds.config.api.model.EntityIdentifier commonEI) {
        EntityIdentifier ei = new EntityIdentifier();
        ei.setScopingEntityId(commonEI.getScopingEntityId());
        ei.setBusinessId(commonEI.getBusinessId());
        ei.setVersion(commonEI.getVersion());
        return ei;
    }
    
    public static EntityIdentifier makeEI( String eiString ) {
        EntityIdentifier ei = new EntityIdentifier();
        ei.setScopingEntityId( eiString.substring(0, eiString.indexOf("^")) );
        ei.setBusinessId( eiString.substring(eiString.indexOf("^")+1, eiString.lastIndexOf("^")) );
        ei.setVersion( eiString.substring(eiString.lastIndexOf("^")+1) );
        return ei;
    }
    
    public static EntityIdentifier makeEI(String scopingEntityId, String businessId, String version) {
        EntityIdentifier ei = new EntityIdentifier();
        ei.setScopingEntityId(scopingEntityId);
        ei.setBusinessId(businessId);
        ei.setVersion(version);
        return ei;
    }
    
    public static String makeEIString ( EntityIdentifier ei ) {
        String scopingEntityId = ei.getScopingEntityId();
        String businessId = ei.getBusinessId();
        String version = ei.getVersion();
        return scopingEntityId + "^" + businessId + "^" + version;
    }

    /**
     * Checks if an input stream is gzipped.
     *
     * @param in
     * @return
     */
    public static boolean isGzipped(InputStream in) {
        if (!in.markSupported()) {
            in = new BufferedInputStream(in);
        }
        in.mark(2);
        int magic;
        try {
            magic = in.read() & 0xff | ((in.read() << 8) & 0xff00);
            in.reset();
        } catch (IOException e) {
            return false;
        }
        return magic == GZIPInputStream.GZIP_MAGIC;
    }
 
    /**
     * Checks if an containing entity is designated as gzipped.
     *
     * @param driData
     * @return
     */
    public static boolean isGzipDesignated(DataRequirementItemData driData) {
        boolean result = false;

        if (driData != null
                && driData.getDriId() != null
                && driData.getDriId().getContainingEntityId() != null
                && driData.getDriId().getContainingEntityId().getBusinessId() != null
                && driData.getDriId().getContainingEntityId().getBusinessId().toLowerCase().contains("gzip")) {
            result = true;
        }
        return result;
    }

    /**
     * Gunzip incoming payload before base64 encoding if containing entity id has the gzip
     * designation.
     *
     * @param driData
     * @return
     */
    public static List<byte[]> gUnzipData(DataRequirementItemData driData) {
        final String METHODNAME = "gUnzipData ";
        long start = System.nanoTime();
        List<byte[]> result = new ArrayList<byte[]>();

        SemanticPayload data = driData.getData();
        List<byte[]> base64EncodedPayload = data.getBase64EncodedPayload();
        for (byte[] chunk : base64EncodedPayload) {
            if (isGzipDesignated(driData)) {
                InputStream inputStream = new ByteArrayInputStream(chunk);
                if (isGzipped(inputStream)) {
                    GZIPInputStream gzipInputStream = null;
                    try {
                        byte[] buffer = new byte[1024];
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        int len;
                        gzipInputStream = new GZIPInputStream(inputStream);
                        while ((len = gzipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, len);
                        }
                        gzipInputStream.close();
                        result.add(outputStream.toByteArray());
                        gzipInputStream = null;
                    } catch (IOException e) {
                        throw new IllegalStateException("Error processing a designated gzipped payload: " + e.getMessage());
                    } finally {
                        if (gzipInputStream != null) {
                            try {
                                gzipInputStream.close();
                            } catch (IOException e) {
                                //nada
                            }
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Containing entity ID designated a gzipped payload but the load wasn't gzipped.");
                }
            } else {
                InputStream inputStream = new ByteArrayInputStream(chunk);
                if (isGzipped(inputStream)) {
                    throw new IllegalArgumentException("Containing entity ID not designating a gzipped payload but the load was gzipped.");
                } else {
                    result.add(chunk);
                }
            }
        }
        log.debug(METHODNAME + "duration=" + ((System.nanoTime() - start) / 1000000.0));
        return result;
    }

    /**
     * Checks if an containing entity is designated as gzipped.
     *
     * @param evaluationRequestDataItem
     * @return
     */
    public static boolean isGzipDesignated(EvaluationRequestDataItem evaluationRequestDataItem) {
        boolean result = false;

        if (evaluationRequestDataItem != null
                && evaluationRequestDataItem.getInputContainingEntityId() != null
                && evaluationRequestDataItem.getInputContainingEntityId().toLowerCase().contains("gzip")) {
            result = true;
        }
        return result;
    }

    /**
     * Gzip outgoing payload before base64 encoding if containing entity id has the gzip
     * designation.
     *
     * @param payload
     * @param evaluationRequestDataItem
     * @return
     */
    public static byte[] gZipData(byte[] payload, EvaluationRequestDataItem evaluationRequestDataItem) {
        final String METHODNAME = "gZipData ";
        long start = System.nanoTime();
        byte[] result;
        if (isGzipDesignated(evaluationRequestDataItem)) {
            GZIPOutputStream gzipOutputStream = null;
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(payload.length);
                gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                gzipOutputStream.write(payload);
                gzipOutputStream.close();
                result = byteArrayOutputStream.toByteArray();
                gzipOutputStream = null;
            } catch (IOException e) {
                throw new IllegalStateException("Error gzipping a payload: " + e.getMessage());
            } finally {
                if (gzipOutputStream != null) {
                    try {
                        gzipOutputStream.close();
                    } catch (IOException e) {
                        // nada
                    }
                }
            }
        } else {
            result = payload;
        }
        log.debug(METHODNAME + "duration=" + ((System.nanoTime() - start) / 1000000.0));
        return result;
    }

}
