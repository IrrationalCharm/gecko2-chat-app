--Part 1 just run this
DROP DATABASE IF EXISTS user_service_db;
DROP ROLE IF EXISTS flyway_admin;
DROP ROLE IF EXISTS user_service;

CREATE DATABASE user_service_db
    WITH ENCODING 'UTF8'
    LC_COLLATE='en_US.utf8'
    LC_CTYPE='en_US.utf8'
    TEMPLATE=template0;

CREATE ROLE flyway_admin WITH LOGIN PASSWORD 'thisIsWild123';
CREATE ROLE user_service WITH LOGIN PASSWORD 'nonSecureUserPassword';

GRANT ALL ON DATABASE user_service_db TO flyway_admin;

--Part 2 connect to postgre
-- docker exec -it postgres psql -d postgres -U admin
-- inside the container we connect to the new database
-- \c user_service_db
-- Then we run the grants for the new database
REVOKE ALL ON SCHEMA public FROM PUBLIC; -- This is a good security practice
GRANT CREATE, USAGE ON SCHEMA public TO flyway_admin;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO flyway_admin;
GRANT ALL ON DATABASE user_service_db TO flyway_admin;

ALTER DEFAULT PRIVILEGES FOR ROLE flyway_admin IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO user_service;

ALTER DEFAULT PRIVILEGES FOR ROLE flyway_admin IN SCHEMA public
    GRANT USAGE, SELECT ON SEQUENCES TO user_service;