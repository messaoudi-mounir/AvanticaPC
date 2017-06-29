This tree_cep_engine\Engine\src\main\resources\dictionary\rigstate is used for storing dictionary of rigstate, which normally come from 

    https://{Hostname}/{PetroVaultName}/mbe/rigstatedictionary/{Dictionary's UUID}

For example, the two files are from 

    https://pvcloud2.petrolink.net/petrovault/mbe/rigstatedictionary/29526fa8-99b8-40b3-95ad-ca749cb28568

if you need json file you put  in HTTP header
 
    Accept : application/json 

For xml version you use

    Accept : application/xml

This files should be able to be pushed by Orchestration UI to MBE engine.


# Reference #
Schema : Petrolink.Analytix.Mbe.Messages.RigstateDefinitionDictionary