
./bin/get --url <url> --username <user> --password <pass>
./bin/upload --url <url> --username <user> --password <pass>
./bin/delete --url <url> --username <user> --password <pass>


GET
    --cdm
        retrieve the full collection of Concept Determination Methods
    --ee
        retrieve the full collection of Execution Engines
    --km
        retrieve the full collection of Knowledge Modules (metadata)
    --ss
        retreive the full collection of Seamntic Signifiers
    --pp
        retrieve the full collection of Plugin Packages
        
    --cdmid <CDMID>
        retrieves the Concept Determination Method specified by CDMID
    --eeid <EEID>
        retrieves the Execution Engine metadata specified by EEID
    --kmid <KMID>
        retrieves the Knowledge Module metadata specified by KMID
    --kmid <KMID> --kmp
        retrieves the Knowledge Module package associated with the Knowledge Module specified by KMID
    --kmid <KMID> --sd
        retrieve the full collection of Supporting Data (metadata) associated with the Knowledge Module specified by KMID 
    --kmid <KMID> --sdid <SDID>
        retrieves the Supporting Data (metadata) specified by SDID associated with the Knowledge Module specified by KMID
    --kmid <KMID> --sdid <SDID> --sdp
        retrieves the Supporting Data package specified by SDID associated with the Knowledge Module specified by KMID
    --ssid <SSID>
        retrieves the Semantic Signifier metadata specified by the SSID
    --ppid <PPID>
        retrieve the Plugin Package metadata specified by the PPID

UPLOAD
    --file <file>
    --kmid <KMID> --kmp --file <Knowledge Module package file>
    --kmid <KMID> --sdid <SDID> --file <Supporting Data metadata file>
    --kmid <KMID> --sdid <SDID> --sdp --file <Supporting Data package file>
    
DELETE
    --cdmid <CDMID>
    --eeid <EEID>
    --kmid <KMID>
    --kmid <KMID> --kmp
    --kmid <KMID> --sdid <SDID>
    --kmid <KMID> --sdid <SDID> --sdp
    --ssid <SSID>
    --ppid <PPID>

