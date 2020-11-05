package org.opencds.config.cli

import java.nio.file.Paths

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder

import org.opencds.config.cli.commands.CDMCommands
import org.opencds.config.cli.commands.Commands
import org.opencds.config.cli.commands.EECommands
import org.opencds.config.cli.commands.KMCommands
import org.opencds.config.cli.commands.PPCommands
import org.opencds.config.cli.commands.SSCommands
import org.opencds.config.cli.commands.TransferCommand
import org.opencds.config.cli.util.ResourceUtil
import org.opencds.config.client.rest.RestClient

import groovy.util.logging.Log4j2

@Log4j2
class RestCli {

    public void run(Closure command, String outfile) {
        def result = command()
        if (outfile && (result instanceof String || result instanceof InputStream)) {
            File file = new File(outfile)
            file.delete()
            file << result
        }
    }

    public static void main(String[] args) {
        def cli = buildCli(args)
        try {
            OptionAccessor options = cli.options()
            if (!options.url) {
                error('url option required')
            }
            if (!options.username) {
                error('username option required')
            }
            if (!options.password) {
                error('password option required')
            }
            String outfile = '' 
            if (options.outfile) {
                outfile = options.outfile
            }

            RestCli client = new RestCli()

            client.run(buildCommand(options), outfile)
        } catch (Exception e) {
            e.printStackTrace()
            log.error(e.message, e)
            cli.usage()
        }
    }

    static Map buildCli(String[] args) {
        def cli = new CliBuilder()
        cli._(longOpt: 'url', args: 1, 'Base URL to OpenCDS Configuration Rest Service')
        cli._(longOpt: 'username', args: 1, "Username")
        cli._(longOpt: 'password', args: 1, "Password")
        // HTTP methods (except for UPLOAD which means POST or PUT)
        cli.g(longOpt: 'get', args: 0, 'GET')
        cli.d(longOpt: 'delete', args: 0, 'DELETE')
        cli.u(longOpt: 'upload', args: 0, 'UPLOAD')
        // transfer a knowledge repository
        cli._(longOpt: 'transfer', args: 0, 'Transfer file-based configuration to REST service.')
        // content (source to UPLOAD, target to write to disk)
        cli._(longOpt: 'file', args: 1, "File to upload.")
        cli._(longOpt: 'folder', args: 1, 'Used with --transfer. Base folder to transfer artifacts.  Follows the same structure/naming as OpenCDS configuration.')
        cli._(longOpt: 'outfile', args: 1, 'File to which the output is written')
        // "business" methods, to be combined with HTTP methods above
        cli._(longOpt: 'cdm', args: 0, 'ConceptDeterminationMethod')
        cli._(longOpt: 'ee', args: 0, 'ExecutionEngine')
        cli._(longOpt: 'km', args: 0, 'KnowledgeModule')
        cli._(longOpt: 'kmp', args: 0, 'Knowledge Package')
        cli._(longOpt: 'sd', args: 0, 'SupportingData')
        cli._(longOpt: 'sdp', args: 0, 'SupportingData Package')
        cli._(longOpt: 'ss', args: 0, 'SemanticSignifier')
        cli._(longOpt: 'pp', args: 0, 'PluginPackage')
        // identifiers to assist "business methods"
        cli._(longOpt: 'cdmid', args: 1, 'ConceptDeterminationMethod ID')
        cli._(longOpt: 'eeid', args: 1, 'ExecutionEngine ID')
        cli._(longOpt: 'kmid', args: 1, 'KnowledgeModule ID')
        cli._(longOpt: 'sdid', args: 1, 'SupportingData ID')
        cli._(longOpt: 'ssid', args: 1, 'SemanticSignifier ID')
        cli._(longOpt: 'ppid', args: 1, 'PluginPackage ID')
        def options = cli.parse(args)
        [usage: { cli.usage() }, options: { options }]
    }

    static void error(String msg) {
        throw new IllegalArgumentException(msg)
    }

    static Closure buildCommand(OptionAccessor options) {
        Closure command
        if (options.get) {
            command = buildGet(options)
        } else if (options.upload) {
            command = buildUpload(options)
        } else if (options.delete) {
            command = buildDelete(options)
        } else if (options.transfer) {
            command = buildTransfer(options)
        }

        if (command == null) {
            throw new RuntimeException("Unknown command.")
        }
        Client c = ClientBuilder.newClient()
        RestClient restClient = new RestClient(c.target(options.url), options.username, options.password)
        command.curry(restClient)
    }

    static Closure buildGet(OptionAccessor options) {
        if (options.cdm) {
            return CDMCommands.getCollection
        } else if (options.cdmid) {
            return CDMCommands.get.curry(options.cdmid)
        } else if (options.ee) {
            return EECommands.getCollection
        } else if (options.eeid) {
            return EECommands.get.curry(options.eeid)
        } else if (options.km) {
            return KMCommands.getCollection
        } else if (options.kmid) {
            if (options.kmp) {
                return KMCommands.getPackage.curry(options.kmid) // returns an InputStream
            } else if (options.sd) {
                return KMCommands.getSDCollection.curry(options.kmid)
            } else if (options.sdid) {
                if (options.sdp) {
                    return KMCommands.getSDPackage.curry(options.kmid, options.sdid)
                } else {
                    return KMCommands.getSD.curry(options.kmid, options.sdid)
                }
            } else {
                return KMCommands.get.curry(options.kmid)
            }
        } else if (options.ss) {
            return SSCommands.getCollection
        } else if (options.ssid) {
            return SSCommands.get.curry(options.ssid)
        } else if (options.pp) {
            return PPCommands.getCollection
        } else if (options.ppid) {
            return PPCommands.get.curry(options.ppid)
        }
    }

    static Closure buildUpload(OptionAccessor options) {
        def resource = ResourceUtil.get(options.file)
        if (resource.type == 'binary') {
            if (options.kmid) {
                if (options.kmp) {
                    KMCommands.uploadPackage.curry(options.kmid, resource.input)
                } else if (options.sdid) {
                    if (options.sdp) {
                        KMCommands.uploadSDPackage.curry(options.kmid, options.sdid, resource.input)
                    }
                }
            }
        } else {
            return Commands.resolve(resource)
        }
    }

    static Closure buildDelete(OptionAccessor options) {
        if (options.cdmid) {
            return CDMCommands.delete.curry(options.cdmid)
        } else if (options.eeid) {
            return EECommands.delete.curry(options.eeid)
        } else if (options.kmid) {
            if (options.kmp) {
                return KMCommands.deletePackage.curry(options.kmid)
            } else if (options.sdid) {
                if (options.sdp) {
                    return KMCommands.deleteSDPackage.curry(options.kmid, options.sdid)
                } else {
                    return KMCommands.deleteSD.curry(options.kmid, options.sdid)
                }
            } else {
                return KMCommands.delete.curry(options.kmid)
            }
        } else if (options.ssid) {
            return SSCommands.delete.curry(options.ssid)
        } else if (options.ppid) {
            return PPCommands.delete.curry(options.ppid)
        }
    }
    
    static Closure buildTransfer(OptionAccessor options) {
        if (options.folder) {
            return TransferCommand.transfer.curry(Paths.get(options.folder))
        } else {
            throw new RuntimeException('folder option required')
        }
    }

}
