ll notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased
    - Use AdviceController
    - Add Swagger UI

[0.0.4] - 2026-08-26
## Added
    - CreditService updateSatus api method
    - Added User Role CREDIT_MANAGER
    - Added api method: /credit-service/v1/api/info/{id}
    - Added api method: /credit-service/v1/api/status/update/{id}

[0.0.3] - 2026-08-25
### Fixed
    - use CreditEntity.id for EqualsAndHash

### Changed
    - creditService .info() -> getInfo()
    - use HttpStatus instead HttpRequstResponse.SC_{CODE}
    - use abstract RestException instead of handle every exception individually. (Polymorphism)

[0.0.2] - 2026-08-21
### Added
    - Map struct

[0.0.1] - 2026-08-20
### Added
    - Add JWT authorization
    - Add CreditController.create and depends