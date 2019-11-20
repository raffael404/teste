INSERT IGNORE INTO oauth_client_details (client_id, client_secret, web_server_redirect_uri, scope, access_token_validity, refresh_token_validity, resource_ids, authorized_grant_types, additional_information) VALUES
    ('cliente', '{bcrypt}$2y$12$MpMD6DmaJb9YCOFi7feDkOSyODF1qy2KKk4PDpUiQidPZs.8zXuim', 'http://localhost:8080/banking', 'READ,WRITE', '3600', '10000', 'usuario,banco,cliente', 'authorization_code,password,refresh_token,implicit', '{}');

INSERT IGNORE INTO permission (name) VALUES
    ('create_agencia'),
    ('delete_agencia'),
    ('create_conta'),
    ('delete_conta'),
    ('create_transacao'),
    ('read_extrato');

INSERT IGNORE INTO role (name) VALUES
	('ROLE_cliente'),
    ('ROLE_banco');

INSERT IGNORE INTO permission_role (permission_id, role_id) VALUES
    (3,1),
    (4,1),
    (5,1),
    (6,1),
    (1,2),
    (2,2);