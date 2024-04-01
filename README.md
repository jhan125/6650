# Project 3: Multiple Multi-threaded Key-Value Store


## Project Overview

This project aims to enhance a KV store service by distributing it across five servers for better reliability and implementing a two-phase commit protocol to maintain data consistency across these replicas during update operations.

It has two main requirements:
1) **Replicate Key-Value Store Server Across 5 Instances:**
- System needs to transform from using a single Key-Value (KV) Store server to having KV store replicated across 5 separate server instances. This change is aimed at increasing server bandwidth and ensuring that your service remains available.
- Clients interacting with the service should not need to undergo significant changes in their code. They should be able to contact any of the 5 KV store replicas to perform GET operations (retrieving data) and expect to receive consistent data from any replica they connect to.
- Clients should also be able to perform PUT operations (updating or adding data) and DELETE operations (removing data) on any of the 5 replicas.

2) **Ensure Consistency Across Replicas with Two-Phase Commit Protocol for Updates**:
- When a client issues a PUT or DELETE operation on any server replica, system needs to ensure that all the KV stores across the replicas remain consistent with these changes. 
- To achieve this consistency, a two-phase commit (2PC) protocol needs to be implemented for updates. This protocol helps coordinate the update process across all replicas to ensure that either all replicas commit the update or none do, thereby maintaining consistency. 
- Although we can assume that server failures will not occur (thereby not stalling the 2PC process), it's recommended to defensively program in 2PC implementation to include timeouts. This precaution ensures that your system can handle unexpected scenarios gracefully.

This document outlines the system architecture, project structure, design considerations,
instructions on how to run this program, tests on local environments, as well as limitations and future iterations.

## System Architecture

The system consists of two main components: the server and the client. The server hosts the key-value store, while the
client provides an interface for users to interact with the store via RPC.

### Server

- **Concurrent Key-Value Store**: At the heart of the server is a thread-safe key-value store implemented using Java's
  ConcurrentHashMap. This choice ensures thread-safe operations without the need for explicit synchronization.
- **Multi-Threading**: The server is designed to be multi-threaded, capable of handling multiple client requests
  concurrently. This is achieved by using a thread pool to manage incoming requests, ensuring efficient resource use and
  responsiveness.
- **RPC Mechanism**: The server exposes its functionalities through an RPC interface, allowing clients to perform PUT,
  GET, and DELETE operations remotely. Java RMI or Apache Thrift can be used to implement the RPC mechanism, depending
  on the project requirements and language compatibility.

### Client

- **RPC Communication**: The client communicates with the server using RPC, invoking remote methods as if they were
  local. This simplifies the client-server interaction and abstracts the complexity of network communication.
- **Concurrency Support**: The client is designed to support concurrent operations, allowing multiple instances or
  threads within a single instance to communicate with the server simultaneously.

## Project Structure

- **`src/`**: Contains all source code for the project.
- **`src/server/`**: Includes server-side logic such as `ServerApp`, the `KeyValue` store
  implementation, `KeyValueRpcImpl` for handling RPC requests, and `ServerLogger` for server-side logging utilities.
- **`src/client/`**: Contains client-specific code including the `ClientApp` and tasks for different
  operations (`ClientGetTask`, `ClientPutTask`, `ClientDeleteTask`), plus a `ClientLogger` for client-side logging
  utilities.
- **`src/api/`**: Holds interfaces and shared data models like `KeyValueResponse.java` and `KeyValueRpc.java` that
  facilitate RPC communication.
- **`docs/`**: Documentation files, including class diagram and project specifications.
- **`res/`**: Testing screenshots, including concurrent client requests and interactions on both server and client
  sides.

