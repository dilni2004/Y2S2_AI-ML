spring.application.name=educonnect
server.port=8080

# SQL Server connection (temporary)
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=educonnect
spring.datasource.username=sa
spring.datasource.password=1234

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect