# Scalable Tracking Number Generator

A Spring Boot application that generates unique tracking numbers using a Snowflake-like algorithm with Redis for sequence management.

## Docker Setup

This project includes Docker and Docker Compose configuration for easy deployment.

### Prerequisites

- Docker
- Docker Compose

### Running with Docker Compose

1. Clone the repository
2. Navigate to the project directory
3. Run the following command:

```bash
docker-compose up -d
```

This will:
- Build the Spring Boot application
- Start a Redis instance
- Connect the application to Redis
- Expose the application on port 8080

### Generating Tracking Numbers

Once the containers are running, you can generate tracking numbers at:

```
http://localhost:8080/next-tracking-number
```

You can add the required parameters in the get request as follows:

```
http://localhost:8080/next-tracking-number?origin_country_id=MY&destination_country_id=ID&weight=1.234&created_at=2023-01-01T12:00:00%2B08:00&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=redbox-logistic
```

### Stopping the Application

To stop the application, run:

```bash
docker-compose down
```

To stop the application and remove the volumes (including Redis data), run:

```bash
docker-compose down -v
```

