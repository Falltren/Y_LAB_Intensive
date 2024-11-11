
INSERT INTO my_schema.users (id, name, email, password, role, create_at, update_at, is_blocked, is_active)
VALUES (100, 'admin', 'admin@admin.com', '$2a$10$o64Ue.UXV/CxRdXis.7Sle.IeMfoAKbsHVSSEPoLcrwUDDdAcOYlK', 'ROLE_ADMIN', '2024-09-01 12:00:00.000000', null, false, true),
       (101, 'user1', 'email1@user.com', '$2a$10$lb7OBJQp.DKJtQMh1PuHc.ckMP882TqzCB7yiWg6KmIs7nDNpaEZW', 'ROLE_USER', '2024-09-02 12:00:00.000000', null, false, true),
       (102, 'blocked', 'blocked@user.com', '$2a$10$BWjpGKxCLXR0gGKJ6dX4QeKaipmD79gi9OoH0zRZNC0s51C2NrhO6', 'ROLE_USER', '2024-09-02 12:00:00.000000', null, true, true);

INSERT INTO my_schema.habits(id, title, text, execution_rate, create_at, user_id)
VALUES (100, 'title1', 'text', 'WEEKLY', '2024-10-03 00:00:00.000000', 101),
       (101, 'title2', 'text', 'MONTHLY', '2024-10-04 00:00:00.000000', 101),
       (102, 'title3', 'text', 'DAILY', '2024-10-04 00:00:00.000000', 101);

INSERT INTO my_schema.habit_execution (id, date, habit_id)
VALUES (100, '2024-10-05 00:00:00.000000', 100),
       (101, '2024-10-20 00:00:00.000000', 100),
       (102, '2024-10-21 00:00:00.000000', 101),
       (103, '2024-10-21 00:00:00.000000', 102),
       (104, '2024-10-23 00:00:00.000000', 102),
       (105, '2024-10-24 00:00:00.000000', 102);