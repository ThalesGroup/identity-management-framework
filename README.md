# Identity Management Framework
## Overview
The Identity Management (IdM) Framework enables users and groups provisioning between SafeNet Trusted Access and other third-party applications and directories. It utilizes an open-source identity management and governance platform, midPoint, underneath.

Please refer to STA Documentation for more information about Identity Management Framework.

## Tags:
- `latest`[(IdM Framework GA Release version 1.2.0)](https://github.com/ThalesGroup/identity-management-framework/releases/tag/v1.2.0)

## Accessing the Image:
- download image without building:
```
$ docker pull artifactory.thalesdigital.io/docker-public/identity-management-framework/idm_framework:latest
```

## How to use:
For the steps to deploy, please follow [Deploying Identity Management Framework][def2].

## Examine the log file of the container:

docker logs -f &lt;container-name&gt;

## Access IdM Framework:
- URL: https://127.0.0.1/midpoint
- username: Administrator
- password: 5ecr3t

## Documentation:
The official documentation of the Identity Management Framework is available at [Thalesdocs](https://thalesdocs.com/sta/crns/identity_management_framework_crn/index.html)

## Contributing:

If you are interested in contributing to the Identity Management Framework project, start by reading the [Contributing guide](/CONTRIBUTING.md).

## License:

The project uses [Apache-2.0 license](/LICENSE).


[def]: https://docs.evolveum.com/midpoint/
[def2]: https://thalesdocs.com/sta/operator/user_synchronization/user_provisioning_through_safenet_trusted_access_idm_connector/idm_deployment/index.html
[def3]: https://thalesdocs.com/sta/operator/user_synchronization/user_provisioning_through_safenet_trusted_access_idm_connector/index.html