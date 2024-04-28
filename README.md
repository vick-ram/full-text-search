**FULL-TEXT-SEARCH**
- [x] [Full-Text Search](https://www.postgresql.org/docs/current/textsearch-tables.html#TEXTSEARCH-TABLES-SEARCH)
- [x] [Full-Text Search Functions and Operators](https://www.postgresql.org/docs/current/functions-textsearch.html)

The full-text search feature allows you to efficiently search text stored in a PostgreSQL database. The feature includes the following:

- Configurable text search configurations
- Full-text indexing
- Full-text search functions and operators
- Full-text search queries

The full-text search feature is based on the following concepts:

- **Document**: A piece of text to be searched.
- **Lexeme**: A word that has been normalized to its base form. For example, the words "jumping", "jumps", and "jumped" are all normalized to the lexeme "jump".
  - **Token**: A lexeme that is indexed for searching.
  - **Stopword**: A common word that is ignored during full-text search. For example, "the" and "is" are stopwords.
- **Parser**: A function that converts a document into a list of tokens.

The full-text search feature is useful for searching text in the following scenarios:

- **Web search engines**: Search engines use full-text search to search for web pages that contain specific keywords.
- **Document management systems**: Document management systems use full-text search to search for documents that contain specific keywords.
- **E-commerce websites**: E-commerce websites use full-text search to search for products that contain specific keywords.
- **Social media websites**: Social media websites use full-text search to search for posts that contain specific keywords.
- **Chat applications**: Chat applications use full-text search to search for messages that contain specific keywords.

**Technologies Used**

- **PostgreSQL**: A powerful, open-source object-relational database system.
- **pg_trgm**: A PostgreSQL extension that provides functions and operators for text similarity measurement and indexing.
- **Ktor**: A Kotlin-based framework for building asynchronous web applications.
- **Exposed**: A Kotlin SQL framework that provides a type-safe SQL API.
- **HikariCP**: A high-performance JDBC connection pool.
- **Kotlin Coroutines**: A Kotlin feature that allows you to write asynchronous code in a sequential style.

**Features**

- **Full-Text Indexing**: Indexes the text columns in the database to improve search performance.
- **Full-Text Search**: Searches for text in the database using the full-text search feature.
- **Text Similarity**: Measures the similarity between two text values using the pg_trgm extension.
- **Asynchronous Web Application**: Uses Kotlin Coroutines to build an asynchronous web application.
- **Type-Safe SQL API**: Uses Exposed to write type-safe SQL queries in Kotlin.
- **Connection Pooling**: Uses HikariCP to manage JDBC connections efficiently.

**Setup**

To set up the project, you need to have Kotlin and Java installed on your machine. You also need to set the following environment variables for the database connection:  
- `URL`: The JDBC URL of your PostgreSQL database.
- `USER`: The username for your PostgreSQL database.
- `DB_PASSWORD`: The password for your PostgreSQL database.
- `DRIVER`: The JDBC driver class name for your PostgreSQL database.

Once you have these set up, you can initialize the database by calling `DatabaseFactory.init()`. This will set up the connection pool, create the necessary tables and indices, and set up a trigger for updating the full-text search vector.

**API Endpoints**

The project provides the following API endpoints:

- `GET /api/v1/users`: Fetches users based on the provided query parameters. If an email is provided, it fetches the user with the matching email. If a search term is provided, it performs a search for users matching the term. If no parameters are provided, it fetches all users.  
- `POST /api/v1/users`: Creates a new user. The request body should contain the details of the user to be created.  
- `PUT /api/v1/users/{id}`: Updates the user with the provided ID. The request body should contain the updated details of the user.  
- `DELETE /api/v1/users/{id}`: Deletes the user with the provided ID.

**Running the Project**

You can run the project using the Gradle run task. This will start the server and make the API endpoints available for use.