package org.opencds.config.cli.commands

import groovy.util.slurpersupport.GPathResult;

import java.nio.file.Path
import java.nio.file.Paths

import org.opencds.config.cli.util.IdUtil
import org.opencds.config.cli.util.ResourceUtil
import org.opencds.config.client.rest.RestClient

class TransferCommand {
    private static final String CDMS = 'conceptDeterminationMethods'
    private static final String SDS = 'supportingData'
    private static final String KM = 'knowledgeModule'
    private static final String KMS = 'knowledgeModules'
    private static final String KP = 'knowledgePackages'
    private static final String PLUGINS = 'plugins'
    private static final String PKGS = 'packages'
    
    static def transfer = {Path location, RestClient restClient ->
        File configLoc = location.toFile()
        for (File file : configLoc.listFiles()) {
            if (file.directory) {
                if (file.name == CDMS) {
                    println "Location.toString() : ${location.toString()}"
                    File cdmDir = Paths.get(location.toString(), CDMS).toFile()
                    for (File cdm : cdmDir.listFiles()) {
                        transferFile(cdm, restClient)
                    }
                } else if (file.name == SDS) {
                    File sdDir = Paths.get(location.toString(), SDS).toFile()
                    for (File sd : sdDir.listFiles()) {
                        def sdresource = transferFile(sd)
                        transferSDPkg(sdresource, location, restClient)
                    }
                } else if (file.name == PLUGINS) {
                    File pluginDir = Paths.get(location.toString(), PLUGINS).toFile()
                    for (File plugin : pluginDir.listFiles()) {
                        if (plugin.directory) {
                            println "Resource ${plugin.absolutePath} is a folder."
                        } else {
                            transferFile(plugin, restClient)
                        }
                    }
                }
            } else {
                def resource = transferFile(file, restClient)
                if (resource.type == KM) {
                    transferKMPkg(resource, location, restClient)
                } else if (resource.type == KMS) {
                    transferKMPkgs(resource, location, restClient)
                }
            }
        }
    }
    
    private static Map transferFile(File file, RestClient restClient) {
        def resource = ResourceUtil.get(file)
        def cmd = Commands.resolve(resource)
        if (cmd == null) {
            println "Unknown resource type: ${file.name}"
        } else {
            cmd(restClient)
            println "Transferred resource: \n\t'${resource.type}' in \n\t'${file.absolutePath}'"
        }
        return resource
    }
    
    private static transferKMPkgs(Map resource, Path location, RestClient restClient) {
        for (GPathResult km : resource.xml.knowledgeModule.list()) {
            def kmres = resource.clone()
            kmres.xml = km
            transferKMPkg(kmres, location, restClient)
        }
    }
    
    private static transferKMPkg(Map resource, Path location, RestClient restClient) {
        def kmid = IdUtil.entityid(resource.xml.identifier)
        def pkgid = resource.xml.'package'.packageId.text()
        File file = Paths.get(location.toString(), KP, pkgid).toFile()
        if (file.exists() && !file.directory) {
            KMCommands.uploadPackage(kmid, new FileInputStream(file), restClient)
            println "Transferred resource: \n\tKMID: ${kmid}; Type: '${resource.type}' in \n\t'${file.absolutePath}'"
        } else {
            println "WARNING: File '${file.absolutePath}' is a folder or does not exist. (KMID: $kmid; Resource: $pkgid)"
        }
    }
    
    private static transferSDPkg(Map resource, Path location, RestClient restClient) {
        def kmid = IdUtil.entityid(resource.xml.kmId)
        def sdid = resource.xml.identifier.text()
        def pkgid = resource.xml.'package'.packageId.text()
        File file = Paths.get(location.toString(), SDS, PKGS).toFile()
        KMCommands.uploadSDPackage(kmid, sdid, new FileInputStream(file), restClient)
        println "Transferred resource: \n\t'${resource.type}' in \n\t'${file.absolutePath}'"
    }
}
