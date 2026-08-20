ll notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased
    - Use records for DTO as a gold Java standard since Java 14(2020). LTS 17(2021)
    - Use AdviceController
    - Use MapStruct

[0.0.3] - 2026-08-19
### Added
    - Add CreditProxyContoller. Simple RestTemplate proxy

[0.0.2] - 2026-08-18
 ### Fixed
    - PasswordConfig change annotation from @Service to @Configuration
    - Use 6 annotated Dto-s. Boilerplate, support inheritance, not convinient(callSuper, SuperBuilder(experimental)). Should bereplaced by records. 
    - Move logic to services
    - Remove ApigatewayApplicationTests
    - Remove IF [NOT] EXISTS from migrations.
    - Add application-test.yaml