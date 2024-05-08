/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.config.cli

import groovy.cli.picocli.CliBuilder
import groovy.cli.picocli.OptionAccessor
import groovy.util.logging.Commons
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import org.opencds.config.cli.commands.CDMCommands
import org.opencds.config.cli.commands.Commands
import org.opencds.config.cli.commands.EECommands
import org.opencds.config.cli.commands.KMCommands
import org.opencds.config.cli.commands.PPCommands
import org.opencds.config.cli.commands.SDCommands
import org.opencds.config.cli.commands.SSCommands
import org.opencds.config.cli.commands.TransferCommand
import org.opencds.config.cli.util.ResourceUtil
import org.opencds.config.client.rest.RestClient

import java.nio.file.Paths

@Commons
class RestCli {

    public void run(Closure command, List<String> urls, String username, String password, OutputStream out) {
        for (String url : urls) {
            Client c = ClientBuilder.newClient()
            RestClient restClient = new RestClient(c.target(url), username, password)
            def result = command(restClient)
            if (out && (result instanceof String || result instanceof InputStream)) {
                out << result
            }
        }
    }

    public static void main(String[] args) {
        def cli = buildCli(args)
        try {
            OptionAccessor options = cli.options()
            def config = null
            if (options.config) {
                config = new ConfigSlurper().parse(Paths.get(options.config).toUri().toURL())
            }
            println config
            // TODO: Override config with command-line options.
            List<String> urls = []
            String username = ''
            String password = ''

            if (config?.cli?.url) {
                urls << config.cli.url
            }
            if (config?.cli?.urls) {
                urls.addAll(config.cli.urls)
            }
            username = config?.cli?.username
            password = config?.cli?.password

            if (options.url) {
                urls << options.url
            }
            if (options.username) {
                username = options.username
            }
            if (options.password) {
                password = options.password
            }

            if (!urls) {
                error('url option required')
            }
            if (!username) {
                error('username option required')
            }
            if (!password) {
                error('password option required')
            }
            def outfile
            if (options.outfile) {
                File file = null
                file = new File(options.outfile)
                file.delete()
                outfile = new FileOutputStream(file)
            } else {
                outfile = System.out
            }

            RestCli client = new RestCli()

            client.run(buildCommand(options), urls, username, password, outfile)
        } catch (Exception e) {
            e.printStackTrace()
            log.error(e.message, e)
            cli.usage()
        }
    }

    static Map buildCli(String[] args) {
        def cli = new CliBuilder()
        cli._(longOpt: 'config', args: 1, 'File containing basic configuration (url, urls, username, password.')
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
        command
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
            } else {
                return KMCommands.get.curry(options.kmid)
            }
        } else if (options.sd) {
            return SDCommands.getCollection
        } else if (options.sdid) {
            if (options.sdp) {
                return SDCommands.getPackage.curry(options.sdid)
            } else {
                return SDCommands.get.curry(options.sdid)
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
                }
            } else if (options.sdid) {
                if (options.sdp) {
                    SDCommands.uploadPackage.curry(options.sdid, resource.input)
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
            } else {
                return KMCommands.delete.curry(options.kmid)
            }
        } else if (options.sdid) {
            if (options.sdp) {
                return SDCommands.deletePackage.curry(options.kmid, options.sdid)
            } else {
                return SDCommands.delete.curry(options.kmid, options.sdid)
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
