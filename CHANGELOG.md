# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
### Changed
### Deprecated
### Removed
### Fixed
### Security

## [v5.2] - 2024-11-25
### Fixed
- Calculadora de IBIS

## [v5.0] - 2024-11-23
### Added
- Se re-implementa el mecanismo de mocked y todas las calculadores.
- Se agregan todos los ejemplos de IBIS, MSP y ACS. Se agregan preguntas faltantes a la ontología.

## [v4.0] - 2024-11-18
### Changed
- Se implementan calculadores de ACS y MSP UY, generando interface de calculadora y modificando estructura de RiskCalculatorService.

## [v3.0] - 2024-11-17
### Changed
- Internacionalización según el accept-language pasado por parámetro. Ahora todo lo retornado por ontología lo tiene.
- Modificadas todas las funciones para cambiar igualdades por label y pasar a que sean por uri.

## [v2.1] - 2024-11-15
### Changed
- Estructura de generación de formulario según el modelo de riesgo. 
- Se agrega un orden para las secciones.
- Se obtiene el factor de riesgo de cada pregunta a partir de la relación AboutRiskFactor en vez de la anotación.

## [v2.0] - 2024-11-11
### Changed
- Se traen todas las personalizaciones desde Ontoforms para esta aplicación y así poder completar el formulario.

## [v1.6] - 2024-09-23
### Added
- Integración formulario - Calculadora.


## [v1.5] - 2024-09-15
### Added
- Se agrega soporte para los 10 casos mocked de mujeres y las preguntas relacionadas a sister.
- Selenium ejecuta un grid.
### Changed
- Se simplifican clases de cliente contra ontoforms y triplestore. Se utilizan propiedades para la url.
- Cambios en los servicios de mock para que dependan del riesgo.


## [v1.4] - 2024-09-09
### Added
- Se agrega el soporte para la selección de guías de recomendación y el filtrado utilizando las mismas.

## [v1.3] - 2024-09-08
### Added
- Servicio para poder modificar el uso de stub o uso real de calculadora IBIS. 

## [v1.2] - 2024-09-08
### Added
- Se modifica la estructura del servicio de cálculo de riesgo para independizarlo y formar estrategias.