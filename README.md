# Library Management System

This is a Library Management System built as a Model-View-Controller (MVC) application using Spring Boot.

## Features

- User authentication and role-based access control
- CRUD operations for books and patrons
- Borrowing and returning of books

## Prerequisites

- Java 17
- MySQL

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Installing

1. Clone the repository:
    ```
    git clone https://github.com/yourusername/library_management_system.git
    ```
2. Navigate into the project directory:
    ```
    cd library_management_system
    ```
3. Install the dependencies:
    ```
    mvn install
    ```

### Running the Application

1. Start the application:
    ```
    mvn spring-boot:run
    ```
2. Once the application is running, navigate to http://localhost:8080/lms/ in your web browser.

## Built With

- Spring Boot - The web framework used
- Maven - Dependency Management
- MySQL - Used for the database
- Thymeleaf - Server-side Java template engine for web applications
- Spring Security - Framework for authentication and authorization
- JWT - Used for generating JSON Web Tokens
- ModelMapper - Object model mapper that intelligently maps objects to each other
- Lombok - Java library tool used to minimize boilerplate code

## Authors

- bitscoder-dotcom

## Image Descriptions
![](/src/main/resources/snapshots/homepage.png "Home page")
![](/src/main/resources/snapshots/register.png "user registration page")
![](/src/main/resources/snapshots/signin.png "user sign in")
![](/src/main/resources/snapshots/userpage.png "user page")
![](/src/main/resources/snapshots/addbook.png "add book page")
![](/src/main/resources/snapshots/Listofbooks.png)
![](/src/main/resources/snapshots/bookdetails.png "Book details")
![](/src/main/resources/snapshots/updatepatron.png "update patron page")
![](/src/main/resources/snapshots/updatedbook.png "updated book")
![](/src/main/resources/snapshots/allpatrons.png "List of patrons")
![](/src/main/resources/snapshots/deletepatron.png "Delete patron")
![](/src/main/resources/snapshots/booktransaction.png "Book transaction")

## Feedback
- Your feedback is much appreciated. Your are free to modify this code, as far as you don't digress from the application scope. Lets communicate!!
- https://twitter.com/bitscoder93

