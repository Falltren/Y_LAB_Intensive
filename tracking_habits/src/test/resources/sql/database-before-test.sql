TRUNCATE TABLE my_schema.users cascade;

INSERT INTO my_schema.users (id, name, email, password, role, create_at, update_at, is_blocked, is_active)
VALUES (100, 'admin', 'admin@admin.com', '$2a$10$o64Ue.UXV/CxRdXis.7Sle.IeMfoAKbsHVSSEPoLcrwUDDdAcOYlK', 'ROLE_ADMIN', '2024-11-01 12:00:00.000000', null, false, true),
       (101, 'user1', 'email1@user.com', '$2a$10$lb7OBJQp.DKJtQMh1PuHc.ckMP882TqzCB7yiWg6KmIs7nDNpaEZW', 'ROLE_USER', '2024-11-02 12:00:00.000000', null, false, true),
       (102, 'blocked', 'blocked@user.com', '$2a$10$BWjpGKxCLXR0gGKJ6dX4QeKaipmD79gi9OoH0zRZNC0s51C2NrhO6', 'ROLE_USER', '2024-11-02 12:00:00.000000', null, true, true);