```
.
├── ExecutiveSummary.md
├── README.md
├── docs
│   ├── Project1_Description.md
│   └── Project2_Description.md
├── res
│   ├── TestOperationsSuccess
│   │   ├── client_delete_success.png
│   │   ├── client_exit_success.png
│   │   ├── client_get_success.png
│   │   ├── client_put_success.png
│   │   ├── client_update_success.png
│   │   ├── server_delete_success.png
│   │   ├── server_get_success.png
│   │   ├── server_put_success.png
│   │   └── server_update_success.png
│   ├── testClientInvalidInput
│   │   ├── client_delete_invalidInput.png
│   │   ├── client_get_invalidInput.png
│   │   ├── client_invalidRequest.png
│   │   └── client_put_invalidInput.png
│   ├── testConcurrentRequests
│   │   ├── client_5concurrentRequests.png
│   │   └── server_5concurrentRequests.png
│   ├── testMultiClientsRequests
│   │   ├── client1_step1_putKV.png
│   │   ├── client1_step3_getKV.png
│   │   ├── client2_step2_deleteKV.png
│   │   ├── client2_step3_getKV.png
│   │   └── server_logs.png
│   ├── testOperationsFail
│   │   ├── client_delete_keyNotFound.png
│   │   ├── client_get_keyNotFound.png
│   │   ├── server_delete_keyNotFound.png
│   │   └── server_get_keyNotFound.png
│   ├── testSetup
│   │   ├── client_noPreload_start.png
│   │   ├── client_preload_start1.png
│   │   ├── client_preload_start2.png
│   │   ├── server_noPreload_start.png
│   │   └── server_preload_start.png
│   └── testTimeout
│       ├── client_timeout.png
│       └── server_timeout.png
└── src
    ├── api
    │   ├── KeyValueResponse.java
    │   └── KeyValueRpc.java
    ├── client
    │   ├── ClientApp.java
    │   ├── ClientDeleteTask.java
    │   ├── ClientGetTask.java
    │   ├── ClientLogger.java
    │   ├── ClientPutTask.java
    │   └── Dockerfile
    ├── client_noPreload_1.sh
    ├── client_noPreload_2.sh
    ├── client_preload.sh
    ├── server
    │   ├── Dockerfile
    │   ├── KeyValue.java
    │   ├── KeyValueRpcImpl.java
    │   ├── ServerApp.java
    │   └── ServerLogger.java
    └── server.sh

14 directories, 52 files
```

## Design Considerations

1. **Leverage RMI for Simplified Remote Communication**: By utilizing Java`RMI` framework to abstract the complexity of
   network programming, my design meets the requirement to transition from socket-based communication to a more
   streamlined, object-oriented approach. This choice thus significantly simplifies the development of remote interactions
   between the client and server.

2. **Managed Key-Value Pair Access with Mutex Locks for Thread-safety**: By transitioning from a
   easily-implemented `ConcurrentHashMap` to a more controlled access model, my design chose to use `ReentrantLock` to
   safeguard critical sections during write operations (especially the put and delete methods). According to TA's
   guidance, this shift is based on the anticipation of future enhancements that may introduce additional checks or
   function calls within these operations, which might potentially complicate thread-safe access. Therefore, by
   explicitly locking these critical sections, my design ensures sequential execution of write operations. In this way,
   it can not only maintain the integrity and reliability of the data store in the face of concurrent access but also
   provide the flexibility to accommodate more complex operations as the project evolves.

3. **Facilitate Concurrent Operations through Multi-threading**: By encapsulating client-side operations within tasks
   implemented as `Runnable` (e.g., `ClientPutTask`, `ClientDeleteTask`, and `ClientGetTask` ), my design supports
   simultaneous execution in separate threads. This approach aligns with the requirement for the server to handle multiple
   outstanding client requests simultaneously.

4. **Effective Thread Management:**: By structuring client operations as discrete tasks and managing them with threads,
   my design ensures that the operations are executed in an orderly and controlled manner thus further enhances the
   responsiveness and throughput of the program.

5. **Unified Response Handling with `KeyValueResponse` Class**: By implementing a unified response
   class `KeyValueResponse` rather than using separate classes `Response` and `Protocol` in project 1, my design
   simplifies response handling and enhances error reporting, thus avoiding the need for multiple protocol-specific
   classes.

