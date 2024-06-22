START TRANSACTION;

INSERT INTO users (id,
                   user_id,
                   first_name,
                   username,
                   last_name,
                   email,
                   bio,
                   reference_id,
                   image_url,
                   created_by,
                   updated_by,
                   created_at,
                   updated_at,
                   account_non_expired,
                   account_non_locked,
                   enabled,
                   mfa)
VALUES (0,
        '023a7479-e7a7-079f-3ae5-a766fe25eca9',
        'System',
        'System',
        'System',
        'system@gmail.com',
        'This is not a user but the system itself',
        '897f3898-31ed-654a-eb93-531648a0db88',
        'https://play-lh.googleusercontent.com/ki_oNQS0vtmA2eah8qbnjEhQ_ZP_f6llQ5CkNhTqvVfxRV6wtQaAxQDmq2CkjHFbeUA=w2560-h1440-rw',
        0,
        0,
        '2024-06-22 10:13:19.634806',
        '2024-06-22 10:13:19.634806',
        true,
        true,
        false,
        false);

#-- First, insert the user if not already present
#INSERT INTO users (id, user_id, first_name, last_name, email, created_by, updated_by, created_at,updated_at)
#VALUES (1, 'user123', 'John', 'Doe', 'john.doe@example.com', 1, 1, '2024-06-22 10:13:19.634806', '2024-06-22 10:13:19.634806')
#ON DUPLICATE KEY UPDATE first_name = 'John',
#                        last_name  = 'Doe',
#                        email      = 'john.doe@example.com',
#                        created_by = 1,
#                        updated_by = 1
#;
#
#-- Then, insert into confirmations table
#INSERT INTO confirmations (uuidKey, user_id, created_by, updated_by)
#VALUES ('some-uuid-key', 1, 1, 1);

COMMIT;