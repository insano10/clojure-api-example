-- :name sql-insert-user!
-- :result :returning-execute
-- :doc inserts a user returning the ID
INSERT INTO users (id, data)
VALUES (uuid(:id), jsonb(:data))
RETURNING :id;