6. **Flexible RMI Registry Management**: By modifying the Dockerfiles and shell scripts to accept an RMI port as an
   environment variable, my design allows developers to easily configure the RMI registry port with Docker. In other
   words, deployers can easily adjust the RMI port number from default port of 1099 in shell scripts `server.sh` `client.sh` `client_preload.sh` if they
   want this application uses a different port.

7. **Option for Preloading Data on Client-side**: By revising the shell scripts to deploy server and client components
   in Docker, my design provides clients with the option to preload data upon establishing a connection with the server,
   enhancing data readiness and accessibility.

8. **Graceful Timeout Handling Strategy**: By incorporating a timeout mechanism that effectively manages such
   conditions, my design gracefully handles the scenarios where the server is slow to respond or entirely unresponsive,
   thus preventing indefinite client hangs. This ensures a robust and efficient client-server system.

9. **Test Cases for Comprehensive Evaluation**:

- **`PUT` a Key:** Tests the system's ability to store a new key-value pair when putting a new pair of `<key:value>`.
- **`GET` a Key:** Validates retrieval of values based on their corresponding keys.
- **`DELETE` a Key:** Assesses the system's capability to remove a key-value pair.
- **Override an Existing Key Using `PUT` Request**: Checks the system's behavior when updating the value of an existing key.
- **Timeout Functionality:** Evaluates the system's response to operations that exceed a predefined time limit, ensuring
  that timeouts are handled gracefully.
- **Multi-threading environment:** Uses two separate clients to run a series of PUT, GET, and DELETE requests on a
  server, first running them one after the other and then running both at the same time on a fresh server. By comparing
  the server's data (keyValueStore) after both tests, I checked if they matched the expectation. Since the data from
  both the sequential and parallel runs were the same, my program proves itself to be server thread-safe and can handle
  concurrent operations by different clients accurately.

## How to Run This Project

### Notes

1. There are 4 shell scripts provided in the source code named
   as `server.sh` `client_preload.sh` `client_noPreload_1.sh` `client_noPreload_2.sh` that could be used for the deployment.
2. The shell script `server.sh` will be used to deploy server.
3. The shell scripts `client_preload.sh` `client_noPreload_1.sh` `client_noPreload_2.sh` will be used to deploy client.
   The only difference is `client_preload.sh` contains `preload` option for client to pre-populate operations of 5 PUTs,
   5 GETs, 5 DELETEs, and 5 PUTs. `client_noPreload_1.sh` `client_noPreload_2.sh` only deploy client app with no
   preloading operations. If you want to deploy more clients for testing, you simply need to create shell scripts
   named `client_noPreload_3.sh`... then run the specific script to start a different client app.
4. The port number can be changed by the environment variable `RMI_PORT=${1:-1099}` in all shell scripts if required.
5. See below step-by-step instructions on how to create RMI registry on certain port and then make connection with the
   support of Docker and how to communicate with the server, choose whether to preload data, then perform basic
   operations:

- PUT (key, value)
- GET (key)
- DELETE (key).

### Step-by-Step Instructions on TCP Connection

#### I. Installed & Started Docker

Make sure your have **installed and started your Docker engine** on the local machine.

#### II. Set up Server

1. Enter the root directory of this project and go to the `/src` location.

```
cd src
```

2. Run this command in the terminal.

```
sh server.sh
```

3. If you see the log message similar as below (Timestamp would vary), this means the server has been set up
   successfully. Please note that RMI port can be configured by developers in the shell scripts.

```angular2html
----------watching logs from server app----------
[PST-Time-Zone] 2024-03-19 17:30:38.875 [Level] INFO, [Message] RMI registry created on port: 1098
[PST-Time-Zone] 2024-03-19 17:30:39.088 [Level] INFO, [Message] Server ready and listening on port: 1098
```

#### III. Set up Client

1. Enter the root directory of this project and go to the `/src` location.

```
cd src
```

2. Open another terminal window, run command in the terminal.

Note: If you want this client preload the data (5 PUTs, 5 GETs, 5 DELETEs), run the shell script `client_preload.sh`.

