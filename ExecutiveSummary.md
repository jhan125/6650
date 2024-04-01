# Executive Summary

## Assignment Overview

Based on the valuable guidance from TAs on Piazza, I learned that this assignment asks us to advance the development of 
a key-value store system by integrating multi-threaded server capabilities and Remote Procedure Calls (RPC) to facilitate
client-server communication. This underlines the focus on transitioning from Project 1's single-thread server-client
key-value store system to Project 2's multi-threaded key-value store system and utilize Remote Procedure Calls (RPC) so
system can manage multiple client requests concurrently, thus ensuring efficient, thread-safe operations and data
consistency. From Dr. Saripalli's previous lectures, I also learned that RPC is a communication mechanism that can
simplify client-server interactions by abstracting the complexities of network programming. Therefore, I decided to 
include server and client implementations, shared APIs for RPC interactions, and extensive documentation and testing 
materials in Project 2's structure, so the framework not only supports basic PUT, GET, and DELETE operations
but also lays the groundwork for future scalability and improvements in the system’s design and functionality.

## Technical Impression

My first impression of this project is that I should design a multi-tasking system that can communicate with multiple 
users at the same time through RPC. The key-value store should still support basic operations such as PUT, GET, and 
DELETE but the key point is that the system should be able to handle multiple outstanding client requests simultaneously, 
while ensuring thread-safe operations and data consistency. Since I have learned how to use Java RMI framework for RPC 
in Dr. Saripalli's lectures and our textbook, I decided to improve my application from the old socket-based method to 
the more advanced, object-oriented RMI framework in order to meet the 2 main project requirements: 
1) enable RPC communication 
2) enable multi-threading and mutual exclusion

To achieve this goal, I made several design considerations and implemented accordingly. First, to adapt my project to use
RMI, I first defined the interface `KeyValueRpc` that can be called remotely. Then I implemented the class 
`KeyValueRpcImpl` to make objects ready for remote communication. In `ServerApp`, I created an instance of the remote object 
implementation and register it with the RMI registry so clients can look up this remote object by its name. On the
client side, I adapted the `ClientApp` so client can use the registered name of the remote object to obtain a reference. 
During the implementation, I also put efforts on the exception handling and user input handling work. By rewriting and
re-fractoring the code, my program is able to use an object-oriented approach to simplify remote interactions. Second,
to ensure thread safety and data consistency among concurrent operations, I used special locks (`ReentrantLock`) to
manage key-value pair access and to control critical sections during write operations. To be honest, at first I used 
`ConcurrentHashMap` in the `KeyValue` implementation, but later inspired by TA Rammanoj's guidance, I decided to use this 
lock approach in order to prepare the system for future complexities while maintaining smooth and fast operations for 
users. Third, to enhance user experience and system reliability, I implemented a graceful timeout handling strategy for
instances when server responses are delayed, thus ensuring the users won't get stuck waiting forever. Fourth, to make 
deployment simple and flexible, I also rewrite Dockerfiles and shell scripts so developers can easily configure the RMI 
registry port with Docker. Additionally, developers can also run different shell scripts that enable clients to choose 
whether to preload data upon establishing a connection with the server. Finally, I did comprehensive test cases to 
evaluate the system's functionality and included the testing logs in both `README` and folder `src/res/`. This proves 
that my system is thread-safe and is capable to handle concurrent operations and multiple client requests simultaneously 
without compromising data consistency or system reliability. Moving forward, I will follow TA's guidance and focus on 
improving the system’s robustness and fault tolerance through server replication and a consistency protocol so the 
program can maintain availability and consistency across all replicated KV stores when clients interact with any replica
for GET, PUT, and DELETE operations. I will also incorporate defensive coding, such as timeouts, to handle scenarios 
where servers might fail or stall during the process.