# Scalable Tracking Number Generator

A Spring Boot application that generates unique tracking numbers using a Snowflake-like algorithm with Redis.
This tracking number generator is designed to be scalable and efficient, making it suitable for high-volume applications.

## Tracking Number Generation Logic
- Prefixes the code with origin and destination country codes if provided.
- Encodes a 60-bit unique identifier into Base-36 format using a Snowflake-like algorithm which consists of:
    - 42 bits for timestamp (milliseconds since epoch)
    - 10 bits for sequence number (to handle multiple requests in the same millisecond)
    - 9 bits for worker ID (to support distributed systems)

## Key Features
- The generated tracking number is unique for every call. 
- The worker ID is configured to allow multiple instances of the application to run concurrently without generating duplicate tracking numbers.
- Redis is used to increment the sequence number for each milisecond call, ensuring that even if multiple requests are made at the same time, each will receive a unique tracking number.
- The Redis TTL is set to 5 seconds, which means that it can be scalable without consuming too much memory.
- Uses Java 21 virtual threads to handle high concurrency efficiently.


## Docker Setup

This project includes Docker and Docker Compose configuration for easy deployment.

### Prerequisites

- Docker

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

### Stopping the Application

To stop the application, run:

```bash
docker-compose down
```

To stop the application and remove the volumes (including Redis data), run:

```bash
docker-compose down -v
```

## Example

Below is a deployed endpoint where you can test generating tracking numbers at:

```
https://game-keen-sturgeon.ngrok-free.app/next-tracking-number
```

You can also add the required parameters in the get request as follows:

```
https://game-keen-sturgeon.ngrok-free.app/next-tracking-number?origin_country_id=MY&destination_country_id=ID&weight=1.234&created_at=2023-01-01T12:00:00%2B08:00&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=redbox-logistic
```