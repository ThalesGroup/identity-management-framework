# Identity Management Framework 

## Overview

The Identity Management (IdM) Framework enables users and groups provisioning between SafeNet Trusted Access and other third-party applications and directories. It utilizes an open-source identity management and governance platform, [midPoint][def], underneath.

Please refer to STA documentation for more information about Identity Management Framework. 

## Accessing the Image

To load the image the &#39;docker pull&#39; command should be used. The image name should be supplied with the pull command, along with a tag that corresponds to the image version number. For example:

docker pull artifactory.thalesdigital.io/docker-public/identity-management-framework/idm_framework:1.1.0

## How to use
For the steps to deploy, please follow [Deploying Identity Management Framework][def2]

## Examine the log file of the container

docker logs -f &lt;container-name&gt;

## Documentation

The official documentation of the Identity Management Framework is available at [Thalesdocs][def3].

## Contributing

If you are interested in contributing to the STA IdM connector project, start by reading the [Contributing guide](/CONTRIBUTING.md).

## License

The project uses [Apache-2.0 license](/LICENSE).


[def]: https://docs.evolveum.com/midpoint/
[def2]: https://thalesdocs.com/sta/operator/user_synchronization/user_provisioning_through_safenet_trusted_access_idm_connector/idm_deployment/index.html
[def3]: https://thalesdocs.com/sta/operator/user_synchronization/user_provisioning_through_safenet_trusted_access_idm_connector/index.html