```
sh client_preload.sh
```

If you don't want this client preload the data, just run the shell script `client_noPreload_1.sh` or `client_noPreload_2.sh`

```
sh client_noPreload_1.sh
```

2. If you see the log message similar as below (Timestamp would vary), this means the client has been set up
   successfully.

```angular2html
[PST-Time-Zone] 2024-03-19 17:38:12.188 [Level] INFO, [Message] Client attempting to build RMI connection with host[6650-server] port[1098]...
[PST-Time-Zone] 2024-03-19 17:38:12.537 [Level] INFO, [Message] Client successfully built RMI connection with host[6650-server] port[1098]...
```

3. After the successful connection:

1) If you chose to preload the data, you will first see the log messages on the client side as below for preloading requests BY DEFAULT.

```angular2html
[PST-Time-Zone] 2024-03-19 17:40:47.297 [Level] INFO, [Message] Inserting pair <key=6650, value=Scalable Distributed Systems>
[PST-Time-Zone] 2024-03-19 17:40:47.376 [Level] INFO, [Message] Pair <key=6650, value=Scalable Distributed Systems> added successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.377 [Level] INFO, [Message] Inserting pair <key=5800, value=Algorithms>
[PST-Time-Zone] 2024-03-19 17:40:47.385 [Level] INFO, [Message] Pair <key=5800, value=Algorithms> added successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.386 [Level] INFO, [Message] Inserting pair <key=5700, value=Computer Networking>
[PST-Time-Zone] 2024-03-19 17:40:47.392 [Level] INFO, [Message] Pair <key=5700, value=Computer Networking> added successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.394 [Level] INFO, [Message] Inserting pair <key=6140, value=Machine Learning>
[PST-Time-Zone] 2024-03-19 17:40:47.400 [Level] INFO, [Message] Pair <key=6140, value=Machine Learning> added successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.401 [Level] INFO, [Message] Inserting pair <key=5200, value=Database Management>
[PST-Time-Zone] 2024-03-19 17:40:47.408 [Level] INFO, [Message] Pair <key=5200, value=Database Management> added successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.409 [Level] INFO, [Message] Getting value by key=6650
[PST-Time-Zone] 2024-03-19 17:40:47.418 [Level] INFO, [Message] Value=Scalable Distributed Systems for key=6650 retrieved successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.418 [Level] INFO, [Message] Getting value by key=5800
[PST-Time-Zone] 2024-03-19 17:40:47.432 [Level] INFO, [Message] Value=Algorithms for key=5800 retrieved successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.434 [Level] INFO, [Message] Getting value by key=5700
[PST-Time-Zone] 2024-03-19 17:40:47.444 [Level] INFO, [Message] Value=Computer Networking for key=5700 retrieved successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.445 [Level] INFO, [Message] Getting value by key=6140
[PST-Time-Zone] 2024-03-19 17:40:47.455 [Level] INFO, [Message] Value=Machine Learning for key=6140 retrieved successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.457 [Level] INFO, [Message] Getting value by key=5200
[PST-Time-Zone] 2024-03-19 17:40:47.465 [Level] INFO, [Message] Value=Database Management for key=5200 retrieved successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.466 [Level] INFO, [Message] Deleting pair with key=6650
[PST-Time-Zone] 2024-03-19 17:40:47.476 [Level] INFO, [Message] Pair <key=6650, value=Scalable Distributed Systems> deleted successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.478 [Level] INFO, [Message] Deleting pair with key=5800
[PST-Time-Zone] 2024-03-19 17:40:47.487 [Level] INFO, [Message] Pair <key=5800, value=Algorithms> deleted successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.488 [Level] INFO, [Message] Deleting pair with key=5700
[PST-Time-Zone] 2024-03-19 17:40:47.496 [Level] INFO, [Message] Pair <key=5700, value=Computer Networking> deleted successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.498 [Level] INFO, [Message] Deleting pair with key=6140
[PST-Time-Zone] 2024-03-19 17:40:47.505 [Level] INFO, [Message] Pair <key=6140, value=Machine Learning> deleted successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.507 [Level] INFO, [Message] Deleting pair with key=5200
[PST-Time-Zone] 2024-03-19 17:40:47.516 [Level] INFO, [Message] Pair <key=5200, value=Database Management> deleted successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.517 [Level] INFO, [Message] Inserting pair <key=6650, value=Scalable Distributed Systems>
[PST-Time-Zone] 2024-03-19 17:40:47.521 [Level] INFO, [Message] Pair <key=6650, value=Scalable Distributed Systems> added successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.524 [Level] INFO, [Message] Inserting pair <key=5800, value=Algorithms>
[PST-Time-Zone] 2024-03-19 17:40:47.529 [Level] INFO, [Message] Pair <key=5800, value=Algorithms> added successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.531 [Level] INFO, [Message] Inserting pair <key=5700, value=Computer Networking>
[PST-Time-Zone] 2024-03-19 17:40:47.536 [Level] INFO, [Message] Pair <key=5700, value=Computer Networking> added successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.537 [Level] INFO, [Message] Inserting pair <key=6140, value=Machine Learning>
[PST-Time-Zone] 2024-03-19 17:40:47.543 [Level] INFO, [Message] Pair <key=6140, value=Machine Learning> added successfully.
[PST-Time-Zone] 2024-03-19 17:40:47.544 [Level] INFO, [Message] Inserting pair <key=5200, value=Database Management>
[PST-Time-Zone] 2024-03-19 17:40:47.548 [Level] INFO, [Message] Pair <key=5200, value=Database Management> added successfully.
```

