# Distributor Permissions - Kotlin Spring WebFlux

## Table of Contents

1. [Run](#run)
2. [API Reference](#api-reference)
    - [Create Distributor](#create-distributor)
    - [List Distributors](#list-distributors)
    - [Get Distributor by ID](#get-distributor-by-id)
    - [Add Permission](#add-permission)
    - [Check Permission](#check-permission)
    - [Delete Distributor](#delete-distributor)

***

## Run

**Requirements:** JDK 17+, Gradle 8.1

Start the application:

    ./gradlew bootRun

***

## API Reference

### Create Distributor

**Endpoint:**  
`POST /api/distributors`

**Description:**  
Creates a distributor.

**Request Body:**

    {
      "id": "D1",
      "parentId": null
    }

**cURL (Windows PowerShell):**

    curl -X POST -H "Content-Type: application/json" -d "{\"id\":\"D1\",\"parentId\":null}" http://localhost:8080/api/distributors

**Response:**

    {
      "id": "D1",
      "parentId": null,
      "permissions": []
    }

***

### List Distributors

**Endpoint:**  
`GET /api/distributors`

**Description:**  
Returns all distributors.

**cURL:**

    curl http://localhost:8080/api/distributors

**Response:**

    [
      {
        "id": "D1",
        "parentId": null,
        "permissions": []
      }
    ]

***

### Get Distributor by ID

**Endpoint:**  
`GET /api/distributors/{id}`

**Example:**

    curl http://localhost:8080/api/distributors/D1

***

### Add Permission

**Endpoint:**  
`POST /api/distributors/{id}/permissions`

**Description:**  
Adds a permission to a distributor.  
Sub-distributors cannot include regions outside their parent’s allowed regions.

**Request Body (Include Example):**

    {
      "type": "INCLUDE",
      "regionCode": "INDIA"
    }

**Request Body (Exclude Example):**

    {
      "type": "EXCLUDE",
      "regionCode": "KARNATAKA-INDIA"
    }

**cURL (Windows PowerShell):**

    curl -X POST -H "Content-Type: application/json" -d "{\"type\":\"INCLUDE\",\"regionCode\":\"INDIA\"}" http://localhost:8080/api/distributors/D1/permissions

**Response:**

    {
      "id": "D1",
      "parentId": null,
      "permissions": [
        {"type": "INCLUDE", "regionCode": "INDIA"}
      ]
    }

***

### Check Permission

**Endpoint:**  
`GET /api/distributors/{id}/can/{region}`

**Description:**  
Checks if a distributor can distribute in a given region (code or name).

**Example:**

    curl http://localhost:8080/api/distributors/D1/can/CHENNAI-TAMILNADU-INDIA

**Response:**

    YES

***

### Delete Distributor

**Endpoint:**  
`DELETE /api/distributors/{id}`

**Description:**  
Removes a distributor.

**Example:**

    curl -X DELETE http://localhost:8080/api/distributors/D1
