# Network Chat

A chat program with server/client structure, using UDP protocol to send data over the network. 
The server runs on the console and has a few commands; the client GUI was developed with Java Swing.

### Compiling
To build the project, compile it and create a jar file.
The class with the main function will compile everything else.

Inside the server folder:

```
javac ServerMain.java
```

Then, run the below command:
```
jar cfe Server.jar ServerMain *.class
```

Inside the client folder:

```
javac Login.java
```

Then, run the below command:
```
jar cfe Client.jar Login *.class
```

### Running
Run the server with the command below from the server folder:
``` 
java -jar Server.jar <port number> 
```

Run the client by simply opening the Client.jar file.

### Login
![Login](/resources/login.PNG "Login")

### The Chat
![Chat](/resources/running.PNG "Chat")

### Online Users
![Online Users](/resources/onlineusers.PNG "Online Users")

### Kicking User
![Kicking User](/resources/kickuser.PNG "Kicking User")