You will also see the log messages on the server side as below for preloading requests BY DEFAULT.

```js
[PST-Time-Zone] 2024-03-19 17:40:47.348 [Level] INFO, [Message] Added/Updated pair <key=6650, value=Scalable Distributed Systems> in the database.
[PST-Time-Zone] 2024-03-19 17:40:47.381 [Level] INFO, [Message] Added/Updated pair <key=5800, value=Algorithms> in the database.
[PST-Time-Zone] 2024-03-19 17:40:47.389 [Level] INFO, [Message] Added/Updated pair <key=5700, value=Computer Networking> in the database.
[PST-Time-Zone] 2024-03-19 17:40:47.397 [Level] INFO, [Message] Added/Updated pair <key=6140, value=Machine Learning> in the database.
[PST-Time-Zone] 2024-03-19 17:40:47.405 [Level] INFO, [Message] Added/Updated pair <key=5200, value=Database Management> in the database.
[PST-Time-Zone] 2024-03-19 17:40:47.412 [Level] INFO, [Message] Retrieved value=Scalable Distributed Systems for key=6650 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.416 [Level] INFO, [Message] Retrieved value=Scalable Distributed Systems for key=6650 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.425 [Level] INFO, [Message] Retrieved value=Algorithms for key=5800 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.430 [Level] INFO, [Message] Retrieved value=Algorithms for key=5800 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.435 [Level] INFO, [Message] Retrieved value=Computer Networking for key=5700 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.438 [Level] INFO, [Message] Retrieved value=Computer Networking for key=5700 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.446 [Level] INFO, [Message] Retrieved value=Machine Learning for key=6140 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.451 [Level] INFO, [Message] Retrieved value=Machine Learning for key=6140 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.459 [Level] INFO, [Message] Retrieved value=Database Management for key=5200 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.463 [Level] INFO, [Message] Retrieved value=Database Management for key=5200 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.468 [Level] INFO, [Message] Retrieved value=Scalable Distributed Systems for key=6650 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.473 [Level] INFO, [Message] Deleted key=6650 from the database.
[PST-Time-Zone] 2024-03-19 17:40:47.480 [Level] INFO, [Message] Retrieved value=Algorithms for key=5800 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.484 [Level] INFO, [Message] Deleted key=5800 from the database.
[PST-Time-Zone] 2024-03-19 17:40:47.490 [Level] INFO, [Message] Retrieved value=Computer Networking for key=5700 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.494 [Level] INFO, [Message] Deleted key=5700 from the database.
[PST-Time-Zone] 2024-03-19 17:40:47.501 [Level] INFO, [Message] Retrieved value=Machine Learning for key=6140 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.504 [Level] INFO, [Message] Deleted key=6140 from the database.
[PST-Time-Zone] 2024-03-19 17:40:47.509 [Level] INFO, [Message] Retrieved value=Database Management for key=5200 in the database
[PST-Time-Zone] 2024-03-19 17:40:47.512 [Level] INFO, [Message] Deleted key=5200 from the database.
[PST-Time-Zone] 2024-03-19 17:40:47.519 [Level] INFO, [Message] Added/Updated pair <key=6650, value=Scalable Distributed Systems> in the database.
[PST-Time-Zone] 2024-03-19 17:40:47.527 [Level] INFO, [Message] Added/Updated pair <key=5800, value=Algorithms> in the database.
[PST-Time-Zone] 2024-03-19 17:40:47.533 [Level] INFO, [Message] Added/Updated pair <key=5700, value=Computer Networking> in the database.
[PST-Time-Zone] 2024-03-19 17:40:47.539 [Level] INFO, [Message] Added/Updated pair <key=6140, value=Machine Learning> in the database.
[PST-Time-Zone] 2024-03-19 17:40:47.546 [Level] INFO, [Message] Added/Updated pair <key=5200, value=Database Management> in the database.
```

