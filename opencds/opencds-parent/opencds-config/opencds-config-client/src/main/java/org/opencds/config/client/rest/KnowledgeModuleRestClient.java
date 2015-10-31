package org.opencds.config.client.rest;

import org.opencds.config.client.KnowledgeModuleClient;
import org.opencds.config.client.rest.util.PathUtil;

public class KnowledgeModuleRestClient implements KnowledgeModuleClient {
    private static final String PATH = "knowledgemodules";
    private static final String PKG_PATH = "package";
    private static final String SD_PATH = "supportingdata";
    
    private RestClient restClient;
    
    public KnowledgeModuleRestClient(RestClient restClient) {
        this.restClient = restClient;
    }
    
    @Override
    public <T> T getKnowledgeModules(Class<T> clazz) {
        return restClient.get(PATH, clazz);
    }
    
    @Override
    public <T> void putKnowledgeModules(T kms) {
        restClient.put(PATH, kms);
    }

    @Override
    public <T> T getKnowledgeModule(String kmId, Class<T> clazz) {
        return restClient.get(PathUtil.buildPath(PATH, kmId), clazz);
    }

    @Override
    public <T> void postKnowledgeModule(T knowledgeModule) {
        restClient.post(PATH, knowledgeModule);
    }
    
    @Override
    public <T> void putKnowledgeModule(String kmId, T knowledgeModule) {
        restClient.put(PathUtil.buildPath(PATH, kmId), knowledgeModule);
    }

    @Override
    public void deleteKnowledgeModule(String kmId) {
        restClient.delete(PathUtil.buildPath(PATH, kmId));
    }

    @Override
    public <T> T getKnowledgePackage(String kmId, Class<T> clazz) {
        return restClient.getBinary(PathUtil.buildPath(PATH, kmId, PKG_PATH), clazz);
    }

    @Override
    public <T> void putKnowledgePackage(String kmId, T knowledgePackage) {
        restClient.putBinary(PathUtil.buildPath(PATH, kmId, PKG_PATH), knowledgePackage);
    }

    @Override
    public void deleteKnowledgePackage(String kmId) {
        restClient.delete(PathUtil.buildPath(PATH, kmId, PKG_PATH));
    }

    @Override
    public <T> T getSupportingData(String kmId, Class<T> clazz) {
        return restClient.get(PathUtil.buildPath(PATH, kmId, SD_PATH), clazz);
    }

    @Override
    public <T> T getSupportingData(String kmId, String supportingDataId, Class<T> clazz) {
        return restClient.get(PathUtil.buildPath(PATH, kmId, SD_PATH, supportingDataId), clazz);
    }

    @Override
    public <T> void postSupportingData(String kmId, T supportingData) {
        restClient.post(PathUtil.buildPath(PATH,  kmId, SD_PATH), supportingData);
    }
    
    @Override
    public <T> void putSupportingData(String kmId, String supportingDataId, T supportingData) {
        restClient.put(PathUtil.buildPath(PATH, kmId, SD_PATH, supportingDataId), supportingData);
    }

    @Override
    public void deleteSupportingData(String kmId, String supportingDataId) {
        restClient.delete(PathUtil.buildPath(PATH, kmId, SD_PATH, supportingDataId));
    }

    @Override
    public <T> T getSupportingDataPackage(String kmId, String supportingDataId, Class<T> clazz) {
        return restClient.getBinary(PathUtil.buildPath(PATH, kmId, SD_PATH, supportingDataId, PKG_PATH), clazz);
    }

    @Override
    public <T> void putSupportingDataPackage(String kmId, String supportingDataId, T supportingDataPackage) {
        restClient.putBinary(PathUtil.buildPath(PATH, kmId, SD_PATH, supportingDataId, PKG_PATH), supportingDataPackage);
    }

    @Override
    public void deleteSupportingDataPackage(String kmId, String supportingDataId) {
        restClient.delete(PathUtil.buildPath(PATH, kmId, SD_PATH, supportingDataId, PKG_PATH));
    }

}
