DO $$
    DECLARE
        user_id UUID := gen_random_uuid();
    BEGIN

        insert into users (id, email, password_hash) values ( user_id, 'admin@admin.ru', '$2a$12$wBSwQr4Es44j7tcnttqOUuX/xGdE/NqhVqNyFo4jNBQGVcf9qtB4W');
        insert into user_roles ( user_id, role ) values ( user_id, 'ROLE_USER');
        insert into user_roles ( user_id, role ) values ( user_id, 'ROLE_ADMIN');

END $$;
;;