2) If you chose NOT to preload the data, you will NOT see the previous log messages for preloading requests BY DEFAULT.
   Instead, you will jump to the next interaction part with the server to perform basic operations.

#### IV. Interact with the server and perform three basic operations.

1. Once the preloading requests have completed, you will see message as below to guide you how to enter the request.

```angular2html
How to Enter Your Request:
Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request:
```

2. For example, if you want to add or update a key-value pair as a `PUT` operation, you enter `2`, and the log messages
   indicating whether your operation is successful will be shown as below.

```angular2html
Please enter a number for your request:
2
Enter the key:
6650
Enter the value:
distributed systems
PUT/UPDATE Operation for
<key=6650, value=distributed systems> completed successfully!
```

3. You will see 1 new log on the server terminal, which means you have completed this operation successfully.

```angular2html
[PST-Time-Zone] 2024-03-19 17:43:53.678 [Level] INFO, [Message] Added/Updated pair <key=6650, value=distributed systems> in the database.
``` 

4. You can also try `PUT` or `DELETE` operations by yourself following the instructions above. 

**Note:** 
1) To ensure system robustness, inputs are validated for null or empty values. Should you enter an 
invalid input, a prompt will appear: `Key or value must NOT be NULL or EMPTY. Please enter a valid input.` Following 
this, you'll be guided back to instructions for making a request.

2) For user convenience, the system automatically trims leading and trailing spaces from inputs. For instance, 
a key-value pair entered as <key=   5001 , value=   python > will be stored as <key=5001, value=python> in the database. 
This not only ensures that inadvertent spaces do not affect the storage and retrieval of data but also reduces the 
likelihood of errors or confusion related to unexpected spaces in key or value fields.


## Test

Please note that all screenshots of my testing done on my local environment for tcp and udp protocols are attached to
the folder of `/res`. Feel free to check them out!

Here, I will only copy and paste the log messages as an example.

### Success

#### PUT

```angular2html
How to Enter Your Request:
Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request:
2
Enter the key:
6650
Enter the value:
distributed systems
PUT/UPDATE Operation for <key=6650, value=distributed systems> completed successfully!
```

You can also **UPDATE** a key-value pair:

```angular2html
How to Enter Your Request:
Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request:
2
Enter the key:
6650
Enter the value:
good course
PUT/UPDATE Operation for <key=6650, value=good course> completed successfully!
```

#### GET

```angular2html
How to Enter Your Request:
Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request:
1
Enter the key:
6650
GET Operation for <key=6650> completed successfully! <value=good course>
```

