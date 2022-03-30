# UniProt Open-Targets
This project contains the source code for generating the disease association evidence data which is used by the Open-Targets platform.

* https://www.targetvalidation.org/
* https://www.opentargets.org/

## Generating a new data release
To generate a new data release, run the following script:
```
% cd src/bin
% ./OpenTargetsCreator -all
```
This will create several files:
1. uniprot-valid.json - contains a JSON object representing a disease association on each line.
2. open-targets-*.log - the log file reporting on the progress of the JSON generation.
3. cttv011-DD-MM-YYYY.json.gz - a zipped file containing uniprot-valid.json. This is the file that can be submitted to the Open-Targets CoreDB team. Note: this will only be generated if the JSON generation completed successfully.

Please view the logs generated to see whether many errors are being encountered. If unusual errors are seen, then fix the codebase and rerun the script.
### Log contents
The contents of the log files often contains the following, which is not a problem:
* WARN  u.a.e.u.ot.mapper.FFOmim2EfoMapper - No mapping found for OMIM: XXXXXX

## Submitting a data release

After data has been successfully generated, it needs to be deposited to the Open-Targets CoreDB team, in their [Google Bucket location](https://console.cloud.google.com/storage/browser/otar011-uniprot/). 

## Contacting the Open-Targets CoreDB team
Should you need to contact the CoreDB team, they can be emailed here: [data@opentargets.org](mailto:data@opentargets.org)

## Issues and resolution
1. Please make sure the json version is up-to-date in DefaultBaseFactory.java (CTTV_SCHEMA_VERSION) as required by open target team
2. Please make sure the japi version in pom is pointing to latest private repo - https://mvnrepository.com/artifact/uk.ac.ebi.uniprot/japi?repo=ebi-repo
3. Rerun the command if it fails for java.net.SocketTimeoutException: Read timed out
4. Python version 2 required
