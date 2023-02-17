ATM Client-Server Application
=============================

This project is an implementation of an ATM client-server application using Java, JavaFX, sockets, and multithreading. The application simulates the basic functionalities of an Automated Teller Machine (ATM), where a user can authenticate, check their account balance, withdraw or deposit funds from their account.

Requirements
------------

-   Java 8 or higher
-   JavaFX
-   Intellij or another Java IDE
-   Maven

How to run
----------

1.  Clone the repository using the following command:

    `git clone https://github.com/JonMukaj/ATM.git`

2.  Open the project in your Java IDE.

3.  Compile and run Server with maven plugin to start the server.

4.  Compile and run Client with maven plugin to start the client.

5.  In addition for ease of use, we have provided executables initially for Windows and Linux, which can be used even in systems where JDK is missing. You can find the  executables on the "platforms" directory.

Usage
----------`
Upon running the client-side application, the user will be prompted to enter the server address and port number to connect to. The user can then enter various commands to interact with the ATM, such as START to establish a connection with the ATM, CLOSE to close the connection, AUTH <PIN> to authenticate with the ATM, BALANCE to check their account balance, DEBIT <amount> to withdraw money from their account, and CREDIT <amount> to deposit money into their account.

The client-side application also has a graphical user interface (GUI) built using JavaFX, which provides an intuitive and user-friendly interface for the user to interact with the ATM.

License
-------

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

Authors
-------

-   [Jon Mukaj](https://github.com/jonmukaj)
-   [Joana Jaupi](https://github.com/joanajaupi)
-   [Kevin Tenolli](https://github.com/ProfessorGustavi)
-   [Fabio Marku](https://github.com/fabiomarku1)
