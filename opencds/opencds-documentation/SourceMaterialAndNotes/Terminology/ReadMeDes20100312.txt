This zip file was constructed from an unzipped and installed DTS 3.5 for Oracle.

The configuration was extensively modified to point to a [separately installed] Intersystems Cache database.  

These modifications are fully documented at 
\\cfmsrv1\administration\Products\TerminologyServices\Research into Apelon and Multum Stage2 20100309.doc

The zip file also contains the following:
1. Apelon Sample Data in the \DTS 3.5 Cache\bin\data\ directory, ready to load.
2. Apelon Source code for plugins, etc.

To load the sample data, you will have to setup Cache as follows:
namespace DTS
database dts
user dts (initially with superuser privileges)
password dts
with other settings at defaults