#### DELETE

```angular2html
How to Enter Your Request:
Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request:
3
Enter the key:
6650
DELETE Operation for <key=6650, value=good course> completed successfully!
```

#### Concurrent Operations

To verify that my program can correctly handle concurrent operations from different clients, I conducted the tests as below:

1) Client 1 on `Terminal | Local(2)`adds a key-value pair: PUT <key=6650, value=distributed systems>. 
```js
How to Enter Your Request:
Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request: 
2
Enter the key: 
6650
Enter the value: 
distributed systems
[PST-Time-Zone] 2024-03-20 13:50:59.313 [Level] INFO, [Message] PUT/UPDATE Operation for <key=6650, value=distributed systems> completed successfully!
PUT/UPDATE Operation for <key=6650, value=distributed systems> completed successfully!
```
2) Client 2 on `Terminal | Local(3)` then deletes this key-value pair: DELETE <key=6650>
```js
How to Enter Your Request:
Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request: 
3
Enter the key: 
6650
DELETE Operation for <key=6650, value=distributed systems> completed successfully!
```
3) Attempting to GET <key=6650> from either Client 1 or Client 2 should result in the key cannot be found in the keyValueStore.
```js
How to Enter Your Request:
Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request: 
1
Enter the key: 
6650
Error! Key=6650 is not found in the database.
```

4) Server also logs these operations done through both Client 1 and Client 2.
```js
----------watching logs from server app----------
[PST-Time-Zone] 2024-03-20 13:50:06.077 [Level] INFO, [Message] RMI registry created on port: 1098
[PST-Time-Zone] 2024-03-20 13:50:06.288 [Level] INFO, [Message] Server ready and listening on port: 1098
[PST-Time-Zone] 2024-03-20 13:50:59.305 [Level] INFO, [Message] Added/Updated pair <key=6650, value=distributed systems> in the database.
[PST-Time-Zone] 2024-03-20 13:51:59.416 [Level] INFO, [Message] Retrieved value=distributed systems for key=6650 in the database
[PST-Time-Zone] 2024-03-20 13:51:59.421 [Level] INFO, [Message] Deleted key=6650 from the database.
[PST-Time-Zone] 2024-03-20 13:53:15.628 [Level] ERROR, [Message] Key=6650 is not found in the database.
[PST-Time-Zone] 2024-03-20 13:53:15.644 [Level] ERROR, [Message] Key=6650 is not found in the database.
[PST-Time-Zone] 2024-03-20 13:53:22.028 [Level] ERROR, [Message] Key=6650 is not found in the database.
```

### Failure

#### PUT

When you tend to put key-value pair in a invalid format in PUT request, the client side will show you guidance:

```angular2html
How to Enter Your Request:
Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request:
2
Enter the key:
6650
Enter the value:

Key or value must NOT be NULL or EMPTY. Please enter a valid input.
```
The client-side will show a log ERROR as well:
```js
[PST-Time-Zone] 2024-03-19 17:57:14.313 [Level] ERROR, [Message] Error: Key and value must not be null or empty after being trimmed.
```

#### GET

1) When you try to retrieve a key that not exists:

```angular2html
How to Enter Your Request:
Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request:
1
Enter the key:
6650
Error! Key=6650 is not found in the database.
```

The server-side will show a log ERROR as well:
```js
[PST-Time-Zone] 2024-03-19 17:58:19.629 [Level] ERROR, [Message] Key=6650 is not found in the database.
```

2) When your try to get a value by a invalid key in GET request, the client side will show you guidance:
```js
How to Enter Your Request:
    Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request: 
1
Enter the key: 

Key or value must NOT be NULL or EMPTY. Please enter a valid input.
```

#### DELETE

1) When you delete a key that does not exist:

