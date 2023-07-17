---
layout: sub-navigation
order: 1
title: OIDV
---

### Repo

<a href="https://gitlab.com/dwp/health/pip-apply/components/ms-application-manager" target="_blank">MS Application Manager Repo</a>

### Tech Stack

- Java Spring Boot Microservice
- Mongo DB
- Docker

### Purpose

The purpose of the changes within the Application Manager is to provide a new internal API endpoint which - for a given nino - is to return an application id stored against the nino. This is called from the **ms-identity-status** microservice, with the intention of writing the application id into the identity collection within that service when storing an identity outcome for that given claimant.

### Main Tasks

- A new endpoint created named /v1/application/matcher
- For a given nino, find the application id associated to that nino:
  - If one application is found, return the application_id - this is the only output returned from the endpoint
  - If zero applications are found, return the application_id as a null value
  - If more than one application is found, return a 409 Conflict response. This is because we would not know - as it stands - which application to mark the identity as verified against (this longer term needs more analysis to provide a long term solution - especially when we allow a Claimant to do Reclaims).

### Flow Diagram

<a href="../images/oidv-ms-application-manager.png"><img src="../images/oidv-ms-application-manager.png" alt="MS Application Manager"/></a>

### Error Scenarios

As per main task section:
- 409 error if we receive 2 or more application ids for a given nino

### Mocked Responses

#### by Nino

| Nino      | Response Code                               | 
|-----------|---------------------------------------------|
| RN000003A | 500                                         |
| RN000001A | 409                                         |
| RN000002A | 200 - with **null** application id returned |
| Any other | 200 - with application id returned          |
