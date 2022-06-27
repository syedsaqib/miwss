INSERT INTO items (id, name, price, quantity, description, internal_notes)
VALUES
    ('54f2dbf0-f3f5-11ec-b939-0242ac120002', 'gizmo1', 10, 1, 'gizmo 1', 'generated by auto bootstrap'),
    ('54f2df42-f3f5-11ec-b939-0242ac120002', 'gizmo2', 20, 1,'gizmo 2', 'generated by auto bootstrap'),
    ('54f2e258-f3f5-11ec-b939-0242ac120002', 'gizmo3', 30, 1, 'gizmo 3', 'generated by auto bootstrap'),
    ('54f2e41a-f3f5-11ec-b939-0242ac120002', 'gizmo4', 40, 0, 'gizmo 4', 'generated by auto bootstrap'),
    ('54f2e578-f3f5-11ec-b939-0242ac120002', 'gizmo5', 15, 3, 'gizmo 5', 'generated by auto bootstrap'),
    ('54f2e776-f3f5-11ec-b939-0242ac120002', 'gizmo6', 17, 4, 'gizmo 6', 'generated by auto bootstrap'),
    ('54f2e8a2-f3f5-11ec-b939-0242ac120002', 'gizmo7', 23, 5, 'gizmo 7', 'generated by auto bootstrap');

INSERT INTO users (id, username, password, full_name, role)
VALUES
    ('30d39854-da4f-43ec-85bc-c999a4e3a1a9', 'customer1', 'abcd1234', 'One', 'CUSTOMER'),
    ('bb16a1fd-e630-4c06-a2fb-05cda54292eb', 'customer2', 'abcd1234', 'Two', 'CUSTOMER'),
    ('88d7bd2a-a092-4f2a-b969-d50e31db2c62', 'customer3', 'abcd1234', 'Three', 'CUSTOMER'),
    ('13f59dd9-4552-436a-b858-66358ffa6b30', 'admin1', 'admin1234', 'Admin User', 'ADMIN');