spring.application.name=educonnect
server.port=8080

# SQL Server connection (temporary)
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=educonnect
spring.datasource.username=sa
spring.datasource.password=1234

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect

# File upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/sfs_educonnect
spring.datasource.username=root
spring.datasource.password=yourpassword

