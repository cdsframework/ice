/*
 * Copyright 2015-2020 OpenCDS.org
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
import org.opencds.config.cli.commands.CDMTransformCommand

@Commons
class CdmCli {

	Closure command

	public CdmCli(Closure command) {
		this.command = command;
	}

    public void run() {
		command()
    }

    public static void main(String[] args) {
        def cli = buildCli(args)
        try {
            OptionAccessor options = cli.options()

			String user
			if (options.user) {
				user = options.user
			} else {
				throw new RuntimeException('user option is required')
			}

			def inFile
			if (options.file) {
				File iFile = new File(options.file)
				inFile = new FileInputStream(iFile)
			} else {
				throw new RuntimeException('file option is required')
			}

            def outFile
            if (options.outfile) {
                File oFile = new File(options.outfile)
                oFile.delete()
                outFile = new FileOutputStream(oFile)
            } else {
                throw new RuntimeException("outfile option is required")
            }
			CDMTransformCommand command = new CDMTransformCommand(inFile, outFile, user)
			Closure cmd
			if (options.'psv-to-xml') {
				cmd = command.psvToCdm
			} else if (options.'xml-to-psv') {
				cmd = command.cdmToPsv
			} else {
				throw new RuntimeException('psv-to-xml or xml-to-psv option is required')
			}

            CdmCli client = new CdmCli(cmd)

            client.run()
        } catch (Exception e) {
            e.printStackTrace()
            log.error(e.message, e)
            cli.usage()
        }
    }

    static Map buildCli(String[] args) {
        def cli = new CliBuilder()
		cli._(longOpt: 'user', args: 1, "User ID")
        cli._(longOpt: 'file', args: 1, "File to upload.")
        cli._(longOpt: 'outfile', args: 1, 'File to which the output is written')
		cli._(longOpt: 'psv-to-xml', args: 0, 'xml input to pipe delimited output')
		cli._(longOpt: 'xml-to-psv', args: 0, 'pipe delimited input to xml output')
        def options = cli.parse(args)
        [usage: { cli.usage() }, options: { options }]
    }

}
