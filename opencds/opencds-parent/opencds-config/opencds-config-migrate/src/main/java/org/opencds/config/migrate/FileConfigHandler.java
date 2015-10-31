package org.opencds.config.migrate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.model.KMId;

public class FileConfigHandler implements ConfigHandler {
    private static final Log log = LogFactory.getLog(FileConfigHandler.class);
    private static final String CONFIG_SCHEMA_URL = "org.opencds.config.schema";

    private static final String CDM_DIR = "conceptDeterminationMethods";
    private static final String CDM_FILE = "cdm.xml";
    private static final String EE_FILE = "executionEngines.xml";
    private static final String SS_FILE = "semanticSignifiers.xml";
    private static final String KM_FILE = "knowledgeModules.xml";
    private static final String KP_DIR = "knowledgePackages";
    
    protected static final String XML_SUFFIX = ".xml";

    private Path path;
    private JAXBContext context;

    private FileConfigHandler(Path path) throws Exception {
        this.path = path;
        context = JAXBContext.newInstance(CONFIG_SCHEMA_URL);
    }

    public static FileConfigHandler createForCDM(Path targetPath, boolean splitCDM) throws Exception {
        if (splitCDM) {
            return new SplitFileConfigHandler(Paths.get(targetPath.toString(), CDM_DIR));
        } else {
            return new FileConfigHandler(Paths.get(targetPath.toString(), CDM_DIR, CDM_FILE));
        }
    }

    public static FileConfigHandler createForEE(Path targetPath) throws Exception {
        return new FileConfigHandler(Paths.get(targetPath.toString(), EE_FILE));
    }

    public static FileConfigHandler createForSS(Path targetPath) throws Exception {
        return new FileConfigHandler(Paths.get(targetPath.toString(), SS_FILE));
    }

    public static FileConfigHandler createForKM(Path targetPath) throws Exception {
        return new FileConfigHandler(Paths.get(targetPath.toString(), KM_FILE));
    }

    public static Map<Pair<KMId, String>, FileConfigHandler> createForKP(Path targetPath, List<Pair<KMId, String>> pairs)
            throws Exception {
        Map<Pair<KMId, String>, FileConfigHandler> fsbs = new HashMap<>();
        for (Pair<KMId, String> pair : pairs) {
            String filename = Paths.get(pair.getRight()).toFile().getName();
            fsbs.put(pair, new FileConfigHandler(Paths.get(targetPath.toString(), KP_DIR, filename)));
        }
        return fsbs;
    }

    private void write(InputStream inputStream) throws Exception {
        int size = 2 * 1024;
        byte[] buffer = new byte[size];
        int bytesRead = 0;
        int offset = 0;
        try (OutputStream out = getOutputStream()) {
            while ((bytesRead = inputStream.read(buffer, offset, size)) != -1) {
                out.write(buffer, offset, bytesRead);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public <T> void write(T t) throws Exception {
        if (t instanceof InputStream) {
            write((InputStream) t);
        } else {
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(t, getOutputStream());
        }
    }

    @Override
    public <T> void write(T t, String baseName) throws Exception {
        if (t instanceof InputStream) {
            write((InputStream) t);
        } else {
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(t, getOutputStream(baseName));
        }
    }

    @Override
    public OutputStream getOutputStream() throws Exception {
        // targetPath represents the file
        ensureExists(path.getParent().toFile());
        return new FileOutputStream(path.toFile());
    }
    
    protected void ensureExists(File targetDir) {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
            log.info("Created target dir: " + targetDir.getAbsolutePath());
        }
    }

    @Override
    public OutputStream getOutputStream(String baseName) throws Exception {
        // targetPath represents the file
        ensureExists(path.getParent().toFile());
        Path newPath = Paths.get(path.toString(), baseName + XML_SUFFIX);
        return new FileOutputStream(newPath.toFile());
    }
    
    private static class SplitFileConfigHandler extends FileConfigHandler {
        private Path basePath;
        
        public SplitFileConfigHandler(Path path) throws Exception {
            super(path);
            basePath = path;
        }
        
        @Override
        public OutputStream getOutputStream(String baseName) throws Exception {
            // targetPath represents the file
            ensureExists(basePath.toFile());
            Path newPath = Paths.get(basePath.toString(), baseName + XML_SUFFIX);
            return new FileOutputStream(newPath.toFile());
        }
        
    }

}
