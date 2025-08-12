# File System Watcher – Real-Time File Tracking App  
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-008080?style=for-the-badge)
![JavaMail](https://img.shields.io/badge/JavaMail-FF6F00?style=for-the-badge)
![WatchService](https://img.shields.io/badge/WatchService_API-4B8BBE?style=for-the-badge)
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-25A162?style=for-the-badge)
![Checkstyle](https://img.shields.io/badge/Checkstyle-FFD43B?style=for-the-badge)
![MVC](https://img.shields.io/badge/MVC_Architecture-2C3E50?style=for-the-badge)
![Observer Pattern](https://img.shields.io/badge/Observer_Pattern-34495E?style=for-the-badge)
![Interface Abstraction](https://img.shields.io/badge/Interface_Abstraction-7F8C8D?style=for-the-badge)



## 📌 Description 
File System Watcher is a **Java-based desktop application** that monitors real-time file system events (**CREATE**, **MODIFY**, **DELETE**) using the **WatchService API**. It logs events into an **SQLite** database, supports **query filtering**, allows **CSV export**, and enables **secure email delivery** of reports.  

Think of it like a *security camera for your files*. It watches a folder in real time, records what happens, and lets you search, export, and even email a report of those changes.  

This project was developed as part of **TCSS 360 – Software Development & Quality Assurance** at the **University of Washington Tacoma**. The project was designed to simulate **real-world software development** from planning and architecture to coding, testing, and delivering a working application, following **MVC architecture** and incorporating the **Observer pattern** for responsive UI updates.


---

## 🏆 Why This Project Matters  
- **Practical use:** Helps users keep records of important file changes which are useful for IT teams, small businesses, or anyone who needs to track file activity for security or audit purposes.  
- **Professional relevance:** Simulates common workplace software features such as data logging, reporting, database integration, and email automation.  
- **Technical depth:** Combines **front-end (GUI)** and **back-end (logic, database)** work in a single application.  

---

## 🚀 Key Features  

- **Real-Time File Monitoring**: Detects when files are created, changed, or deleted.
- **Custom monitoring** – choose your own file types to track.  
- **Search Filter**: Find specific changes by date, file type, or action taken.
- **Save Records**: Stores file activity in a database for future reference.
- **Export Reports**: Download results as a CSV spreadsheet. 
- **Email Reports**: Send reports directly from the app via Gmail.
- **Graphical User Interface** : Simple easy to use, user-friendly interface to start/stop monitoring and view file events instantly. 

---

## 📌 How It Works
1. **Select what to monitor** – Choose a folder and specify the file types to track (e.g., `.docx`, `.pdf`).  
2. **Monitor in real time** – The application continuously detects and records file events such as creation, modification, and deletion.  
3. **Search and filter results** – Apply filters to quickly locate specific file changes based on date, type, or action.  
4. **Save and share reports** – Export filtered results as a CSV file or send them directly via email with just a few clicks.  

---

## 🛠 Tech Stack  

- **Language:** Java 21  
- **Frameworks & APIs:** Java Swing GUI, JavaMail API, WatchService API  
- **Database:** SQLite (JDBC)  
- **Tools:** IntelliJ IDEA, JUnit, Checkstyle  
- **Architecture & Patterns:** MVC, Observer Pattern, Interface Abstraction  

---

## 👩‍💻 My Contributions
- Designed and implemented **QueryWindow** GUI, event filtering, and CSV export functionality.  
- Integrated email sending feature using **JavaMail API** + **Gmail SMTP** (with TLS encryption).  
- Developed core classes: `QueryWindow`, `FileEvent`, `EmailSender`, `IEmailSender`.  
- Wrote **JUnit test cases** for email and export features.  
- Applied **MVC architecture** and **Observer pattern** for modularity and maintainability.  

---

## 📸 Screenshots / Demo  
*(Add screenshots of the main window, query window, and CSV export results)*  

---

## 📂 Project Structure  

```plaintext
src/
├── controller/
│   ├── FileMonitor.java
│   └── FileSystemMain.java
├── model/
│   ├── DatabaseManager.java
│   ├── FileEvent.java
│   ├── EmailSender.java
│   ├── IEmailSender.java
│   └── CSVExporter.java
├── view/
│   ├── MainWindowFile.java
│   └── QueryWindow.java
└── tests/
    ├── EmailSenderTest.java
    ├── CSVExporterTest.java
    └── DatabaseManagerTest.java


