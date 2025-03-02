TCP Image Transfer
A Java TCP Client-Server program for transferring images with a JavaFX GUI.

📌 Project Overview
This project consists of a TCP Server and a TCP Client that communicate over a network. The client can:
✅ Request a list of available images stored on the server.
✅ Retrieve a specific image from the server by its ID.
✅ Upload an image to the server for storage.
✅ Use a JavaFX GUI for an intuitive user experience.
======================================================================================================
⚙️ Technologies Used
Java (Socket Programming for TCP communication)
JavaFX (GUI for client interaction)
Multi-threaded Server
File Handling (Storing and retrieving images)
🚀 How to Run
======================================================================================================
1️⃣ Start the Server
Navigate to the server directory and run:
sh
Copy
Edit
java server.Server
The server will start and listen for incoming client connections.
======================================================================================================
2️⃣ Start the Client
Navigate to the client directory and run:
sh
Copy
Edit
java client.Client
The JavaFX GUI will launch, allowing the user to interact with the server.
======================================================================================================
📝 Features
✅ Client requests a list of available images.
✅ Client retrieves an image by its ID.
✅ Client uploads an image to the server.
✅ Multi-threaded server handles multiple client requests.
✅ JavaFX GUI for easy interaction.
======================================================================================================
🔧 Future Improvements
Add encryption for secure image transfers.
Implement a database to store image metadata.
Allow multiple file types, not just images.
======================================================================================================
👨‍💻 Author:
Created by Likhwa Mpika