```angular2html
How to Enter Your Request:
Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request:
3
Enter the key:
6650
Error! Key=6650 is not found or has already been deleted from the database.
```
The server-side will show a log ERROR as well:
```js
[PST-Time-Zone] 2024-03-19 17:59:51.142 [Level] ERROR, [Message] Key=6650 is not found in the database.
```
2) When your try to delete a value by a invalid key in DELETE request, the client side will show you guidance:

```js
How to Enter Your Request:
    Enter 1: GET the value by a certain key
Enter 2: ADD or UPDATE a key-value pair
Enter 3: DELETE an existing key-value pair
Enter 4: EXIT this program
--------------------------------
Please enter a number for your request: 
3
Enter the key: 
 
Key or value must NOT be NULL or EMPTY. Please enter a valid input.
```

#### Server unresponsive/timeout failure

When the server is not response, the client is robust to server failure by using a timeout mechanism to deal with it.

To test for timeouts in client-server communication in this Java RMI environment, I simulated the scenarios where the 
server takes longer to respond than the client is willing to wait. Through this timeout failure test, my application 
shows it can gracefully handle situations where a service is unresponsive or slow, preventing it from hanging indefinitely.

To achieve this goal, I first set system properties to define the timeout duration for remote method calls before the RMI 
client starts the lookup or invokes methods on the remote object.

To test this scenario, I first modified the server to intentionally delay responses for certain requests by adding a 
`Thread.sleep()` call in the server's `KeyValueRpcImpl` class.

Then I modified the code in `ClientApp` so it can call the server method that I've modified to simulate the delay. 
Since the delay exceeds the timeout I've configured, the client encountered a RemoteException then exited.

In this test, the client shows logs as below:
```angular2html
----------watching logs from client app----------
[PST-Time-Zone] 2024-03-20 12:20:46.803 [Level] INFO, [Message] Client attempting to build RMI connection with host[6650-server] port[1098]...
[PST-Time-Zone] 2024-03-20 12:20:47.290 [Level] INFO, [Message] Client successfully built RMI connection with host[6650-server] port[1098]...
[PST-Time-Zone] 2024-03-20 12:20:47.291 [Level] INFO, [Message] Inserting pair <key=6650, value=Scalable Distributed Systems>
[PST-Time-Zone] 2024-03-20 12:20:50.320 [Level] ERROR, [Message] Client setup error: Error unmarshaling return header; nested exception is: 
        java.net.SocketTimeoutException: Read timed out
```

The server show logs as below:
```js
----------watching logs from server app----------
[PST-Time-Zone] 2024-03-20 12:20:40.261 [Level] INFO, [Message] RMI registry created on port: 1098
[PST-Time-Zone] 2024-03-20 12:20:40.451 [Level] INFO, [Message] Server ready and listening on port: 1098
Sleeping for 10 seconds...
[PST-Time-Zone] 2024-03-20 12:20:57.306 [Level] INFO, [Message] Added/Updated pair <key=6650, value=Scalable Distributed Systems> in the database.
```


## Limitations and Future Iterations

Despite the progress made, there are several areas waiting for future development. For example, although my current
design can meet the project 2's requirements of handling multiple client requests simultaneously using RMI, from TA's
guidance, I realized that implementing a thread pool could be particularly beneficial on the server side. In this case,
server can handle a high volume of concurrent requests efficiently and preventing resource overload by reusing a fixed
number of threads. Also, to improve the system's robustness, consistency, and fault tolerance, it would also be a good
practice to set a solid foundation for a distributed, highly available key-value store.

Therefore, in subsequent iterations of this project, I aim to focus on improving the system’s robustness and fault
tolerance through server replication and a consistency protocol:

- **Server Replication**: To enhance the system's resilience and bandwidth, I will replicate the Key-Value Store across
  5 distinct servers so the program can ensure availability and consistent data access, with clients able to interact
  with any replica for GET, PUT, and DELETE operations without significant changes to client code.

- **Consistency Protocol**: To maintain consistency across all replicated KV stores on PUT and DELETE operations, I will
  implement a two-phase commit (2PC) protocol incorporating defensive coding, such as timeouts, to handle scenarios
  where servers might fail or stall during the 2PC process.

